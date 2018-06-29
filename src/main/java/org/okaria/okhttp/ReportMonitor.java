package org.okaria.okhttp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.okaria.Utils;
import org.okaria.manager.Item;
import org.okaria.speed.NetworkMonitorSpeed;
import org.terminal.Ansi;

public class ReportMonitor extends NetworkMonitorSpeed {

	Map<File, Item> saveDatas = new HashMap<>();
	long remmLength = 0;

	public void addLength(long remm) {
		remmLength += remm;
	}
	
	public void subLength(long remm) {
		remmLength -= remm;
		remmLength = (remmLength >= 0) ? remmLength : 0;
	}

	public void add(String pathname, Item item) {
		add(new File(pathname), item);
	}

	public void add(File file, Item item) {
		saveDatas.put(file, item);
	}

	public void clear() {
		saveDatas.clear();
	}

	public Item get(File file) {
		return saveDatas.get(file);
	}

	// public TerminalNetworkMonitor(SaveData save) {
	// this.save = save;
	// }

//	@Override
//	public void start() {
//		Thread t = new Thread(this);
//		showSpeed = true;
//		timer = 0;
//		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//			@Override
//			public void run() {
//				if (showSpeed)
//					System.out.println("\n\n");
//			}
//		}));
//		t.start();
//	}

	public String getReport(Ansi Ansi) {
		StringBuilder  summery = new StringBuilder();
		summery.append( "  [  ");
		summery.append( Ansi.Blue(Ansi.Bright("▼ ") + Utils.getStringWidth(getTotalReceiveMB(), 15)) );
		summery.append( Ansi
				.Blue(Ansi.Bright("↓ ") + Utils.getStringWidth(getSpeedTCPReceiveMB() + "/s", 15)) );
		summery .append(" ] ");
		return summery.toString();
	}

	// ↓↑⇔⇧⇩⇅⛗⌚▽△▲▼⬆⬇⬌

	@Override
	public void run() {
		String summery = "\n";
		Ansi ansi = new Ansi();
		// long hh, mm, ss;
		while (showSpeed) {
			// hh = ((timer / 60) / 60) % 60;
			// mm = (timer / 60) % 60;
			// ss = timer % 60;

			
			summery = "";
			summery += Ansi.EraseLine + "\n";
			summery += Ansi.EraseLine + "\n";
			summery += Ansi.EraseLine + "\n";
			summery += Ansi.EraseLine + "\n";
			summery += Ansi.EraseLine;// + Ansi.SaveCursor;

			// summery += "\r ";

			summery += ansi.BlueLight(Ansi.Bright + "⌚ ");

			// summery += Ansi.Green + Ansi.Underscore;
			// summery += (hh != 0 ? hh+"" : "00") + ':' ;
			// summery += (mm != 0 ? mm+"" : "00") + ':' + ss ;
			// summery += Ansi.ResetAllAttributes;

			summery += "[";
			summery += Ansi.Green + Ansi.Underscore;
			// summery += (hh > 9 ? hh + "" : "0" + hh) + ':';
			// summery += (mm > 9 ? mm + "" : "0" + mm) + ':';
			// summery += (ss > 9 ? ss + "" : "0" + ss);

			summery += Utils.timeformate(timer);

			summery += Ansi.ResetAllAttributes;
			summery += "]";

			// summery += Ansi.BlueLight( " ⇔ ⇅ [ ");
			summery += ansi.BlueLight("   ⇔  ⇅");
			summery += "  [  ";

			// summery += Ansi.Yellow(Ansi.Bright + "▼ " + Ansi.BoldOff +
			// Utils.getStringWidth(getTotalReceiveMB(), 15));
			// summery += Ansi.Blue(Ansi.Bright + "↓ " + Ansi.BoldOff +
			// Utils.getStringWidth(getSpeedTCPReceiveMB() + "/s", 15));

			
			summery += Utils.getStringWidth(ansi.Red(Ansi.Bright + "⅀  " + Ansi.BoldOff + toUnitLength(remmLength)),
					35);
			
			summery += Utils.getStringWidth(ansi.Yellow(Ansi.Bright + "▼ " + Ansi.BoldOff + getTotalReceiveMB()),
					35);
			summery += Utils.getStringWidth(
					ansi.Blue(Ansi.Bright + "↓ " + Ansi.BoldOff + getSpeedTCPReceiveMB() + "/s"),
					35);
			
			
			summery += "  " + ansi.Green( Utils.timeformate(getReminningTime())) + "   " ;

			
			// summery += Ansi.BlueLight( " ] ");
			summery += "] ";

			summery += '\n' + Ansi.CursorUp;

			// summery += Ansi.UnsaveCursor;
			summery += Ansi.CursorUp;
			// summery += Ansi.EraseLine;
			summery += Ansi.CursorUp;
			// summery += Ansi.EraseLine;
			summery += Ansi.CursorUp;
			// summery += Ansi.EraseLine;
			summery += Ansi.CursorUp;

			System.out.print(summery);

			demondSpeedNow();
			timer++;
			try {
				// Thread.sleep(1000);
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//if(timer % 60 == 0 )
			for (File file : saveDatas.keySet()) {
				Item item = get(file);
				Utils.toJsonFile(file, item);
			}
			if(getTotalReceive() >= remmLength) {
				return;
			}
		}
	}

	private long getReminningTime() {
		return (getReminning() +1 ) / (speedOfTCPReceive() + 1) ;
	}

	private long getReminning() {
		return remmLength - getReceiveTCP();
	}

}
