package ProxyServer.model.DAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ProxyServer.datastructures.CacheHeap;
import shared.entities.OrdemServico;


public class OrdemServicoCacheDAO {
    // Same as server, but it is responsible for the cache
    CacheHeap cache;
    final int CACHE_SIZE = 30;
    private final String ARQUIVO = "src/ProxyServer/database/cache.dat";


    public OrdemServicoCacheDAO() {
        // Initialize cache
        cache = new CacheHeap(CACHE_SIZE);
        // Load cache from file
        carregarArquivo();
    }


    public void addOrdemServico(OrdemServico ordemServico) {
        // Add ordemServico to cache
        cache.inserir(ordemServico);
        updateArquivo();
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
        return ordemServico;
    }


    public void updateOrdemServico(OrdemServico ordemServico) {
        if (cache.buscar(ordemServico.getCodigo()) == null) {
            // Cache miss
            LogDAO.addLog("[CACHE MISS] Ordem de Serviço " + ordemServico.getCodigo() + " não encontrada");
            return;
        }
        
        cache.update(ordemServico);
        updateArquivo();
        LogDAO.addLog("[CACHE UPDATE] " + cache);
    }


    public void deleteOrdemServico(int codigo) {
        if (cache.buscar(codigo) == null) {
            // Cache miss
            LogDAO.addLog("[CACHE MISS] Ordem de Serviço " + codigo + " não encontrada");
            return;
        }

        cache.remover(codigo);
        updateArquivo();
        LogDAO.addLog("[CACHE DELETE] " + cache);
    }


    public void updateArquivo() {
        try (FileOutputStream fileOut = new FileOutputStream(ARQUIVO);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);){
            
            objectOut.writeObject(cache);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void carregarArquivo() {
        File file = new File(ARQUIVO);

        if (!file.exists()) {
            try {
                file.createNewFile();
                LogDAO.addLog("[CACHE FILE] Cache file created.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LogDAO.addLog("[CACHE FILE] Loading cache from file.");

        try (FileInputStream fileIn = new FileInputStream(ARQUIVO);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn);){
            
            cache = (CacheHeap) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LogDAO.addLog("[CACHE FILE] The cache is empty.");
        }
    }
}
