package org.ariia.web.app;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.ariia.core.api.client.Client;
import org.ariia.core.api.service.ServiceManager;
import org.ariia.mvc.sse.EventProvider;
import org.ariia.mvc.sse.SourceEvent;
import org.ariia.util.Utils;
import org.ariia.web.app.model.LiteItem;

public class WebServiceManager extends ServiceManager {

	protected EventProvider sessionProvider;

	protected EventProvider wattingItemProvider;
	protected EventProvider downloadingItemProvider;
	protected EventProvider completeingItemProvider;
	
	protected int trackWating = 0;
	protected int trackComplete = 0;
	
	public WebServiceManager(Client client, SourceEvent sourceEvent) {
		super(client);
		sourceEvent = Objects.requireNonNull(sourceEvent);
		this.sessionProvider = new EventProvider("event-session", sourceEvent);
		this.wattingItemProvider = new EventProvider("event-item-watting", sourceEvent);
		this.downloadingItemProvider = new EventProvider("event-item-download", sourceEvent);
		this.completeingItemProvider = new EventProvider("event-item-complete", sourceEvent);
		
	}
	
	@Override
	public void startScheduledService() {
		super.startScheduledService();
		scheduledService.scheduleWithFixedDelay(this::sendwebReport, 1, 1, TimeUnit.SECONDS);
	}
	
	private void sendwebReport() {
		sessionProvider.send(Utils.toJson(sessionMonitor));
		if (wattingList.size() != trackWating) {
			wattingItemProvider.send(Utils.toJson(wattingList.stream().map(LiteItem::bind).collect(Collectors.toList())));
			trackWating = wattingList.size();
		}
		if (!downloadingList.isEmpty()) {
			downloadingItemProvider.send(Utils.toJson(downloadingList.stream().map(LiteItem::bind).collect(Collectors.toList())));
		}
		if (completeingList.size() != trackComplete) {
			completeingItemProvider.send(Utils.toJson(completeingList.stream().map(LiteItem::bind).collect(Collectors.toList())));			
			trackComplete = completeingList.size();
		}
	}

}
