package org.okaria.chrome;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class ChromeTerminal {
	protected InputStream systemInput;
	protected InputStream terminalInput;
	protected OutputStream systemOutput;
	protected OutputStream terminalOutput;
	protected OutputStream systemErr;
	protected InputStream terminalErr;
	protected Process terminalProcess;

	public ChromeTerminal() {
		
	}
	
	public void defaultLifeCycle() {
		saveCurrentSystemIO();
		setupProcess();
		directCurrentSystemIO2Terminal();
	}
	
	public void saveCurrentSystemIO() {
		systemInput		= System.in;
		systemOutput	= System.out;
		systemErr		= System.err;
	}
	
	protected void setupProcess() {
		
		ProcessBuilder builder = new ProcessBuilder("gnome-terminal", "-e", "echo 'okaria chrome terminal'");
		
		try {
			terminalProcess = builder.start();
			terminalInput = terminalProcess.getInputStream();
			terminalOutput = terminalProcess.getOutputStream();
			terminalErr = terminalProcess.getErrorStream();
			
			terminalOutput.write("echo 'hi'".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void directCurrentSystemIO2Terminal() {
		System.setIn (new InputStream() {
			
			@Override
			public int read() throws IOException {
				return terminalInput.read();
			}
		});
		PrintStream out = new PrintStream(new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				terminalOutput.write(b);
			}
		});
		System.setOut(out);
		System.setErr(out);
	}
	
	public void setDefaultSystemIO() {
		System.setIn(systemInput);
		System.setOut(new PrintStream(systemOutput));
		System.setErr(new PrintStream(systemErr));
	}

	public InputStream getSystemInput() {
		return systemInput;
	}

	public InputStream getTerminalInput() {
		return terminalInput;
	}

	public OutputStream getSystemOutput() {
		return systemOutput;
	}

	public OutputStream getTerminalOutput() {
		return terminalOutput;
	}

	public OutputStream getSystemErr() {
		return systemErr;
	}

	public InputStream getTerminalErr() {
		return terminalErr;
	}
}