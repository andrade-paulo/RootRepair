package ProxyServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

import shared.entities.OrdemServico;

public interface ProxyReplicaInterface extends Remote {
    void updateCache(OrdemServico ordem) throws RemoteException;
    void removeFromCache(int codigo) throws RemoteException;
}