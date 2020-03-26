package org.okaria.mointors;

import java.util.concurrent.Callable;

import org.okaria.util.Utils;
import org.terminal.Ansi;

public class SimpleSessionMointor extends SessionMonitor {

	@Override
	protected String pattern() {
		StringBuilder builder = new  StringBuilder();
		builder.append('\r');
		
		builder.append( Ansi.EraseDown );
		builder.append("\n\n\n");
		builder.append("\r {0} [ {1} ]" );
		builder.append(Ansi.EraseEndofLine );
		
		builder.append("\r\n");
		builder.append("\r {2} [ {3} ]" );
		builder.append(Ansi.EraseEndofLine );
		builder.append("\r\n");
		
		builder.append(Ansi.EraseLine);
		builder.append("\r{4}");
		builder.append( Ansi.EraseEndofLine);
		builder.append("\n\r");
		builder.append(ansi.cursorUp(6));
		return builder.toString();
	}

	@Override
	protected Callable<String> updateDataCallable() {
		return()->{
			Object[] args = { getTimer(), firstLine(), getRemainingTimeString(), secondLine(), progressLine(78) };
			return format.format(args);
		};
	}
	
	private String firstLine() {
		StringBuilder builder = new StringBuilder();
		builder.append(ansi.red(ansi.bold(Utils.getStringWidth("T: " + getTotalLengthMB(), 16))));
		builder.append(ansi.magentaLight(ansi.bold(Utils.getStringWidth("Down: " + getDownloadLengthMB(), 19))));
		builder.append(ansi.yellow(ansi.bold(Utils.getStringWidth("Remain: " + getRemainingLengthMB(), 19))));
		return builder.toString();
	}
	
	private String secondLine() {
		StringBuilder builder = new StringBuilder();
		builder.append(ansi.magentaLight(ansi.bold(Utils.getStringWidth( "⇩ " + getTotalReceiveMB(), 15))));
		builder.append(ansi.blue(ansi.bold(Utils.getStringWidth("↓ " + getSpeedTCPReceiveMB() + "/s", 16))));
		return builder.toString();
	}
	

}
