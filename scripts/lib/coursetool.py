#!/usr/bin/env python3
"""Course tooling behind scripts/new-module.sh and scripts/check-module.sh.

    coursetool.py new   <module-id> [--title-tr T] [--title-en T] [--infra p1,p2] [--force]
    coursetool.py check <module-id>... | --all  [--strict]
    coursetool.py sync-snippets <module-id>...   # refresh doc code blocks from their source

Only the Python standard library is used, so the scripts run anywhere Python 3.9+ exists.
"""
from __future__ import annotations

import argparse
import datetime as dt
import filecmp
import hashlib
import re
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
MODULES = ROOT / "modules"
TEMPLATES = ROOT / "docs" / "templates"
ROOT_POM = ROOT / "pom.xml"
SPEC = ROOT / "SPEC.md"

ID_PATTERN = re.compile(r"^\d{2}-[a-z0-9]+(-[a-z0-9]+)*$")
INFRA_PROFILES = {"postgres", "mongo", "elastic", "redis", "kafka", "hazelcast", "observability", "ai"}
DOC_PAIRS = [("tr/ders.md", "en/lesson.md"), ("tr/odevler.md", "en/exercises.md")]
PDFS = ["tr/ders.pdf", "tr/odevler.pdf", "en/lesson.pdf", "en/exercises.pdf"]
# <!-- snippet: lesson/src/main/java/.../File.java#tag-name -->   (region between // tag::tag-name[] and // end::tag-name[])
# <!-- snippet: lesson/src/main/java/.../File.java#L10-L25 -->    (fixed line range)
SNIPPET = re.compile(r"^<!-- snippet: (\S+?)#(?:L(\d+)-L(\d+)|([a-z0-9][a-z0-9-]*)) -->\s*$")
TAG_LINE = re.compile(r"(tag|end)::[a-z0-9][a-z0-9-]*\[\]")


# ---------------------------------------------------------------------------------------------
# naming helpers
# ---------------------------------------------------------------------------------------------
def slug_words(module_id: str) -> list[str]:
    return module_id.split("-")[1:]


def package_name(module_id: str) -> str:
    """06-data-jpa-postgres → datajpapostgres (Java package segment)."""
    return "".join(slug_words(module_id))


def class_prefix(module_id: str) -> str:
    """06-data-jpa-postgres → DataJpaPostgres."""
    return "".join(w.capitalize() for w in slug_words(module_id))


def spec_module_ids() -> set[str]:
    return set(re.findall(r"^\| \d{2} \| `(\d{2}-[a-z0-9-]+)`", SPEC.read_text(), flags=re.M))


# ---------------------------------------------------------------------------------------------
# new
# ---------------------------------------------------------------------------------------------
POM = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.springbootedu</groupId>
        <artifactId>build-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../../../build-parent/pom.xml</relativePath>
    </parent>

    <artifactId>{module_id}-{kind}</artifactId>
    <name>{module_no} · {title_en} :: {kind}</name>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>{compose_dependency}
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
"""

COMPOSE_DEPENDENCY = """
        <!-- Starts the services from the root compose.yaml on spring-boot:run (not in tests) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-docker-compose</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>"""

APPLICATION = """package com.springbootedu.{pkg};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// tag::application[]
@SpringBootApplication
public class {prefix}Application {{

    public static void main(String[] args) {{
        SpringApplication.run({prefix}Application.class, args);
    }}
}}
// end::application[]
"""

PACKAGE_INFO = """/**
 * Module {module_no} — {title_en}.
 */
@NullMarked
package com.springbootedu.{pkg};

import org.jspecify.annotations.NullMarked;
"""

APPLICATION_TEST = """package com.springbootedu.{pkg};

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class {prefix}ApplicationTest {{

    @Test
    void contextLoads() {{
    }}
}}
"""

EXERCISE_STARTER = """package com.springbootedu.{pkg};

/**
 * Exercise 1 — placeholder created by new-module.sh. Replace it with the module's real exercise.
 */
public class Exercise1 {{

    public String answer() {{
        // TODO Exercise 1: return "42"
        throw new UnsupportedOperationException("TODO Exercise 1");
    }}
}}
"""

EXERCISE_SOLUTION = """package com.springbootedu.{pkg};

/**
 * Exercise 1 — reference solution (placeholder created by new-module.sh).
 */
public class Exercise1 {{

