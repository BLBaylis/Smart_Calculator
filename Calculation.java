package calculator;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Calculation {
    private String inputString;
    private String[] inputArr;
    private String result;
    static final private String operators = "[+-*/]";
    static final private Pattern minusPattern = Pattern.compile("-");

    Calculation(String inputString) {
        this.inputString = inputString;
    }

    String getResult() {
        if (result == null) {
            performCalculation();
        }
        return result;
    }

    void formatInputs() {
        for (int i = 0; i < inputArr.length; i++) {
            if (i % 2 == 0 && UserVariables.containsKey(inputArr[i])) {
                inputArr[i] = UserVariables.get(inputArr[i]);
                continue;
            }
            if (i % 2 == 1 && inputArr[i].matches("[+-]+")) {
                inputArr[i] = parseOperator(inputArr[i]);
            }
        }
    }

    static private String parseOperator(String operator) {
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

    int getOperatorPriority(String operator) {
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
                throw new IllegalArgumentException();
        }
    }

    private void performCalculation() throws IllegalArgumentException {
        String inputString = "3 + 8 * ((4 + 3) * 2 + 1) - 6 / (2 + 1)";
        String validSingleNumberRegex = String.format("%s*\\d+", "[+-]");
        if (inputString.matches(validSingleNumberRegex)) {
            result = inputString;
            return;
        }
        //if here, then invalid or multi part
        String separatedBySpacesRegex = "[^ ]+( [^ ]+)*";
        if (!inputString.matches(separatedBySpacesRegex)) {
            //invalid spacing
            throw new IllegalArgumentException("Invalid expression");
        }
        //if here there is a space between each pair of operands
        inputString = inputString.replace("(", "( ");
        inputString = inputString.replace(")", " )");
        System.out.println(inputString);
        String[] inputArr = inputString.split("\\s+");
        System.out.println(Arrays.toString(inputArr));
        formatInputs();
        //convert to postfix
        Deque<String> notationQueue = new ArrayDeque<>();
        Deque<String> operatorStack = new ArrayDeque<>();
        for (String currString : inputArr) {
            if (currString.matches("[+-]*\\d+")) {
                notationQueue.addLast(currString);
            } else if (operatorStack.isEmpty() || "(".equals(operatorStack.peekFirst())) {
                operatorStack.addFirst(currString);
            } else if (")".equals(currString)) {
                boolean leftFound = false;
                while (leftFound) {
                    String nextOperator = operatorStack.removeFirst();
                    if ("(".equals(nextOperator)) {
                        leftFound = true;
                    } else {
                        notationQueue.addLast(nextOperator);
                    }
                }
            } else if ("(".equals(currString)) {
                operatorStack.addFirst(currString);
            } else {
                int operatorPriority = getOperatorPriority(currString);
                assert operatorStack.peekFirst() != null;
                int stackOperatorPriority = getOperatorPriority(operatorStack.peekFirst());
                while (!operatorStack.isEmpty() && operatorPriority <= stackOperatorPriority) {
                    notationQueue.addLast(operatorStack.removeFirst());
                    stackOperatorPriority = operatorStack.isEmpty() ? 0 : getOperatorPriority(operatorStack.peekFirst());
                }
                operatorStack.addFirst(currString);
            }
        }
        while (!operatorStack.isEmpty()) {
            notationQueue.addLast(operatorStack.removeFirst());
        }
        /*String validExpressionRegex = String.format("%s*\\w+( %<s+ %<s*\\w+)*", operators);
        if (!inputString.matches(validExpressionRegex)) {
            throw new IllegalArgumentException("Invalid expression");
        }*/
        Deque<Integer> calcStack = new ArrayDeque<>();
        //System.out.println(notationQueue);
        while (!notationQueue.isEmpty()) {
            String next = notationQueue.removeFirst();
            if (next.matches("[+-]?\\d+")) {
                calcStack.addFirst(Integer.parseInt(next));
            } else {
                int result;
                int pop1 = calcStack.removeFirst();
                int pop2 = calcStack.removeFirst();
                switch (next) {
                    case "+":
                        result = pop1 + pop2;
                        break;
                    case "-":
                        result = pop2 - pop1;
                        break;
                    case "*":
                        result = pop2 * pop1;
                        break;
                    case "/":
                        result = pop2 / pop1;
                        break;
                    case "^":
                        result = (int) Math.pow(pop2, pop1);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                calcStack.addFirst(result);
            }
        }
        result = Integer.toString(calcStack.peekFirst());
    }

    /*private String performMultiNumberOperation() throws IllegalArgumentException {
        String validExpressionRegex = String.format("%s*\\d+( %<s+ %<s*\\d+)*", operators);
        if (!inputString.matches(validExpressionRegex)) {
            throw new IllegalArgumentException("Unknown variable");
        }
        int output = Integer.parseInt(inputArr[0]);
        for (int i = 1; i < inputArr.length; i += 2) {
            String operator = parseOperator(inputArr[i]);
            if ("+".equals(operator)) {
                output += Integer.parseInt(inputArr[i + 1]);
            } else {
                output -= Integer.parseInt(inputArr[i + 1]);
            }
        }
        return Integer.toString(output);
    }*/
}
