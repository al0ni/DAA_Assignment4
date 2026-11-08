import csv
from collections import defaultdict
from pathlib import Path
from statistics import median

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parent.parent
CSV_PATH = ROOT / "results" / "results.csv"
PLOT_DIR = ROOT / "results" / "plots"

with CSV_PATH.open(encoding="utf-8", newline="") as file:
    rows = list(csv.DictReader(file))

if len(rows) != 535:
    raise ValueError(f"Expected 535 rows, got {len(rows)}")

groups = defaultdict(list)
for row in rows:
    key = (
        row["experiment"],
        row["algorithm"],
        row["input"],
        int(row["n"]),
        int(row["m"]),
    )
    groups[key].append(row)

metrics = ("calls", "cells", "allocated_cells", "comparisons")
summary = {}

for key, runs in groups.items():
    if sorted(int(row["run"]) for row in runs) != [1, 2, 3, 4, 5]:
        raise ValueError(f"Expected runs 1 through 5 for {key}")

    values = {"time_ms": median(float(row["time_ms"]) for row in runs)}
    for metric in metrics:
        counts = {int(row[metric]) for row in runs}
        if len(counts) != 1:
            raise ValueError(f"{metric} changed between runs for {key}")
        values[metric] = counts.pop()

    summary[key] = values

PLOT_DIR.mkdir(parents=True, exist_ok=True)


def plot_line(ax, experiment, algorithm, input_type, metric, axis="n", label=None):
    index = 3 if axis == "n" else 4
    points = sorted(
        (key[index], values[metric])
        for key, values in summary.items()
        if key[:3] == (experiment, algorithm, input_type)
    )
    if not points:
        raise ValueError(f"No data for {experiment}, {algorithm}, {input_type}")

    x, y = zip(*points)
    ax.plot(x, y, marker="o", label=label or algorithm)


def style(ax, title, x_label, y_label):
    ax.set_title(title)
    ax.set_xlabel(x_label)
    ax.set_ylabel(y_label)
    ax.grid(True, alpha=0.25)
    ax.legend()


def save(fig, name):
    fig.tight_layout()
    fig.savefig(PLOT_DIR / name, dpi=180)
    plt.close(fig)


fig, ax = plt.subplots(figsize=(8, 4.5))
for input_type in ("identical", "disjoint", "random"):
    plot_line(
        ax, "edit_small", "Recursive", input_type, "calls",
        label=f"Recursive calls ({input_type})",
    )
plot_line(
    ax, "edit_small", "Table", "random", "cells",
    label="Computed cells (both DP versions)",
)
ax.set_yscale("log")
style(ax, "Edit distance work", "Length of each string", "Operation count (log scale)")
save(fig, "01_edit_work.png")

fig, axes = plt.subplots(1, 2, figsize=(11, 4.5))
for algorithm in ("Table", "TwoRows"):
    plot_line(axes[0], "edit_large", algorithm, "random", "time_ms")
    plot_line(axes[1], "edit_large", algorithm, "random", "allocated_cells")
style(axes[0], "Median runtime on random inputs", "Length of each string", "Time (ms)")
axes[1].set_yscale("log")
style(axes[1], "DP storage", "Length of each string", "Allocated integer slots (log scale)")
save(fig, "02_dp_time_and_storage.png")

fig, axes = plt.subplots(1, 3, figsize=(13, 4.5))
for ax, input_type in zip(axes, ("random", "near_miss", "overlap")):
    for algorithm in ("Naive", "Kmp"):
        plot_line(ax, "search_text", algorithm, input_type, "comparisons")
        ax.set_xticks([0, 200_000, 400_000, 600_000], ["0", "200k", "400k", "600k"])
        style(ax, input_type.replace("_", " ").title(),
              "Text length (characters)", "Character comparisons")
fig.suptitle("Search comparisons by text length (pattern length = 32)")
save(fig, "03_search_text_length.png")

fig, ax = plt.subplots(figsize=(8, 4.5))
for algorithm in ("Naive", "Kmp"):
    plot_line(ax, "search_pattern", algorithm, "near_miss", "comparisons", axis="m")
style(ax, "Near-miss search (text length = 160,000)", "Pattern length", "Character comparisons")
save(fig, "04_search_pattern_length.png")

print(f"Saved four plots to {PLOT_DIR}")