package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

class Server {
    private Vector<ClientHandler> clients;

    Server() {
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

    void subscribe(ClientHandler client) {
        clients.add(client);
    }

    void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    boolean checkAuthDuplicate(String nickName) {
        for (ClientHandler ch: clients) {
            if (ch.getNickName().equalsIgnoreCase(nickName)) {
                return true;
            }
        }
        return false;
    }

    boolean checkBlackList(ClientHandler ch, String nickName) {
        return BlackListService.checkUserOnBlackList(ch, nickName) == null ?  false : true;
    }

    void sendBroadcastMsg(ClientHandler clientHandler, String msg) {
        for (ClientHandler ch: clients) {
            if (checkBlackList(ch, clientHandler.getNickName())) {
                ch.sendMessage(msg);
            }
        }
    }

    void sendPrivateMsg(ClientHandler clientHandler, String nickName, String msg) {
        String myName = clientHandler.getNickName();
        for (ClientHandler ch: clients) {
            if (ch.getNickName().equalsIgnoreCase(nickName)) {
                ch.sendMessage(myName + ": " + msg);
                clientHandler.sendMessage(myName + ": " + msg);
                return;
            }
        }
        clientHandler.sendMessage("Клиент с ником: " + nickName + " не найден!");
    }
}
