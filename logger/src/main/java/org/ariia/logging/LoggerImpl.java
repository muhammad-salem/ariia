package org.ariia.logging;

public class LoggerImpl implements Logger {

    private final Class<?> clazz;

    public LoggerImpl(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void log(String message) {
        Log.log(this.clazz, message);
    }

    @Override
    public void error(String message) {
        Log.error(this.clazz, message);
    }

    @Override
    public void warn(String message) {
        Log.warn(this.clazz, message);
    }

    @Override
    public void info(String message) {
        Log.info(this.clazz, message);
    }

    @Override
    public void assertions(boolean assertions, String message) {
        Log.assertions(assertions, this.clazz, message);
    }

    @Override
    public void debug(String message) {
        Log.debug(this.clazz, message);
    }

    @Override
    public void trace(String message) {
        Log.trace(this.clazz, message);
    }

    @Override
    public void log(String title, String message) {
        Log.log(this.clazz, title, message);
    }

    @Override
    public void error(String title, String message) {
        Log.error(this.clazz, title, message);
    }

    @Override
    public void warn(String title, String message) {
        Log.warn(this.clazz, title, message);
    }

    @Override
    public void info(String title, String message) {
        Log.info(this.clazz, title, message);
    }

    @Override
    public void assertions(boolean assertions, String title, String message) {
        Log.assertions(assertions, this.clazz, title, message);
    }

    @Override
    public void debug(String title, String message) {
        Log.debug(this.clazz, title, message);
    }

    @Override
    public void trace(String title, String message) {
        Log.trace(this.clazz, title, message);
    }

    @Override
    public void log(String title, String... message) {
        Log.log(this.clazz, title, message);
    }

    @Override
    public void error(String title, String... message) {
        Log.error(this.clazz, title, message);
    }

    @Override
    public void warn(String title, String... message) {
        Log.warn(this.clazz, title, message);
    }

    @Override
    public void info(String title, String... message) {
        Log.info(this.clazz, title, message);
    }

    @Override
    public void assertions(boolean assertions, String title, String... message) {
        Log.assertions(assertions, this.clazz, title, message);
    }

    @Override
    public void debug(String title, String... message) {
        Log.debug(this.clazz, title, message);
    }

    @Override
    public void trace(String title, String... message) {
        Log.trace(this.clazz, title, message);
    }
}
