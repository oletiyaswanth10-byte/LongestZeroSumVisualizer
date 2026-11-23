package algorithms;

import java.util.*;

/**
 * Core algorithmic engine for finding the Longest Contiguous Zero-Sum Subarray
 * using the optimal O(n) Prefix Sum and HashMap approach.
 * 
 * Supports both direct execution and detailed step-by-step recording
 * for rich visualization and pedagogical review.
 */
public class LongestZeroSum {

    public enum ActionType {
        INITIALIZE,
        PREFIX_SUM_ZERO,
        SUM_EXISTS_IN_MAP,
        NEW_SUM_STORED,
        COMPLETED
    }

    /**
     * Snapshot of the algorithm's state at a single step.
     */
    public static class AlgorithmStep {
        private final int stepNumber;
        private final int currentIndex;
        private final int currentElement;
        private final int previousSum;
        private final int runningSum;
        private final ActionType actionType;
        private final boolean sumIsZero;
        private final boolean sumInMap;
        private final int prevOccurrenceIndex;
        private final int candidateSubarrayStart;
        private final int candidateSubarrayEnd;
        private final int candidateLength;
        private final int currentMaxLength;
        private final int bestStartIndex;
        private final int bestEndIndex;
        private final boolean newMaxFound;
        private final Map<Integer, Integer> mapSnapshot;
        private final String actionSummary;
        private final String detailedExplanation;

        public AlgorithmStep(int stepNumber,
                             int currentIndex,
                             int currentElement,
                             int previousSum,
                             int runningSum,
                             ActionType actionType,
                             boolean sumIsZero,
                             boolean sumInMap,
                             int prevOccurrenceIndex,
                             int candidateSubarrayStart,
                             int candidateSubarrayEnd,
                             int candidateLength,
                             int currentMaxLength,
                             int bestStartIndex,
                             int bestEndIndex,
                             boolean newMaxFound,
                             Map<Integer, Integer> mapSnapshot,
                             String actionSummary,
                             String detailedExplanation) {
            this.stepNumber = stepNumber;
            this.currentIndex = currentIndex;
            this.currentElement = currentElement;
            this.previousSum = previousSum;
            this.runningSum = runningSum;
            this.actionType = actionType;
            this.sumIsZero = sumIsZero;
            this.sumInMap = sumInMap;
            this.prevOccurrenceIndex = prevOccurrenceIndex;
            this.candidateSubarrayStart = candidateSubarrayStart;
            this.candidateSubarrayEnd = candidateSubarrayEnd;
            this.candidateLength = candidateLength;
            this.currentMaxLength = currentMaxLength;
            this.bestStartIndex = bestStartIndex;
            this.bestEndIndex = bestEndIndex;
            this.newMaxFound = newMaxFound;
            this.mapSnapshot = new LinkedHashMap<>(mapSnapshot);
            this.actionSummary = actionSummary;
            this.detailedExplanation = detailedExplanation;
        }

        public int getStepNumber() { return stepNumber; }
        public int getCurrentIndex() { return currentIndex; }
        public int getCurrentElement() { return currentElement; }
        public int getPreviousSum() { return previousSum; }
        public int getRunningSum() { return runningSum; }
        public ActionType getActionType() { return actionType; }
        public boolean isSumZero() { return sumIsZero; }
        public boolean isSumInMap() { return sumInMap; }
        public int getPrevOccurrenceIndex() { return prevOccurrenceIndex; }
        public int getCandidateSubarrayStart() { return candidateSubarrayStart; }
        public int getCandidateSubarrayEnd() { return candidateSubarrayEnd; }
        public int getCandidateLength() { return candidateLength; }
        public int getCurrentMaxLength() { return currentMaxLength; }
        public int getBestStartIndex() { return bestStartIndex; }
        public int getBestEndIndex() { return bestEndIndex; }
        public boolean isNewMaxFound() { return newMaxFound; }
        public Map<Integer, Integer> getMapSnapshot() { return mapSnapshot; }
        public String getActionSummary() { return actionSummary; }
        public String getDetailedExplanation() { return detailedExplanation; }
    }

    /**
     * Complete result of the algorithm execution.
     */
    public static class AlgorithmResult {
        private final int[] originalArray;
        private final int maxLength;
        private final int startIndex;
        private final int endIndex;
        private final int[] longestSubarray;
        private final int totalIterations;
        private final List<AlgorithmStep> steps;

        public AlgorithmResult(int[] originalArray,
                               int maxLength,
                               int startIndex,
                               int endIndex,
                               int[] longestSubarray,
                               int totalIterations,
                               List<AlgorithmStep> steps) {
            this.originalArray = originalArray;
            this.maxLength = maxLength;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.longestSubarray = longestSubarray;
            this.totalIterations = totalIterations;
            this.steps = steps;
        }

