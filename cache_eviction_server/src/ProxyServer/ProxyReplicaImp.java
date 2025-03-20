package ProxyServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import ProxyServer.model.DAO.OrdemServicoCacheDAO;
import shared.entities.OrdemServico;

public class ProxyReplicaImp extends UnicastRemoteObject implements ProxyReplicaInterface {
    private static OrdemServicoCacheDAO cacheDAO = new OrdemServicoCacheDAO();

    public ProxyReplicaImp() throws RemoteException {
        super();
    }

    @Override
    public void updateCache(OrdemServico ordem) throws RemoteException {
        // Atualiza a cache local
        cacheDAO.updateOrdemServico(ordem);
        System.out.println("Cache updated for ordem: " + ordem.getCodigo());
    }

    @Override
    public void removeFromCache(int codigo) throws RemoteException {
        // Remove da cache local
        cacheDAO.deleteOrdemServico(codigo);
        System.out.println("Cache removed for ordem: " + codigo);
    }

    @Override
    public OrdemServico select(int codigo) throws RemoteException {
        // Busca na cache local
        OrdemServico ordem = cacheDAO.getOrdemServico(codigo);
        System.out.println("Cache selected for ordem: " + codigo);
        return ordem;
    }
}