    public String answer() {{
        return "42";
    }}
}}
"""

EXERCISE_TEST = """package com.springbootedu.{pkg};

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Exercise1Test {{

    @Test
    void answersTheQuestion() {{
        assertThat(new Exercise1().answer()).isEqualTo("42");
    }}
}}
"""

APPLICATION_YAML = """spring:
  application:
    name: {module_id}
  threads:
    virtual:
      enabled: true
"""

COMPOSE_YAML = """  docker:
    compose:
      # Reuse the root compose.yaml; only the listed profiles are started.
      file: ../../../compose.yaml
      profiles:
        active: {profiles}
      # Keep containers running between restarts (stop them with: docker compose --profile all down)
      lifecycle-management: start-only
"""

REQUESTS_HTTP = """# HTTP examples for module {module_id}. Run them from IntelliJ IDEA / VS Code (REST Client).
# Start the lesson first: ./mvnw -pl modules/{module_id}/lesson spring-boot:run

### Example
GET http://localhost:8080/
"""


def write(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content)


def fill(template: str, values: dict[str, str]) -> str:
    for key, value in values.items():
        template = template.replace("{{" + key + "}}", value)
    return template


def register_in_root_pom(module_id: str) -> None:
    """Adds the module between the pom.xml markers, keeping each list sorted by module id."""
    pom = ROOT_POM.read_text()

    def add(pom: str, begin: str, end: str, indent: str, entries: list[str]) -> str:
        head, rest = pom.split(begin, 1)
        body, tail = rest.split(end, 1)
        lines = {l.strip() for l in body.splitlines() if l.strip()} | set(entries)
        block = "".join(f"\n{indent}{l}" for l in sorted(lines))
        return f"{head}{begin}{block}\n{indent}{end}{tail}"

    pom = add(pom, "<!-- course-modules:begin -->", "<!-- course-modules:end -->", " " * 8,
              [f"<module>modules/{module_id}/lesson</module>", f"<module>modules/{module_id}/solution</module>"])
    pom = add(pom, "<!-- exercise-modules:begin -->", "<!-- exercise-modules:end -->", " " * 16,
              [f"<module>modules/{module_id}/exercise</module>"])
    ROOT_POM.write_text(pom)


def cmd_new(args: argparse.Namespace) -> int:
    module_id = args.module_id
    if not ID_PATTERN.match(module_id):
        return fail(f"Invalid module id '{module_id}'. Expected NN-kebab-case, e.g. 06-data-jpa-postgres.")
    if module_id not in spec_module_ids() and not args.force:
        return fail(f"'{module_id}' is not in the SPEC.md capability map. Adding modules needs approval "
                    "(CLAUDE.md → Ask first). Update SPEC.md first, or pass --force for throwaway experiments.")
    module_dir = MODULES / module_id
    if module_dir.exists():
        return fail(f"{module_dir.relative_to(ROOT)} already exists.")
    infra = [p for p in (args.infra or "").split(",") if p]
    unknown = set(infra) - INFRA_PROFILES
    if unknown:
        return fail(f"Unknown infra profile(s): {', '.join(sorted(unknown))}. Known: {', '.join(sorted(INFRA_PROFILES))}")

    words = " ".join(w.capitalize() for w in slug_words(module_id))
    v = {
        "MODULE_ID": module_id,
        "MODULE_NO": module_id[:2],
        "TITLE_TR": args.title_tr or words,
        "TITLE_EN": args.title_en or words,
        "DATE": dt.date.today().isoformat(),
        "PACKAGE": package_name(module_id),
    }
    fmt = dict(module_id=module_id, module_no=v["MODULE_NO"], title_en=v["TITLE_EN"],
               pkg=v["PACKAGE"], prefix=class_prefix(module_id))
    java_dir = Path("src/main/java/com/springbootedu") / v["PACKAGE"]
    test_dir = Path("src/test/java/com/springbootedu") / v["PACKAGE"]

    for kind in ("lesson", "exercise", "solution"):
        base = module_dir / kind
        compose_dep = COMPOSE_DEPENDENCY if infra and kind == "lesson" else ""
        write(base / "pom.xml", POM.format(kind=kind, compose_dependency=compose_dep, **fmt))
        write(base / java_dir / f"{fmt['prefix']}Application.java", APPLICATION.format(**fmt))
        write(base / java_dir / "package-info.java", PACKAGE_INFO.format(**fmt))
        yaml = APPLICATION_YAML.format(**fmt)
        if infra and kind == "lesson":
            yaml += COMPOSE_YAML.format(profiles=",".join(infra))
        write(base / "src/main/resources/application.yaml", yaml)
        write(base / test_dir / f"{fmt['prefix']}ApplicationTest.java", APPLICATION_TEST.format(**fmt))
        if kind == "exercise":
            write(base / java_dir / "Exercise1.java", EXERCISE_STARTER.format(**fmt))
        if kind == "solution":
            write(base / java_dir / "Exercise1.java", EXERCISE_SOLUTION.format(**fmt))
        if kind in ("exercise", "solution"):
            write(base / test_dir / "Exercise1Test.java", EXERCISE_TEST.format(**fmt))

    # the lesson template's example snippet points at the generated application class
    app_rel = Path("lesson") / java_dir / f"{fmt['prefix']}Application.java"
    v["APP_SNIPPET"] = f"{app_rel.as_posix()}#application"
    v["APP_SNIPPET_CODE"] = "\n".join(extract_snippet(module_dir / app_rel, SNIPPET.match(f"<!-- snippet: {v['APP_SNIPPET']} -->")))

    docs = {"tr/ders.md": "tr/ders.md", "tr/odevler.md": "tr/odevler.md",
            "en/lesson.md": "en/lesson.md", "en/exercises.md": "en/exercises.md"}
    for target, source in docs.items():
        write(module_dir / "docs" / target, fill((TEMPLATES / source).read_text(), v))
    write(module_dir / "README.md", fill((TEMPLATES / "README.module.md").read_text(), v))
    write(module_dir / "requests.http", REQUESTS_HTTP.format(**fmt))
    register_in_root_pom(module_id)

    print(f"✓ Created modules/{module_id} (lesson, exercise, solution, docs) and registered it in pom.xml", flush=True)
    if args.no_pdf:
        print(f"  Next: ./scripts/build-pdfs.sh {module_id}")
        return 0
    return subprocess.call([str(ROOT / "scripts" / "build-pdfs.sh"), module_id])


# ---------------------------------------------------------------------------------------------
# check
# ---------------------------------------------------------------------------------------------
class Report:
    def __init__(self, module_id: str):
        self.module_id = module_id
        self.errors: list[str] = []

    def check(self, ok: bool, message: str) -> bool:
        if not ok:
            self.errors.append(message)
        return ok


def headings(text: str) -> list[str]:
    in_code = False
    result = []
    for line in text.split("\n"):
        if line.startswith("```"):
            in_code = not in_code
        elif not in_code and re.match(r"^#{1,6} ", line):
            result.append(line)
    return result


def code_blocks(text: str) -> int:
    return sum(1 for line in text.split("\n") if line.startswith("```")) // 2


def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def extract_snippet(source: Path, m: re.Match) -> list[str] | str:
    """Returns the snippet lines, or an error message."""
    lines = source.read_text().split("\n")
    if m.group(4):
        tag = m.group(4)
        begin = next((i for i, l in enumerate(lines) if f"tag::{tag}[]" in l), None)
        finish = next((i for i, l in enumerate(lines) if f"end::{tag}[]" in l), None)
        if begin is None or finish is None or finish < begin:
            return f"tag '{tag}' not found in {m.group(1)} (need // tag::{tag}[] ... // end::{tag}[])"
        body = [l for l in lines[begin + 1:finish] if not TAG_LINE.search(l)]
    else:
        start, end = int(m.group(2)), int(m.group(3))
        if end > len(lines):
            return f"{m.group(1)} has only {len(lines)} lines (snippet asks for {start}-{end})"
        body = [l for l in lines[start - 1:end] if not TAG_LINE.search(l)]
    body = [l.rstrip() for l in body]
    indent = min((len(l) - len(l.lstrip()) for l in body if l.strip()), default=0)
    return [l[indent:] for l in body]


def iter_snippets(doc: Path):
    """Yields (line index of marker, match, index of code block start, index of code block end)."""
    lines = doc.read_text().split("\n")
    for i, line in enumerate(lines):
        m = SNIPPET.match(line)
        if not m:
            continue
        if i + 1 >= len(lines) or not lines[i + 1].startswith("```"):
            yield i, m, None, None
            continue
        close = next((j for j in range(i + 2, len(lines)) if lines[j].startswith("```")), None)
        yield i, m, i + 1, close


def check_snippets(r: Report, module_dir: Path, doc: Path) -> None:
    lines = doc.read_text().split("\n")
    for i, m, open_idx, close_idx in iter_snippets(doc):
        where = f"{doc.relative_to(module_dir)}:{i + 1}"
        source = module_dir / m.group(1)
        if not r.check(source.is_file(), f"{where}: snippet source {m.group(1)} does not exist"):
            continue
        if not r.check(open_idx is not None and close_idx is not None,
                       f"{where}: snippet marker must be directly followed by a code block"):
            continue
        expected = extract_snippet(source, m)
        if not r.check(not isinstance(expected, str), f"{where}: {expected}"):
            continue
        block = [l.rstrip() for l in lines[open_idx + 1:close_idx]]
        r.check(block == expected, f"{where}: code block differs from its source {m.group(0)[14:-4]} "
                                   f"— run ./scripts/sync-snippets.sh {module_dir.name}")


def cmd_sync(args: argparse.Namespace) -> int:
    status = 0
    for module_id in args.module_ids:
        module_dir = MODULES / module_id
        for doc in sorted((module_dir / "docs").rglob("*.md")):
            lines = doc.read_text().split("\n")
            changed = 0
            # walk backwards so earlier indexes stay valid while replacing blocks
            for i, m, open_idx, close_idx in reversed(list(iter_snippets(doc))):
                source = module_dir / m.group(1)
                snippet = extract_snippet(source, m) if source.is_file() else f"missing {m.group(1)}"
                if isinstance(snippet, str) or open_idx is None or close_idx is None:
                    print(f"✗ {doc.relative_to(ROOT)}:{i + 1}: {snippet if isinstance(snippet, str) else 'no code block'}")
                    status = 1
                    continue
                if lines[open_idx + 1:close_idx] != snippet:
                    lines[open_idx + 1:close_idx] = snippet
                    changed += 1
            if changed:
                doc.write_text("\n".join(lines))
                print(f"✓ {doc.relative_to(ROOT)}: {changed} snippet(s) updated")
    return status


def check_module(module_id: str, strict: bool) -> Report:
    r = Report(module_id)
    module_dir = MODULES / module_id
    if not r.check(module_dir.is_dir(), f"modules/{module_id} does not exist"):
        return r

    # 1. required files
    required = ["README.md", "lesson/pom.xml", "exercise/pom.xml", "solution/pom.xml"]
    lesson_pom = module_dir / "lesson/pom.xml"
    if lesson_pom.is_file() and re.search(r"spring-boot-starter-web(mvc|flux)?<|spring-boot-starter-graphql<", lesson_pom.read_text()):
        required.append("requests.http")      # HTTP examples are only expected from modules that serve HTTP
    required += [f"docs/{d}" for pair in DOC_PAIRS for d in pair] + [f"docs/{p}" for p in PDFS]
    for rel in required:
        r.check((module_dir / rel).is_file(), f"missing {rel}")

    # 2. registered in the root pom
    pom = ROOT_POM.read_text()
    for kind in ("lesson", "solution", "exercise"):
        r.check(f"<module>modules/{module_id}/{kind}</module>" in pom, f"{kind}/ is not registered in the root pom.xml")

    # 3. exercise tests == solution tests
    ex_tests, sol_tests = module_dir / "exercise/src/test", module_dir / "solution/src/test"
    if ex_tests.is_dir() and sol_tests.is_dir():
        r.check(same_tree(ex_tests, sol_tests), "exercise/src/test and solution/src/test differ — they must be identical")
    else:
        r.check(False, "exercise/src/test and solution/src/test must both exist")

    # 4. the exercise has TODOs, the solution has none
    ex_main, sol_main = module_dir / "exercise/src/main", module_dir / "solution/src/main"
    r.check(any("TODO" in f.read_text() for f in ex_main.rglob("*.java")) if ex_main.is_dir() else False,
            "exercise/src/main has no TODO — students need to know what to implement")
    if sol_main.is_dir():
        leftovers = [f.relative_to(module_dir).as_posix() for f in sol_main.rglob("*.java") if "TODO" in f.read_text()]
        r.check(not leftovers, f"solution/ still contains TODOs: {', '.join(leftovers)}")

    # 5. TR/EN parity, 6. snippets, 7. fresh PDFs
    manifest = parse_manifest(module_dir / "docs/.pdf-manifest")
    for tr_rel, en_rel in DOC_PAIRS:
        tr, en = module_dir / "docs" / tr_rel, module_dir / "docs" / en_rel
        if not (tr.is_file() and en.is_file()):
            continue
        tr_text, en_text = tr.read_text(), en.read_text()
        r.check(len(headings(tr_text)) == len(headings(en_text)),
                f"heading count differs: {tr_rel}={len(headings(tr_text))}, {en_rel}={len(headings(en_text))}")
        r.check(code_blocks(tr_text) == code_blocks(en_text),
                f"code block count differs: {tr_rel}={code_blocks(tr_text)}, {en_rel}={code_blocks(en_text)}")
        for doc, rel in ((tr, tr_rel), (en, en_rel)):
            check_snippets(r, module_dir, doc)
            r.check(manifest.get(rel) == sha256(doc), f"docs/{rel.replace('.md', '.pdf')} is stale or was never built "
                                                      f"— run ./scripts/build-pdfs.sh {module_id}")
            r.check("{{" not in doc.read_text(), f"docs/{rel} still has {{{{placeholders}}}}")
            if strict:
                strict_doc_checks(r, rel, doc.read_text())

    if strict:
        readme = (module_dir / "README.md").read_text() if (module_dir / "README.md").is_file() else ""
        r.check(not re.search(r"^- \.\.\.$", readme, flags=re.M), "README.md still has '- ...' placeholder bullets")
    return r


def strict_doc_checks(r: Report, rel: str, text: str) -> None:
    r.check("AUTHOR NOTES" not in text and "YAZAR NOTLARI" not in text, f"docs/{rel}: remove the author notes block")
    r.check(not re.search(r"^(- |## \d+\.\d+ |# .*— )\.\.\.", text, flags=re.M) and "(`../assets/diyagram.svg`)" not in text
            and "(`../assets/diagram.svg`)" not in text, f"docs/{rel}: template placeholders ('...') are left")
    if rel in ("tr/ders.md", "en/lesson.md"):
        examples = len(re.findall(r"^## 3\.\d+ ", text, flags=re.M))
        r.check(examples >= 5, f"docs/{rel}: {examples} examples in section 3 — SPEC requires at least 5")
    else:
        exercises = len(re.findall(r"^# (Ödev|Exercise) \d+", text, flags=re.M))
        r.check(exercises >= 3, f"docs/{rel}: {exercises} exercises — SPEC requires at least 3")


def same_tree(a: Path, b: Path) -> bool:
    cmp = filecmp.dircmp(a, b)
    if cmp.left_only or cmp.right_only or cmp.funny_files:
        return False
    _, mismatch, errors = filecmp.cmpfiles(a, b, cmp.common_files, shallow=False)
    if mismatch or errors:
        return False
    return all(same_tree(a / d, b / d) for d in cmp.common_dirs)


def parse_manifest(path: Path) -> dict[str, str]:
    if not path.is_file():
        return {}
    entries = {}
    for line in path.read_text().splitlines():
        digest, _, rel = line.partition("  ")
        entries[rel] = digest
    return entries


def cmd_check(args: argparse.Namespace) -> int:
    ids = sorted(p.name for p in MODULES.iterdir() if p.is_dir()) if args.all and MODULES.is_dir() else args.module_ids
    if not ids:
        print("No modules to check.")
        return 0
    failed = 0
    for module_id in ids:
        report = check_module(module_id, args.strict)
        if report.errors:
            failed += 1
            print(f"✗ {module_id}")
            for e in report.errors:
                print(f"    - {e}")
        else:
            print(f"✓ {module_id}{' (strict)' if args.strict else ''}")
    return 1 if failed else 0


def fail(message: str) -> int:
    print(f"✗ {message}", file=sys.stderr)
    return 2


def main() -> int:
    parser = argparse.ArgumentParser(prog="coursetool")
    sub = parser.add_subparsers(dest="command", required=True)

    new = sub.add_parser("new", help="scaffold a course module")
    new.add_argument("module_id")
    new.add_argument("--title-tr")
    new.add_argument("--title-en")
    new.add_argument("--infra", help=f"comma-separated compose profiles: {', '.join(sorted(INFRA_PROFILES))}")
    new.add_argument("--force", action="store_true", help="allow an id that is not in SPEC.md (throwaway experiments)")
    new.add_argument("--no-pdf", action="store_true", help="do not build the PDFs after scaffolding")
    new.set_defaults(func=cmd_new)

    check = sub.add_parser("check", help="check a module against the Definition of Done")
    check.add_argument("module_ids", nargs="*")
    check.add_argument("--all", action="store_true")
    check.add_argument("--strict", action="store_true", help="also require finished content (DoD)")
    check.set_defaults(func=cmd_check)

    sync = sub.add_parser("sync-snippets", help="copy current source into the docs' snippet code blocks")
    sync.add_argument("module_ids", nargs="+")
    sync.set_defaults(func=cmd_sync)

    args = parser.parse_args()
    return args.func(args)


if __name__ == "__main__":
    sys.exit(main())
