import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import shared.Message;
import modelo.entities.*;

public class Controller {
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    
    public Controller() {}

    public static void setProxyServer(ObjectOutputStream proxyOut, ObjectInputStream proxyIn) {
        Controller.out = proxyOut;
        Controller.in = proxyIn;
    }

    // Usuários
    public static void addUsuario(Usuario usuario) throws Exception {
        Message request = new Message(usuario, "INSERTUSER");
        out.writeObject(request);
        out.flush();

        Message response = (Message) in.readObject();
        if (response.getInstrucao().equals("ERROR")) {
            throw new Exception("Erro ao adicionar Usuario");
        }
    }

    public static Usuario getUsuario(String cpf) throws Exception {
        Message request = new Message(cpf, "LOGIN");
        out.writeObject(request);
        out.flush();

        Message response = (Message) in.readObject();
        if (response.getInstrucao().equals("NOTFOUND")) {
            throw new Exception("Usuario nao encontrado");
        }

        return response.getUsuario();
    }


    public static void updateUsuario(Usuario usuario) throws Exception {
        Message request = new Message(usuario, "UPDATE");
        out.writeObject(request);
        out.flush();

        Message response = (Message) in.readObject();
        if (response.getInstrucao().equals("NOTFOUND")) {
            throw new Exception("Usuario nao encontrado");
        }
    }


    // Ordens de Serviço
    public static void addOrdemServico(OrdemServico ordem) throws Exception {
        Message request = new Message(ordem, "INSERT");
        out.writeObject(request);
        out.flush();

        Message response = (Message) in.readObject();
        if (response.getInstrucao().equals("ERROR")) {
            throw new Exception("Erro ao adicionar Ordem de Servico");
        }
    }


    public static OrdemServico getOrdemServico(int codigo) throws Exception {
        Message request = new Message(codigo, "SELECT");
        out.writeObject(request);
        out.flush();
        
        Message response = (Message) in.readObject();
        if (response.getInstrucao().equals("NOTFOUND")) {
            throw new Exception("Ordem de Servico nao encontrada");
        }

        return response.getOrdem();
    }


    public static void removerOrdemServico(int codigo) throws Exception {
        Message request = new Message(codigo, "DELETE");
        out.writeObject(request);
        out.flush();

        Message response = (Message) in.readObject();

        if (response.getInstrucao().equals("NOTFOUND")) {
            throw new Exception("Ordem de Servico nao encontrada");
        }
    }


    public static void updateOrdemServico(OrdemServico ordem) throws Exception {
        Message request = new Message(ordem, "UPDATE");
        out.writeObject(request);
        out.flush();

        Message response = (Message) in.readObject();

        if (response.getInstrucao().equals("NOTFOUND")) {
            throw new Exception("Ordem de Servico nao encontrada");
        }
    }


    public static OrdemServico[] getOrdensByUsuario(Usuario usuario) throws Exception {
        Message request = new Message(usuario, "SELECTBYUSER");
        out.writeObject(request);
        out.flush();

        Message response = (Message) in.readObject();
        return response.getOrdensServicos();
    }


    public static OrdemServico[] getAllOrdemServicos() throws Exception {
        Message request = new Message("SELECTALL");
        out.writeObject(request);
        out.flush();
        
        Message response = (Message) in.readObject();
        return response.getOrdensServicos();
    }
}
