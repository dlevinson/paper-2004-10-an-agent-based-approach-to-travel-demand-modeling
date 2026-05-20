# Source Review

The paper describes an agent-based travel-demand model with three agent types: traveler, node, and arc. The model distributes trips through local movement, opportunity acceptance, and information exchange between travelers and node agents. The paper demonstrates the method on a 10-by-10 grid and on the Chicago sketch network, with the Chicago calibration using CATS 1990 Household Travel Survey trip-length data.

The strongest package match is the Java source under `code/original/da_model_2002_array/`. It implements node opportunity counts, traveler/workers moving through adjacent demand nodes, a beta parameter for opportunity acceptance, arc lengths, and path/knowledge exchange. The sample `data/sample_inputs/grid2.txt` uses the same input structure expected by that code.

The source under `code/related_model_variants/evolve_vector_variant/` is a related variant of the same model family. It uses Java `Vector` path storage and keeps the same node, worker, arc, and beta-search framing. It is retained as context, but the array-based 2002 source plus `grid2.txt` is the cleaner archival core.

The supplied `/Users/dlev2617/Documents/Software/ARC - Zhang` folder was reviewed. Its zip archives contain later or related ARC/network model families, including 2006 route-choice, network-policy, network-regulation, network-reliability, and value-of-time code. Those archives are not copied into this package because they do not appear to be the direct 2004 TRR travel-demand model source, and several contain compiled classes, bundled third-party JAMA files, and later project metadata.

No separate public data upload is needed for the Chicago calibration sources in this package. The paper identifies the Chicago sketch network and CATS 1990 HTS calibration data as source/calibration evidence; those are third-party/source datasets rather than author-created material to deposit here.

Execution was not tested in this pass because no Java runtime was installed on this machine.
