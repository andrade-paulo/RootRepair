# Root Repair
Root Repair is a distributed system designed to manage service orders for equipment repairs. The software provides a simple CRUD, but has its own implementations of each data structure used, adapting to the system's needs.

This system was developed as a final project for the Distributed Systems course at UFERSA.

# Stack
The project is made with pure Java 21, using only sockets and RMI, so you don't need to install any dependencies.

# Running the system
## Server
The system is divided into two parts: the **server** and the **client**. 

To start the server, you need to run the each of the following classes, respectively:
- `cache_eviction_server/src/ApplicationServer/ApplicationServer.java`
- `cache_eviction_server/src/LocationServer/LocationServer.java`

To start the proxy, you need to run the following class:
- `cache_eviction_server/src/ProxyServer/ProxyServer.java`
  
Note that the proxy can be replicated by running each instance in a different directory. You may change the access address and ports by passing as arguments:

```bash
path_to_proxy_replica/ProxyServer.java <address> <proxyPort> <heartbeatPort> <rmiReplicaPort>
```

>The heartbeatPort is used by the location server to check if the proxy is alive. The rmiReplicaPort is used to communicate with the other replicas.

## Client

To start the client, you need to run the following class:
- `cache_eviction_client/src/Client/Client.java`

>Make sure all the components are running in the same network, as the system uses sockets and RMI to communicate between the servers and the client.

## Documentation
- [System's architecture diagram](https://www.figma.com/board/l9XCHE4ECMlqXk8n1a0jJV/Cache-Eviction---Whiteboard?node-id=110-262&t=jg87HPr0hWubt6l3-1)
