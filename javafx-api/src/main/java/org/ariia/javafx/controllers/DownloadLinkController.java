package org.ariia.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.ariia.items.Builder;
import org.ariia.items.Item;
import org.ariia.logging.Logger;
import org.ariia.util.R;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DownloadLinkController implements Initializable {
    private static Logger log = Logger.create(DownloadLinkController.class);

    @FXML private TextField url;
    @FXML private TextField referrer;
    @FXML private TextArea headers;
    @FXML private TextField saveDirectory;
    @FXML private CheckBox streaming;

    private final Stage stage;
    private final DownloadFxService downloadService;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private Item getItem(){
        var builder = new Builder(this.url.getText());
        if (saveDirectory.getText() != null && !saveDirectory.getText().isBlank()){
            builder.saveDir(saveDirectory.getText());
        } else {
            builder.saveDir(R.DownloadsPath);
        }
        var headerMap = new HashMap<String, List<String>>();
        if (referrer.getText() != null && !referrer.getText().isBlank()) {
            headerMap.put("Referer", List.of(referrer.getText()));
        }
        if (headers.getText() != null && !headers.getText().isBlank()) {
            var lines = headers.getText().split("\n");
            var list = Arrays.stream(lines)
                    .map(line -> line.split(": "))
                    .collect(Collectors.groupingBy(strings -> strings[0], Collectors.mapping(strings -> strings[1], Collectors.toList())));
            headerMap.putAll(list);
        }
        builder.addHeaders(headerMap);
        return builder.build();
    }

    @FXML
    void changeSaveDirectory(ActionEvent event) {

    }

    @FXML
    void startDownload(ActionEvent event) {
        var item = getItem();
        this.downloadService.initializeItemOnlineAndDownload(item);
    }

    @FXML
    void pauseDownload(ActionEvent event) {
        var item = getItem();
        this.downloadService.initializeItemOnlineAndDownload(item);
    }

    @FXML
    void close(ActionEvent event) {
        stage.close();
    }

    @FXML
    void minimize(ActionEvent event) {
        stage.setIconified(true);
    }

    @FXML
    void cancel(ActionEvent event) {
        stage.close();
    }

}
