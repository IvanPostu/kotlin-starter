import json
from pathlib import Path

import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt
from matplotlib.ticker import FuncFormatter


def read_jsonl(filepath):
    data = []
    with open(filepath, 'r') as file:
        for line in file:
            try:
                point = json.loads(line)
                if 'numberOfThreads' in point and 'readCount' in point:
                    data.append((int(point['numberOfThreads']), int(point['readCount'])))
            except json.JSONDecodeError:
                print(f"Skipping invalid line: {line.strip()}")
    return data

def plot_data(datasets, labels=None, x_label='X', y_label='Y'):
    if not datasets:
        print("No valid data to plot.")
        return

    def millions_formatter(x, pos):
        return f'{x * 1e-6:.1f}M'

    plt.figure(figsize=(10, 6))

    for i, data in enumerate(datasets):
        if not data:
            continue
        x_vals, y_vals = zip(*data)
        label = labels[i] if labels and i < len(labels) else f"Line {i+1}"
        plt.plot(x_vals, y_vals, marker='o', linestyle='-', label=label)

    plt.title('Multiple 2D Lines from Data')
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.gca().yaxis.set_major_formatter(FuncFormatter(millions_formatter))
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.show()

if __name__ == "__main__":
    current_dir = Path(__file__).parent
    path = current_dir / "temp.jsonl"
    data_points = read_jsonl(path)
    line1 = [(1, 1_000_000), (2, 2_000_000), (3, 3_000_000), (4, 4_000_000)]
    line2 = [(1, 1_500_000), (2, 2_700_000), (3, 3_500_000), (4, 4_200_000)]
    line3 = [(1, 800_000),  (2, 1_900_000), (3, 2_500_000), (4, 3_100_000)]
    line4 = [(1, 2_200_000), (2, 3_100_000), (3, 3_900_000), (4, 5_000_000)]

    datasets = [line1, line2, line3, line4]
    labels = ["Dataset A", "Dataset B", "Dataset C", "Dataset D"]
    plot_data(datasets, labels, "x test", "y test")
