package ApplicationServer.model.DAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ApplicationServer.datastructures.Hash;
import shared.entities.OrdemServico;
import shared.entities.Usuario;


public class OrdemServicoDAO {
    private Hash<OrdemServico> ordensServico;
    private int ocupacao;

    //private final String ARQUIVO = "src/ApplicationServer/database/database.dat";
    private final String ARQUIVO = "ApplicationServer/database/database.dat";
    private final int TAMANHO_INICIAL = 100; 
    
    public OrdemServicoDAO() {
        ordensServico = new Hash<>(TAMANHO_INICIAL);
        carregarArquivo();
        ocupacao = ordensServico.getOcupacao();
    }


    public OrdemServico[] selectAll() {
        LogDAO.addLog("[DB SELECT] Selecionando todas as Ordens de Serviço");

        return ordensServico.getAllOrdens();
    }


    public OrdemServico[] listarOS(Usuario usuario) {
        LogDAO.addLog("[DB SELECT] Selecionando Ordens de Serviço do Usuário " + usuario.getNome());
        return ordensServico.getOrdensByUsuario(usuario);
    }


    public void addOrdemServico(OrdemServico ordem) {
        // Update the code of the new order
        ordem.setCodigo(ordensServico.getOcupacao() + 1);
        ordensServico.inserir(ordem.getCodigo(), ordem);
        ocupacao++;

        LogDAO.addLog("[DB INSERT] Ordem de Serviço " + ordem.getCodigo() + ", ocupação: " + ocupacao + "/" + ordensServico.getTamanho());
        
        updateArquivo();
    }


    public OrdemServico getOrdemServico(int codigo) {
        OrdemServico ordemServico = ordensServico.buscar(codigo);
        
        if (ordemServico == null) {
            LogDAO.addLog("[DB MISS] Ordem de Serviço " + codigo + " não encontrada");
            return null;
        }

        LogDAO.addLog("[DB HIT] Ordem de Serviço " + codigo + " encontrada");
        return ordemServico;
    }


    public boolean updateOrdemServico(OrdemServico ordem) {
        if (ordensServico.buscar(ordem.getCodigo()) == null) {
            LogDAO.addLog("[DB MISS] Ordem de Serviço " + ordem.getCodigo() + " não encontrada");
            return false;
        }

        ordensServico.inserir(ordem.getCodigo(), ordem);
        LogDAO.addLog("[DB UPDATE] Ordem de Serviço " + ordem.getCodigo() + " atualizada");

        updateArquivo();
        return true;
    }


    public OrdemServico deleteOrdemServico(int codigo) {
        try {
            OrdemServico ordem = ordensServico.remover(codigo);
            ocupacao--;
            
            LogDAO.addLog("[DB DELETE] Ordem de Serviço " + codigo + ", ocupação: " + ocupacao + "/" + ordensServico.getTamanho());
            
            updateArquivo();
            
            return ordem;
        } catch (Exception e) {
            LogDAO.addLog("[DB MISS] Ordem de Serviço " + codigo + " não encontrada");
            return null;
        }
    }


    private void updateArquivo() {
        try {
            FileOutputStream file = new FileOutputStream(ARQUIVO);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(ordensServico);
            out.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    @SuppressWarnings("unchecked")
    public void carregarArquivo() {
        // Carregar arquivo binário "database.dat" e preencher Hash com as OSs
        try {
            File file = new File(ARQUIVO);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                FileInputStream fileIn = new FileInputStream(ARQUIVO);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                ordensServico = (Hash<OrdemServico>) objectIn.readObject();
                objectIn.close();
                fileIn.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public boolean isEmpty() {
        return ocupacao == 0;
    }

    public OrdemServico getUltimo() {
        return ordensServico.getUltimo();
    }

    public int getOcupacao() {
        return ocupacao;
    }

    public int getTamanho() {
        return ordensServico.getTamanho();
    }
}
