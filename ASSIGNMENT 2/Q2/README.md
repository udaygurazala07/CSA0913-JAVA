# Task Q2: When Is ArrayList an Appropriate Engineering Choice?

## Overview
This task investigates the operational boundaries and performance tradeoffs of `ArrayList`, `LinkedList`, and `ArrayDeque` in a Music Playlist Editor context.

## Files in this Directory
- `Song.java`: Domain model representing a music track.
- `PlaylistBenchmark.java`: Experimental benchmark suite evaluating 7 operations across 3 sizes (1k, 10k, 100k) and the 100k "Up Next" queue workload.
- `input_playlists.txt`: Description of the three playlist scales tested.
- `q2_results.csv`: Measured execution timings and winner analysis in CSV format.
- `q2_output.txt`: Raw console output from running the benchmark.

## How to Compile & Run
```bash
# From workspace root
javac -d bin Java-Assignment-2/Q2/Song.java Java-Assignment-2/Q2/PlaylistBenchmark.java
java -cp bin q2.PlaylistBenchmark
```

## Key Findings
- **Random Access `get(k)`:** At 100,000 songs, `ArrayList` took 2.79 ms for 10k lookups vs 1,893.12 ms for `LinkedList` (678x faster).
- **Front Modification:** `LinkedList` took 0.22 ms vs 22.55 ms for `ArrayList` due to array copy shifting.
- **Middle Modification:** `ArrayList` outperformed `LinkedList` (9.07 ms vs 131.48 ms) because pointer traversal in `LinkedList` is slower than native memory shifting.
- **"Up Next" Queue:** `ArrayDeque` was fastest (27.51 ms), outperforming `LinkedList` (28.48 ms) and `ArrayList` (218.18 ms).
