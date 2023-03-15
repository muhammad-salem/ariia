package org.ariia.javafx.controllers;

import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MovingStage {

    public static void move(Stage stage, AnchorPane pane) {
        new MovingStage(stage, pane);
    }

    private boolean moving;
    private double x, y;

    /***
     * create a new Stage and its AnchorPane
     * then assigns them to given stage and pane
     *
     * @param stage
     * @param pane
     */
    public MovingStage(Stage stage, AnchorPane pane) {
        moveWithMouse(stage, pane);
//        pane.heightProperty().addListener((observable, oldValue, newValue) -> stage.setHeight(pane.getHeight()));
//        pane.widthProperty().addListener((observable, oldValue, newValue) -> stage.setWidth(pane.getWidth()));
    }

    /**
     * move stage over drag mouse
     *
     * @param stage which will move.
     * @param pane  the root parent that will drag.
     */
    public void moveWithMouse(final Stage stage, Pane pane) {

        // moving = false;
        pane.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                moving = true;
                x = event.getSceneX();
                y = event.getSceneY();
            }
        });
        pane.setOnMouseDragged(event -> {
            if (moving) {
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
                pane.setCursor(Cursor.HAND);
            }
        });

        pane.setOnMouseReleased(event -> {
            moving = false;
            pane.setCursor(Cursor.DEFAULT);
        });

    }
}
