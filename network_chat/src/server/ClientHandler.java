package server;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class ClientHandler {

    private Socket socket = null;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private String nickName;
    private String tmpNick;

    public String getNickName() {
        return nickName;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();

                            if (str.startsWith("/auth")) {
                                String[] tokens = str.split(" ");
                                tmpNick = AuthService.getNickLoginAndPass(tokens[1], tokens[2]);

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
                                    sendMessage("Пользователь уже авторизовался, попробуйте другой логин");
                                }
                            }
                        }

                        while (true) {
                            String str = in.readUTF();
                            String[] tmpStr = str.split(" ");
                            if(tmpStr[1].equals("/end")) {
                                out.writeUTF("/serverClosed");
                                server.unsubscribe(ClientHandler.this);
                                break;
                            }
                            if (tmpStr[1].equals("/w")) {
                                String msg = str.substring(str.indexOf("/") + 4 + tmpStr[2].toCharArray().length);
                                server.sendPrivateMsg(nickName, tmpStr[2], msg);
                            } else {
                                server.sendBroadcastMsg(str);
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