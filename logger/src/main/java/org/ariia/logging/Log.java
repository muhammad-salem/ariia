package org.ariia.logging;

import org.terminal.console.log.Level;
import org.terminal.console.log.api.Printer;
import org.terminal.console.log.impl.Message;
import org.terminal.console.log.impl.PrinterImpl;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Log {
    private static Level level = Level.info;
    private static Printer printer = new PrinterImpl(System.out);
    private static final ConcurrentLinkedQueue<Message> MESSAGES = new ConcurrentLinkedQueue<>();
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(1, Thread.ofVirtual().factory());

    private Log() {
    }

    public static void stopLogging() {
        if (!MESSAGES.isEmpty()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
        MESSAGES.clear();
    }

    public static void initService() {
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(Log::scheduleLoggingTask, 0, 100, TimeUnit.MILLISECONDS);
    }

    private static void scheduleLoggingTask() {
        while (!MESSAGES.isEmpty()) {
            try {
                Message message = MESSAGES.remove();
                printer.print(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void level(String levelName) {
        level(Level.valueOf(levelName));
    }

    public static void level(Level level) {
        Log.level = level;
    }

    public static Level getLevel() {
        return Log.level;
    }

    public static void printer(Printer printer) {
        Log.printer = printer;
    }

    public static void printer(Printer printer, String levelName) {
        printer(printer, Level.valueOf(levelName));
    }

    public static void printer(Printer printer, Level level) {
        Log.printer = printer;
        Log.level = level;
    }

    private static boolean isAllowed(Level level) {
        return level.ordinal() <= Log.level.ordinal();
    }


    public static void log(String message) {
//		if (isAllowed(Level.log)) {
        MESSAGES.add(new Message(Level.log, message));
//		}
    }

    public static void error(String message) {
        if (isAllowed(Level.error)) {
            MESSAGES.add(new Message(Level.error, message));
        }
    }

    public static void warn(String message) {
        if (isAllowed(Level.warn)) {
            MESSAGES.add(new Message(Level.warn, message));
        }
    }

    public static void info(String message) {
        if (isAllowed(Level.info)) {
            MESSAGES.add(new Message(Level.info, message));
        }
    }

    public static void assertions(Boolean assertions, String message) {
        if (!assertions) {
            MESSAGES.add(new Message(Level.assertion, message));
        }
    }

    public static void debug(String message) {
        if (isAllowed(Level.debug)) {
            MESSAGES.add(new Message(Level.debug, message));
        }
    }

    public static void trace(String message) {
        if (isAllowed(Level.trace)) {
            MESSAGES.add(new Message(Level.trace, message));
        }
    }

    public static void log(Class<?> classname, String message) {
//		if (isAllowed(Level.log)) {
        MESSAGES.add(new Message(Level.log, classname, message));
//		}
    }

    public static void error(Class<?> classname, String message) {
        if (isAllowed(Level.error)) {
            MESSAGES.add(new Message(Level.error, classname, message));
        }
    }

    public static void warn(Class<?> classname, String message) {
        if (isAllowed(Level.warn)) {
            MESSAGES.add(new Message(Level.warn, classname, message));
        }
    }

    public static void info(Class<?> classname, String message) {
        if (isAllowed(Level.info)) {
            MESSAGES.add(new Message(Level.info, classname, message));
        }
    }

    public static void assertions(Boolean assertions, Class<?> classname, String message) {
        if (!assertions) {
            MESSAGES.add(new Message(Level.assertion, classname, message));
        }
    }

    public static void debug(Class<?> classname, String message) {
        if (isAllowed(Level.debug)) {
            MESSAGES.add(new Message(Level.debug, classname, message));
        }
    }

    public static void trace(Class<?> classname, String message) {
        if (isAllowed(Level.trace)) {
            MESSAGES.add(new Message(Level.trace, classname, message));
        }
    }

    public static void log(String title, String message) {
//		if (isAllowed(Level.log)) {
        MESSAGES.add(new Message(Level.log, title, message));
//		}
    }

    public static void error(String title, String message) {
        if (isAllowed(Level.error)) {
            MESSAGES.add(new Message(Level.error, title, message));
        }
    }

    public static void warn(String title, String message) {
        if (isAllowed(Level.warn)) {
            MESSAGES.add(new Message(Level.warn, title, message));
        }
    }

    public static void info(String title, String message) {
        if (isAllowed(Level.info)) {
            MESSAGES.add(new Message(Level.info, title, message));
        }
    }

    public static void assertions(Boolean assertions, String title, String message) {
        if (!assertions) {
            MESSAGES.add(new Message(Level.assertion, title, message));
        }
    }

    public static void debug(String title, String message) {
        if (isAllowed(Level.debug)) {
            MESSAGES.add(new Message(Level.debug, title, message));
        }
    }

    public static void trace(String title, String message) {
        if (isAllowed(Level.trace)) {
            MESSAGES.add(new Message(Level.trace, title, message));
        }
    }

    public static void log(Class<?> classname, String title, String message) {
//		if (isAllowed(Level.log)) {
        MESSAGES.add(new Message(Level.log, classname, title, message));
//		}
    }

    public static void error(Class<?> classname, String title, String message) {
        if (isAllowed(Level.error)) {
            MESSAGES.add(new Message(Level.error, classname, title, message));
        }
    }

    public static void warn(Class<?> classname, String title, String message) {
        if (isAllowed(Level.warn)) {
            MESSAGES.add(new Message(Level.warn, classname, title, message));
        }
    }

    public static void info(Class<?> classname, String title, String message) {
        if (isAllowed(Level.info)) {
            MESSAGES.add(new Message(Level.info, classname, title, message));
        }
    }

    public static void assertions(Boolean assertions, Class<?> classname, String title, String message) {
        if (!assertions) {
            MESSAGES.add(new Message(Level.assertion, classname, title, message));
        }
    }

    public static void debug(Class<?> classname, String title, String message) {
        if (isAllowed(Level.debug)) {
            MESSAGES.add(new Message(Level.debug, classname, title, message));
        }
    }

    public static void trace(Class<?> classname, String title, String message) {
        if (isAllowed(Level.trace)) {
            MESSAGES.add(new Message(Level.trace, classname, title, message));
        }
    }

    public static void log(Class<?> classname, String title, String... message) {
//		if (isAllowed(Level.log)) {
        MESSAGES.add(new Message(Level.log, classname, title, String.join("\n", message)));
//		}
    }

    public static void error(Class<?> classname, String title, String... message) {
        if (isAllowed(Level.error)) {
            MESSAGES.add(new Message(Level.error, classname, title, String.join("\n", message)));
        }
    }

    public static void warn(Class<?> classname, String title, String... message) {
        if (isAllowed(Level.warn)) {
            MESSAGES.add(new Message(Level.warn, classname, title, String.join("\n", message)));
        }
    }

    public static void info(Class<?> classname, String title, String... message) {
        if (isAllowed(Level.info)) {
            MESSAGES.add(new Message(Level.info, classname, title, String.join("\n", message)));
        }
    }

    public static void assertions(Boolean assertions, Class<?> classname, String title, String... message) {
        if (!assertions) {
            MESSAGES.add(new Message(Level.assertion, classname, title, String.join("\n", message)));
        }
    }

    public static void debug(Class<?> classname, String title, String... message) {
        if (isAllowed(Level.debug)) {
            MESSAGES.add(new Message(Level.debug, classname, title, String.join("\n", message)));
        }
    }

    public static void trace(Class<?> classname, String title, String... message) {
        if (isAllowed(Level.trace)) {
            MESSAGES.add(new Message(Level.trace, classname, title, String.join("\n", message)));
        }
    }
}
