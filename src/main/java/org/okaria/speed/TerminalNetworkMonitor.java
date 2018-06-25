package org.okaria.speed;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.okaria.Utils;
import org.terminal.Ansi;

public class TerminalNetworkMonitor extends NetworkMonitorSpeed {

	Map<File, long[][]> saveDatas = new HashMap<File, long[][]>();

	public void add(String pathname, long[][] arg0) {
		saveDatas.put(new File(pathname), arg0);
	}

	public void add(File file, long[][] arg0) {
		saveDatas.put(file, arg0);
	}

	public void clear() {
		saveDatas.clear();
	}

	public long[][] get(File file) {
		return saveDatas.get(file);
	}

	// public TerminalNetworkMonitor(SaveData save) {
	// this.save = save;
	// }

	@Override
	public void start() {
		Thread t = new Thread(this);
		showSpeed = true;
		timer = 0;
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				if (showSpeed)
					System.out.println("\n\n");
			}
		}));
		t.start();
	}

	// ↓↑⇔⇧⇩⇅⛗⌚▽△▲▼⬆⬇⬌

	@Override
	public void run() {
		String summery = "\n";
		Ansi ansi = new Ansi();
		long hh, mm, ss;
		while (showSpeed) {
			hh = ((timer / 60) / 60) % 60;
			mm = (timer / 60) % 60;
			ss = timer % 60;

			summery = "";
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
			summery += (hh > 9 ? hh + "" : "0" + hh) + ':';
			summery += (mm > 9 ? mm + "" : "0" + mm) + ':';
			summery += (ss > 9 ? ss + "" : "0" + ss);
			summery += Ansi.ResetAllAttributes;
			summery += "]";

			// summery += Ansi.BlueLight( " ⇔ ⇅ [ ");
			summery += ansi.BlueLight("   ⇔  ⇅");
			summery += "  [  ";

			summery += ansi.Yellow(Ansi.Bright + "▼ " + Ansi.BoldOff + Utils.getStringWidth(getTotalReceiveMB(), 15));
			summery += ansi
					.Blue(Ansi.Bright + "↓ " + Ansi.BoldOff + Utils.getStringWidth(getSpeedTCPReceiveMB() + "/s", 15));

			// summery += Ansi.BlueLight( " ] ");
			summery += " ] ";

			summery += '\n' + Ansi.CursorUp;

			// summery += Ansi.UnsaveCursor;
			summery += Ansi.CursorUp;
			// summery += Ansi.EraseLine;
			summery += Ansi.CursorUp;
			// summery += Ansi.EraseLine;

			System.out.print(summery);

			demondSpeedNow();
			timer++;
			try {
				// Thread.sleep(1000);
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (File file : saveDatas.keySet()) {
				Utils.toJsonFile(file, get(file));
			}
		}
	}

}
