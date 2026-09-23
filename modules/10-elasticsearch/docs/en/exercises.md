---
title: "Module 10 — Search with Elasticsearch"
subtitle: "Exercises"
module: "10-elasticsearch"
lang: en-US
date: "2026-09-23"
---

# How to Work

1. The starter code is in `modules/10-elasticsearch/exercise/`. Find the `TODO` comments.
2. The tests run against a real Elasticsearch (Testcontainers). **Docker must be running** with at least 2 GB of memory.
3. Run the tests:

```bash
./mvnw -Pexercises -pl modules/10-elasticsearch/exercise -am test
```

4. The exercise is done when all tests are green.
5. If you get stuck, read the hints first, then the solution in `modules/10-elasticsearch/solution/`.

> [!TIP]
> Every test class deletes and recreates its own index in `@BeforeEach`, so the exercises are independent of each other.

# Exercise 1 — Autocomplete (Easy)

**Goal:** suggest book titles while the user is still typing: "spr" should already find "Spring in Action".

**Tasks** (package `exercise1`):

- `TODO 1a` — `TitleDocument`: map `title` as a *search-as-you-type* field. Elasticsearch then creates the sub-fields `title._2gram` and `title._3gram` for word pairs and triples.
- `TODO 1b` — `TitleSuggestions.suggest`: a `multi_match` query of type `bool_prefix` on `title`, `title._2gram` and `title._3gram`. The last word may be incomplete.
- `TODO 1c` — at most `limit` results. Return only the titles.

**Hints:**

- `@Field(type = FieldType.Search_As_You_Type)`.
- `q.multiMatch(m -> m.query(typed).type(TextQueryType.BoolPrefix).fields(...))`.
- Lesson section 3.3 shows how to run a `NativeQuery` and read its hits. `withMaxResults(limit)` limits the hits.

**Acceptance criteria:** all 5 tests in `Exercise1Test` pass.

**Estimated time:** 25 minutes

# Exercise 2 — Facets That Stay Useful (Medium)

**Goal:** a shop search page. After the user chooses the category "books", the hits show only books, but the category list still shows how many "kitchen" and "stationery" products match. Otherwise the user could never switch to another category.

**Tasks** (`exercise2.FacetedSearch.search`):

- `TODO 2a` — products whose `name` matches the text. With `maxPrice`, only products up to that price.
- `TODO 2b` — a `terms` aggregation named `categories` on the `category` field.
- `TODO 2c` — the chosen category must filter the **hits**, but **not** the category counts.
- `TODO 2d` — return the hits and the count per category.

**Hints:**

- Elasticsearch computes aggregations on the result of the **query**. A **post filter** is applied afterwards and only removes hits.
- `NativeQueryBuilder.withFilter(Query)` sends a `post_filter`. The price condition belongs in the query (`bool` + `filter`), because it should change the counts too.
- Lesson section 3.4 shows how to read the buckets of a `terms` aggregation.

**Acceptance criteria:** all 4 tests in `Exercise2Test` pass.

**Estimated time:** 40 minutes

# Exercise 3 — Reindexing Without Downtime (Hard)

**Goal:** the mapping of a field cannot be changed in an existing index. To change it, build a new index and copy all data into it, without a moment in which the search finds nothing.

The trick is an **alias**: the application always searches `catalog`, a second name that points to the real index `catalog-<suffix>`. A reindex builds a new index next to the old one, then moves the alias in one atomic step.

**Tasks** (`exercise3.Reindexer.reindex`):

- `TODO 3a` — create a new index `catalog-<unique suffix>` with the settings and mapping of `CatalogBook`.
- `TODO 3b` — write every book of the `BookSource` into it and refresh it.
- `TODO 3c` — find the indices the alias `catalog` points to now. On the first run there are none.
- `TODO 3d` — in **one** alias request, add the alias to the new index and remove it from the old ones.
- `TODO 3e` — delete the old indices and return the name of the new one.

**Hints:**

- `IndexOperations ops = operations.indexOps(IndexCoordinates.of(name))`, then `ops.create(ops.createSettings(CatalogBook.class), ops.createMapping(CatalogBook.class))`.
- `operations.save(books, IndexCoordinates.of(name))` writes to exactly that index.
- `operations.indexOps(IndexCoordinates.of(ALIAS)).exists()` is also true for an alias. `getAliases(ALIAS)` returns a map from index name to alias data.
- `new AliasActions(new AliasAction.Add(...), new AliasAction.Remove(...))` with `AliasActionParameters.builder().withIndices(...).withAliases(ALIAS).build()`, sent with `ops.alias(actions)`.
- Index names must be lowercase, and two runs in the same millisecond must not clash.

**Acceptance criteria:** all 3 tests in `Exercise3Test` pass.

**Estimated time:** 60 minutes

# Extra Challenge (Optional)

Add an endpoint `POST /api/admin/reindex` to the lesson that runs your `Reindexer` against the books in PostgreSQL. What happens to books that are saved *while* a reindex is running? Describe two ways to make sure they are not lost.
