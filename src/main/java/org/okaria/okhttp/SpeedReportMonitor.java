package org.okaria.okhttp;

import java.text.MessageFormat;

import org.okaria.Utils;
import org.okaria.speed.SpeedMonitor;
import org.terminal.Ansi;

public class SpeedReportMonitor extends SpeedMonitor {

	private long remmLength = 0;
	protected long timer = 0;
	private Ansi ansi = new Ansi();

	public void addLength(long remm) {
		remmLength += remm;
	}

	public void subLength(long remm) {
		remmLength -= remm;
		remmLength = (remmLength >= 0) ? remmLength : 0;
	}

	private long getReminningTime() {
		return (getRemaining() + 1) / (speedOfTCPReceive() + 1);
	}

	private long getRemaining() {
		return remmLength - getReceiveTCP();
	}
	
	private String getRemainingMB() {
	
		return toUnitLength(getRemaining());
	}

	public String getTimer() {
		//StringBuilder builder = new StringBuilder();
		//builder.append(ansi.BlueLight(Ansi.Bright + "⌚ "));
		//builder.append("[ ");
		//builder.append( ansi.Green( ansi.Underscore(Utils.timeformate(timer)) ));
		//builder.append(" ]");
		//timer++;
		return ansi.Green( ansi.Underscore(Utils.timeformate(timer++)) );
	}

	// ↓↑⇔⇧⇩⇅⛗⌚▽△▲▼⬆⬇⬌
	public String getReportLine() {
		StringBuilder builder = new StringBuilder();

		builder.append( ansi.Red( ansi.Bright( "T: " + toUnitLength(remmLength) ) ) );
		builder.append(",  ");
		builder.append( ansi.Yellow( ansi.Bright( "Down: " + getTotalReceiveMB() ) ) );
		builder.append(",  ");
		builder.append ( ansi.Blue(ansi.Bright( "↓ " + getSpeedTCPReceiveMB() + "/s" ) ) ) ;
		builder.append(",  ");
		builder.append(ansi.Red(ansi.Underscore(getPercent() ) ) );
		builder.append(",  ");
		builder.append( ansi.Yellow( ansi.Bright("Remain: " + getRemainingMB()  ) ));
		builder.append(",  ");
		builder.append( ansi.Green(Utils.timeformate(getReminningTime())) );
		
		
//		builder.append(Utils.getStringWidth(ansi.Red(ansi.Bright("Total : ") + ansi.Underscore(toUnitLength(remmLength))), 48));	
//		builder.append(Utils.getStringWidth(ansi.Yellow(ansi.Bright("Download : ") + ansi.Underscore(getTotalReceiveMB())), 50));
//		builder.append(Utils.getStringWidth(ansi.Yellow(ansi.Bright("Remaining : ") + ansi.Underscore(getRemainingMB())), 50));	
//		builder.append ( ansi.Blue(ansi.Bright( Utils.getStringWidth("↓ " + getSpeedTCPReceiveMB() + "/s" , 22) ) ) ) ;
//		builder.append(Utils.getStringWidth(ansi.Blue(ansi.Bright("↓ ") + ansi.Underscore(getSpeedTCPReceiveMB() + "/s")), 43));
//		builder.append(ansi.Red(ansi.Underscore(Utils.getStringWidth(getPercent(), 12) ) ) );
//		builder.append("  " + ansi.Green(Utils.timeformate(getReminningTime())) + "  " );
		demondSpeedNow();
		return builder.toString();
	}

	public String getPercent() {
		return Utils.percent(getTotalReceive(), remmLength);
	}

//	protected String getPrintMessage() {
//		return Ansi.CursorUp + '\r' + Ansi.EraseLine + "\n" + Ansi.EraseLine  + "\n\r" 
//				+ Ansi.EraseLine + "\r{0}" + " [ {1} ] \r\n" + Ansi.EraseLine
//				+ Ansi.CursorUp + Ansi.CursorUp + Ansi.CursorUp;
//	}
	
	protected String getPrintMessage() {
		return    "\r\n\n\n\r"
				+ Ansi.EraseLine + "\r {0} [ {1} ] \r\n"
				+ Ansi.CursorUp + Ansi.CursorUp + Ansi.CursorUp + Ansi.CursorUp
				;
	}

	private MessageFormat format = new MessageFormat(getPrintMessage());

	public String getMointorPrintMessage() {
		Object[] args = { getTimer(), getReportLine() };
		return format.format(args);
	}
	
	public String getMointorPrintMessageln() {
		return '\r' + getMointorPrintMessage()+ Ansi.CursorUp;
	}

	@Override
	protected void finalize() throws Throwable {
		ansi = null;
		super.finalize();
	}

}
