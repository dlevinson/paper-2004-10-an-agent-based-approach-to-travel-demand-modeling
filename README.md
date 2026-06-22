# An Agent-Based Approach To Travel Demand Modeling

## Contribution

This paper develops a travel-demand model in which trips and traffic patterns emerge from interactions among traveler, node, and link agents. Explicitly representing goals, knowledge, search, and learning enables disaggregate trip distribution and assignment without path enumeration, with demonstrations on a grid and the Chicago sketch network.

## Bibliographic Information

- Row ID: `paper-2004-10`
- Citation: Zhang, L., and Levinson, D. (2004). An Agent-Based Approach To Travel Demand Modeling. Transportation Research Record, 1898, 28-36. https://doi.org/10.3141/1898-04
- Local paper reference: `paper/Agent.pdf`

## Package Boundary

This package contains the author-side Java model source that matches the paper's node, arc, traveler, opportunity-search, and learning formulation, plus a small sample input file. The package does not include the third-party Chicago sketch network, CATS 1990 Household Travel Survey calibration data, or publisher-licensed article files for public redistribution.

## Included Contents

- `code/original/da_model_2002_array/`: original Java source variant dated November 2002, using array-based path and knowledge storage. This is the strongest match to the model described in the paper.
- `data/sample_inputs/grid2.txt`: small sample input file for the 2002 Java source.
- `code/related_model_variants/evolve_vector_variant/`: related Java source variant using `Vector` path storage, retained as model-code context because it implements the same conceptual mechanism but lacks a separate input file.
- `documentation/SOURCE_REVIEW.md`: paper-to-package review and boundary notes.
- `metadata/SOURCE_FILE_REVIEW.csv`: file-level package review.
- `metadata/ARC_ZHANG_RELATED_SOURCE_REVIEW.csv`: review of the separate `ARC - Zhang` zip archives supplied as likely related sources.

## Assessment

The package is ready for public upload as an archival code-and-sample-input package. The empirical Chicago calibration data mentioned in the paper are third-party/source data and are not part of this public package. The `ARC - Zhang` zip archives were reviewed, but they appear to be later or related ARC/network model families rather than the direct 2004 TRR travel-demand model source.

## Execution Status

The Java source has not been execution-tested in this pass because no Java runtime was installed on this machine. The package preserves the original source and records the input boundary so a future Java setup can test or modernize it without having to rediscover the source files.

<!-- package-hardening-status:start -->
## Package Hardening Status

Generated: 2026-05-21 20:04:48 AEST

- Pipeline: `UPLOADED`
- Sidecars added/updated: `PACKAGE_STATUS.md`, `PACKAGE_MANIFEST.csv`, `LICENSE_STATUS.md`.
- Paper reference copies are for local audit convenience and are not public-upload assets without rights review.
- Final GitHub upload should use the manifest include statuses and the license-status note.
<!-- package-hardening-status:end -->
