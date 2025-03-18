# Root Repair
Root Repair is a distributed system designed to manage service orders for equipment repairs. The software provides a simple CRUD, but has its own implementations of each data structure used, adapting to the system's needs.

This system was developed as a final project for the Distributed Systems course at UFERSA.

## Stack
The project is made with pure Java 21, so you don't need to install any dependencies.

## Running the system
The system is divided into two parts: the **server** and the **client**. 

To run the server, you need to run the each of the following classes, respectively:
- `cache_eviction_server/src/ApplicationServer/ApplicationServer.java`
- `cache_eviction_server/src/ProxyServer/ProxyServer.java`
- `cache_eviction_server/src/LocationServer/LocationServer.java`

To run the client, you need to run the following class:
- `cache_eviction_client/src/Client/Client.java`

>Make sure all the components are running in the same network, as the system uses sockets and RMI to communicate between the servers and the client.

## Documentation
<iframe style="border: 1px solid rgba(0, 0, 0, 0.1);" width="800" height="450" src="https://embed.figma.com/board/l9XCHE4ECMlqXk8n1a0jJV/Cache-Eviction---Whiteboard?node-id=0-1&embed-host=share" allowfullscreen></iframe>