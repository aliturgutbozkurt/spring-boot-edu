# PDF toolchain for the course docs: pandoc/extra (Pandoc, XeLaTeX, Eisvogel, rsvg-convert, DejaVu fonts)
# plus Turkish language support, which the base image does not ship.
# Built automatically by scripts/build-pdfs.sh — no need to build it by hand.
FROM pandoc/extra:3.11.0.0-debian

RUN tlmgr install babel-turkish hyphen-turkish
