package LocationServer;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.ServerSocket;
import java.net.Socket;



// Accepts connections from clients and sends them the location of the server. Use threads to handle multiple clients.
class LocationServer {
    private static final int PORT = 5000;

    public static final String proxyAdress = "localhost";
    public static final int proxyPort = 5001;


    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT);) {
            System.out.println("\r\n" + //
                                "============================================================\r\n" + //
                                "  _____             _     _____                  _      \r\n" + //
                                " |  __ \\           | |   |  __ \\                (_)     \r\n" + //
                                " | |__) |___   ___ | |_  | |__) |___ _ __   __ _ _ _ __ \r\n" + //
                                " |  _  // _ \\ / _ \\| __| |  _  // _ \\ '_ \\ / _` | | '__|\r\n" + //
                                " | | \\ \\ (_) | (_) | |_  | | \\ \\  __/ |_) | (_| | | |   \r\n" + //
                                " |_|  \\_\\___/ \\___/ \\__| |_|  \\_\\___| .__/ \\__,_|_|_|   \r\n" + //
                                "                                    | |                 \r\n" + //
                                "                                    |_|                 \r\n" + //
                                "============================================================\r\n" + //
                                "\n");

            
            System.out.println("Location Server started on port " + PORT + "\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("----------------------------------------------------------");
                System.out.println("Client connected from " + clientSocket.getInetAddress().getHostAddress());

                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


// Handles a single client connection.
class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            
            // Validate the key sent by the client
            String message = in.readUTF();
            if (message.equals("eW91IHNoYWxsIG5vdCBwYXNz")) {
                System.out.println("   -> Client's key is valid. Sending proxy location...");

                // Send the location of the server to the client
                out.writeUTF(LocationServer.proxyAdress);
                out.writeInt(LocationServer.proxyPort);
            } else {
                System.out.println("WARNING! Client's key is not recognized: " + message);
            }
            
            clientSocket.close();
            System.out.println("----------------------------------------------------------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
