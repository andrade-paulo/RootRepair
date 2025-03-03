package model;

import model.entities.OrdemServico;
import model.entities.Usuario;
import datastructures.Huffman.ArvoreHuffman;
import model.DAO.LogDAO;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Message {
    private String content;
    private ArvoreHuffman arvore;
    private boolean isCompressed;

    public Message(OrdemServico ordem, String instrucao) {
        this.content = ordem.getCodigo() + "|" + ordem.getTitulo() + "|" + ordem.getDescricao() + "|" + ordem.getUsuario().getNome() + "|" + ordem.getUsuario().getCpf() + "|" + ordem.getHora() + "|" + instrucao;
        this.arvore = new ArvoreHuffman();

        // Cria a árvore de Huffman
        char[] caracteres = new char[content.length()];
        int[] frequencias = new int[content.length()];

        arvore.contarCaractereFrequencia(content, caracteres, frequencias);
        arvore.construirArvore(caracteres, frequencias);
        arvore.imprimirCodigos();

        // Comprime a mensagem
        this.content = arvore.comprimir(content);
        this.isCompressed = true;

        LogDAO.addLog("[MESSAGE] Ordem de Serviço " + ordem.getCodigo() + " comprimida com instrução " + instrucao);
    }


    public Message(int codigo, String instrucao) {
        this.content = codigo + "|" + instrucao;
        this.arvore = new ArvoreHuffman();

        // Cria a árvore de Huffman
        char[] caracteres = new char[content.length()];
        int[] frequencias = new int[content.length()];

        arvore.contarCaractereFrequencia(content, caracteres, frequencias);
        arvore.construirArvore(caracteres, frequencias);
        arvore.imprimirCodigos();

        // Comprime a mensagem
        this.content = arvore.comprimir(content);
        this.isCompressed = true;

        LogDAO.addLog("[MESSAGE] Ordem de Serviço " + codigo + " comprimida com instrução " + instrucao);
    }


    public OrdemServico getOrdem() throws ParseException {
        // Checa se a mensagem está comprimida
        if (isCompressed) this.content = arvore.descomprimir(this.content);
        isCompressed = false;
        
        // splited array
        String[] splited = this.content.split("[|]");

        // Transforma a string em um objeto OrdemServico
        int codigo = Integer.parseInt(splited[0]);
        String titulo = splited[1];
        String descricao = splited[2];

        String nome = splited[3];
        String cpf = splited[4];

        // Parse data e.g.: Thu Oct 24 23:34:10 BRT 2024
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Date hora = formatter.parse((String)splited[5]);

        Usuario usuario = new Usuario(nome, cpf);
        
        // Passa .getContador() para que a criação desse objeto não interfira na contagem do database
        OrdemServico ordem = new OrdemServico(codigo, titulo, descricao, usuario, hora, OrdemServico.getContador());

        return ordem;
    }


    public int getCodigo() {
        // Checa se a mensagem está comprimida
        if (isCompressed) {
            this.content = arvore.descomprimir(this.content);
        }
        
        isCompressed = false;

        return Integer.parseInt(this.content.split("[|]")[0]);
    }

    
    public String getInstrucao() {
        // Checa se a mensagem está comprimida
        if (isCompressed) arvore.descomprimir(this.content);
        isCompressed = false;

        try {
            return this.content.split("[|]")[6];
        } catch (Exception e) {
            return this.content.split("[|]")[1];
        }
    }
}
