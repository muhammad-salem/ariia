package org.okaria.speed.report;

import java.text.DecimalFormat;

public interface SpeedReport extends SpeedTotal, CalculateSpeed {

	default String getTotalReceiveMB() {
		return toUnitLengthBytes(getTotalReceive());
	}

	default String getTotalSendMB() {
		return toUnitLengthBytes(getTotalSend());
	}

	default String getTotalMB() {
		return toUnitLengthBytes(getTotal());
	}

	default String getReceiveTCPMB() {
		return toUnitLengthBytes(getReceiveTCP());
	}

	default String getReceiveUDPMB() {
		return toUnitLengthBytes(getReceiveUDP());
	}

	default String getSendTCPMB() {
		return toUnitLengthBytes(getSendTCP());
	}

	default String getSendUDPMB() {
		return toUnitLengthBytes(getSendUDP());
	}

	default String getSpeedTCPReceiveMB() {
		return toUnitLengthBits(speedOfTCPReceive());
	}

	default String getSpeedTCPSendMB() {
		return toUnitLengthBits(speedOfTCPSend());
	}

	default String getSpeedUDPReceiveMB() {
		return toUnitLengthBits(speedOfUDPReceive());
	}

	default String getSpeedUDPSendMB() {
		return toUnitLengthBits(speedOfUDPSend());
	}

	// ******************Utils*******************//

	DecimalFormat decFormat = new DecimalFormat("0.00");
	float KBytes = 1024f;	// = 1000f;		// use (10^3) instead of (2^10)
	float KBites = 1000f;	// = 1024f;		// use (10^3) instead of (2^10)
	default String toUnitLengthBytes(long length) {
		return toUnitLength(length, true, KBytes);
	}

	default String toUnitLengthBits(long length) {
		return toUnitLength(length, false, KBites);
	}

	default String toUnitLength(long length, boolean isByte, float kilo) {
		float b = isByte ? length : length * 8;
		float k = b / kilo;
		float m = k / kilo;
		float g = m / kilo;
		float t = g / kilo;

		if (t >= 1) {
			return decFormat.format(t).concat(" TB").concat(isByte? "" : "i");
		} else if (g >= 1) {
			return decFormat.format(g).concat(" GB").concat(isByte? "" : "i");
		} else if (m >= 1) {
			return decFormat.format(m).concat(" MB").concat(isByte? "" : "i");
		} else if (k >= 1) {
			return decFormat.format(k).concat(" KB").concat(isByte? "" : "i");
		}

		return decFormat.format(b).concat(isByte? "Bytes" : "Bites");
	}
}
