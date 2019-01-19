package client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private Date date;
    private Label label;

    @FXML
    TextArea msg;

    @FXML
    TextField textField;

    @FXML
    Button button;

    @FXML
    ScrollPane sp;

    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            if (str.equalsIgnoreCase("closeChat")) break;
                            msg.appendText(str + "\n");
                            System.out.println(str);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage() {
        if (!textField.getText().trim().isEmpty()) {
            try {
                out.writeUTF(textField.getText());
                textField.clear();
                textField.requestFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}