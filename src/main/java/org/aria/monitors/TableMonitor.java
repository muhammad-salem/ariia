package org.aria.monitors;

public interface TableMonitor {

	boolean add(OneRangeMonitor monitor);

	void remove(OneRangeMonitor monitor);

	void clear();

	String getTableReport();
	
	SessionMonitor getSessionMonitor();

}