package client;

import ui.Repl;

public class ClientMain {
    public static void main(String[] args) {
        var port = 8080;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        new Repl("http://localhost:" + port).run();
    }
}
