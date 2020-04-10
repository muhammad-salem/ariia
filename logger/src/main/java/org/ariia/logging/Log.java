package org.ariia.logging;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.terminal.console.log.Level;
import org.terminal.console.log.impl.LevelLoggerImpl;


public final class Log {
	private Log() {}
	
	private static boolean active = false;
	public static boolean isActive() {
		return active;
	}

	private static LevelLoggerImpl logger = new LevelLoggerImpl();
	private static ConcurrentLinkedQueue<Message> queue;
	private static ScheduledExecutorService ex;

	public static void stopLogging() {
		if (!queue.isEmpty()) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		ex.shutdownNow();
		queue.clear();
	}

	static void startLogging() {
		ex.scheduleAtFixedRate(Log::scheduleLoggingTask, 0, 100, TimeUnit.MILLISECONDS);
	}

	public static void initService() {
		if (queue == null)
			queue = new ConcurrentLinkedQueue<>();
		// else queue.clear();
		if (ex == null) {
			ex = new ScheduledThreadPoolExecutor(1);
			startLogging();
		}
		active = true;
	}

	private static void scheduleLoggingTask() {
		while (!queue.isEmpty()) {
			try {
				Message message = queue.remove();
				if(logger.isAllowed(message.getLevel()) ){
					logger.getPrinter().print(message.getLevel(), 
							message.getClassname(), message.getTitle(), message.getMessage());
				}
//				logger.log(queue.remove());
			} catch (Exception e) {

			}
		}
	}

	
	public static void level(String levelName) {
		logger.setLevel(Level.valueOf(levelName));
		initService();
	}

	public static void log(String message) {queue.add(new Message(Level.log, message));}
	public static void error(String message) {queue.add(new Message(Level.error, message));}
	public static void warn(String message) {queue.add(new Message(Level.warn, message));}
	public static void info(String message) {queue.add(new Message(Level.info, message));}
	public static void assertions(Boolean assertions, String message) {
							if (!assertions) {
								queue.add(new Message(Level.assertion, message));
							}}
	public static void debug(String message) {queue.add(new Message(Level.debug, message));}
	public static void trace(String message) {queue.add(new Message(Level.trace, message));}

	public static void log(Class<?> classname, String message)
						{queue.add(new Message(Level.log, classname, message));}
	public static void error(Class<?> classname, String message)
							{queue.add(new Message(Level.error, classname, message));}
	public static void warn(Class<?> classname, String message)
							{queue.add(new Message(Level.warn,classname, message));}
	public static void info(Class<?> classname, String message)
							{queue.add(new Message(Level.info, classname, message));}
	public static void assertions(Boolean assertions, Class<?> classname, String message) 
							{if (!assertions) {
								queue.add(new Message(Level.assertion, classname, message));
							}}
	public static void debug(Class<?> classname, String message)
							{queue.add(new Message(Level.debug, classname, message));}
	public static void trace(Class<?> classname, String message)
							{queue.add(new Message(Level.trace, classname, message));}

	public static void log(String title, String message)
							{queue.add(new Message(Level.log, title, message));}
	public static void error(String title, String message)
							{queue.add(new Message(Level.error, title, message));}
	public static void warn(String title, String message)
							{queue.add(new Message(Level.warn,title, message));}
	public static void info(String title, String message)
							{queue.add(new Message(Level.info, title, message));}
	public static void assertions(Boolean assertions, String title, String message)
							{if (!assertions) {
								queue.add(new Message(Level.assertion, title, message));
							}}
	public static void debug(String title, String message)
							{queue.add(new Message(Level.debug, title, message));}
	public static void trace(String title, String message)
							{queue.add(new Message(Level.trace, title, message));}

	
	public static void log(Class<?> classname, String title, String message)
							{queue.add(new Message(Level.log, classname, title, message));}
	public static void error(Class<?> classname, String title, String message)
							{queue.add(new Message(Level.error, classname, title, message));}
	public static void warn(Class<?> classname, String title, String message)
							{queue.add(new Message(Level.warn, classname, title, message));}
	public static void info(Class<?> classname, String title, String message)
							{queue.add(new Message(Level.info, classname, title, message));}
	public static void assertions(Boolean assertions, Class<?> classname, String title, String message)
							{ if (!assertions) {
								queue.add(new Message(Level.assertion, classname, title, message));
							}}
	public static void debug(Class<?> classname, String title, String message)
							{queue.add(new Message(Level.debug, classname, title, message));}
	public static void trace(Class<?> classname, String title, String message)
							{queue.add(new Message(Level.trace, title, message));}
}
