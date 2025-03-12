package LocationServer;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


class LocationServer {
    private static final int PORT = 5000;

    private static String proxyAdress;
    private static int proxyPort;


    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT);) {
            System.out.println("\r\n" + //
                                "============================================================\r\n" + //
                                "   _____             _     _____                  _      \r\n" + //
                                "  |  __ \\           | |   |  __ \\                (_)     \r\n" + //
                                "  | |__) |___   ___ | |_  | |__) |___ _ __   __ _ _ _ __ \r\n" + //
                                "  |  _  // _ \\ / _ \\| __| |  _  // _ \\ '_ \\ / _` | | '__|\r\n" + //
                                "  | | \\ \\ (_) | (_) | |_  | | \\ \\  __/ |_) | (_| | | |   \r\n" + //
                                "  |_|  \\_\\___/ \\___/ \\__| |_|  \\_\\___| .__/ \\__,_|_|_|   \r\n" + //
                                "                                     | |                 \r\n" + //
                                "                                     |_|                 \r\n" + //
                                "============================================================\r\n" + //
                                "\n");

            
            System.out.println("Location Server started on port " + PORT + "\n");

            System.out.println("-=- Proxy server configuration -=-");
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the proxy server address: ");
            proxyAdress = scanner.nextLine();
            System.out.print("Enter the proxy server port: ");
            proxyPort = scanner.nextInt();
            scanner.close();

            System.out.println("\nLocation server up and running!\n");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n----------------------------------------------------------");
                System.out.println("Client connected from " + clientSocket.getInetAddress().getHostAddress());

                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getProxyAdress() {
        return proxyAdress;
    }

    public static int getProxyPort() {
        return proxyPort;
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
                out.writeUTF(LocationServer.getProxyAdress());
                out.writeInt(LocationServer.getProxyPort());
            } else {
                System.out.println("   -> WARNING! Client's key is not recognized: " + message);
            }
            
            clientSocket.close();
            System.out.println("----------------------------------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
