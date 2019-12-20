package calculator;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Calculator {
    private UserVariables userVariables = new UserVariables();

    Calculator() {
    }

    enum InputType {
        COMMAND, ASSIGNMENT, CALCULATION
    }

    private String getCommandMessage(String command) {
        if ("/exit".equals(command)) {
            return "Bye!";
        } else if ("/help".equals(command)) {
            return "Type in a calculation with spaces between each number and operator";
        } else {
            return "Unknown command";
        }
    }

    private InputType determineInputType(String userInput) {
        if (userInput.charAt(0) == '/') {
            return InputType.COMMAND;
        }
        Matcher assignmentMatcher = Pattern.compile("=").matcher(userInput);
        if (assignmentMatcher.find()) {
            return InputType.ASSIGNMENT;
        }
        return InputType.CALCULATION;
    }

    final void run() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String userInput = scanner.nextLine();
            if (userInput.isEmpty()) {
                continue;
            }
            InputType inputType = determineInputType(userInput);
            switch (inputType) {
                case COMMAND:
                    String message = getCommandMessage(userInput);
                    System.out.println(message);
                    if ("\\exit".equals(userInput)) {
                        return;
                    }
                    break;
                case ASSIGNMENT:
                    try {
                        userVariables.assignVariable(userInput);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case CALCULATION:
                    try {
                        System.out.println(new Calculation(userInput).getResult());
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
            }
        }
    }
}
