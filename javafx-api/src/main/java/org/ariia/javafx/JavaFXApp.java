package org.ariia.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.cli.AriiaCli;
import org.ariia.cli.LogCLI;
import org.ariia.config.Properties;
import org.ariia.core.api.client.Clients;
import org.ariia.internal.JavaHttpClient;
import org.ariia.javafx.controllers.DownloadFxService;
import org.ariia.javafx.controllers.MainController;
import org.ariia.javafx.controllers.MovingStage;
import org.ariia.util.R;
import org.terminal.console.log.Level;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class JavaFXApp extends Application {

    public static URL getResource(String path) {
        return JavaFXApp.class.getResource("/static/javafx/" + path);
    }

    public static InputStream openStream(String path) throws IOException {
        return JavaFXApp.getResource(path).openStream();
    }

    private static Argument arguments;

    public static void main(String[] args) {
        arguments = new Argument(args);
        if (arguments.isHelp()) {
            System.out.println(TerminalArgument.help());
            return;
        }
        if (arguments.isVersion()) {
            System.out.println(arguments.getVersion() + " - JDK 17 and JavaFX 19");
            return;
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        stage.getIcons().add(new Image(openStream("ariia.png")));
        LogCLI.initLogServices(arguments, Level.log);
        var properties = new Properties(arguments);
        if (!arguments.isSavePath()){
            properties.setDefaultSaveDirectory(R.getDownloadDirectory());
            R.mkdir(R.getDownloadDirectory());
        }
        var downloadService = new DownloadFxService();
        var httpClient = new JavaHttpClient(arguments.getProxy(), arguments.isInsecure());
        var client = Clients.segmentClient(properties, httpClient);
        var ariiaCli = new AriiaCli(downloadService, client);
        ariiaCli.lunchAsWebApp(arguments, properties);

        var url = getResource("gui/fxml/main-controller.fxml");
        var loader = new FXMLLoader(url);
        var controller = new MainController(stage, downloadService);
        loader.setController(controller);
        var pane = loader.<AnchorPane>load();

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Ariia");
        stage.setScene(new Scene(pane, 1000, 600));
        MovingStage.move(stage, pane);
        stage.show();
    }

}
