package modelo.entities;

import java.io.Serializable;
import java.util.Date;

public class OrdemServico implements Serializable{
    private static int contador = 0;
    private int codigo;
    private String titulo;
    private String descricao;
    private Usuario usuario;
    private Date hora;

    private static final long serialVersionUID = 2L;

    public OrdemServico(String titulo, String descricao, Usuario usuario) {
        this.codigo = ++contador;
        this.titulo = titulo;
        this.descricao = descricao;
        this.hora = new Date();
        this.usuario = usuario;
    }

    public OrdemServico(int codigo, String titulo, String descricao, Usuario usuario, Date hora, int contador) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.hora = hora;
        this.usuario = usuario;
        OrdemServico.contador = contador;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Date getHora() {
        return hora;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static void setContador(int contador) {
        OrdemServico.contador = contador;
    }

    public static int getContador() {
        return contador;
    }

    public String toString() {
        String horaFormatada = String.format("%tF %tT", hora, hora);
        return "Codigo: " + Integer.toString(codigo) + "\nTitulo: " + titulo + "\nCliente: " + usuario.getNome() + "\nDescricao: " + descricao + "\nHora: " + horaFormatada;
    }

    public boolean equals(OrdemServico ordemServico) {
        return this.codigo == ordemServico.getCodigo();
    }
}
