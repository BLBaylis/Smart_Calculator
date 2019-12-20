package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Operator {
    static final private Pattern minusPattern = Pattern.compile("-");

    static String parseOperator(String operator) {
        if (operator.length() == 1) {
            return operator;
        }
        Matcher minusRegexMatcher = minusPattern.matcher(operator);
        int minusCount = 0;
        while (minusRegexMatcher.find()) {
            minusCount++;
        }
        return minusCount % 2 == 1 ? "-" : "+";
    }

    static int getOperatorPriority(String operator) {
        switch (operator) {
            case "^":
                return 3;
            case "*":
            case "/":
                return 2;
            case "+":
            case"-":
                return 1;
            default:
                throw new IllegalArgumentException("Unknown Operator");
        }
    }
}
