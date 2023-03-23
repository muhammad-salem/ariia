package org.ariia.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.ariia.core.api.service.DownloadService;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.items.Builder;
import org.ariia.items.Item;
import org.ariia.logging.Logger;
import org.ariia.util.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DownloadLinkController implements Initializable {
    private static Logger log = Logger.create(DownloadLinkController.class);
    private final Stage stage;
    private final DownloadService downloadService;
    @FXML
    private Label fileName;
    @FXML
    private Label fileSize;
    @FXML
    private TextField url;
    @FXML
    private TextField referrer;
    @FXML
    private TextArea headers;
    @FXML
    private TextField saveDirectory;
    @FXML
    private CheckBox streaming;
    private Item item;
    private ItemMetaData metaData;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        url.textProperty().addListener((observable, oldValue, newValue) -> {
            buildItem();
            if (item == null) {
                return;
            }
            downloadService.fetchUrlInfo(item);
            metaData = downloadService.initializeItemMetaData(item);
            fileName.setText(metaData.getItem().getFilename());
            fileSize.setText(metaData.getRangeReport().getFileLength());
        });
    }

    private boolean isUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    private void buildItem() {
        metaData = null;
        fileSize.setText("");
        var url = this.url.getText();
        if (url == null || url.isBlank() || !isUrl(url)) {
            item = null;
            fileName.setText("");
            return;
        }
        var builder = new Builder(this.url.getText());
        if (saveDirectory.getText() != null && !saveDirectory.getText().isBlank()) {
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
        item = builder.build();
        fileName.setText(item.getFilename());
    }

    @FXML
    void changeSaveDirectory(ActionEvent event) {

    }

    @FXML
    void startDownload(ActionEvent event) {
        downloadService.addItemMetaData(metaData);
        downloadService.moveToDownloadList(metaData);
        stage.close();
    }

    @FXML
    void pauseDownload(ActionEvent event) {
        downloadService.addItemMetaData(metaData);
        downloadService.moveToPauseList(metaData);
        stage.close();
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
