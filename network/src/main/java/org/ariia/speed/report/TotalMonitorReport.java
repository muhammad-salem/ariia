package org.ariia.speed.report;

public interface TotalMonitorReport extends MonitorReport{	
	String getTotalDownload();
	String getTotalUpload();
	String getTotal() ;
	
	String getTotalDownloadSpeed();
	String getTotalUploadSpeed();
	String getTotalSpeed();
}
