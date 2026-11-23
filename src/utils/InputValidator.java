package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for validating and parsing array input strings.
 * Supports positive integers, negative integers, zero, duplicate values,
 * and handles comma-separated, space-separated, and bracketed inputs.
 */
public class InputValidator {

    public static class ValidationResult {
        private final boolean valid;
        private final int[] array;
        private final String errorMessage;

        public ValidationResult(boolean valid, int[] array, String errorMessage) {
            this.valid = valid;
            this.array = array;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public int[] getArray() {
            return array;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    /**
     * Parses and validates a raw input string into an integer array.
     *
     * @param input Raw text from user
     * @return ValidationResult indicating success or specific error
     */
    public static ValidationResult validateAndParse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ValidationResult(false, null, "Input is empty. Please enter comma-separated or space-separated integers.");
        }

        String cleaned = input.trim();
        // Remove surrounding brackets if present, e.g. [1, 2, -2]
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1).trim();
        }

        if (cleaned.isEmpty()) {
            return new ValidationResult(false, null, "Array contains no elements.");
        }

        // Split by commas and/or whitespace
        String[] tokens = cleaned.split("[,\\s]+");
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (token.isEmpty()) {
                continue;
            }

            try {
                int value = Integer.parseInt(token);
                list.add(value);
            } catch (NumberFormatException e) {
                // Check if user entered floating-point or letters
                if (token.matches("-?\\d+\\.\\d+")) {
                    return new ValidationResult(false, null,
                            "Invalid element '" + token + "' at position " + (i + 1) +
                            ": Only integers (positive, negative, or zero) are allowed.");
                }
                return new ValidationResult(false, null,
                        "Invalid element '" + token + "' at position " + (i + 1) +
                        ": Not a valid integer.");
            }
        }

        if (list.isEmpty()) {
            return new ValidationResult(false, null, "No valid integer values found in input.");
        }

        if (list.size() > 100) {
            return new ValidationResult(false, null, "Array size (" + list.size() + ") exceeds visualization limit of 100 elements.");
        }

        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }

        return new ValidationResult(true, arr, null);
    }
}
