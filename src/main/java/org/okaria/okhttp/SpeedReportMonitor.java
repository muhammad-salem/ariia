package org.okaria.okhttp;

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
		return (getReminning() +1 ) / (speedOfTCPReceive() + 1) ;
	}

	private long getReminning() {
		return remmLength - getReceiveTCP();
	}
	
	public String getTimer() {
		
		
		StringBuilder builder = new StringBuilder();
		builder.append(ansi.BlueLight(Ansi.Bright + "⌚ "));
		builder.append("[");
		builder.append(ansi.Green(ansi.Underscore(Utils.timeformate(timer))));
		builder.append("]");
		timer++;
		return builder.toString();
	}
	// ↓↑⇔⇧⇩⇅⛗⌚▽△▲▼⬆⬇⬌
	public String getReportLine() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( Utils.getStringWidth(ansi.Red(ansi.Bright("⅀ ")
				+ ansi.Underscore(toUnitLength(remmLength))),45));
		
		builder.append( Utils.getStringWidth(ansi.Yellow(ansi.Bright("▼ ")
				+ ansi.Underscore(getTotalReceiveMB())),45));
		
		builder.append( Utils.getStringWidth(ansi.Blue(ansi.Bright("↓ ")
				+ ansi.Underscore(getSpeedTCPReceiveMB() + "/s" )),45));
		
		builder.append( Utils.getStringWidth(ansi.Red( 
				ansi.Underscore( getPercent() )),28));
		
		builder.append( "  " + ansi.Green( Utils.timeformate(getReminningTime())) );
		demondSpeedNow();
		return builder.toString();
	}
	
	
	public String getPercent() {
		return Utils.percent( getTotalReceive(), remmLength)  ;
	}
	
	@Override
	protected void finalize() throws Throwable {
		ansi = null;
		super.finalize();
	}

}
