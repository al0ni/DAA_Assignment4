\# Experiment Design Card



\## Research questions



1\. How does input length affect the work done by recursive, full-table, and two-row Edit Distance?

2\. How much DP storage does the two-row version save?

3\. How do text length, pattern length, and input type affect the number of comparisons in Naive Search and KMP?



\## Inputs



For Edit Distance, n is the length of each of the two strings.



\- Compare all three implementations at n = 2, 4, 6, 8, 10.

\- Compare full-table and two-row DP at n = 128, 256, 512, 1024, 2048.

\- Use three input types: identical strings, disjoint strings made of only `a` and only `b`, and random lowercase strings.

\- Use a separate timed run to find the largest tested size that plain recursion can finish on disjoint strings. Try n = 2 through 16 and stop at the first two-second process timeout. Record the last completed size and the first timeout, if any.



For string search, n is the text length and m is the pattern length.



\- Vary text length: n = 10000, 40000, 160000, 640000 with m = 32.

\- Vary pattern length: m = 8, 32, 128, 512 with n = 160000.

\- Use random text and patterns, a text of repeated `a` characters with a pattern ending in `b`, and a text and pattern made only of `a` characters.

\- Use all three input types for the text-length sweep. Use the pattern ending in `b` for the pattern-length sweep.



\## Measurement procedure



\- Use the fixed random seed 2509. Generate each input before timing and give the same input to each implementation in the comparison.

\- Run each measured case three times for warmup and five times for measurement. Record the five measured runs in the raw CSV and use their median time in the plots.

\- Measure elapsed time with `System.nanoTime()`. Include the algorithm and its counters in the timed section. Exclude input generation and CSV writing.

\- For recursive Edit Distance, record calls, including base cases. For both DP versions, record computed interior cells and allocated integer array slots.

\- For string search, count character comparisons, including mismatches. Record KMP's LPS and search comparisons separately and use their sum for the main comparison with Naive Search.

\- Record the returned distance or match count so the results can be checked across implementations. The separate recursion timeout probe is reported separately from the five-run measurements.



\## Predictions



\- Recursion should make few calls on identical strings and many more on disjoint strings. On disjoint inputs, it should become slow at small sizes.

\- The two DP versions should compute the same number of interior cells. The full table should allocate `(n + 1) \* (n + 1)` integer slots, while two rows should allocate `2 \* (n + 1)`.

\- Naive Search should do much more work on the pattern ending in `b`, because it checks a long matching prefix at each possible start. KMP should reuse information from LPS and avoid most repeated comparisons.

\- On random inputs, the difference in comparisons may be small because many Naive Search attempts stop after the first few symbols. When there are many overlaps, both algorithms must also store the returned matches.



\## Planned plots



1\. Edit Distance work versus string length, with a log scale for recursive calls and DP cells.

2\. Median runtime and allocated integer slots for both DP versions on larger inputs.

3\. Total character comparisons versus text length for Naive Search and KMP.

4\. Total character comparisons versus pattern length on the pattern ending in `b`.

