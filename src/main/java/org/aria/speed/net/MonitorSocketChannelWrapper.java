package org.aria.speed.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MonitorSocketChannelWrapper extends SocketChannel {

	private SocketChannel originalSocket;
	private List<SocketChannelMonitor> monitors;
	
	private MonitorSocketChannelWrapper() {
		super(SelectorProvider.provider());
	}
	
	public MonitorSocketChannelWrapper(SocketChannel socket, SocketChannelMonitor... monitors) {
		this();
		this.originalSocket = socket;
		this.monitors = new ArrayList<>(monitors.length);
		Collections.addAll(this.monitors, monitors);
	}

	public MonitorSocketChannelWrapper(SocketChannel socket, List<SocketChannelMonitor> monitors) {
		this();
		this.originalSocket = socket;
		this.monitors = monitors;
	}

	public MonitorSocketChannelWrapper(SocketChannel socket) {
		this();
		this.originalSocket = socket;
	}

	public static SocketChannel wrap(SocketChannel socket, SocketChannelMonitor... monitors) {
		return new MonitorSocketChannelWrapper(socket, monitors);
	}

	public static SocketChannel wrap(SocketChannel socket, List<SocketChannelMonitor> monitors) {
		return new MonitorSocketChannelWrapper(socket, monitors);
	}

	public MonitorSocketChannelWrapper addMonitor(SocketChannelMonitor monitor) {
		if (monitors == null) {
			monitors = new ArrayList<>(1);
		}
		monitors.add(monitor);
		return this;
	}

	public MonitorSocketChannelWrapper removeMonitor(SocketChannelMonitor monitor) {
		if (monitors != null) {
			monitors.remove(monitor);
		}
		return this;
	}

	public SocketChannel getOriginalSocket() {
		return originalSocket;
	}

	public void setOriginalSocket(SocketChannel originalSocket) {
		this.originalSocket = originalSocket;
	}

	public List<SocketChannelMonitor> getMonitors() {
		return monitors;
	}

	public void setMonitors(List<SocketChannelMonitor> monitors) {
		this.monitors = monitors;
	}

	@Override
	public <T> T getOption(SocketOption<T> name) throws IOException {
		return originalSocket.getOption(name);
	}

	@Override
	public Set<SocketOption<?>> supportedOptions() {
		return originalSocket.supportedOptions();
	}

	@Override
	public SocketChannel bind(SocketAddress local) throws IOException {
		return originalSocket.bind(local);
	}

	@Override
	public <T> SocketChannel setOption(SocketOption<T> name, T value) throws IOException {
		return originalSocket.setOption(name, value);
	}

	@Override
	public SocketChannel shutdownInput() throws IOException {
		return originalSocket.shutdownInput();
	}

	@Override
	public SocketChannel shutdownOutput() throws IOException {
		return originalSocket.shutdownOutput();
	}

	@Override
	public Socket socket() {
		return originalSocket.socket();
	}

	@Override
	public boolean isConnected() {
		return originalSocket.isConnected();
	}

	@Override
	public boolean isConnectionPending() {
		return originalSocket.isConnectionPending();
	}

	@Override
	public boolean connect(SocketAddress remote) throws IOException {
		return originalSocket.connect(remote);
	}

	@Override
	public boolean finishConnect() throws IOException {
		return originalSocket.finishConnect();
	}

	@Override
	public SocketAddress getRemoteAddress() throws IOException {
		return originalSocket.getRemoteAddress();
	}

	@Override
	public int read(ByteBuffer dst) throws IOException {
		int len = originalSocket.read(dst);
		onReadMonitor(len);
		return len;
	}

	@Override
	public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
		long len = originalSocket.read(dsts, offset, length);
		onReadMonitor((int)len);
		return len;
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		int len = originalSocket.write(src);
		onWriteMonitor(len);
		return len;
	}

	@Override
	public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
		long len = originalSocket.write(srcs, offset, length);
		onWriteMonitor((int) len);
		return len;
	}
	
	private void onReadMonitor(int len) {
		if (monitors != null) {
			for (InputStreamMonitor monitor : monitors) {
				monitor.onRead(len);
			}
		}
	}
	
	private void onWriteMonitor(int len) {
		if (monitors != null) {
			for (OutputStreamMonitor monitor : monitors) {
				monitor.onWrite(len);
			}
		}
	}

	@Override
	public SocketAddress getLocalAddress() throws IOException {
		return originalSocket.getLocalAddress();
	}

	@Override
	protected void implCloseSelectableChannel() throws IOException {
		originalSocket.close();
	}

	@Override
	protected void implConfigureBlocking(boolean block) throws IOException {
		originalSocket.configureBlocking(block);
	}
	
}
