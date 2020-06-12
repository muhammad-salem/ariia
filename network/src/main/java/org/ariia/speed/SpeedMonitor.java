package org.ariia.speed;

import org.ariia.speed.net.DatagramSocketMonitor;
import org.ariia.speed.net.NetworkMonitor;
import org.ariia.speed.net.SocketMonitor;
import org.ariia.speed.report.SpeedReport;

public class SpeedMonitor extends NetworkMonitor implements SocketMonitor, DatagramSocketMonitor, SpeedReport {

	protected transient long receiveTCP_old = 0;
	protected transient long receiveUDP_old = 0;
	protected transient long sendTCP_old = 0;
	protected transient long sendUDP_old = 0;
	
	
	protected long speedOfTCPReceive = 0;
	protected long speedOfTCPSend = 0;
	
	protected long speedOfUDPReceive = 0;
	protected long speedOfUDPSend = 0;

	/**
	 * Call this method to snapshot old vales of Send and Received of TCP and UDP transfer.
	 */
	@Override
	public void snapshotLength() {
		receiveTCP_old = receiveTCP;
		sendTCP_old = sendTCP;
		sendUDP_old = sendUDP;
		receiveUDP_old = receiveUDP;
	}
	
	@Override
	public long speedOfTCPReceive() {
		return speedOfTCPReceive;
	}

	@Override
	public long speedOfUDPReceive() {
		return speedOfUDPReceive;
	}

	@Override
	public long speedOfTCPSend() {
		return speedOfTCPSend;
	}

	@Override
	public long speedOfUDPSend() {
		return speedOfUDPSend;
	}
	
	@Override
	public void snapshotSpeed() {
		speedOfTCPReceive  = receiveTCP - receiveTCP_old;
		speedOfUDPReceive  = receiveUDP - receiveUDP_old;
		speedOfTCPSend = sendTCP - sendTCP_old;
		speedOfUDPSend = sendUDP - sendUDP_old;
	}
	
	@Override
	public String toString() {
		return "SpeedMonitor: {" + "sendTCP: " + sendTCP + ", receiveTCP: " + receiveTCP + ", sendUDP: " + sendUDP
				+ ", receiveUDP: " + receiveUDP + '}';
	}

}
