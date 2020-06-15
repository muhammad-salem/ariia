package org.ariia.speed.report;

public class TotalSpeedReport<T extends TotalSpeedMonitor> extends SpeedReport<T> implements TotalMonitorReport {
	
	
	public TotalSpeedReport(T mointor) {
		this(mointor, true, true);
	}
	
	public TotalSpeedReport(T mointor, boolean isBinary, boolean isByte) {
		super(mointor, isBinary, isByte);
	}
	
	@Override
	public String getTotalDownload() {
		return unitLength(mointor.getTotalDownload());
	}

	@Override
	public String getTotalUpload() {
		return unitLength(mointor.getTotalUpload());
	}

	@Override
	public String getTotal() {
		return unitLength(mointor.getTotal());
	}

	@Override
	public String getTotalDownloadSpeed() {
		return unitLength(mointor.getTotalDownloadSpeed());
	}

	@Override
	public String getTotalUploadSpeed() {
		return unitLength(mointor.getTotalUploadSpeed());
	}

	@Override
	public String getTotalSpeed() {
		return unitLength(mointor.getTotalSpeed());
	}

}
