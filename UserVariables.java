package calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class UserVariables {
    static private Map<String, String> userVariables = new HashMap<>();

    private static void put(String key, String value) {
        userVariables.put(key, value);
    }

    static String get(String key) {
        return userVariables.get(key);
    }

    static boolean containsKey(String key) {
        return userVariables.containsKey(key);
    }

    void assignVariable(String userInput) throws IllegalArgumentException {
        userInput = userInput.replaceAll("\\s+", "");
        String[] assignmentArr = userInput.split("=");
        if (assignmentArr.length != 2) {
            throw new IllegalArgumentException("Invalid assignment");
        }
        String variableName = assignmentArr[0];
        Matcher validVariableNameMatcher = Pattern.compile("[a-zA-Z]+").matcher(variableName);
        if (!validVariableNameMatcher.matches()) {
            throw new IllegalArgumentException("Invalid identifier");
        }
        try {
            String variableValue = new Calculation(assignmentArr[1]).getResult();
            UserVariables.put(variableName, variableValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid assignment");
        }
    }

}
