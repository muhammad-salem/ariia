package org.ariia.speed.report;

import java.text.DecimalFormat;
import java.util.Objects;

public class SpeedReport<T extends SpeedMonitor> implements MonitorReport {
	
	
	private static DecimalFormat decimalFormat = new DecimalFormat("0.000");
	/**
	* Base 2 (1024 bytes)
	* The kibibyte is a multiple of the unit byte for digital information.
	* The binary prefix kibi means 2^10, or 1024; therefore, 1 kibibyte is 1024 bytes.
	* The unit symbol for the kibibyte is KiB.
	*/
	private static double kibibyte = 1024;
	
	/**
	* Base 10 (1000 bytes)
	* The prefix kilo means 1000 (103); therefore, one kilobyte is 1000 bytes.
	* The unit symbol is kB.
	* 
	*/
	private static double kilobyte = 1000;
	
	
	public static String unitLength(double length, boolean isBinary, boolean isByte) {
		
		double kilo = isBinary ? kibibyte : kilobyte;
		double k = length / kilo;
		if(k < 1){
			return length + (isBinary ? " B" : " b");
		}
		double m = k / kilo;
		if(m < 1){
			if (isByte){
				return decimalFormat.format(k).concat(" KB");
			} else {
				return decimalFormat.format(k*8).concat(" Kb");
			}
		}
		double g = m / kilo;
		if(g < 1){
			if (isByte){
				return decimalFormat.format(m).concat(" MB");
			} else {
				return decimalFormat.format(m*8).concat(" Mb");
			}
		}
		double t = g / kilo;
		if(t < 1){
			if (isByte){
				return decimalFormat.format(g).concat(" GB");
			} else {
				return decimalFormat.format(g*8).concat(" Gb");
			}
		} else {
			if (isByte){
				return decimalFormat.format(t).concat(" TB");
			} else {
				return decimalFormat.format(t*8).concat(" Tb");
			}
		}
	}
	
	protected T mointor;
	protected transient boolean isBinary;
	protected transient boolean isByte;
	
	public SpeedReport(T mointor) {
		this(mointor, true, true);
	}
	
	public SpeedReport(T mointor, boolean isBinary, boolean isByte) {
		this.mointor = Objects.requireNonNull(mointor);
		this.isBinary = isBinary;
		this.isByte = isByte;
	}
	
	public T getMointor() {
		return mointor;
	}
	
	public void setBinary(boolean isBinary) {
		this.isBinary = isBinary;
	}
	
	public boolean isBinary() {
		return isBinary;
	}
	
	public void toggleBinaryMode() {
		this.isBinary = !this.isBinary;
	}
	
	public void setByte(boolean isByte) {
		this.isByte = isByte;
	}
	
	public boolean isByte() {
		return isByte;
	}
	
	public void toggleByteMode() {
		this.isByte = !this.isByte;
	}
	
	public String unitLength(double length) {
		return unitLength(length, isBinary, isByte);
	}

	@Override
	public String getTcpDownload() {
		return unitLength(mointor.getTcpDownload());
	}

	@Override
	public String getTcpUpload() {
		return unitLength(mointor.getTcpUpload());
	}

	@Override
	public String getUdpDownload() {
		return unitLength(mointor.getUdpDownload());
	}

	@Override
	public String getUdpUpload() {
		return unitLength(mointor.getUdpUpload());
	}

	@Override
	public String getTcpDownloadSpeed() {
		return unitLength(mointor.getTcpDownloadSpeed());
	}

	@Override
	public String getTcpUploadSpeed() {
		return unitLength(mointor.getTcpUploadSpeed());
	}

	@Override
	public String getUdpDownloadSpeed() {
		return unitLength(mointor.getUdpDownloadSpeed());
	}

	@Override
	public String getUdpUploadSpeed() {
		return unitLength(mointor.getUdpUploadSpeed());
	}

}
