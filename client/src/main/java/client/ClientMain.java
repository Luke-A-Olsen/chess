package client;

import ui.Repl;

public class ClientMain {
    public static void main(String[] args) {
        var port = 8080;
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
                if (port < 1 || port > 65535) {
                    System.out.println("Port must be between 1 and 65535.");
                    return;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Expected: java ClientMain [port]");
                return;
            }
        }
        new Repl("http://localhost:" + port).run();
    }
}
