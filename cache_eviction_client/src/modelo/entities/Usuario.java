package modelo.entities;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nome;
    private String cpf;
    private long codigo;

    public Usuario(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
        this.codigo = Long.parseLong(cpf);
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public long getCodigo() {
        return codigo;
    }

    public String toString() {
        return "Nome: " + nome + "\nCPF: " + cpf;
    }

    public boolean equals(Usuario cliente) {
        return this.cpf.equals(cliente.getCpf());
    }
}
