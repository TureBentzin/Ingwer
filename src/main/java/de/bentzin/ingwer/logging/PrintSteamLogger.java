package de.bentzin.ingwer.logging;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class PrintSteamLogger extends Logger {

    public static PrintSteamLogger SYSTEM_LOGGER = new PrintSteamLogger("SYSTEM",System.out);


    private PrintStream printStream;

    public PrintSteamLogger(String name, PrintStream printStream) {
        super(name);
        this.printStream = printStream;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    @Override
    public void log(String message, LogLevel logLevel) {
                printStream.println(prefix(message,logLevel));

    }


}
