import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.util.Scanner;

import modelo.DAO.LogDAO;
import shared.Message;

public class Client {
    public static Scanner scanner = new Scanner(System.in);
    private static boolean loggedIn = false; 


    public static void main(String[] args) throws Exception {
        LogDAO.loadLog();

        // Connect to location server and get the proxy adress and port
        String proxyAdress = "";
        int proxyPort = -1;

        System.out.println("\r\n" + //
                                "============================================================\r\n" + //
                                "   _____             _     _____                  _      \r\n" + //
                                "  |  __ \\           | |   |  __ \\                (_)     \r\n" + //
                                "  | |__) |___   ___ | |_  | |__) |___ _ __   __ _ _ _ __ \r\n" + //
                                "  |  _  // _ \\ / _ \\| __| |  _  // _ \\ '_ \\ / _` | | '__|\r\n" + //
                                "  | | \\ \\ (_) | (_) | |_  | | \\ \\  __/ |_) | (_| | | |   \r\n" + //
                                "  |_|  \\_\\___/ \\___/ \\__| |_|  \\_\\___| .__/ \\__,_|_|_|   \r\n" + //
                                "                                     | |                 \r\n" + //
                                "                                     |_|                 \r\n" + //
                                "============================================================\r\n" + //
                                "\n");

        System.out.print("Enter the location server adress: ");
        String locationAdress = scanner.nextLine();

        System.out.print("Enter the location server port: ");
        int locationServerPort = scanner.nextInt();

        String newProxy = getNewProxy(locationAdress, locationServerPort);
        proxyAdress = newProxy.split(":")[0];
        proxyPort = Integer.parseInt(newProxy.split(":")[1]);

        // Initiate the system for the user
        while (proxyPort != -1 && proxyAdress != "") {
            try(Socket proxySocket = new Socket(proxyAdress, proxyPort);
            ObjectOutputStream proxyOut = new ObjectOutputStream(proxySocket.getOutputStream());
            ObjectInputStream proxyIn = new ObjectInputStream(proxySocket.getInputStream())) {
     
                // Set the controller
                Controller.setProxyServer(proxyOut, proxyIn);

                // Start the user interface
                View.limparTela();

                if (loggedIn) {
                    View.showMenu();
                } else {
                    if (View.realizarLogin()) {
                        loggedIn = true;
                        View.showMenu();
                    } else {
                        System.out.println("\nVolte sempre!");
                    }
                }


                // Send close message to the proxy server
                proxyOut.writeObject(new Message("CLOSE"));
                proxyOut.flush();

                // Close the loop
                proxyPort = -1;
                proxyAdress = "";
            } catch (IOException e) {
                System.out.println("\nProxy server went down. Changing server...");
                newProxy = getNewProxy(locationAdress, locationServerPort);

                String newProxyAdress = newProxy.split(":")[0];
                int newProxyPort = Integer.parseInt(newProxy.split(":")[1]);
                
                // Se o proxy adress estiver vazio, ou se o novo proxy for igual ao antigo, encerra o programa
                if (newProxyAdress.equals("") || (newProxyAdress.equals(proxyAdress) && newProxyPort == proxyPort)) {
                    System.out.println(Color.RED + "No proxy available. Exiting..." + Color.RESET);
                    break;
                } else {
                    proxyAdress = newProxyAdress;
                    proxyPort = newProxyPort;
                }
            }
        }

        scanner.close();
    }

    private static String getNewProxy(String locationAdress, int locationServerPort) {
        String newProxyAdress = "";
        int newProxyPort = -1;

        try (Socket locationSocket = new Socket(locationAdress, locationServerPort);
             DataInputStream locationIn = new DataInputStream(locationSocket.getInputStream());
             DataOutputStream locationOut = new DataOutputStream(locationSocket.getOutputStream())) {

            locationOut.writeUTF("eW91IHNoYWxsIG5vdCBwYXNz");  // Send a key as request to the location server
            newProxyAdress = locationIn.readUTF();  // Read the proxy adress
            newProxyPort = locationIn.readInt();  // Read the proxy port

            // Log the proxy adress and port
            LogDAO.addLog("[LOCATION SERVER] Proxy running on " + newProxyAdress + ":" + newProxyPort);
        } catch (IOException e) {
            System.out.println(Color.RED + "\nError. Location server went down, or there's no proxy available." + Color.RESET);
            //e.printStackTrace();
        }

        return newProxyAdress + ":" + newProxyPort;
    }
}
