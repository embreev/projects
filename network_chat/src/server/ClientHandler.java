package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;


public class ClientHandler {

    private Socket socket = null;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private String nickName;
    private String tmpNick;
    private long startTime;
    private long currentTime;
    private long timeStamp;
    private boolean timeout;

    public String getNickName() {
        return nickName;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            startTime = new Date().getTime();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            currentTime = new Date().getTime();
                            timeStamp = currentTime - startTime;
//                            sendMessage(String.valueOf(timeStamp));
                            if (timeStamp > 5000) {
                                timeout = true;
                                break;
                            }
                            String str = in.readUTF();
                            String[] tokens = str.split(" ");
                            if (str.startsWith("/reg")) {
                                String login = tokens[1];
                                String pass = tokens[2];
                                String nickName = tokens[3];
                                AuthService.addUser(login, pass, nickName);
                                sendMessage("/regok " + login + " " + nickName);
                            }
                            if (str.startsWith("/auth")) {
                                tmpNick = AuthService.getNickByLoginAndPass(tokens[1], tokens[2]);
                                if (!server.checkAuthDuplicate(tmpNick)) {
                                    if (tmpNick != null) {
                                        nickName = tmpNick;
                                        sendMessage("/authok " + nickName);
                                        server.subscribe(ClientHandler.this);
                                        break;
                                    } else {
                                        sendMessage("Неверный логин/пароль!");
                                    }
                                } else {
                                    sendMessage("Пользователь уже авторизовался, \n" +
                                            "попробуйте другой логин");
                                }
                            }
                        }

                        while (true) {
                            if (timeout) {
                                out.writeUTF("/serverClosed");
                                server.unsubscribe(ClientHandler.this);
                                break;
                            }
                            String str = in.readUTF();
                            String[] tmpStr = str.split(" ");
                            if (tmpStr[1].startsWith("/")) {
                                if (tmpStr[1].equals("/end")) {
                                    out.writeUTF("/serverClosed");
                                    server.unsubscribe(ClientHandler.this);
                                    break;
                                }
                                if (tmpStr[1].equals("/w")) {
                                    String msg = str.substring(str.indexOf("/") + 4 + tmpStr[2].toCharArray().length);
                                    server.sendPrivateMsg(ClientHandler.this, tmpStr[2], msg);
                                }
                                if (tmpStr[1].equals("/abl")) {
                                    BlackListService.addUserOnBlackList(ClientHandler.this, tmpStr[2]);
                                    sendMessage(tmpStr[2] + " добавлен в ваш блэклист!");
                                }
                                if (tmpStr[1].equals("/rbl")) {
                                    BlackListService.removeUserFromBlackList(ClientHandler.this, tmpStr[2]);
                                    sendMessage(tmpStr[2] + " удален из вашего блэклиста!");
                                }
                            } else {
                                server.sendBroadcastMsg(ClientHandler.this, tmpStr[1]);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                            out.close();
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

    public void sendMessage(String str) {
        if (!str.trim().isEmpty()) {
            try {
                out.writeUTF(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}