package org.ariia.javafx.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.ariia.core.api.service.DownloadService;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public MenuBar menu;

    @FXML
    public HBox hBoxHeader, hBoxFind;

    @FXML
    public ListView<AnchorPane> list;

    @FXML
    public TextField findField;

    @FXML
    public Label labelMessages;

    @FXML
    public ContextMenu contextOpt, contextMenuTree;

    @FXML
    public Button btnOpt, deleteButton, deleteCompButton;

    @FXML
    public ImageView findIcon;

    @FXML
    public AnchorPane anchorRoot;

    private final Stage stage;
    private final DownloadService downloadService;

    public MainController(Stage stage, DownloadService downloadService) {
        this.stage = stage;
        this.downloadService = downloadService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
    public void addNewDownload(ActionEvent event) {

    }

    @FXML
    public void startDownload(ActionEvent event) {
        resumeDownloadFile(event);
    }

    @FXML
    public void resumeDownloadFile(ActionEvent event) {

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
    public void addLink(ActionEvent event) {

    }

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
