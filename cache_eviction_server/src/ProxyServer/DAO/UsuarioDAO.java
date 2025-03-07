package ProxyServer.DAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import datastructures.AVL;
import model.entities.Usuario;

public class UsuarioDAO {
    private AVL<Usuario> usuarios;
    private final String ARQUIVO = "src/ProxyServer/database/usuarios.dat"; 

    public UsuarioDAO() {
        usuarios = new AVL<>();
        carregarArquivo();
    }

    public void addCliente(Usuario usuario) {
        try {
            getCliente(usuario.getCpf());
            throw new Exception("Cliente já cadastrado");
        } catch (Exception e) {
            usuarios.insert(usuario.getCodigo(), usuario);
            updateArquivo();
        }
    }

    public Usuario getCliente(int codigo) throws Exception {
        Usuario usuario = usuarios.search(codigo);
        return usuario;
    }

    public Usuario getCliente(String cpf) throws Exception {
        Usuario usuario = usuarios.search(Integer.parseInt(cpf));
        
        if (usuario == null) {
            throw new Exception("Cliente não encontrado");
        }
        
        return usuario;
    }

    public void removerCliente(int codigo) throws Exception {
        try {
            usuarios.remove(codigo);
        } catch (Exception e) {
            throw new Exception("Cliente não encontrado");
        }

        updateArquivo();
    }

    public void updateCliente(Usuario usuario) {
        usuarios.insert(usuario.getCodigo(), usuario);
        updateArquivo();
    }

    public void updateArquivo() {
        try {
            FileOutputStream fileOut = new FileOutputStream(ARQUIVO);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(usuarios);
            objectOut.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void carregarArquivo() {
        try {
            File file = new File(ARQUIVO);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                FileInputStream fileIn = new FileInputStream(ARQUIVO);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                usuarios = (AVL<Usuario>) objectIn.readObject();
                objectIn.close();
                fileIn.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void listarUsuarios() {
        usuarios.order();
    }
}
