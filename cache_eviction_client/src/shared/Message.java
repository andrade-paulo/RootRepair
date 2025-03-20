package shared;

import modelo.DAO.LogDAO;
import shared.entities.OrdemServico;
import shared.entities.Usuario;
import shared.Huffman.ArvoreHuffman;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Message implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    private String content;
    private ArvoreHuffman arvore;
    private boolean isCompressed;
    

    public Message(String instrucao) {
        this.content = instrucao;
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

        LogDAO.addLog("[MESSAGE] Ordem de Serviço comprimida com instrução " + instrucao);
    }


    // Construtor para Ordem de Serviço
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


    public Message(OrdemServico[] ordensServico, String instrucao) {
        this.content = "";
        for (OrdemServico ordem : ordensServico) {
            this.content += ordem.getCodigo() + "|" + ordem.getTitulo() + "|" + ordem.getDescricao() + "|" + ordem.getUsuario().getNome() + "|" + ordem.getUsuario().getCpf() + "|" + ordem.getHora() + "|";
        }
        this.content += instrucao;
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

        LogDAO.addLog("[MESSAGE] Ordem de Serviço comprimida com instrução " + instrucao);
    }


    // Construtor para Usuario
    public Message(Usuario usuario, String instrucao) {
        this.content = usuario.getNome() + "|" + usuario.getCpf() + "|" + instrucao;
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

        LogDAO.addLog("[MESSAGE] Usuário " + usuario.getCpf() + " comprimido com instrução " + instrucao);
    }

    public Message(String cpf, String instrucao) {
        this.content = cpf + "|" + instrucao;
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

        LogDAO.addLog("[MESSAGE] Usuário " + cpf + " comprimido com instrução " + instrucao);
    }


    // Getters  
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

    public OrdemServico[] getOrdensServicos() throws ParseException {
        // Checa se a mensagem está comprimida
        if (isCompressed) this.content = arvore.descomprimir(this.content);
        isCompressed = false;

        // splited array
        String[] splited = this.content.split("[|]");

        OrdemServico[] ordens = new OrdemServico[splited.length / 6];
        int j = 0;

        for (int i = 0; i < splited.length-1; i += 6) {
            // Transforma a string em um objeto OrdemServico
            int codigo = Integer.parseInt(splited[i]);
            String titulo = splited[i + 1];
            String descricao = splited[i + 2];
            String nome = splited[i + 3];
            String cpf = splited[i + 4];

            //System.out.println("\n\n-------------------------\n" + codigo + " - " + titulo + " - " + descricao + "\n-------------------------\n\n");

            // Parse data e.g.: Thu Oct 24 23:34:10 BRT 2024
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);
            Date hora = formatter.parse((String)splited[i + 5]);

            Usuario usuario = new Usuario(nome, cpf);
            
            // Passa .getContador() para que a criação desse objeto não interfira na contagem do database
            ordens[j] = new OrdemServico(codigo, titulo, descricao, usuario, hora, OrdemServico.getContador());
            j++;
        }

        return ordens;
    }

    public int getCodigo() {
        // Checa se a mensagem está comprimida
        if (isCompressed) {
            this.content = arvore.descomprimir(this.content);
        }
        
        isCompressed = false;

        return Integer.parseInt(this.content.split("[|]")[0]);
    }
    
    public Usuario getUsuario() {
        // Checa se a mensagem está comprimida
        if (isCompressed) this.content = arvore.descomprimir(this.content);
        isCompressed = false;

        // splited array
        String[] splited = this.content.split("[|]");

        // Transforma a string em um objeto Usuario
        String nome = splited[0];
        String cpf = splited[1];

        return new Usuario(nome, cpf);
    }
    
    public String getInstrucao() {
        // Checa se a mensagem está comprimida
        if (isCompressed) this.content = arvore.descomprimir(this.content);
        isCompressed = false;

        String[] spplited_array = this.content.split("[|]");

        // Last element will be the instruction
        return spplited_array[spplited_array.length - 1];
    }

    public String getCPF() {
        // Checa se a mensagem está comprimida
        if (isCompressed) this.content = arvore.descomprimir(this.content);
        isCompressed = false;

        // splited array
        String[] splited = this.content.split("[|]");

        return splited[0];
    } 
}
