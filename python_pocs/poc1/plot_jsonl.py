import json
from pathlib import Path
from collections import defaultdict

import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt
from matplotlib.ticker import FuncFormatter


def read_jsonl(filepath, fields):
    data = []
    with open(filepath, 'r') as file:
        for line in file:
            try:
                point = json.loads(line)
                if all(field in point for field in fields):
                    obj = {field: point[field] for field in fields}
                    data.append(obj)
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

def groupBy(list, fieldName):
    grouped = defaultdict()
    for item in list:
        key = item[fieldName]
        if key not in grouped:
            grouped[key] = []
        grouped[key].append(item)
    return grouped

if __name__ == "__main__":
    current_dir = Path(__file__).parent
    path = current_dir / "temp.jsonl"
    data_points = read_jsonl(path, ["readCount", "timeFrameInMs", "numberOfThreads", "name"])

    grouped_by_name = groupBy(data_points, "name")
    
    line1 = [(d['numberOfThreads'], d['readCount']) for d in grouped_by_name["synchronized HashMap"]]
    line2 = [(d['numberOfThreads'], d['readCount']) for d in grouped_by_name["synchronized TreeMap"]]
    line3 = [(d['numberOfThreads'], d['readCount']) for d in grouped_by_name["ConcurrentHashMap"]]
    line4 = [(d['numberOfThreads'], d['readCount']) for d in grouped_by_name["ConcurrentSkipListMap"]]

    print(grouped_by_name)

    datasets = [line1, line2, line3, line4]
    labels = list(grouped_by_name.keys())
    plot_data(datasets, labels, "readCount (throughput)", "numberOfThreads")
