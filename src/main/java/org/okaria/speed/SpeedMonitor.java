package org.okaria.speed;

import org.okaria.speed.net.DatagramSocketMonitor;
import org.okaria.speed.net.NetworkMonitor;
import org.okaria.speed.net.SocketMonitor;
import org.okaria.speed.report.SpeedReport;

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
		receiveTCP_old = getReceiveTCP();
		sendTCP_old = getSendTCP();
		sendUDP_old = getSendUDP();
		receiveUDP_old = getReceiveUDP();
	}

	@Override
	public long speedOfTCPReceive() {
		return getReceiveTCP() - receiveTCP_old;
	}

	@Override
	public long speedOfUDPReceive() {
		return getReceiveUDP() - receiveUDP_old;
	}

	@Override
	public long speedOfTCPSend() {
		return getSendTCP() - sendTCP_old;
	}

	@Override
	public long speedOfUDPSend() {
		return getSendUDP() - sendUDP_old;
	}

	@Override
	public String toString() {
		return "SpeedMonitor: {" + "sendTCP: " + sendTCP + ", receiveTCP: " + receiveTCP + ", sendUDP: " + sendUDP
				+ ", receiveUDP: " + receiveUDP + '}';
	}

}
