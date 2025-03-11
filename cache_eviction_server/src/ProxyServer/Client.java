import java.text.ParseException;

import datastructures.CacheHeap;
import model.entities.OrdemServico;
import model.Message;
import model.DAO.LogDAO;

public class Client {
    // Same as server, but it is responsible for the cache
    CacheHeap cache;
    final int CACHE_SIZE = 30;


    public Client() {
        cache = new CacheHeap(CACHE_SIZE);
    }


    public void replyByOrdem(Message mensagemComprimida) throws Exception {
        OrdemServico ordem = mensagemComprimida.getOrdem();
        String instrucao = mensagemComprimida.getInstrucao();

        if (instrucao.equals("INSERT")) {
            addOrdemServico(ordem);
        } else if (instrucao.equals("UPDATE")) {
            updateOrdemServico(ordem);
        } else if (instrucao.equals("DELETE")) {
            deleteOrdemServico(ordem.getCodigo());
        } else {
            throw new ParseException("Instrução inválida", 0);
        }
    }


    public void addOrdemServico(OrdemServico ordemServico) {
        // Add ordemServico to cache
        cache.inserir(ordemServico);

        LogDAO.addLog("[CACHE INSERT] " + cache);
    }


    public OrdemServico getOrdemServico(int codigo) {
        OrdemServico ordemServico = cache.buscar(codigo);

        if (ordemServico == null) {
            // Cache miss
            LogDAO.addLog("[CACHE MISS] Ordem de Serviço " + codigo + " não encontrada");
            return null;
        }

        LogDAO.addLog("[CACHE HIT] Ordem de Serviço " + codigo + " encontrada");
        LogDAO.addLog("[PRIORITY UPDATE] " + cache);

        return ordemServico;
    }


    public void updateOrdemServico(OrdemServico ordemServico) {
        // Update ordemServico in cache
        cache.atualizar(ordemServico);

        LogDAO.addLog("[CACHE UPDATE] Ordem de Serviço " + ordemServico.getCodigo() + " atualizada");
    }


    public void deleteOrdemServico(int codigo) throws Exception {
        // Remove ordemServico from cache
        cache.remover(codigo);

        LogDAO.addLog("[CACHE REMOVE] " + cache);
    }


    public void clearCache() {
        cache.limpar();
    }
}
