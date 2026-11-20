import os
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Set high-DPI style
plt.style.use('seaborn-v0_8-whitegrid' if 'seaborn-v0_8-whitegrid' in plt.style.available else 'default')
plt.rcParams['font.sans-serif'] = 'Arial'
plt.rcParams['font.size'] = 11

graphs_dir = r"C:\Users\Dhanush\OneDrive\Documents\Assignment 1 -java\Java-Assignment-3\Test-Results\Graphs"
csv_dir = r"C:\Users\Dhanush\OneDrive\Documents\Assignment 1 -java\Java-Assignment-3\Test-Results\CSV"
os.makedirs(graphs_dir, exist_ok=True)

# 1. Q1: Error Codes vs Exceptions Execution Time
fig, ax = plt.subplots(figsize=(7, 4.5), dpi=300)
categories = ['Error Codes (Defensive)', 'Exceptions (try-catch)']
times_ms = [168.14, 204.39]
colors = ['#2b5c8f', '#d95f02']
bars = ax.bar(categories, times_ms, color=colors, width=0.45, edgecolor='black', linewidth=1.2)
ax.set_ylabel('Execution Time for 100,000 Iterations (ms)', fontweight='bold')
ax.set_title('Q1: Runtime Cost of Exception Creation vs Defensive Error Codes', fontweight='bold', pad=12)
for bar in bars:
    yval = bar.get_height()
    ax.text(bar.get_x() + bar.get_width()/2.0, yval + 3, f'{yval:.2f} ms\n(1.22x)', ha='center', va='bottom', fontweight='bold')
ax.set_ylim(0, 250)
plt.tight_layout()
plt.savefig(os.path.join(graphs_dir, 'q1_error_codes_vs_exceptions.png'))
plt.close()
print("Q1 graph generated.")

# 2. Q3: Resource Safety - Peak Handles & Time
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(11, 4.5), dpi=300)
approaches = ['Manual (try-finally)', 'Try-With-Resources']
peak_handles = [4, 2]
exec_times = [112.42, 20.95]

b1 = ax1.bar(approaches, peak_handles, color=['#e7298a', '#1b9e77'], width=0.45, edgecolor='black')
ax1.set_ylabel('Peak Open File Handles (OS Limit Safety)', fontweight='bold')
ax1.set_title('Peak File Handles Held During Failure', fontweight='bold')
for b in b1:
    ax1.text(b.get_x() + b.get_width()/2.0, b.get_height() + 0.1, f'{int(b.get_height())} handles', ha='center', va='bottom', fontweight='bold')
ax1.set_ylim(0, 5)

b2 = ax2.bar(approaches, exec_times, color=['#e7298a', '#1b9e77'], width=0.45, edgecolor='black')
ax2.set_ylabel('Execution Time (ms)', fontweight='bold')
ax2.set_title('Batch Processing Time (ms)', fontweight='bold')
for b in b2:
    ax2.text(b.get_x() + b.get_width()/2.0, b.get_height() + 3, f'{b.get_height():.2f} ms', ha='center', va='bottom', fontweight='bold')
ax2.set_ylim(0, 140)
plt.suptitle('Q3: Resource Safety & Performance Comparison', fontweight='bold', y=1.02)
plt.tight_layout()
plt.savefig(os.path.join(graphs_dir, 'q3_resource_handles_comparison.png'))
plt.close()
print("Q3 graph generated.")

# 3. Q4: Finish Order Frequency in 10 Runs
df_q4 = pd.read_csv(os.path.join(csv_dir, 'q4_finish_orders.csv'))
first_place_counts = {1: 0, 2: 0, 3: 0, 4: 0}
for _, row in df_q4.iterrows():
    for st in [1, 2, 3, 4]:
        if row[f'Station{st}FinishPos'] == 1:
            first_place_counts[st] += 1

fig, ax = plt.subplots(figsize=(7, 4.5), dpi=300)
st_labels = [f'Station {k}' for k in first_place_counts.keys()]
st_vals = list(first_place_counts.values())
bars = ax.bar(st_labels, st_vals, color='#7570b3', width=0.5, edgecolor='black')
ax.set_ylabel('Frequency of Finishing 1st (Out of 10 Runs)', fontweight='bold')
ax.set_title('Q4: Nondeterministic Thread Scheduler - 1st Place Distribution', fontweight='bold', pad=12)
for b in bars:
    ax.text(b.get_x() + b.get_width()/2.0, b.get_height() + 0.15, f'{int(b.get_height())} wins ({int(b.get_height())*10}%)', ha='center', va='bottom', fontweight='bold')
ax.set_ylim(0, 6)
plt.tight_layout()
plt.savefig(os.path.join(graphs_dir, 'q4_thread_finish_order_distribution.png'))
plt.close()
print("Q4 graph generated.")

# 4. Q5: Race Condition Double Bookings Across 10 Runs
df_q5_race = pd.read_csv(os.path.join(csv_dir, 'q5_race_runs.csv'))
fig, ax = plt.subplots(figsize=(8, 4.5), dpi=300)
runs = df_q5_race['RunNumber']
sold = df_q5_race['SeatsSoldObserved']
ax.plot(runs, sold, marker='o', linewidth=2.5, color='#d95f02', label='Unsynchronized Seats Sold')
ax.axhline(y=10, color='#1b9e77', linestyle='--', linewidth=2, label='Expected Maximum Capacity (10 Seats)')
ax.fill_between(runs, 10, sold, color='#d95f02', alpha=0.15, label='Double-Booked / Overcapacity Seats')
ax.set_xlabel('Run Number (1 to 10)', fontweight='bold')
ax.set_ylabel('Total Seats Sold (Capacity: 10)', fontweight='bold')
ax.set_title('Q5: Race Condition Impact - Double Bookings Across 10 Runs', fontweight='bold', pad=12)
ax.set_xticks(range(1, 11))
ax.set_ylim(5, 35)
ax.legend(loc='upper right')
for x, y in zip(runs, sold):
    ax.annotate(f'{y}', (x, y), textcoords="offset points", xytext=(0, 7), ha='center', fontweight='bold')
