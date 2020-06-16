package org.ariia.monitors;

public interface TableMonitor {

	boolean add(RangeMonitor monitor);

	void remove(RangeMonitor monitor);

	void clear();

	String getTableReport();
	
	SessionMonitor getSessionMonitor();

}