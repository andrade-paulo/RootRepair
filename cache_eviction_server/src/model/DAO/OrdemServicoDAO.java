package model.DAO;

import client.Client;
import model.entities.OrdemServico;
import model.entities.Usuario;
import server.Message;
import server.Server;

public class OrdemServicoDAO {
    private int ocupacao;
    private Server server;
    private Client client;

    public OrdemServicoDAO() {
        server = new Server();
        client = new Client();

        if (server.isEmpty()) {
            OrdemServico.setContador(0);
            ocupacao = 0;
        } else {
            OrdemServico.setContador(server.getUltimo().getCodigo());
            ocupacao = server.getOcupacao();
        }
    }


    public void addOrdemServico(OrdemServico ordemServico) throws Exception {
        Message mensagem = new Message(ordemServico, "INSERT");
        server.requestByOrdem(mensagem);

        ocupacao++;
    }


    public OrdemServico getOrdemServico(int codigo) throws Exception {
        // Não há necessidade de consultar o arquivo, pois sempre vai estar atualizado
        OrdemServico ordemServico = client.getOrdemServico(codigo);

        if (ordemServico == null) {
            // Dispara mensagem de request ao server
            Message request = new Message(codigo, "SELECT");
            ordemServico = server.requestByCodigo(request);

            if (ordemServico == null) {
                throw new Exception("Ordem de Serviço não encontrada");
            }

            // Dispara mensagem de reply ao client
            Message reply = new Message(ordemServico, "INSERT");
            client.replyByOrdem(reply);
        } else {
            return ordemServico;
        }
        
        return ordemServico;
    }


    public void removerOrdemServico(int codigo) throws Exception {
        OrdemServico ordemRemovida;

        try {
            Message request = new Message(codigo, "DELETE");
            ordemRemovida = server.requestByCodigo(request);
        } catch (Exception e) {
            throw new Exception("Ordem de Serviço não encontrada");
        }
        
        ocupacao--;
        
        // Remove from cache
        try{
            Message reply = new Message(ordemRemovida, "DELETE");
            client.replyByOrdem(reply);
        } catch (Exception e) {
            LogDAO.addLog("[CACHE DELETE] Ordem de Serviço " + codigo + " não encontrada");
        }
    }


    public void updateOrdemServico(OrdemServico ordemServico) throws Exception {
        try {
            Message request = new Message(ordemServico, "UPDATE");
            server.requestByOrdem(request);
        } catch (Exception e) {
            throw new Exception("Ordem de Serviço não encontrada");
        }

        try {
            Message reply = new Message(ordemServico, "UPDATE");
            client.replyByOrdem(reply);
        } catch (Exception e) {
            LogDAO.addLog("[CACHE UPDATE] Ordem de Serviço " + ordemServico.getCodigo() + " não encontrada");
        }
    }


    public void clearCache() {
        client.clearCache();
    }


    public void listarTodosOS() {
        server.selectAll();
    }


    public void listarOS(Usuario usuario) {
        server.listarOS(usuario);
    }


    public int getOcupacao() {
        return ocupacao;
    }
}
