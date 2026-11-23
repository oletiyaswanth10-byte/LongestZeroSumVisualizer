package test;

import algorithms.LongestZeroSum;
import algorithms.LongestZeroSum.AlgorithmResult;
import utils.InputValidator;

import java.util.Arrays;

public class AlgorithmTest {

    public static void main(String[] args) {
        System.out.println("Running LongestZeroSum Test Suite...\n");
        boolean allPassed = true;

        // Test 1: [1, 2, -2, 4, -4] -> length 4, subarray [2, -2, 4, -4]
        int[] t1 = {1, 2, -2, 4, -4};
        AlgorithmResult r1 = LongestZeroSum.solve(t1);
        boolean p1 = (r1.getMaxLength() == 4) && Arrays.equals(r1.getLongestSubarray(), new int[]{2, -2, 4, -4});
        printTestResult("Test 1 [1, 2, -2, 4, -4]", p1, 4, r1.getMaxLength(), Arrays.toString(new int[]{2, -2, 4, -4}), Arrays.toString(r1.getLongestSubarray()));
        if (!p1) allPassed = false;

        // Test 2: [1, 2, 3, 4] -> length 0
        int[] t2 = {1, 2, 3, 4};
        AlgorithmResult r2 = LongestZeroSum.solve(t2);
        boolean p2 = (r2.getMaxLength() == 0) && (r2.getLongestSubarray().length == 0);
        printTestResult("Test 2 [1, 2, 3, 4]", p2, 0, r2.getMaxLength(), "[]", Arrays.toString(r2.getLongestSubarray()));
        if (!p2) allPassed = false;

        // Test 3: [0, 0, 0] -> length 3
        int[] t3 = {0, 0, 0};
        AlgorithmResult r3 = LongestZeroSum.solve(t3);
        boolean p3 = (r3.getMaxLength() == 3) && Arrays.equals(r3.getLongestSubarray(), new int[]{0, 0, 0});
        printTestResult("Test 3 [0, 0, 0]", p3, 3, r3.getMaxLength(), Arrays.toString(new int[]{0, 0, 0}), Arrays.toString(r3.getLongestSubarray()));
        if (!p3) allPassed = false;

        // Test 4: [15, -2, 2, -8, 1, 7, 10, 23] -> length 5, subarray [-2, 2, -8, 1, 7]
        int[] t4 = {15, -2, 2, -8, 1, 7, 10, 23};
        AlgorithmResult r4 = LongestZeroSum.solve(t4);
        boolean p4 = (r4.getMaxLength() == 5) && Arrays.equals(r4.getLongestSubarray(), new int[]{-2, 2, -8, 1, 7});
        printTestResult("Test 4 [15, -2, 2, -8, 1, 7, 10, 23]", p4, 5, r4.getMaxLength(), Arrays.toString(new int[]{-2, 2, -8, 1, 7}), Arrays.toString(r4.getLongestSubarray()));
        if (!p4) allPassed = false;

        // Edge Test 5: Single 0 -> [0]
        int[] t5 = {0};
        AlgorithmResult r5 = LongestZeroSum.solve(t5);
        boolean p5 = (r5.getMaxLength() == 1) && Arrays.equals(r5.getLongestSubarray(), new int[]{0});
        printTestResult("Edge Test 5 [0]", p5, 1, r5.getMaxLength(), "[0]", Arrays.toString(r5.getLongestSubarray()));
        if (!p5) allPassed = false;

        // Edge Test 6: Entire array sums to 0: [3, -1, -2] -> length 3
        int[] t6 = {3, -1, -2};
        AlgorithmResult r6 = LongestZeroSum.solve(t6);
        boolean p6 = (r6.getMaxLength() == 3) && Arrays.equals(r6.getLongestSubarray(), new int[]{3, -1, -2});
        printTestResult("Edge Test 6 [3, -1, -2]", p6, 3, r6.getMaxLength(), "[3, -1, -2]", Arrays.toString(r6.getLongestSubarray()));
        if (!p6) allPassed = false;

        // InputValidator Test
        InputValidator.ValidationResult v1 = InputValidator.validateAndParse("[ 15, -2, 2, -8, 1, 7, 10, 23 ]");
        boolean pv1 = v1.isValid() && Arrays.equals(v1.getArray(), t4);
        System.out.println("InputValidator Bracket/Comma Test: " + (pv1 ? "PASSED" : "FAILED"));
        if (!pv1) allPassed = false;

        InputValidator.ValidationResult v2 = InputValidator.validateAndParse("abc 123");
        boolean pv2 = !v2.isValid();
        System.out.println("InputValidator Invalid String Test: " + (pv2 ? "PASSED" : "FAILED"));
        if (!pv2) allPassed = false;

        System.out.println("\nOverall Test Result: " + (allPassed ? "ALL TESTS PASSED SUCCESSFULLY! ✓" : "FAILURES DETECTED ✗"));
        if (!allPassed) {
            System.exit(1);
        }
    }

    private static void printTestResult(String name, boolean passed, int expLen, int actLen, String expSub, String actSub) {
        System.out.printf("[%s] %s\n  Expected Length: %d, Actual: %d\n  Expected Subarray: %s, Actual: %s\n\n",
                passed ? "PASS" : "FAIL", name, expLen, actLen, expSub, actSub);
    }
}
