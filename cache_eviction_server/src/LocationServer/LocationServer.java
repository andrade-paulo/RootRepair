import java.io.PrintWriter;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;


public class LocationServer {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Servidor de localização iniciado na porta " + PORT);

            while (true) {
                Socket client_socket = server.accept();
                System.out.println("New client conected: " + client_socket.getInetAddress().getHostAddress());

                PrintWriter out = new PrintWriter(client_socket.getOutputStream(), true);

                out.println("localhost:5001");
                
                client_socket.close();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
