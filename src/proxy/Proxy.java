/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AresS
 */

public class Proxy implements Runnable {

    private int port;
    private String[] forbiddenWords;

    public Proxy(int port, String[] forbiddenWords) {
        this.port = port;
        this.forbiddenWords = forbiddenWords;
    }

    @Override
    public void run() {
        
        ServerSocket serverSocket = null;
        boolean listening = true;
        
        try {
            serverSocket = new ServerSocket(port);
        } 
        
        catch (IOException e) {
            System.out.println("Port Error");
            System.exit(-1);
        }
        
        while (listening) {
            try {
                new ProxyThread(serverSocket.accept(), forbiddenWords).start();
            } catch (IOException ex) {
                Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
}

