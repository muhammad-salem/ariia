package org.ariia.speed.report;

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
		return toUnitLengthBytes(speedOfTCPReceive());
	}

	default String getSpeedTCPSendMB() {
		return toUnitLengthBytes(speedOfTCPSend());
	}

	default String getSpeedUDPReceiveMB() {
		return toUnitLengthBytes(speedOfUDPReceive());
	}

	default String getSpeedUDPSendMB() {
		return toUnitLengthBytes(speedOfUDPSend());
	}
	
	//
	
	default String getSpeedTCPReceiveMb() {
		return toUnitLengthBits(speedOfTCPReceive());
	}

	default String getSpeedTCPSendMb() {
		return toUnitLengthBits(speedOfTCPSend());
	}

	default String getSpeedUDPReceiveMb() {
		return toUnitLengthBits(speedOfUDPReceive());
	}

	default String getSpeedUDPSendMb() {
		return toUnitLengthBits(speedOfUDPSend());
	}

	// ******************Utils*******************//

	DecimalFormat decFormat = new DecimalFormat("0.00");
	float KBytes = 1024f;	// use (2^10)
	float KBites = 1000f;	// use (10^3)
	default String toUnitLengthBytes(long length) {
		return toUnitLength(length, true, KBytes);
	}

	default String toUnitLengthBits(long length) {
		return toUnitLength(length, false, KBites);
	}

	default String toUnitLength(long length, boolean isByte, float kilo) {
		float b = length;
		float k = b / kilo;
		float m = k / kilo;
		float g = m / kilo;
		float t = g / kilo;

		if (t >= 1) {
			if (isByte){
				return decFormat.format(t).concat(" TB");
			} else {
				return decFormat.format(t*8).concat(" Tb");
			}
		} else if (g >= 1) {
			if (isByte){
				return decFormat.format(g).concat(" GB");
			} else {
				return decFormat.format(g*8).concat(" Gb");
			}
		} else if (m >= 1) {
			if (isByte){
				return decFormat.format(m).concat(" MB");
			} else {
				return decFormat.format(m*8).concat(" Mb");
			}
		} else if (k >= 1) {
			if (isByte){
				return decFormat.format(k).concat(" KB");
			} else {
				return decFormat.format(k*8).concat(" Kb");
			}
		}
		return decFormat.format(b).concat(isByte? " B" : " b");
	}
}
