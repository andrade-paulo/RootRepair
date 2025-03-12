package ProxyServer.model.DAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ProxyServer.datastructures.AVL;
import ProxyServer.model.entities.Usuario;

public class UsuarioDAO {
    private AVL<Usuario> usuarios;
    private final String ARQUIVO = "src/ProxyServer/database/usuarios.dat";

    public UsuarioDAO() {
        usuarios = new AVL<>();
        carregarArquivo();
    }

    public void addUsuario(Usuario usuario) {
        // Check if the user already exists
        if (usuarios.search(usuario.getCodigo()) != null) {
            return;
        }

        usuarios.insert(usuario.getCodigo(), usuario);
        LogDAO.addLog("[USUARIO] Usuário " + usuario.getNome() + " adicionado"); 
        updateArquivo();
    }

    public Usuario getUsuario(int codigo) throws Exception {
        Usuario usuario = usuarios.search(codigo);
        return usuario;
    }

    public Usuario getUsuario(String cpf) throws Exception {
        Usuario usuario = usuarios.search(Integer.parseInt(cpf));
        
        if (usuario == null) {
            throw new Exception("Cliente não encontrado");
        }
        
        return usuario;
    }

    public void removerUsuario(int codigo) throws Exception {
        try {
            usuarios.remove(codigo);
        } catch (Exception e) {
            throw new Exception("Cliente não encontrado");
        }

        LogDAO.addLog("[USUARIO] Usuário " + codigo + " removido");

        updateArquivo();
    }

    public void updateUsuario(Usuario usuario) {
        usuarios.insert(usuario.getCodigo(), usuario);

        LogDAO.addLog("[USUARIO] Usuário " + usuario.getNome() + " atualizado");
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
