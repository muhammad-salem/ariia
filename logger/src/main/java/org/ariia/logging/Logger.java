package org.ariia.logging;

public interface Logger {

    static Logger create(Class<?> clazz){
        return new LoggerImpl(clazz);
    }

    void log(String message);

    void error(String message);

    void warn(String message);

    void info(String message);

    void assertions(boolean assertions, String message);

    void debug(String message);

    void trace(String message);

    void log(String title, String message);

    void error(String title, String message);

    void warn(String title, String message);

    void info(String title, String message);

    void assertions(boolean assertions, String title, String message);

    void debug(String title, String message);

    void trace(String title, String message);

    void log(String title, String... message);

    void error(String title, String... message);

    void warn(String title, String... message);

    void info(String title, String... message);

    void assertions(boolean assertions, String title, String... message);

    void debug(String title, String... message);

    void trace(String title, String... message);
}
