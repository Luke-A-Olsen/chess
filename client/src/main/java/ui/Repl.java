package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println(SET_TEXT_BOLD + "♕ 240 Chess Client" + RESET_TEXT_BOLD_FAINT);
        System.out.println(client.eval("help"));

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            result = client.eval(line);
            if (!result.isEmpty() && !result.equals("quit")) {
                System.out.println(result);
            }
        }
        System.out.println("Thanks for playing!");
    }

    private void printPrompt() {
        System.out.print(SET_TEXT_COLOR_BLUE + client.promptLabel() + RESET_TEXT_COLOR + " >>> ");
    }
}
