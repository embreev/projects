package client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Controller {
    @FXML
    TextArea msg;

    @FXML
    TextField textField;

    @FXML
    Button button;

    @FXML
    ScrollPane sp;

    @FXML
    HBox mainPanel;

    @FXML
    HBox authPanel;

    @FXML
    TextField aLoginField;

    @FXML
    PasswordField aPasswordField;

    @FXML
    HBox regPanel;

    @FXML
    TextField rLoginField;

    @FXML
    PasswordField rPasswordField;

    @FXML
    TextField rNickNameField;

    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean isAuthorized;
    private String nickName;

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
        if (!isAuthorized) {
            authPanel.setVisible(true);
            authPanel.setManaged(true);
            regPanel.setVisible(true);
            regPanel.setManaged(true);
            mainPanel.setVisible(false);
            mainPanel.setManaged(false);
        } else {
            authPanel.setVisible(false);
            authPanel.setManaged(false);
            regPanel.setVisible(false);
            regPanel.setManaged(false);
            mainPanel.setVisible(true);
            mainPanel.setManaged(true);
        }
    }

    public void connect() {
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
                            String[] token = str.split(" ");
                            if (str.startsWith("/regok")) {
                                msg.appendText("Пользователь с логином " + token[1] + "\n" +
                                        "успешно зарегистрирован!" + "\n");
                            }
                            if (str.startsWith("/authok")) {
                                setAuthorized(true);
                                msg.appendText("Пользователь " + token[1] + " успешно авторизовался!" + "\n");
                                break;
                            } else {
                                msg.appendText(str + "\n");
                            }
                        }

                        while (true) {
                            String str = in.readUTF();
                            if (str.equals("/serverClosed")) break;
                            msg.appendText(str + "\n");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setAuthorized(false);
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        String msg = textField.getText();
        if (!msg.trim().isEmpty()) {
            try {
                out.writeUTF(nickName + ": " + msg);
                textField.clear();
                textField.requestFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void auth() {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            if (!aLoginField.getText().isEmpty() && !aPasswordField.getText().isEmpty()) {
                out.writeUTF("/auth " + aLoginField.getText() + " " + aPasswordField.getText());
                aLoginField.clear();
                aPasswordField.clear();
            } else {
                out.writeUTF("Заполните все поля!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reg() {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            if (!rLoginField.getText().isEmpty() && !rPasswordField.getText().isEmpty() &&
                    !rNickNameField.getText().isEmpty()) {
                out.writeUTF("/reg " + rLoginField.getText() + " " + rPasswordField.getText() + " " +
                        rNickNameField.getText());
                rLoginField.clear();
                rPasswordField.clear();
                rNickNameField.clear();
            } else {
                out.writeUTF("Заполните все поля!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}