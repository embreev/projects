package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    Socket socket = null;
    DataInputStream in;
    DataOutputStream out;
    Server server;

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
                        sendMessage("Connect to server successfully!");
                        while (true){
                            String str = in.readUTF();
                            if (str.equals("/end")) {
                                out.writeUTF("/serverClosed");
                                break;
                            }
                            sendMessage(str);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
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
