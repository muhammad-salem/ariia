package org.ariia.speed.report;

public interface CalculateSpeed {

	void snapshotLength();
	
	void snapshotSpeed();

	long speedOfTCPSend();

	long speedOfTCPReceive();

	long speedOfUDPSend();

	long speedOfUDPReceive();
}
