package org.ariia.cli;

import java.util.Arrays;

import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.logging.Log;
import org.terminal.console.log.Level;
import org.terminal.console.log.impl.LevelLoggerImpl;

public class LogCli {
	
	public static void initLogServices(Argument arguments) {
		String log_level = 
				arguments.getOrDefault(TerminalArgument.Debug, Level.info.name());
		Log.level(log_level);
		Log.trace(AriiaCli.class, "Terminal Argument", Arrays.toString(arguments.getArgs()));
	}
	
	public static void initLogServices(Argument arguments, Level defaultLevel) {
		String log_level = 
				arguments.getOrDefault(TerminalArgument.Debug, defaultLevel.name());
		Log.level(log_level);
		Log.trace(AriiaCli.class, "Terminal Argument", Arrays.toString(arguments.getArgs()));
	}
	
	public static void initLogServices(Argument arguments, LevelLoggerImpl levelLogger, Level defaultLevel) {
		String log_level = 
				arguments.getOrDefault(TerminalArgument.Debug, defaultLevel.name());
		Log.setLogger(levelLogger, log_level);
		Log.trace(AriiaCli.class, "Terminal Argument", Arrays.toString(arguments.getArgs()));
		
	}

}
