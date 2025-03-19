package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProxyHandlerInterface extends Remote {
    void registerProxy(String address, int port, int heartbeatPort) throws RemoteException;
    String getNextProxyAddress() throws RemoteException;
    int getNextProxyPort() throws RemoteException;
}