        public int[] getOriginalArray() { return originalArray; }
        public int getMaxLength() { return maxLength; }
        public int getStartIndex() { return startIndex; }
        public int getEndIndex() { return endIndex; }
        public int[] getLongestSubarray() { return longestSubarray; }
        public int getTotalIterations() { return totalIterations; }
        public List<AlgorithmStep> getSteps() { return steps; }
        public boolean hasZeroSumSubarray() { return maxLength > 0; }
    }

    /**
     * Executes the Longest Zero-Sum Subarray algorithm and records every execution step.
     *
     * @param arr Input array of integers
     * @return Comprehensive AlgorithmResult with all step records
     */
    public static AlgorithmResult solve(int[] arr) {
        if (arr == null || arr.length == 0) {
            List<AlgorithmStep> emptySteps = new ArrayList<>();
            emptySteps.add(new AlgorithmStep(
                    0, -1, 0, 0, 0, ActionType.COMPLETED,
                    false, false, -1, -1, -1, 0, 0, -1, -1,
                    false, Collections.emptyMap(),
                    "Array is empty", "No elements to process."
            ));
            return new AlgorithmResult(new int[0], 0, -1, -1, new int[0], 0, emptySteps);
        }

        List<AlgorithmStep> steps = new ArrayList<>();
        Map<Integer, Integer> map = new LinkedHashMap<>();

        int runningSum = 0;
        int maxLength = 0;
        int bestStart = -1;
        int bestEnd = -1;

        // Step 0: Initialization
        steps.add(new AlgorithmStep(
                0,
                -1,
                0,
                0,
                0,
                ActionType.INITIALIZE,
                false,
                false,
                -1,
                -1,
                -1,
                0,
                0,
                -1,
                -1,
                false,
                map,
                "Initialized variables: sum = 0, maxLength = 0, HashMap = empty.",
                "Algorithm starts. We will traverse the array from index 0 to " + (arr.length - 1) +
                ", accumulating the prefix sum and keeping the first occurrence of each sum in the HashMap."
        ));

        int stepCounter = 1;

        for (int i = 0; i < arr.length; i++) {
            int element = arr[i];
            int prevSum = runningSum;
            runningSum += element;

            boolean sumIsZero = (runningSum == 0);
            boolean sumInMap = map.containsKey(runningSum);
            int prevOccurIndex = sumInMap ? map.get(runningSum) : -1;

            int candidateStart = -1;
            int candidateEnd = -1;
            int candidateLen = 0;
            boolean newMax = false;
            ActionType actionType;
            String summary;
            StringBuilder explanation = new StringBuilder();

            explanation.append("Processing Index ").append(i).append(" (Element = ").append(element).append("):\n");
            explanation.append("• Running Sum Calculation: ").append(prevSum).append(" + (").append(element).append(") = ").append(runningSum).append("\n");

            // Case 1: Prefix sum equals zero
            if (sumIsZero) {
                actionType = ActionType.PREFIX_SUM_ZERO;
                candidateStart = 0;
                candidateEnd = i;
                candidateLen = i + 1;

                explanation.append("• Prefix sum is 0! The entire subarray from index 0 to ").append(i)
                        .append(" sums to 0 (Length: ").append(candidateLen).append(").\n");

                if (candidateLen > maxLength) {
                    maxLength = candidateLen;
                    bestStart = 0;
                    bestEnd = i;
                    newMax = true;
                    summary = "Running sum is 0! New maximum zero-sum subarray found from index 0 to " + i + " (Length: " + maxLength + ").";
                    explanation.append("• Updated maxLength to ").append(maxLength).append(" (Subarray: 0 to ").append(i).append(").\n");
                } else {
                    summary = "Running sum is 0 (Subarray 0 to " + i + ", length: " + candidateLen + "). Max length remains " + maxLength + ".";
                }

                // If sum 0 is not yet in map, save it
                if (!map.containsKey(0)) {
                    map.put(0, i);
                    explanation.append("• Stored (Sum 0 -> Index ").append(i).append(") in HashMap as its first occurrence.\n");
                } else {
                    explanation.append("• HashMap already holds Sum 0 at index ").append(map.get(0)).append(". Preserving first occurrence.\n");
                }
            }
            // Case 2: Prefix sum already exists in HashMap
            else if (sumInMap) {
                actionType = ActionType.SUM_EXISTS_IN_MAP;
                candidateStart = prevOccurIndex + 1;
                candidateEnd = i;
                candidateLen = i - prevOccurIndex;

                explanation.append("• Prefix sum ").append(runningSum).append(" was already seen at index ").append(prevOccurIndex).append("!\n")
                        .append("• Mathematical property: If PrefixSum[i] == PrefixSum[j], then the sum of elements between (j + 1) and i must be 0.\n")
                        .append("• Candidate Subarray: Index ").append(candidateStart).append(" to ").append(candidateEnd)
                        .append(" (Length = ").append(i).append(" - ").append(prevOccurIndex).append(" = ").append(candidateLen).append(").\n");

                if (candidateLen > maxLength) {
                    maxLength = candidateLen;
                    bestStart = candidateStart;
                    bestEnd = candidateEnd;
                    newMax = true;
                    summary = "Sum " + runningSum + " seen at index " + prevOccurIndex + ". New max zero-sum subarray found: [" +
                              candidateStart + ".." + candidateEnd + "] (Length: " + maxLength + ").";
                    explanation.append("• Updated maxLength from ").append(maxLength - candidateLen).append(" to ").append(maxLength).append("!\n");
                } else {
                    summary = "Sum " + runningSum + " seen at index " + prevOccurIndex + ". Subarray length is " + candidateLen +
                              " (<= current max " + maxLength + "). Max length unchanged.";
                }

                explanation.append("• Retaining original index ").append(prevOccurIndex)
                        .append(" for sum ").append(runningSum).append(" in HashMap to maximize future subarray lengths.");
            }
            // Case 3: First time seeing this prefix sum
            else {
                actionType = ActionType.NEW_SUM_STORED;
                map.put(runningSum, i);
                summary = "Sum " + runningSum + " is new. Stored (Key: " + runningSum + ", Value: " + i + ") in HashMap.";
                explanation.append("• Prefix sum ").append(runningSum).append(" has not been encountered before.\n")
                        .append("• Inserted into HashMap: Key = ").append(runningSum).append(", Value = ").append(i).append(".\n")
                        .append("• Current maxLength remains ").append(maxLength).append(".");
            }

            steps.add(new AlgorithmStep(
                    stepCounter++,
                    i,
                    element,
                    prevSum,
                    runningSum,
                    actionType,
                    sumIsZero,
                    sumInMap,
                    prevOccurIndex,
                    candidateStart,
                    candidateEnd,
                    candidateLen,
                    maxLength,
                    bestStart,
                    bestEnd,
                    newMax,
                    map,
                    summary,
                    explanation.toString()
            ));
        }

        // Final completion step
        int[] longestSubarray;
        if (maxLength > 0) {
            longestSubarray = new int[maxLength];
            System.arraycopy(arr, bestStart, longestSubarray, 0, maxLength);
        } else {
            longestSubarray = new int[0];
        }

        String finalSummary;
        String finalExplanation;
        if (maxLength > 0) {
            finalSummary = "Algorithm completed. Longest zero-sum subarray has length " + maxLength +
                    " spanning indices [" + bestStart + ".." + bestEnd + "].";
            finalExplanation = "Traversal complete!\n" +
                    "• Total elements processed: " + arr.length + "\n" +
                    "• Longest contiguous zero-sum subarray: " + Arrays.toString(longestSubarray) + "\n" +
                    "• Starting Index: " + bestStart + "\n" +
                    "• Ending Index: " + bestEnd + "\n" +
                    "• Length: " + maxLength + "\n" +
                    "• Time Complexity: O(n) — single pass with O(1) average hash map operations.\n" +
                    "• Space Complexity: O(n) — maximum n distinct prefix sums stored in hash map.";
        } else {
            finalSummary = "Algorithm completed. No contiguous subarray with sum 0 exists.";
            finalExplanation = "Traversal complete!\n" +
                    "• Total elements processed: " + arr.length + "\n" +
                    "• Result: No subarray with sum = 0 was found in the input array.\n" +
                    "• Maximum length: 0\n" +
                    "• Time Complexity: O(n)\n" +
                    "• Space Complexity: O(n)";
        }

        steps.add(new AlgorithmStep(
                stepCounter,
                arr.length,
                0,
                runningSum,
                runningSum,
                ActionType.COMPLETED,
                false,
                false,
                -1,
                bestStart,
                bestEnd,
                maxLength,
                maxLength,
                bestStart,
                bestEnd,
                false,
                map,
                finalSummary,
                finalExplanation
        ));

        return new AlgorithmResult(
                arr,
                maxLength,
                bestStart,
                bestEnd,
                longestSubarray,
                arr.length,
                steps
        );
    }
}
