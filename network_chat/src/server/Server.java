package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> clients;
    public ArrayList<String> nickArray = new ArrayList<>();

    public Server() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;

        try {
            AuthService.connect();
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен!");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился!");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
        nickArray.add(client.getNickName());
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
        nickArray.remove(client.getNickName());
    }

    public boolean checkAuthDuplicate(String nickName) {
        return nickArray.contains(nickName);
    }

    public void sendBroadcastMsg(String msg) {
        for (ClientHandler ch: clients) {
            ch.sendMessage(msg);
        }
    }

    public void sendPrivateMsg(String myName, String nickName, String msg) {
        for (ClientHandler ch: clients) {
            if (ch.getNickName().equalsIgnoreCase(nickName)) {
                ch.sendMessage(myName + ": " + msg);
            }
        }
    }
}
