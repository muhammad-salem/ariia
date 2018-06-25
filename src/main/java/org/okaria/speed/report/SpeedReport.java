package org.okaria.speed.report;

import java.text.DecimalFormat;

public interface SpeedReport extends SpeedTotal, CalculateSpeed {

	default String getTotalReceiveMB() {
		return toUnitLength(getTotalReceive());
	}

	default String getTotalSendMB() {
		return toUnitLength(getTotalSend());
	}

	default String getTotalMB() {
		return toUnitLength(getTotal());
	}

	default String getReceiveTCPMB() {
		return toUnitLength(getReceiveTCP());
	}

	default String getReceiveUDPMB() {
		return toUnitLength(getReceiveUDP());
	}

	default String getSendTCPMB() {
		return toUnitLength(getSendTCP());
	}

	default String getSendUDPMB() {
		return toUnitLength(getSendUDP());
	}

	default String getSpeedTCPReceiveMB() {
		return toUnitLength(speedOfTCPReceive());
	}

	default String getSpeedTCPSendMB() {
		return toUnitLength(speedOfTCPSend());
	}

	default String getSpeedUDPReceiveMB() {
		return toUnitLength(speedOfUDPReceive());
	}

	default String getSpeedUDPSendMB() {
		return toUnitLength(speedOfUDPSend());
	}

	// ******************Utils*******************//

	default String toUnitLength(long length) {
		// String size = new String();
		DecimalFormat decFormat = new DecimalFormat("0.00");
		// use (10^3) instead of (2^10)
		float kbyte = 1000; // = 1024;
		
		float b = length;
		float k = b / kbyte;
		float m = k / kbyte;
		float g = m / kbyte;
		float t = g / kbyte;

		if (t >= 1) {
			return decFormat.format(t) + " TB";
		} else if (g >= 1) {
			return decFormat.format(g) + " GB";
		} else if (m >= 1) {
			return decFormat.format(m) + " MB";
		} else if (k >= 1) {
			return decFormat.format(k) + " KB";
		}

		return decFormat.format(b) + " Bytes";
	}
}
