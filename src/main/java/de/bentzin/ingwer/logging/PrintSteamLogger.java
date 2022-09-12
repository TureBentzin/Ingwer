package de.bentzin.ingwer.logging;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class PrintSteamLogger extends Logger {

    public static PrintSteamLogger SYSTEM_LOGGER = new PrintSteamLogger("SYSTEM", System.out);


    private final PrintStream printStream;

    public PrintSteamLogger(@NotNull String name, @NotNull PrintStream printStream, @NotNull Logger parent) {
        super(name, parent);
        this.printStream = printStream;
    }

    public PrintSteamLogger(@NotNull String name, @NotNull PrintStream printStream) {
        super(name);
        this.printStream = printStream;
    }

    @Deprecated
    public PrintSteamLogger(@NotNull PrintStream printStream) {
        super(printStream.getClass().getSimpleName() + printStream.hashCode());
        this.printStream = printStream;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    @Override
    public void log(String message, @NotNull LogLevel logLevel) {
        if (logLevel.equals(LogLevel.COSMETIC)) {
            printStream.println(message);
        } else
            printStream.println(prefix(message, logLevel));

    }

    @Override
    public Logger adopt(String name) {
        return new PrintSteamLogger(name, getPrintStream(), this);
    }


}
