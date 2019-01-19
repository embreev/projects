package client;

import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Date;

public class Controller {
    static int count = 0;

    @FXML
    VBox msg;

    @FXML
    TextField textField;

    @FXML
    Button button;

    @FXML
    ScrollPane sp;

    @FXML
    public void sendMessage() {
        Date date = new Date();
        Label label = new Label();
        count++;
        sp.vvalueProperty().bind(msg.heightProperty());
        if (!textField.getText().isEmpty()) {
            if (count % 2 == 0) {
                label.setText(date + ": " + textField.getText() + "\n");
                label.setStyle("-fx-text-fill: green;");
                label.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                msg.getChildren().add(label);
            } else {
                label.setText(date + ": " + textField.getText() + "\n");
                label.setStyle("-fx-text-fill: red;");
                label.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                msg.getChildren().add(label);
            }
        }
        textField.clear();
        textField.requestFocus();
    }
}
