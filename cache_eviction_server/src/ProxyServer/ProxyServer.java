package ProxyServer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.Scanner;

import shared.entities.Usuario;
import shared.entities.OrdemServico;
import shared.Message;
import ProxyServer.model.DAO.UsuarioDAO;
import ProxyServer.model.DAO.LogDAO;
import ProxyServer.model.DAO.OrdemServicoCacheDAO;

public class ProxyServer {
    private static final int PORT = 5001;

    protected static OrdemServicoCacheDAO cacheDAO = new OrdemServicoCacheDAO();
    protected static UsuarioDAO usuarioDAO = new UsuarioDAO();

    protected static ObjectOutputStream appOutputStream;
    protected static ObjectInputStream appInputStream;

    public static void main(String[] args) {
        System.out.println("\r\n" + //
                                "============================================================\r\n" + //
                                "   _____             _     _____                  _      \r\n" + //
                                "  |  __ \\           | |   |  __ \\                (_)     \r\n" + //
                                "  | |__) |___   ___ | |_  | |__) |___ _ __   __ _ _ _ __ \r\n" + //
                                "  |  _  // _ \\ / _ \\| __| |  _  // _ \\ '_ \\ / _ | | '__|\r\n" + //
                                "  | | \\ \\ (_) | (_) | |_  | | \\ \\  __/ |_) | (_| | | |   \r\n" + //
                                "  |_|  \\_\\___/ \\___/ \\__| |_|  \\_\\___| .__/ \\__,_|_|_|   \r\n" + //
                                "                                     | |                 \r\n" + //
                                "                                     |_|                 \r\n" + //
                                "      ___                    ___                      \r\n" + //
                                "     | _ \\_ _ _____ ___  _  / __| ___ _ ___ _____ _ _ \r\n" + //
                                "     |  _/ '_/ _ \\ \\ / || | \\__ \\/ -_) '_\\ V / -_) '_|\r\n" + //
                                "     |_| |_| \\___/_\\_\\\\_, | |___/\\___|_|  \\_/\\___|_|  \r\n" + //
                                "                      |__/                            \r\n" + //
                                "============================================================\r\n" + //
                                "\n");

        // Get the application server IP and port
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o IP do servidor de aplicação: ");
        String appIP = scanner.nextLine();

        System.out.print("Digite a porta do servidor de aplicação: ");
        int appPort = scanner.nextInt();

        scanner.close();
        
        try (ServerSocket locationSocket = new ServerSocket(PORT);
             Socket appSocket = new Socket(appIP, appPort);) {
            System.out.println("\nServidor Proxy rodando na porta " + PORT);

            appOutputStream = new ObjectOutputStream(appSocket.getOutputStream());
            appInputStream = new ObjectInputStream(appSocket.getInputStream());

            while (true) {
                Socket socket = locationSocket.accept(); // Aguarda conexão de clientes
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
            try (ObjectInputStream clientInputStream = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream clientOutputStream = new ObjectOutputStream(socket.getOutputStream());) {
                
                while (conexao) {
                    Message message = (Message) clientInputStream.readObject();
                    String instrucao = message.getInstrucao();

                    System.out.println("\n----------------------------------------------------------");
                    System.out.println("\"" + instrucao + "\", said " + socket.getInetAddress().getHostAddress());
                    System.out.println("----------------------------------------------------------");
                    LogDAO.addLog("[MESSAGE] " + socket.getInetAddress().getHostAddress() + " requisitou " + instrucao);

                    switch (instrucao) {
                        case "SELECT":
                            handleSelect(message, clientOutputStream);
                            break;
                        case "SELECTBYUSER":
                        case "SELECTALL":
                            handleSelectByUserOrAll(message, clientOutputStream);
                            break;
                        case "INSERT":
                            handleInsert(message, clientOutputStream);
                            break;
                        case "UPDATE":
                            handleUpdate(message, clientOutputStream);
                            break;
                        case "DELETE":
                            handleDelete(message, clientOutputStream);
                            break;
                        case "LOGIN":
                            handleLogin(message, clientOutputStream);
                            break;
                        case "INSERTUSER":
                            handleInsertUser(message, clientOutputStream);
                            break;
                        case "CLOSE":
                            handleClose();
                            break;
                        default:
                            System.out.println("Instrução desconhecida: " + instrucao);
                            break;
                    }
                }

            } catch (EOFException e) {
                // Client disconnected
                handleClientDisconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleSelect(Message message, ObjectOutputStream clientOutputStream) throws IOException, ClassNotFoundException, ParseException {
            OrdemServico ordem = cacheDAO.getOrdemServico(message.getCodigo());

            Message reply;
            if (ordem != null) {
                // Cache hit
                reply = new Message(ordem, "REPLY");
            } else {
                // Cache miss, search application server
                appOutputStream.writeObject(message);
                Message replyFromApp = (Message) appInputStream.readObject();

                if (replyFromApp.getInstrucao().equals("NOTFOUND")) {
                    reply = new Message("NOTFOUND");
                } else {
                    reply = new Message(replyFromApp.getOrdem(), "REPLY");
                    cacheDAO.addOrdemServico(replyFromApp.getOrdem());
                }

                appOutputStream.flush();
            }

            clientOutputStream.writeObject(reply);
            clientOutputStream.flush();
        }

        private void handleSelectByUserOrAll(Message message, ObjectOutputStream clientOutputStream) throws IOException, ClassNotFoundException, ParseException {
            appOutputStream.writeObject(message);
            Message reply = (Message) appInputStream.readObject();

            clientOutputStream.writeObject(reply);
            clientOutputStream.flush();
        }

        private void handleInsert(Message message, ObjectOutputStream clientOutputStream) throws IOException, ClassNotFoundException {
            appOutputStream.writeObject(message);

            // Send confirmation to the client
            Message appReply = (Message) appInputStream.readObject();
            clientOutputStream.writeObject(appReply);
            clientOutputStream.flush();
        }

        private void handleUpdate(Message message, ObjectOutputStream clientOutputStream) throws IOException, ClassNotFoundException, ParseException {
            appOutputStream.writeObject(message);
            Message appReply = (Message) appInputStream.readObject();

            Message reply;
            if (appReply.getInstrucao().equals("NOTFOUND")) {
                reply = new Message("NOTFOUND");
            } else {
                reply = new Message("REPLY");
                cacheDAO.updateOrdemServico(message.getOrdem());
            }
            
            clientOutputStream.writeObject(reply);
            clientOutputStream.flush();
        }

        private void handleDelete(Message message, ObjectOutputStream clientOutputStream) throws IOException, ClassNotFoundException, ParseException {
            appOutputStream.writeObject(message);
            Message appReply = (Message) appInputStream.readObject();

            Message reply;
            if (appReply.getInstrucao().equals("NOTFOUND")) {
                reply = new Message("NOTFOUND");
            } else {
                reply = new Message(appReply.getOrdem(), "REPLY");
                cacheDAO.deleteOrdemServico(message.getCodigo());
            }
            
            clientOutputStream.writeObject(reply);
            clientOutputStream.flush();
        }

        private void handleLogin(Message message, ObjectOutputStream clientOutputStream) throws IOException {
            Message reply;
            try {
                Usuario usuario = usuarioDAO.getUsuario(message.getCPF());
                reply = new Message(usuario, "REPLY");
            } catch (Exception e) {
                reply = new Message("NOUFOUND");
            }
            
            clientOutputStream.writeObject(reply);
            clientOutputStream.flush();
        }

        private void handleInsertUser(Message message, ObjectOutputStream clientOutputStream) throws IOException {
            Message reply;

            try {
                usuarioDAO.addUsuario(message.getUsuario());
                reply = new Message("SUCCESS");
            } catch (Exception e) {
                reply = new Message("ERROR");
            }

            clientOutputStream.writeObject(reply);
            clientOutputStream.flush();            
        }

        private void handleClose() {
            conexao = false;
            System.out.println("\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Bye bye, " + socket.getInetAddress().getHostAddress());
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            LogDAO.addLog("[CONNECTION] Cliente desconectado: " + socket.getInetAddress().getHostAddress());
        }

        private void handleClientDisconnect() {
            System.out.println("\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Bye bye, " + socket.getInetAddress().getHostAddress());
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            LogDAO.addLog("[CONNECTION] Cliente desconectado: " + socket.getInetAddress().getHostAddress());
        }
    }
}