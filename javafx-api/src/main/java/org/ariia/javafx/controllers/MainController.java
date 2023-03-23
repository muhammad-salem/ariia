package org.ariia.javafx.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.items.Builder;
import org.ariia.javafx.JavaFXApp;
import org.ariia.logging.Logger;
import org.ariia.util.R;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.Flow;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MainController implements Initializable {
    private static Logger log = Logger.create(MainController.class);

    private Function<ItemMetaData, ItemProperty> findByMetaDate = itemMetaData -> MainController.this.table.getItems()
                 .stream()
                 .filter(itemProperty -> itemProperty.getItem() == itemMetaData.getItem())
                 .findAny()
                 .orElse(null);
    private Function<ItemProperty, ItemMetaData> findByProperty = property -> MainController.this.downloadService.itemStream()
            .filter(metaData -> metaData.getItem() == property.getItem())
            .findAny()
            .orElse(null);

    private Flow.Subscriber<ItemMetaData> onUpdateSubscriber = new Flow.Subscriber<>() {
        private Flow.Subscription subscription;
        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(1);
        }

        @Override
        public void onNext(ItemMetaData item) {
            var property = findByMetaDate.apply(item);
            if (property != null){
                property.updateMonitoring();
            }
            Platform.runLater(() -> sessionReport.setText(getSessionReport()));
            this.subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            log.info("onError");
        }

        @Override
        public void onComplete() {
            log.info("onComplete");
        }

    };

    private Flow.Subscriber<ItemMetaData> onAddSubscriber = new Flow.Subscriber<>() {
        private Flow.Subscription subscription;
        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(1);
        }

        @Override
        public void onNext(ItemMetaData item) {
            var property = ItemProperty.of(item.getItem(), item.getRangeReport());
            MainController.this.table.getItems().add(property);
            this.subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            log.info("onError");
        }

        @Override
        public void onComplete() {
            log.info("onComplete");
        }

    };

    private Flow.Subscriber<ItemMetaData> onRemoveSubscriber = new Flow.Subscriber<>() {
        private Flow.Subscription subscription;
        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(1);
        }

        @Override
        public void onNext(ItemMetaData item) {
            var property = findByMetaDate.apply(item);
            if (property != null){
                MainController.this.table.getItems().remove(property);
            }
            this.subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            log.info("onError");
        }

        @Override
        public void onComplete() {
            log.info("onComplete");
        }

    };

    @FXML
    public MenuBar menu;

    @FXML
    public HBox hBoxHeader, hBoxFind;

    @FXML
    public TableView<ItemProperty> table;

    @FXML
    private TableColumn<ItemProperty, String> colName;

    @FXML
    private TableColumn<ItemProperty, String> colUrl;

    @FXML
    private TableColumn<ItemProperty, String> colDownloaded;

    @FXML
    private TableColumn<ItemProperty, String> colProgress;

    @FXML
    private TableColumn<ItemProperty, String> colLength;

    @FXML
    private TableColumn<ItemProperty, String> colRemaining;

    @FXML
    private TableColumn<ItemProperty, String> colStatus;

    @FXML
    private TableColumn<ItemProperty, String> colTimeLeft;

    @FXML
    public TextField findField;

    @FXML
    public Label sessionReport;

    @FXML
    public ContextMenu contextOpt, contextMenuTree;

    @FXML
    public Button btnOpt, deleteButton, deleteCompButton;

    @FXML
    public ImageView findIcon;

    @FXML
    public AnchorPane anchorRoot;

    private final Stage stage;
    private final DownloadFxService downloadService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        downloadService.setAllowDownload(true);

        colName.setCellValueFactory(item -> item.getValue().getName());
        colUrl.setCellValueFactory(item -> item.getValue().getUrl());
        colDownloaded.setCellValueFactory(item -> item.getValue().getDownload());
        colProgress.setCellValueFactory(item -> item.getValue().getProgress());
        colLength.setCellValueFactory(item -> item.getValue().getLength());
        colRemaining.setCellValueFactory(item -> item.getValue().getRemaining());
        colStatus.setCellValueFactory(item -> item.getValue().getStatus());
        colTimeLeft.setCellValueFactory(item -> item.getValue().getTimeLeft());

        updateTableItems();

        this.downloadService.getUpdatePublisher().subscribe(this.onUpdateSubscriber);
        this.downloadService.getAddPublisher().subscribe(this.onAddSubscriber);
        this.downloadService.getRemovePublisher().subscribe(this.onRemoveSubscriber);

    }

    private void updateTableItems(){
        var items = downloadService.itemStream()
                .sorted(Comparator.comparing(itemMetaData -> itemMetaData.getItem().getId()))
                .map(i -> ItemProperty.of(i.getItem(), i.getRangeReport()))
                .collect(Collectors.toList());
        items.forEach(ItemProperty::updateMonitoring);
        table.setItems(FXCollections.observableArrayList(items));
    }

    private ObservableList<ItemProperty> getSelectedItems() {
        return table.getSelectionModel().getSelectedItems();
    }

    private String getSessionReport(){
        var session = this.downloadService.getSpeedTableReport().getSessionMonitor();
        return new StringBuilder()
                .append("Session: ")
                .append(session.rangeSize())
                .append(", ")
                .append("Total: ")
                .append(session.getTotalLengthMB())
                .append(", Progress: ")
                .append(session.getPercent())
                .append(", Complete: ")
                .append(session.getDownloadLengthMB())
                .append(", Remaining: ")
                .append(session.getRemainingLengthMB())
                .append(", Speed: ")
                .append(session.getTcpDownloadSpeed())
                .append(", Downloaded: ")
                .append(session.getTcpDownload())
                .toString();
    }

    @FXML
    public void exportToEF2IDM(ActionEvent event) {

    }

    @FXML
    public void exportToJsonFile(ActionEvent event) {

    }

    @FXML
    public void exportToTextFile(ActionEvent event) {

    }

    @FXML
    public void importFromEf2IDM(ActionEvent event) {

    }

    @FXML
    public void importFromJsonFile(ActionEvent event) {

    }

    @FXML
    public void importFromTextFile(ActionEvent event) {

    }

    @FXML
        var downloadStage = new Stage();
        var downloadLinkController = new DownloadLinkController(downloadStage, downloadService);

        var url = JavaFXApp.getResource("gui/fxml/control/fetch-url.fxml");
        var loader = new FXMLLoader(url);
        loader.setController(downloadLinkController);
        var pane = loader.<AnchorPane>load();

        downloadStage.initStyle(StageStyle.UNDECORATED);
        downloadStage.setTitle("Add URL");
        downloadStage.setScene(new Scene(pane, 705, 400));
        MovingStage.move(downloadStage, pane);
        downloadStage.show();
    }

    @FXML
    public void startDownload(ActionEvent event) {
        resumeDownloadFile(event);
    }

    @FXML
    public void resumeDownloadFile(ActionEvent event) {
        for (var property : getSelectedItems()){
            var item = findByProperty.apply(property);
            if (item == null) {
                return;
            }
            downloadService.moveToDownloadList(item);
        }
    }

    @FXML
    public void pauseAllSelected(ActionEvent event) {

    }

    @FXML
    public void pauseAllDownload(ActionEvent event) {

    }

    @FXML
    public void deleteSelectedDownload(ActionEvent event) {

    }

    @FXML
    public void deleteAllComplete(ActionEvent event) {

    }

    @FXML
    public void showQueueManger(ActionEvent event) {

    }

    @FXML
    public void showMinimalView(ActionEvent event) {

    }

    @FXML
    public void showAboutWindows(ActionEvent event) {

    }

    @FXML
    public void showOptionsWindows(ActionEvent event) {

    }

    @FXML
    public void addNewQueue(ActionEvent event) {

    }

    @FXML
    public void addGoogleDriveLink(ActionEvent event) {

    }

    @FXML
    public void minimizeProgram(ActionEvent event) {
        stage.setIconified(true);
    }

    @FXML
    public void closeProgram(ActionEvent event) {
        stage.close();
        exitProgram(event);
    }

    @FXML
    public void exitProgram(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    /*** Context Menu ***/

    @FXML
    public void copyURL(ActionEvent event) {

    }

    @FXML
    public void pauseDownload(ActionEvent event) {

    }

    @FXML
    public void openFile(ActionEvent event) {

    }

    @FXML
    public void openFolder(ActionEvent event) {

    }

    @FXML
    public void deleteFile(ActionEvent event) {

    }

    @FXML
    public void showProperties(ActionEvent event) {

    }

}
