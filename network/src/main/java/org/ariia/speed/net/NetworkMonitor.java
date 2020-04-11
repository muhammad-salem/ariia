package org.ariia.speed.net;

public class NetworkMonitor implements SocketMonitor, DatagramSocketMonitor {

	protected long receiveTCP = 0;
	protected long receiveUDP = 0;
	protected long sendTCP = 0;
	protected long sendUDP = 0;

	@Override
	public void onRead(IntWarp len) {
		receiveTCP += len.value;
	}

	@Override
	public void onWrite(IntWarp len) {
		sendTCP += len.value;
	}

	@Override
	public void onSend(final IntWarp len) {
		sendUDP += len.value;
	}

	@Override
	public void onReceive(final IntWarp len) {
		receiveUDP += len.value;
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
