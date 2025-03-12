package ProxyServer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import ProxyServer.model.entities.Usuario;
import shared.Message;
import ProxyServer.model.entities.OrdemServico;
import ProxyServer.model.DAO.UsuarioDAO;
import ProxyServer.model.DAO.LogDAO;
import ProxyServer.model.DAO.OrdemServicoCacheDAO;


public class ProxyServer {
    private static final int PORT = 5001;

    protected static OrdemServicoCacheDAO cacheDAO = new OrdemServicoCacheDAO();
    protected static UsuarioDAO usuarioDAO = new UsuarioDAO();


    public static void main(String[] args) {
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

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("\nServidor Proxy rodando na porta " + PORT);

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
        private boolean conexao = true;

        public ProxyHandler(Socket socket) {
            System.out.println("\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Hello, " + socket.getInetAddress().getHostAddress());
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            LogDAO.addLog("[CONNECTION] Cliente conectado de " + socket.getInetAddress().getHostAddress());
            this.socket = socket;
        }
        

        @Override
        public void run() {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());) {
                while (conexao) {
                    Message message = (Message) objectInputStream.readObject();
                    String instrucao = message.getInstrucao();

                    System.out.println("\n----------------------------------------------------------");
                    System.out.println("\"" + instrucao + "\", said " + socket.getInetAddress().getHostAddress());
                    System.out.println("----------------------------------------------------------");
                    LogDAO.addLog("[MESSAGE] " + socket.getInetAddress().getHostAddress() + " requisitou " + instrucao);

                    if (instrucao.equals("SELECT")) {
                        // Seleciona todas as ordens de serviço
                        OrdemServico ordem = cacheDAO.getOrdemServico(message.getCodigo());

                        Message reply;
                        if (ordem == null) {
                            reply = new Message("NOTFOUND");
                        } else {
                            reply = new Message(ordem, "REPLY");
                        }

                        objectOutputStream.writeObject(reply);

                    } else if (instrucao.equals("SELECTBYUSER")) {
                        // Seleciona todas as ordens de serviço de um usuário
                        OrdemServico[] ordens = cacheDAO.getOrdensByUsuario(message.getUsuario().getCpf());
                        Message reply = new Message(ordens, "REPLY");
                        objectOutputStream.writeObject(reply);

                    } else if (instrucao.equals("SELECTALL")) {
                        OrdemServico[] ordens = cacheDAO.getAllOrdens();
                        Message reply = new Message(ordens, "REPLY");
                        objectOutputStream.writeObject(reply);

                    } else if (instrucao.equals("INSERT")) {
                        // Insere ordem de serviço no cache
                        OrdemServico newOrdem = message.getOrdem();
                        // Update the code of the new order
                        newOrdem.setCodigo(cacheDAO.getAllOrdens().length + 1);

                        cacheDAO.addOrdemServico(message.getOrdem());

                    } else if (instrucao.equals("UPDATE")) {
                        // Atualiza ordem de serviço no cache
                        cacheDAO.updateOrdemServico(message.getOrdem());

                    } else if (instrucao.equals("DELETE")) {
                        // Deleta ordem de serviço do cache
                        cacheDAO.deleteOrdemServico(message.getCodigo());

                    } else if (instrucao.equals("LOGIN")) {
                        // Login do usuário
                        try{
                            Usuario usuario = usuarioDAO.getUsuario(message.getCPF());
                            Message reply = new Message(usuario, "REPLY");
                            objectOutputStream.writeObject(reply);
                        } catch (Exception e) {
                            Message reply = new Message("REPLY");
                            objectOutputStream.writeObject(reply);
                        }

                    } else if (instrucao.equals("INSERTUSER")) {
                        // Insere usuário no banco de dados
                        usuarioDAO.addUsuario(message.getUsuario());

                    } else if (instrucao.equals("CLOSE")){
                        conexao = false;
                        System.out.println("\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                        System.out.println("Bye bye, " + socket.getInetAddress().getHostAddress());
                        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                        LogDAO.addLog("[CONNECTION] Cliente desconectado: " + socket.getInetAddress().getHostAddress());
                    }
                }

            } catch (EOFException e) {
                // Client disconnected
                System.out.println("\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                System.out.println("Bye bye, " + socket.getInetAddress().getHostAddress());
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                LogDAO.addLog("[CONNECTION] Cliente desconectado: " + socket.getInetAddress().getHostAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
