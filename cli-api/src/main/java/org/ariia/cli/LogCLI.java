package org.ariia.cli;

import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.logging.Log;
import org.ariia.logging.Logger;
import org.terminal.console.log.Level;
import org.terminal.console.log.api.Printer;

import java.util.Arrays;

public class LogCLI {

    private static Logger log = Logger.create(LogCLI.class);

    public static void initLogServices(Argument arguments) {
        var logLevel = arguments.getOrDefault(TerminalArgument.Debug, Level.info.name());
        Log.level(logLevel);
        Log.initService();
        log.trace("Terminal Argument", Arrays.toString(arguments.getArgs()));
    }

    public static void initLogServices(Argument arguments, Level defaultLevel) {
        var logLevel = arguments.getOrDefault(TerminalArgument.Debug, defaultLevel.name());
        Log.level(logLevel);
        Log.initService();
        log.trace("Terminal Argument", Arrays.toString(arguments.getArgs()));
    }

    public static void initLogServices(Argument arguments, Printer printer, Level defaultLevel) {
        var logLevel = arguments.getOrDefault(TerminalArgument.Debug, defaultLevel.name());
        Log.printer(printer, logLevel);
        Log.initService();
        log.trace("Terminal Argument", Arrays.toString(arguments.getArgs()));

    }

    public static void initLogServicesNoStart(Argument arguments, Printer printer, Level defaultLevel) {
        var logLevel = arguments.getOrDefault(TerminalArgument.Debug, defaultLevel.name());
        Log.printer(printer, logLevel);
        log.trace("Terminal Argument", Arrays.toString(arguments.getArgs()));
    }

    public static void startLogService() {
        Log.initService();
    }


}
