package ProxyServer;

import datastructures.CacheHeap;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import DAO.UsuarioDAO;

public class ProxyServer {
    private static final int PORT = 5001;
    private static final int MAX_CACHE_SIZE = 30;

    private CacheHeap cache;
    private UsuarioDAO usuarioDAO;


    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor Proxy rodando na porta " + PORT);

            while (true) {
                Socket socket = serverSocket.accept(); // Aguarda conexão de clientes
                new Thread(new ProxyHandler(socket)).start(); // Inicia nova thread para cada cliente
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class ProxyHandler implements Runnable {
        private Socket socket;

        public ProxyHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Lê a requisição do cliente
                // Verifica se a requisição já está no cache
                // Se estiver, retorna a resposta do cache
                // Se não estiver, faz a requisição ao servidor e armazena no cache
                // Retorna a resposta ao cliente
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