plt.tight_layout()
plt.savefig(os.path.join(graphs_dir, 'q5_race_condition_double_bookings.png'))
plt.close()
print("Q5 race graph generated.")

# 5. Q5: Synchronization Strategies Benchmark (10k requests)
df_q5_bench = pd.read_csv(os.path.join(csv_dir, 'q5_benchmarks.csv'))
fig, ax = plt.subplots(figsize=(8, 4.5), dpi=300)
strats = ['Synchronized Method\n(Coarse Screen Lock)', 'Synchronized Block\n(Fine Per-Seat Lock)', 'Lock-Free Atomic CAS\n(AtomicBoolean)']
latencies = df_q5_bench['ExecutionTimeMs']
colors = ['#e6ab02', '#2b5c8f', '#386cb0']
bars = ax.bar(strats, latencies, color=colors, width=0.5, edgecolor='black')
ax.set_ylabel('Execution Time for 10,000 Bookings (ms)', fontweight='bold')
ax.set_title('Q5: High-Throughput Synchronization Benchmark', fontweight='bold', pad=12)
for b in bars:
    ax.text(b.get_x() + b.get_width()/2.0, b.get_height() + 0.2, f'{b.get_height():.2f} ms', ha='center', va='bottom', fontweight='bold')
ax.set_ylim(0, 16)
plt.tight_layout()
plt.savefig(os.path.join(graphs_dir, 'q5_synchronization_strategies_benchmark.png'))
plt.close()
print("Q5 bench graph generated.")

# 6. Q6: Token Counter Buffer Size Trace Over Time
df_q6 = pd.read_csv(os.path.join(csv_dir, 'q6_token_trace.csv'))
fig, ax = plt.subplots(figsize=(9, 4.5), dpi=300)
steps = df_q6['StepSequence']
buf_sizes = df_q6['BufferSizeAtEvent']
ax.step(steps, buf_sizes, where='post', color='#66a61e', linewidth=2.2, label='Active Buffer Size')
ax.axhline(y=5, color='red', linestyle='--', linewidth=1.5, label='Max Buffer Capacity (5)')
ax.axhline(y=0, color='black', linestyle='--', linewidth=1.5, label='Min Buffer Capacity (0)')
ax.fill_between(steps, 0, buf_sizes, step='post', color='#66a61e', alpha=0.2)
ax.set_xlabel('Event Sequence (Puts and Takes)', fontweight='bold')
ax.set_ylabel('Current Token Count in Buffer', fontweight='bold')
ax.set_title('Q6: TokenCounter Guarded Suspension Proof: Bounded in [0, 5]', fontweight='bold', pad=12)
ax.set_ylim(-1, 6)
ax.legend(loc='upper right')
plt.tight_layout()
plt.savefig(os.path.join(graphs_dir, 'q6_producer_consumer_buffer_trace.png'))
plt.close()
print("Q6 graph generated.")

# 7. Q7: Thread Pool Scaling: Time & Throughput
df_q7 = pd.read_csv(os.path.join(csv_dir, 'q7_thread_scaling.csv'))
fig, ax1 = plt.subplots(figsize=(8, 4.5), dpi=300)
threads = df_q7['ThreadPoolSize']
times = df_q7['ExecutionTimeMs']
throughput = df_q7['ThroughputFilesPerSec']

color = '#1f77b4'
ax1.set_xlabel('Worker Thread Pool Size (Threads)', fontweight='bold')
ax1.set_ylabel('Total Time (ms) [Lower is Better]', color=color, fontweight='bold')
line1 = ax1.plot(threads, times, color=color, marker='s', linewidth=2.5, label='Execution Time (ms)')
ax1.tick_params(axis='y', labelcolor=color)
ax1.set_xticks(threads)
for x, y in zip(threads, times):
    ax1.annotate(f'{y:,} ms', (x, y), textcoords="offset points", xytext=(0, 7), ha='center', color=color, fontweight='bold')

ax2 = ax1.twinx()
color = '#2ca02c'
ax2.set_ylabel('Throughput (Files / Second) [Higher is Better]', color=color, fontweight='bold')
line2 = ax2.plot(threads, throughput, color=color, marker='o', linewidth=2.5, linestyle='--', label='Throughput (files/s)')
ax2.tick_params(axis='y', labelcolor=color)
for x, y in zip(threads, throughput):
    ax2.annotate(f'{y:,.0f} f/s', (x, y), textcoords="offset points", xytext=(0, -15), ha='center', color=color, fontweight='bold')

plt.title('Q7: Thread Pool Scaling Across 10,000 Answer Scripts', fontweight='bold', pad=12)
plt.tight_layout()
plt.savefig(os.path.join(graphs_dir, 'q7_thread_scaling_throughput_speedup.png'))
plt.close()
print("Q7 graph generated.")
print("All 7 graphs successfully rendered in Test-Results/Graphs!")
