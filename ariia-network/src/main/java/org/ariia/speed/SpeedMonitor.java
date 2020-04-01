package org.ariia.speed;

import org.ariia.speed.net.DatagramSocketMonitor;
import org.ariia.speed.net.NetworkMonitor;
import org.ariia.speed.net.SocketMonitor;
import org.ariia.speed.report.SpeedReport;

public class SpeedMonitor extends NetworkMonitor implements SocketMonitor, DatagramSocketMonitor, SpeedReport {

	protected long receiveTCP_old = 0;
	protected long receiveUDP_old = 0;
	protected long sendTCP_old = 0;
	protected long sendUDP_old = 0;

	@Override
	public long getTotalReceive() {
		return receiveTCP + receiveUDP;
	}

	@Override
	public long getTotalSend() {
		return sendTCP + sendUDP;
	}

	@Override
	public long getTotal() {
		return getTotalReceive() + getTotalSend();
	}

	@Override
	public long getReceiveTCP() {
		return receiveTCP;
	}

	@Override
	public long getReceiveUDP() {
		return receiveUDP;
	}

	@Override
	public long getSendTCP() {
		return sendTCP;
	}

	@Override
	public long getSendUDP() {
		return sendUDP;
	}

	/**
	 * Call this method to snapshot old vale of send/received tcp/udp represents.
	 */
	@Override
	public void demondSpeedNow() {
		receiveTCP_old = receiveTCP;
		sendTCP_old = sendTCP;
		sendUDP_old = sendUDP;
		receiveUDP_old = receiveUDP;
	}

	@Override
	public long speedOfTCPReceive() {
		return receiveTCP - receiveTCP_old;
	}

	@Override
	public long speedOfUDPReceive() {
		return receiveUDP - receiveUDP_old;
	}

	@Override
	public long speedOfTCPSend() {
		return sendTCP - sendTCP_old;
	}

	@Override
	public long speedOfUDPSend() {
		return sendUDP - sendUDP_old;
	}

	@Override
	public String toString() {
		return "SpeedMonitor: {" + "sendTCP: " + sendTCP + ", receiveTCP: " + receiveTCP + ", sendUDP: " + sendUDP
				+ ", receiveUDP: " + receiveUDP + '}';
	}

}
