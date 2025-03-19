package LocationServer;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import LocationServer.ProxyHandler.ProxyHandlerImp;
import shared.ProxyHandlerInterface;


class LocationServer {
    private static final int PORT = 5000;
    private static final int RMI_PORT = 1099;
    private static final String RMI_NAME = "ProxyHandler";

    private static ProxyHandlerInterface proxyHandler;

    public static void main(String[] args) {
        try {
            // Start the RMI service
            proxyHandler = new ProxyHandlerImp();
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            registry.rebind(RMI_NAME, proxyHandler);

            System.out.println("RMI Service registered on port " + RMI_PORT + "\n");
            
            // Start the socket server
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
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
                System.out.println("Location server up and running!\n");
    
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("\n----------------------------------------------------------");
                    System.out.println("Client connected from " + clientSocket.getInetAddress().getHostAddress());
    
                    Thread clientThread = new Thread(new ClientHandler(clientSocket, proxyHandler));
                    clientThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


// Handles a single client connection.
class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ProxyHandlerInterface proxyHandler;

    public ClientHandler(Socket clientSocket, ProxyHandlerInterface proxyHandler) {
        this.clientSocket = clientSocket;
        this.proxyHandler = proxyHandler;
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
                out.writeUTF(proxyHandler.getNextProxyAddress());
                out.writeInt(proxyHandler.getNextProxyPort());
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