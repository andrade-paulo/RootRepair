package ApplicationServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;

import ApplicationServer.model.DAO.OrdemServicoDAO;
import ApplicationServer.model.DAO.LogDAO;
import shared.Message;
import shared.entities.OrdemServico;

public class ApplicationServer {
    private static int port = 6000;

    protected static OrdemServicoDAO ordemServicoDAO = new OrdemServicoDAO();
    protected static LogDAO logDAO = new LogDAO();
    
    public static void main(String[] args) {
        System.out.println("\r\n" + //
                            "===========================================================\r\n" + //
                            "   _____             _     _____                  _      \r\n" + //
                            "  |  __ \\           | |   |  __ \\                (_)     \r\n" + //
                            "  | |__) |___   ___ | |_  | |__) |___ _ __   __ _ _ _ __ \r\n" + //
                            "  |  _  // _ \\ / _ \\| __| |  _  // _ \\ '_ \\ / _ | | '__|\r\n" + //
                            "  | | \\ \\ (_) | (_) | |_  | | \\ \\  __/ |_) | (_| | | |   \r\n" + //
                            "  |_|  \\_\\___/ \\___/ \\__| |_|  \\_\\___| .__/ \\__,_|_|_|   \r\n" + //
                            "                                     | |                 \r\n" + //
                            "                                     |_|                 \r\n" + //
                            "           _               ___                      \r\n" + //
                            "          /_\\  _ __ _ __  / __| ___ _ ___ _____ _ _ \r\n" + //
                            "         / _ \\| '_ \\ '_ \\ \\__ \\/ -_) '_\\ V / -_) '_|\r\n" + //
                            "        /_/ \\_\\ .__/ .__/ |___/\\___|_|  \\_/\\___|_|  \r\n" + //
                            "              |_|  |_|                              \r\n" + //
                            "===========================================================\r\n");

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("\nServidor de Aplicação rodando na porta " + port);

            while (true) {
                Socket socket = serverSocket.accept(); // Aguarda conexão do proxy server
                new Thread(new ApplicationHandler(socket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ApplicationHandler implements Runnable {
        private Socket socket;
        private boolean conexao = true;

        public ApplicationHandler(Socket socket) {
            System.out.println("\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Hello, " + socket.getInetAddress().getHostAddress());
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            this.socket = socket;
            
            LogDAO.addLog("[CONNECTION] Conexão estabelecida com " + socket.getInetAddress().getHostAddress());
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                while (conexao) {
                    Message message = (Message) in.readObject();
                    String instrucao = message.getInstrucao();

                    LogDAO.addLog("[MESSAGE] " + socket.getInetAddress().getHostAddress() + " requisitou " + instrucao);

                    try {
                        switch (instrucao) {
                            case "INSERT":
                                handleInsert(message, out);
                                break;
                            case "UPDATE":
                                handleUpdate(message, out);
                                break;
                            case "DELETE":
                                handleDelete(message, out);
                                break;
                            case "SELECT":
                                handleSelect(message, out);
                                break;
                            case "SELECTALL":
                                handleSelectAll(out);
                                break;
                            case "SELECTBYUSER":
                                handleSelectByUser(message, out);
                                break;
                            default:
                                System.out.println("Instrução desconhecida: " + instrucao);
                                break;
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Conexão encerrada");
                LogDAO.addLog("[CONNECTION] Conexão encerrada com " + socket.getInetAddress().getHostAddress());
            }
        }

        private void handleInsert(Message message, ObjectOutputStream out) throws IOException, ParseException {
            Message reply;
            try{
                ordemServicoDAO.addOrdemServico(message.getOrdem());
                reply = new Message("SUCCESS");
            } catch (Exception e) {
                reply = new Message("ERROR");
            }

            out.writeObject(reply);
            out.flush();
        }

        private void handleUpdate(Message message, ObjectOutputStream out) throws IOException, ParseException {
            if (ordemServicoDAO.updateOrdemServico(message.getOrdem())) {
                Message reply = new Message("REPLY");
                out.writeObject(reply);
                out.flush();
            } else {
                Message reply = new Message("NOTFOUND");
                out.writeObject(reply);
                out.flush();
            }
        }

        private void handleDelete(Message message, ObjectOutputStream out) throws IOException {
            OrdemServico ordem = ordemServicoDAO.deleteOrdemServico(message.getCodigo());

            if (ordem == null) {
                Message reply = new Message("NOTFOUND");
                out.writeObject(reply);
                out.flush();
            } else {
                Message reply = new Message(ordem, "REPLYWITHORDER");
                out.writeObject(reply);
                out.flush();
            }
        }

        private void handleSelect(Message message, ObjectOutputStream out) throws IOException {
            OrdemServico ordem = ordemServicoDAO.getOrdemServico(message.getCodigo());
            if (ordem == null) {
                Message reply = new Message("NOTFOUND");
                System.out.println("Ordem não encontrada");
                out.writeObject(reply);
                out.flush();
            } else {
                Message reply = new Message(ordem, "REPLYWITHORDER");
                out.writeObject(reply);
                out.flush();
            }
        }

        private void handleSelectAll(ObjectOutputStream out) throws IOException {
            OrdemServico[] ordens = ordemServicoDAO.selectAll();

            Message reply = new Message(ordens, "REPLY");
            out.writeObject(reply);
            out.flush();
        }

        private void handleSelectByUser(Message message, ObjectOutputStream out) throws IOException {
            OrdemServico[] ordens = ordemServicoDAO.listarOS(message.getUsuario());
            Message reply = new Message(ordens, "REPLY");
            out.writeObject(reply);
            out.flush();
        }
    }
}