package calculator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Calculation {
    private String inputString;
    private String[] inputArr;
    private String result;

    Calculation(String inputString) {
        this.inputString = inputString;
    }

    String getResult() {
        if (result == null) {
            performCalculation();
        }
        return result;
    }

    private void formatInputs() {
        for (int i = 0; i < inputArr.length; i++) {
            if (i % 2 == 0 && UserVariables.containsKey(inputArr[i])) {
                inputArr[i] = UserVariables.get(inputArr[i]);
                continue;
            }
            if (i % 2 == 1 && inputArr[i].matches("[+-]+")) {
                inputArr[i] = Operator.parseOperator(inputArr[i]);
            }
        }
    }

    private void convertInfixToPostfix(Deque<String> notationQueue, Deque<String> operatorStack) {
        for (String currString : inputArr) {
            if (currString.matches("[+-]*\\d+")) {
                notationQueue.addLast(currString);
            } else if (operatorStack.isEmpty() || "(".equals(operatorStack.peekFirst())) {
                operatorStack.addFirst(currString);
            } else if (")".equals(currString)) {
                boolean leftFound = false;
                while (!operatorStack.isEmpty() && !leftFound) {
                    String nextOperator = operatorStack.removeFirst();
                    if ("(".equals(nextOperator)) {
                        leftFound = true;
                    } else {
                        notationQueue.addLast(nextOperator);
                    }
                }
                if (!leftFound) {
                    throw new IllegalArgumentException("Invalid expression");
                }
            } else if ("(".equals(currString)) {
                operatorStack.addFirst(currString);
            } else {
                int operatorPriority = Operator.getOperatorPriority(currString);
                assert operatorStack.peekFirst() != null;
                int stackOperatorPriority = Operator.getOperatorPriority(operatorStack.peekFirst());
                while (!operatorStack.isEmpty() && operatorPriority <= stackOperatorPriority) {
                    notationQueue.addLast(operatorStack.removeFirst());
                    if ("(".equals(operatorStack.peekFirst())) {
                        break;
                    }
                    stackOperatorPriority = operatorStack.isEmpty() ? 0 : Operator.getOperatorPriority(operatorStack.peekFirst());
                }
                operatorStack.addFirst(currString);
            }
        }

        while (!operatorStack.isEmpty()) {
            String next = operatorStack.removeFirst();
            if ("(".equals(next) || ")".equals(next)) {
                throw new IllegalArgumentException("Invalid expression");
            }
            notationQueue.addLast(next);
        }
    }

    private int calculatePostfixExpression(Deque<String> notationQueue, Deque<Integer> calcStack) {
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
        return calcStack.getFirst();
    }


    private void performCalculation() throws IllegalArgumentException {
        String validSingleNumberRegex = String.format("%s*(\\w+|\\d+)", "[+-]");
        if (inputString.matches(validSingleNumberRegex)) {
            String variableValue = UserVariables.get(inputString);
            result = variableValue == null ? inputString : variableValue;
            return;
        }
        inputString = inputString.replaceAll("\\s+", "");
        inputArr = inputString.split("((?<=\\w)(?=([-+*^/]))|(?<=[-+*^/])(?=\\w)|(?<=[()])|(?=[()]))");
        formatInputs();
        Deque<String> notationQueue = new ArrayDeque<>();
        Deque<String> operatorStack = new ArrayDeque<>();
        convertInfixToPostfix(notationQueue, operatorStack);
        Deque<Integer> calcStack = new ArrayDeque<>();
        result = Integer.toString(calculatePostfixExpression(notationQueue, calcStack));
    }


}
