import java.io.*;

import datastructures.Hash;
import model.DAO.LogDAO;
import model.entities.OrdemServico;
import model.entities.Usuario;

public class Server {
    private Hash<OrdemServico> ordensServico;
    private int ocupacao;

    private final String ARQUIVO = "src/database/database.dat";
    private final int TAMANHO_INICIAL = 100; 
    
    public Server() {
        ordensServico = new Hash<>(TAMANHO_INICIAL);
        carregarArquivo();
        ocupacao = ordensServico.getOcupacao();
    }


    public void requestByOrdem(Message mensagemComprimida) throws Exception {
        // Descompacta a mensagem
        OrdemServico ordem = mensagemComprimida.getOrdem();
        String instrucao = mensagemComprimida.getInstrucao();

        // Verifica a instrução
        if (instrucao.equals("INSERT")) {
            addOrdemServico(ordem);
        } else if (instrucao.equals("UPDATE")) {
            updateOrdemServico(ordem);
        } else {
            throw new Exception("Instrução inválida");
        }
    }


    public OrdemServico requestByCodigo(Message mensagemComprimida) throws Exception {
        // Descompacta a mensagem
        int codigo = mensagemComprimida.getCodigo();
        String instrucao = mensagemComprimida.getInstrucao();

        // Verifica a instrução
        if (instrucao.equals("SELECT")) {
            return getOrdemServico(codigo);
        } else if (instrucao.equals("DELETE")) {
            return deleteOrdemServico(codigo);
        } else {
            throw new Exception("Instrução inválida");
        }
    }


    public void selectAll() {
        ordensServico.mostrarValores();
    }


    public void listarOS(Usuario usuario) {
        ordensServico.listarOS(usuario);
    }


    private void addOrdemServico(OrdemServico ordem) {
        ordensServico.inserir(ordem.getCodigo(), ordem);
        ocupacao++;

        LogDAO.addLog("[DB INSERT] Ordem de Serviço " + ordem.getCodigo() + ", ocupação: " + ocupacao + "/" + ordensServico.getTamanho());
        
        updateArquivo();
    }


    private OrdemServico getOrdemServico(int codigo) throws Exception {
        OrdemServico ordemServico = ordensServico.buscar(codigo);
        
        if (ordemServico == null) {
            LogDAO.addLog("[DB MISS] Ordem de Serviço " + codigo + " não encontrada");
            throw new Exception("Ordem de Serviço não encontrada");
        }

        LogDAO.addLog("[DB HIT] Ordem de Serviço " + codigo + " encontrada");
        return ordemServico;
    }


    private void updateOrdemServico(OrdemServico ordem) {
        ordensServico.inserir(ordem.getCodigo(), ordem);
        LogDAO.addLog("[DB UPDATE] Ordem de Serviço " + ordem.getCodigo() + " atualizada");

        updateArquivo();
    }


    private OrdemServico deleteOrdemServico(int codigo) throws Exception {
        try {
            OrdemServico ordem = ordensServico.remover(codigo);
            ocupacao--;
            
            LogDAO.addLog("[DB DELETE] Ordem de Serviço " + codigo + ", ocupação: " + ocupacao + "/" + ordensServico.getTamanho());
            
            updateArquivo();
            
            return ordem;
        } catch (Exception e) {
            LogDAO.addLog("[DB MISS] Ordem de Serviço " + codigo + " não encontrada");
            throw new Exception("Ordem de Serviço não encontrada");
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
