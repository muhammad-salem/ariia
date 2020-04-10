package org.ariia.speed.report;

public interface SpeedTotal {

	long getTotalReceive();

	long getTotalSend();

	long getTotal();

	long getReceiveTCP();

	long getReceiveUDP();

	long getSendTCP();

	long getSendUDP();
}
