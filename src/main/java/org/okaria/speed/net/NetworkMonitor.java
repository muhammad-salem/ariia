package org.okaria.speed.net;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 23, 2015 11:20 AM
 */
public class NetworkMonitor implements SocketMonitor, DatagramSocketMonitor {

	protected long receiveTCP = 0;
	protected long receiveUDP = 0;
	protected long sendTCP = 0;
	protected long sendUDP = 0;

	@Override
	public void onRead(int len) {
		receiveTCP += len;
	}

	@Override
	public void onWrite(int len) {
		sendTCP += len;
	}

	@Override
	public void onSend(final int len) {
		sendUDP += len;
	}

	@Override
	public void onReceive(final int len) {
		receiveUDP += len;
	}

	public long getTotalReceive() {
		return receiveTCP + receiveUDP;
	}

	public long getTotalSend() {
		return sendTCP + sendUDP;
	}

	public long getTotal() {
		return getTotalReceive() + getTotalSend();
	}

	public long getReceiveTCP() {
		return receiveTCP;
	}

	public long getReceiveUDP() {
		return receiveUDP;
	}

	public long getSendTCP() {
		return sendTCP;
	}

	public long getSendUDP() {
		return sendUDP;
	}

	@Override
	public String toString() {
		return "NetworkMonitor{" + "sendTCP=" + sendTCP + ", receiveTCP=" + receiveTCP + ", sendUDP=" + sendUDP
				+ ", receiveUDP=" + receiveUDP + '}';
	}
}
