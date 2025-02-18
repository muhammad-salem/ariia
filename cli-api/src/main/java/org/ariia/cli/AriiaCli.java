package org.ariia.cli;

import lombok.Getter;
import org.ariia.args.Argument;
import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.core.api.service.DownloadService;
import org.ariia.core.api.service.DownloadServiceBuilder;
import org.ariia.core.api.service.DownloadServiceFactory;
import org.ariia.items.ItemBuilder;
import org.ariia.logging.Log;
import org.ariia.util.PlatformUtil;
import org.ariia.util.R;
import org.fusesource.jansi.AnsiConsole;

import java.util.Objects;

import static org.fusesource.jansi.internal.CLibrary.STDOUT_FILENO;
import static org.fusesource.jansi.internal.CLibrary.isatty;

@Getter
public class AriiaCli {

    boolean buildServiceByFactory = false;
    private Client client;
    private DownloadService downloadService;
    private Runnable finishAction = () -> {};

    public AriiaCli(Client client) {
        this.client = Objects.requireNonNull(client);
    }

    public AriiaCli(DownloadService downloadService) {
        this.downloadService = Objects.requireNonNull(downloadService);
    }

    public AriiaCli(Client client, Runnable finishAction) {
        this.client = Objects.requireNonNull(client);
        this.finishAction = Objects.requireNonNull(finishAction);
    }

    public AriiaCli(DownloadService downloadService, Runnable finishAction) {
        this.downloadService = Objects.requireNonNull(downloadService);
        this.finishAction = Objects.requireNonNull(finishAction);
    }

    public AriiaCli(DownloadService downloadService, Client client) {
        this(downloadService, client, () -> {});
    }

    public AriiaCli(DownloadService downloadService, Client client, Runnable finishAction) {
        this.downloadService = Objects.requireNonNull(downloadService);
        this.client = Objects.requireNonNull(client);
        this.finishAction = Objects.requireNonNull(finishAction);
        this.buildServiceByFactory = true;
    }

    public void lunchAsWebApp(Argument arguments, Properties properties) {
        lunch(arguments, properties, LunchMode.ALLOW_DOWNLOAD_DISABLE_PAUSE);
    }

    public void lunchAsCliApp(Argument arguments, Properties properties) {
        lunch(arguments, properties, LunchMode.DISABLE_DOWNLOAD_ALLOW_PAUSE);
    }

    public void lunchAsJavafxApp(Argument arguments, Properties properties) {
        lunch(arguments, properties, LunchMode.ALLOW_DOWNLOAD_ALLOW_PAUSE);
    }

    private void initDownloadService(LunchMode lunchMode) {
        if (Objects.nonNull(client)) {
            DownloadServiceFactory factory = new DownloadServiceFactory(client);
            DownloadServiceBuilder builder = factory.builder();
            builder.setFinishAction(finishAction);
            builder.setLunchMode(lunchMode.isAllowDownload(), lunchMode.isAllowPause());
            if (buildServiceByFactory) {
                downloadService = builder.build(downloadService);
            } else {
                downloadService = builder.build();
            }
        } else {
            throw new RuntimeException("downloadService and client are null");
        }
    }

    private void lunch(Argument arguments, Properties properties, LunchMode lunchMode) {
        initSystemIO();
        R.mkdir(R.CachePath);
        properties.updateArguments(arguments);
        if (Objects.isNull(downloadService) || buildServiceByFactory) {
            initDownloadService(lunchMode);
        }
        Log.trace(getClass(), "Set Shutdown Hook Thread", "register shutdown thread");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            downloadService.runSystemShutdownHook();
            downloadService.printReport();
//			downloadService.close();
            System.out.println("\u001B[50B\u001B[0m\nGood Bye!\n");
        }));

        var builder = new ItemBuilder(arguments, properties);
        downloadService.initializeFromDataStore(builder.getItems());
        builder.clear();
        downloadService.startScheduledService();
    }

    private void initSystemIO() {
        int rc = isatty(STDOUT_FILENO);
        if (rc == 0 || PlatformUtil.isWindows()) {
            AnsiConsole.systemInstall();
        }
    }

}
