package org.okaria.mointors;

import java.util.LinkedList;
import java.util.List;

import org.okaria.Utils;
import org.okaria.manager.Item;
import org.terminal.Ansi;
import org.terminal.beans.Row;

public class TableItemsMonitor {
	
	protected List<OneRangeMointor> mointors;
	protected SessionMonitor session;
	protected TableItems table;
	
	public TableItemsMonitor(SessionMonitor session) {
		this.session = session;
		mointors = new LinkedList<>();
		table = new TableItems(8);
		table.head("#", "Name", "Length", "Complete", "Remain", "Down", "Speed", "%");
	}
	
	
	public boolean add(OneRangeMointor mointor) {
		return mointors.add(mointor);
	}

	public void remove(OneRangeMointor mointor) {
		mointors.remove(mointor);
	}
	
	public void clear() {
		mointors.clear();
	}
	

	private void callSpeedForNextCycle() {
		
		for (OneRangeMointor mointor : mointors) {
			mointor.demondSpeedNow();
		}
		session.demondSpeedNow();
	}
	
	private void updateInfo() {
//		for (OneRangeMointor mointor : mointors) {
//			mointor.updatedata();
//		}
		session.rangeInfoUpdateData();
	}
	
	private void updateTable() {
		table.getRows().clear();
		//table.head("name", "Length", "TD", "Remain", "Down", "Speed", "100%");
		int index = 0;
		for (OneRangeMointor mointor : mointors) {
			Item item = mointor.getItem();
			Row<String> row = table.createRow();
			row.add(++index + "");
			row.add(item.getFilename());
			row.add(mointor.getTotalLengthMB());
			
			row.add(mointor.getDownloadLengthMB());
			row.add(mointor.getRemainingLengthMB());
			row.add(mointor.getTotalReceiveMB());
			row.add(mointor.getSpeedTCPReceiveMB() + "/s");
			row.add(mointor.getPercent());	
		}
		if( table.getRows().size() != 1) {
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


	
//	public static void main(String[] args) {
//		File dir = new File( R.getConfigDirectory());
//		File[] files = dir.listFiles();
//		SimpleSessionMointor sessionMointor = new SimpleSessionMointor();
//		TableItemsMonitor itemsMonitor = new TableItemsMonitor(sessionMointor);
//		int i = 0;
//		for (File file : files) {
//			
//			Item item = Utils.fromJson(file, Item.class);
//			if(item == null) continue;
//			itemsMonitor.add(new OneRangeMointor(item));
//			sessionMointor.add(item.getRangeInfo());
//			if(++i == 4) break;
//		}
//		
//		System.out.println(itemsMonitor.getTableReport());
//	}
	
}
