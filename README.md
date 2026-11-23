# HashMap-Based Longest Zero-Sum Subarray Visualizer

An interactive Java Swing desktop application that visually demonstrates how the **HashMap + Prefix Sum** approach solves the **Longest Contiguous Zero-Sum Subarray** problem in O(n) time.

---

## Project Structure

```
LongestZeroSumVisualizer/
├── src/
│   ├── Main.java                          ← Application entry point
│   ├── algorithms/
│   │   └── LongestZeroSum.java            ← Core algorithm + step recorder
│   ├── gui/
│   │   ├── Dashboard.java                 ← Main Swing dashboard window
│   │   └── ArrayPanel.java                ← Array visualization container
│   ├── visualization/
│   │   ├── ArrayVisualizer.java           ← Block-based array renderer
│   │   └── HashMapVisualizer.java         ← Live HashMap table renderer
│   └── utils/
│       └── InputValidator.java            ← Input parsing and validation
├── test/
│   └── AlgorithmTest.java                 ← Standalone algorithm test suite
├── bin/                                   ← Compiled .class files (auto-generated)
└── README.md
```

---

## Algorithm Overview

**Problem:** Find the longest contiguous subarray whose elements sum to zero.

**Approach:** Prefix Sum + HashMap (First-Occurrence Mapping)

```
1. Initialize sum = 0, maxLength = 0, HashMap = {}
2. For each index i in array:
   a. sum += array[i]
   b. If sum == 0:
        → Entire subarray [0..i] sums to 0, update maxLength = i + 1
   c. Else if sum exists in HashMap:
        → Subarray [HashMap[sum]+1 .. i] sums to 0
        → Candidate length = i - HashMap[sum]
        → Update maxLength if candidate is larger
   d. Else:
        → Store HashMap[sum] = i  (preserve FIRST occurrence only)
3. Return maxLength, start index, end index
```

**Key Invariant:** Only the first occurrence of each prefix sum is stored. This guarantees the longest possible subarray is found when the same sum is seen again.

| Complexity | Value | Reason |
|---|---|---|
| Time  | O(n) | Single array traversal |
| Space | O(n) | At most n entries in HashMap |

---

## Test Cases

| Input | Expected Length | Expected Subarray |
|---|---|---|
| `[1, 2, -2, 4, -4]` | 4 | `[2, -2, 4, -4]` |
| `[1, 2, 3, 4]` | 0 | None |
| `[0, 0, 0]` | 3 | `[0, 0, 0]` |
| `[15, -2, 2, -8, 1, 7, 10, 23]` | 5 | `[-2, 2, -8, 1, 7]` |

---

## Prerequisites

- **Java 11 or later** (tested on Java 17 and Java 21)
- No external libraries required — pure Java Swing

Check your version:
```bash
java -version
javac -version
```

---

## Compile & Run

### Option 1: Use the build scripts (recommended)

**macOS / Linux:**
```bash
cd LongestZeroSumVisualizer
chmod +x build.sh
./build.sh
```

**Windows:**
```cmd
cd LongestZeroSumVisualizer
build.bat
```

---

### Option 2: Manual compile and run

```bash
cd LongestZeroSumVisualizer

# Compile all source files
javac -d bin \
  src/utils/InputValidator.java \
  src/algorithms/LongestZeroSum.java \
  src/visualization/ArrayVisualizer.java \
  src/gui/ArrayPanel.java \
  src/visualization/HashMapVisualizer.java \
  src/gui/Dashboard.java \
  src/Main.java

# Run the application
java -cp bin Main
```

---

### Run the Algorithm Test Suite

```bash
# Compile with test file
javac -d bin \
  src/utils/InputValidator.java \
  src/algorithms/LongestZeroSum.java \
  test/AlgorithmTest.java

# Run tests
java -cp bin test.AlgorithmTest
```

Expected output:
```
Running LongestZeroSum Test Suite...

[PASS] Test 1 [1, 2, -2, 4, -4]
[PASS] Test 2 [1, 2, 3, 4]
[PASS] Test 3 [0, 0, 0]
[PASS] Test 4 [15, -2, 2, -8, 1, 7, 10, 23]
[PASS] Edge Test 5 [0]
[PASS] Edge Test 6 [3, -1, -2]
InputValidator Bracket/Comma Test: PASSED
InputValidator Invalid String Test: PASSED

Overall Test Result: ALL TESTS PASSED SUCCESSFULLY! ✓
```

---

## Application Features

### Input
- Type comma- or space-separated integers directly (e.g. `1, -2, 3, -1`)
- Square brackets optional: `[1, -2, 3]` works too
- 7 built-in preset test cases via dropdown
- Random array generator (guaranteed to contain at least one zero-sum segment)

### Step-by-Step Visualization
| Control | Action |
|---|---|
| **⏮ First** | Jump to initialization step |
| **◀ Prev** | Go one step back |
| **▶ Play / ⏸ Pause** | Auto-play / pause the walkthrough |
| **Next ▶** | Advance one step |
| **Last ⏭** | Jump to final result |
| **Speed Slider** | Adjust playback delay (150 ms – 1500 ms) |

### Live Panels
- **Array Traversal** — block-based visualization with color-coded highlights:
  - 🟡 Amber = current element
  - 🟣 Purple = previous occurrence (HashMap lookup)
  - 🔵 Blue = candidate subarray span
  - 🟢 Green = confirmed longest zero-sum subarray
- **HashMap State Table** — shows all stored prefix sum → first index entries, with glow on insert/lookup
- **Calculation Badges** — live readout of previous sum, running sum, and HashMap lookup result
- **Step Narrative** — full algorithmic reasoning in plain English for each step
- **Results Dashboard** — final subarray, range, length, iteration count, complexity

---

## Input Validation

| Input Type | Handled |
|---|---|
| Positive integers | ✅ |
| Negative integers | ✅ |
| Zero | ✅ |
| Duplicate values | ✅ |
| Comma-separated | ✅ |
| Space-separated | ✅ |
| Square bracket notation | ✅ |
| Empty input | ✅ — error shown |
| Non-numeric characters | ✅ — error shown |
| Floating-point values | ✅ — rejected with message |
| Array > 100 elements | ✅ — rejected with message |
