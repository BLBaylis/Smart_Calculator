package calculator;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;

class Calculation {
    private String result;
    private final static String operatorPrecededByOperandRegex = "(?<=\\w)(?=([-+*^/]))";
    private final static String operatorFollowedByOperandRegex = "(?<=[-+*^/])(?=\\w)";
    private final static String anyBracketRegex = "(?<=[()])|(?=[()])";

    Calculation(String inputString) {
        this.result = performCalculation(inputString);
    }

    String getResult() {
        return result;
    }

    private String performCalculation(String inputString) {
        String validSingleOperandRegex = String.format("%s*(\\w+|\\d+)", "[+-]");
        boolean hasOneOperand = inputString.matches(validSingleOperandRegex);
        if (hasOneOperand) {
            String variableValue = UserVariables.get(inputString);
            return result = variableValue == null ? inputString : variableValue;
        }
        String[] inputArr = processInputString(inputString);
        Deque<String> notationQueue = new ArrayDeque<>();
        Deque<String> operatorStack = new ArrayDeque<>();
        convertInfixToPostfix(inputArr, notationQueue, operatorStack);
        Deque<String> calcStack = new ArrayDeque<>();
        return calculatePostfixExpression(notationQueue, calcStack);
    }

    private String[] processInputString(String inputString) {
        String spacesRemovedInputString = inputString.replaceAll("\\s+", "");
        /*The three regular expressions that comprise this regex each have zero width meaning that they are empty
         and instead these regexps just check for a condition to be true.  The reason for this is so when the inputString
         is split, none of the elements we are looking for are removed from the array in the process.
        */
        String stringSplitRegex = String.format("(%s|%s|%s)",
                operatorPrecededByOperandRegex,
                operatorFollowedByOperandRegex,
                anyBracketRegex
        );
        String[] inputArr = spacesRemovedInputString.split(stringSplitRegex);
        String[] substitutedArr = substituteVariables(inputArr);
        return fixOperators(substitutedArr);
    }

    static private String[] substituteVariables(String[] inputArr) {
        for (int i = 0; i < inputArr.length; i += 2) {
            if (UserVariables.containsKey(inputArr[i])) {
                inputArr[i] = UserVariables.get(inputArr[i]);
            }
        }
        return inputArr;
    }

    static private String[] fixOperators(String[] inputArr) {
        for (int i = 1; i < inputArr.length; i += 1) {
            if (inputArr[i].matches("[+-]+")) {
                inputArr[i] = Operator.parseOperator(inputArr[i]);
            }
        }
        return inputArr;
    }

    private void convertInfixToPostfix(String[] inputArr, Deque<String> notationQueue, Deque<String> operatorStack) throws IllegalArgumentException{
        for (String currInput : inputArr) {
            boolean nextOperatorOnOperatorStackIsOpeningBracket = "(".equals(operatorStack.peekFirst());
            boolean currInputIsOpeningBracket = "(".equals(currInput);
            if (currInput.matches("[+-]*\\d+")) {
                //if curr input is operand, add to notation queue
                notationQueue.addLast(currInput);
            } else if (operatorStack.isEmpty() || nextOperatorOnOperatorStackIsOpeningBracket || currInputIsOpeningBracket) {
                    operatorStack.addFirst(currInput);
            } else if (")".equals(currInput)) {
                /*If the current input is a closing bracket, pop from operator stack and push those onto notation queue
                until an opening bracket is found.  Neither the opening or closing bracket is added to the notation
                queue.*/
                handleClosingBracket(notationQueue, operatorStack);
            } else {
                /*If none of the above conditions are true, then that means the current input is an operator with a
                lower priority than the operator that is next on the operator stack.  At this point, the stack is popped
                until this is no longer the case, then the new operator is pushed onto the same stack*/
                handleLowerPriorityOperator(currInput, notationQueue, operatorStack);
            }
        }

        /*Once all inputs have been checked, add any remaining operators on stack to end of notation queue*/
        while (!operatorStack.isEmpty()) {
            String next = operatorStack.removeFirst();
            if ("(".equals(next) || ")".equals(next)) {
                throw new IllegalArgumentException("Invalid expression");
            }
            notationQueue.addLast(next);
        }
    }

    private void handleClosingBracket(Deque<String> notationQueue, Deque<String> operatorStack) throws IllegalArgumentException {
        boolean openingBracketPopped = false;
        while (!operatorStack.isEmpty() && !openingBracketPopped) {
            String nextOperator = operatorStack.removeFirst();
            if ("(".equals(nextOperator)) {
                openingBracketPopped = true;
            } else {
                //add anything that isn't an opening bracket to notationStack
                notationQueue.addLast(nextOperator);
            }
        }
        if (!openingBracketPopped) {
            throw new IllegalArgumentException("Invalid expression");
        }
    }

    private void handleLowerPriorityOperator(String operator, Deque<String> notationQueue, Deque<String> operatorStack) {
        int operatorPriority = Operator.getOperatorPriority(operator);
        assert operatorStack.peekFirst() != null;
        String stackOperator = operatorStack.peekFirst();
        int stackOperatorPriority = Operator.getOperatorPriority(stackOperator);
        while (!operatorStack.isEmpty() && operatorPriority <= stackOperatorPriority) {
            notationQueue.addLast(operatorStack.removeFirst());
            if ("(".equals(operatorStack.peekFirst())) {
                break;
            }
            stackOperatorPriority = operatorStack.isEmpty() ? 0 : Operator.getOperatorPriority(operatorStack.peekFirst());
        }
        operatorStack.addFirst(operator);
    }

    private String calculatePostfixExpression(Deque<String> notationQueue, Deque<String> calcStack) throws IllegalArgumentException {
        while (!notationQueue.isEmpty()) {
            String next = notationQueue.removeFirst();
            if (next.matches("[+-]?\\d+")) {
                calcStack.addFirst(next);
            } else {
                BigInteger result;
                BigInteger pop1 = new BigInteger(String.valueOf(calcStack.removeFirst()));
                BigInteger pop2 = new BigInteger(String.valueOf(calcStack.removeFirst()));
                switch (next) {
                    case "+":
                        result = pop1.add(pop2);
                        break;
                    case "-":
                        result = pop2.subtract(pop1);
                        break;
                    case "*":
                        result = pop2.multiply(pop1);
                        break;
                    case "/":
                        result = pop2.divide(pop1);
                        break;
                    case "^":
                        result = pop2.pow(pop1.intValue());
                        break;
                    default:
                        throw new IllegalArgumentException("Postfix expression contained character which is neither " +
                                "digit nor allowed operator");
                }
                calcStack.addFirst(result.toString());
            }
        }
        return calcStack.getFirst();
    }

}
