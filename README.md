<h1 align="center">DAA Assignment 4</h1>



<p align="center">

&#x20; Dynamic Programming and String Search<br>

&#x20; <strong>Alua Rakhimzhanova · SE-2509</strong>

</p>



<p align="center">

&#x20; <a href="REPORT.pdf">Report</a> ·

&#x20; <a href="results/results.csv">Results</a> ·

</p>



<h2>Overview</h2>



This project compares three versions of Edit Distance: recursion, full-table DP, and two-row DP. It also compares Naive Search and KMP for finding all matches in a text, including overlapping ones.



The experiments measure runtime and count recursive calls, computed DP cells, allocated integer slots, and character comparisons.



<h2>Run the Project</h2>



\*\*Requirements:\*\* JDK 17 or later, Maven, and Python 3 with Matplotlib for generating plots. Run the commands from the project root.



\*\*Run the tests\*\*



```powershell

mvn test

```



\*\*Run the benchmark\*\*



After the tests pass, run:



```powershell

java -cp target/classes kz.aitu.daa.ExperimentRunner

```



This writes 535 measurements to `results/results.csv`. Each case has three warmup runs and five measured runs. The plots use the median of the five measured times. Random inputs use seed `2509`.



\*\*Generate the plots\*\*



```powershell

python -m pip install matplotlib

python scripts/plot\_results.py

```



The script creates four PNG files in `results/plots/`.



<h2>Recursion Limit</h2>



On my computer, the recursive version finished at length \*\*11\*\*. At length \*\*12\*\*, the Java process exceeded the two-second limit. The measurements are in \[recursive\_limit.csv](results/recursive\_limit.csv).



To check a single length, run:



```powershell

java -cp target/classes kz.aitu.daa.ExperimentRunner probe 11

```



The two-second limit was applied separately during the experiment.

