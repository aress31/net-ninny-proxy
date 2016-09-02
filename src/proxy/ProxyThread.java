/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author AresS
 */
class ProxyThread extends Thread {

    
    String [] forbiddenWords;
    private final Socket clientSocket;
    private final String badURL =       "HTTP/1.1 200 OK\n" +
                                        "Date: Mon, 13 Oct 2014 22:22:22 GMT\n" +
                                        "Server: Apache/2.2.24 (Unix) DAV/2 SVN/1.6.17 PHP/5.3.23 mod_fastcgi/2.4.6 mod_auth_kerb/5.4+ida mod_jk/1.2.31 mod_ssl/2.2.24 OpenSSL/0.9.7d\n" +
                                        "Last-Modified: Wed, 06 May 2009 18:12:35 GMT\n" +
                                        "ETag: \"4c4174-126-4694256fcaac0\"\n" +
                                        "Accept-Ranges: bytes\n" +
                                        "Content-Length: 294\n" +
                                        "Keep-Alive: timeout=5, max=100\n" +
                                        "Connection: Keep-Alive\n" +
                                        "Content-Type: text/html\n" +
                                        "\n" +
                                        "<html>\n" +
                                        "\n" +
                                        "<title>\n" +
                                        "Net Ninny Error Page 1 for CPSC 441 Assignment 1\n" +
                                        "</title>\n" +
                                        "\n" +
                                        "<body>\n" +
                                        "<p>\n" +
                                        "Sorry, but the Web page that you were trying to access\n" +
                                        "is inappropriate for you, based on the URL.\n" +
                                        "The page has been blocked to avoid insulting your intelligence.\n" +
                                        "</p>\n" +
                                        "\n" +
                                        "<p>\n" +
                                        "Net Ninny\n" +
                                        "</p>\n" +
                                        "\n" +
                                        "</body>\n" +
                                        "\n" +
                                        "</html>";
            
    private final String badResponse =  "HTTP/1.1 200 OK\n" +
                                        "Date: Mon, 13 Oct 2014 22:45:06 GMT\n" +
                                        "Server: Apache/2.2.24 (Unix) DAV/2 SVN/1.6.17 PHP/5.3.23 mod_fastcgi/2.4.6 mod_auth_kerb/5.4+ida mod_jk/1.2.31 mod_ssl/2.2.24 OpenSSL/0.9.7d\n" +
                                        "Last-Modified: Wed, 06 May 2009 18:12:35 GMT\n" +
                                        "ETag: \"4c4174-126-4694256fcaac0\"\n" +
                                        "Accept-Ranges: bytes\n" +
                                        "Content-Length: 294\n" +
                                        "Keep-Alive: timeout=5, max=100\n" +
                                        "Connection: Keep-Alive\n" +
                                        "Content-Type: text/html\n" +
                                        "\n" +
                                        "<html>\n" +
                                        "\n" +
                                        "<title>\n" +
                                        "Net Ninny Error Page 1 for CPSC 441 Assignment 1\n" +
                                        "</title>\n" +
                                        "\n" +
                                        "<body>\n" +
                                        "<p>\n" +
                                        "Sorry, but the Web page that you were trying to access\n" +
                                        "is inappropriate for you, based on some of the words it contains.\n" +
                                        "The page has been blocked to avoid insulting your intelligence.\n" +
                                        "</p>\n" +
                                        "\n" +
                                        "<p>\n" +
                                        "Net Ninny\n" +
                                        "</p>\n" +
                                        "\n" +
                                        "</body>\n" +
                                        "\n" +
                                        "</html>";
    
    public ProxyThread(Socket socket, String[] forbiddenWords) {
        this.clientSocket = socket;
        this.forbiddenWords = forbiddenWords;
    }
    
    public boolean filter(byte[] bytes, int responseLength) {
        String response = new String(bytes, 0, responseLength);
        response = response.replaceAll("\\r|\\n", " ");
        String [] words = response.split(" ");
                
        for(String word: words) {
            for(String forbiddenWord: forbiddenWords){
                if(word.toLowerCase().contains(forbiddenWord))
                return true;
           } 
        }
        return false;
    }
    
    public String getHost(String request) {
        String [] strings;
        String [] words;
        String [] results;
        
        strings = request.split("\n");
        
        for(String sentence: strings) {
            if(sentence.contains("Host: ")) {
                words = sentence.split(" ");
                results = words[1].split(":");
                return results[0].replaceAll("\\r|\\n", "");
            }
        }
        return null;
    }
    
    public void run() {
        try {
            // Read request
            boolean filter = false;
            int bufferSize = 0;
            List<byte[]> mylist = new ArrayList<byte[]>();
            
            // Reading of the client request
            InputStream incommingClientStream = clientSocket.getInputStream();
            bufferSize = clientSocket.getReceiveBufferSize();
                        
            byte[] bytes = new byte[bufferSize];
            int clientRequestLength = incommingClientStream.read(bytes);
            
            if (clientRequestLength > 0) {
                System.out.println("REQUEST"
                                    + System.getProperty("line.separator") + "---------");
                System.out.println(new String(bytes, 0, clientRequestLength));
                String host = getHost(new String(bytes, 0, clientRequestLength));
                
                Socket serverSocket = null;
                
                try {                    
                    serverSocket = new Socket(host, 80);
                }
                
                catch(IOException e) {
                    System.out.println("Host doesn't exists!");
                    System.exit(1);
                }
                
                OutputStream outgoingServerStream = serverSocket.getOutputStream();
                   
                filter = filter(bytes, clientRequestLength);
                
                System.out.println(filter);
System.out.println(filter);

System.out.println(filter);

System.out.println(filter);
                
                if(filter){
                   OutputStream outgoingClientStream = clientSocket.getOutputStream();
                   bytes = badURL.getBytes();
                   
                   outgoingClientStream.write(bytes, 0, bytes.length);
                }
                
                else{
                    // Forwarding to the server the client's request
                    outgoingServerStream.write(bytes, 0, clientRequestLength);

                    // Listening the response
                    InputStream incommingServerStream = serverSocket.getInputStream();
                    
                    bufferSize = serverSocket.getReceiveBufferSize();
                    bytes = new byte[bufferSize];
                    OutputStream outgoingClientStream = clientSocket.getOutputStream();
                    int serverResponseLength = incommingServerStream.read(bytes);
                    while(serverResponseLength > 0){

                                System.out.println("RESPONSE"
                                                    + System.getProperty("line.separator") + "---------");

                                System.out.println(new String(bytes, 0, serverResponseLength));

                                 filter = filter(bytes, serverResponseLength);
System.out.println(filter);
System.out.println(filter);

System.out.println(filter);

System.out.println(filter);

                            if(filter){
                               bytes = badResponse.getBytes();
                               outgoingClientStream.write(bytes, 0, bytes.length);
                               break;
                            }
                            else {
//                                mylist.add(bytes);
                                outgoingClientStream.write(bytes, 0, serverResponseLength);
                            }
                        serverResponseLength = incommingServerStream.read(bytes);
                    }
//                    for (byte[] strArr : mylist) {
//                        outgoingClientStream.write(strArr, 0, bytes.length);
//        }
                    incommingClientStream.close();
                    outgoingClientStream.close();
                    incommingServerStream.close();
                    outgoingServerStream.close();

                    serverSocket.close();
                } 
            }
            else {
                incommingClientStream.close();
            }
        } 
        
        catch (IOException e) {
            e.printStackTrace();
        } 
        
        finally {
            try {
                clientSocket.close();
            } 
            
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
