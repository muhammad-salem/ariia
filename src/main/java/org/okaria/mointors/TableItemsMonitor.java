package org.okaria.mointors;

import java.util.LinkedList;
import java.util.List;

import org.okaria.Utils;
import org.terminal.Ansi;
import org.terminal.beans.Row;

public class TableItemsMonitor {
	
	protected List<OneRangeMonitor> mointors;
	protected SessionMonitor session;
	protected TableItems table;
	
	public TableItemsMonitor(SessionMonitor session) {
		this.session = session;
		mointors = new LinkedList<>();
		table = new TableItems(8);
		table.head("#", "Name", "Length", "Complete", "Remain", "Down", "Speed", "%");
	}
	
	
	public boolean add(OneRangeMonitor mointor) {
		return mointors.add(mointor);
	}

	public void remove(OneRangeMonitor mointor) {
		mointors.remove(mointor);
	}
	
	public void clear() {
		mointors.clear();
	}
	

	private void callSpeedForNextCycle() {
		
		for (OneRangeMonitor mointor : mointors) {
			mointor.demondSpeedNow();
		}
		session.demondSpeedNow();
	}
	
	private void updateInfo() {
		for (OneRangeMonitor mointor : mointors) {
			mointor.updateData();
		}
		session.rangeInfoUpdateData();
	}
	
	private void updateTable() {
		table.getRows().clear();
		//table.head("name", "Length", "TD", "Remain", "Down", "Speed", "100%");
		int index = 0;
		for (OneRangeMonitor mointor : mointors) {
			Row<String> row = table.createRow();
			row.add(++index + "");
			row.add(mointor.getName());
			row.add(mointor.getTotalLengthMB());
			
			row.add(mointor.getDownloadLengthMB());
			row.add(mointor.getRemainingLengthMB());
			row.add(mointor.getTotalReceiveMB());
			row.add(mointor.getSpeedTCPReceiveMB() + "/s");
			row.add(mointor.getPercent());	
		}
		if( ! table.getRows().isEmpty() & table.getRows().size() != 1) {
			Row<String> row = table.createRow();
			row.add("#");
			row.add("session (" + session.size() +")");
			row.add(session.getTotalLengthMB());
			
			row.add(session.getDownloadLengthMB());
			row.add(session.getRemainingLengthMB());
			row.add(session.getTotalReceiveMB());
			row.add(session.getSpeedTCPReceiveMB() + "/s");
			row.add(session.getPercent());	
		}
		
		
	}
	
	
	public String getTableReport() {
		updateInfo();
		updateTable();
		StringBuilder message = new StringBuilder();
		message.append(Ansi.EraseDown);
		message.append('\n');
		message.append('\n');
		message.append('\n');
		message.append(table.toString());
		
		message.append('\n');
		message.append(' ');
		message.append(session.getTimer());
		message.append(' ');
		message.append(session.progressLine(78));
		message.append(' ');
		message.append(session.getRemainingTimeString());
		message.append('\n');
		
		String all = message.toString();
		int count = 1;
		for (byte c : all.getBytes()) {
			if(c == '\n')
				count++;
		}
		message.append(Utils.ANSI.cursorUp(count));
		callSpeedForNextCycle();
		return message.toString();
	}


	
	
}
