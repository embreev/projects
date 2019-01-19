package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public Server() {
        ServerSocket server = null;
        Socket socket = null;

        try {
            server = new ServerSocket(8189);
            System.out.println("Server is running!");

            while (true) {
                socket = server.accept();
                System.out.println("Client connected!");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.out.println("Server error!");
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
}
