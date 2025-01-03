package pack.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import pack.server.util.ClientHandler;
import pack.server.util.ServerProperties;

public class Server {
    private static ServerProperties getServerProperties(String[] args){
        if(args.length != 1){
            for (String string : args) {
                    System.out.println(string);
            }
            System.out.println("Incorrect Parameters passed: Please use: java client {config_dir}");
            throw new IllegalArgumentException("Incorrect Parameters passed: Please use: java client {config_dir}");
        }
        String configDirStr = args[0];
        File configFile = new File(configDirStr);
        if(!(configFile.exists() && configFile.isFile())){
            System.out.println("Config File not found at :".concat(configDirStr));
            throw new IllegalArgumentException("Config File not found at :".concat(configDirStr));
        }
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(configFile)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Error reading config file");
        }
        ServerProperties serverProperties = new ServerProperties(properties);
        return serverProperties;

    }
    public static void main(String[] args) {
    ServerSocket serverSocket = null;

        ServerProperties serverProperties = getServerProperties(args);
        try {
            serverSocket = new ServerSocket(serverProperties.getServerPort());
            serverSocket.setReuseAddress(true);
            System.out.println("Server waiting on port: ".concat(serverProperties.getServerPort().toString()));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                new Thread(new ClientHandler(clientSocket, serverProperties)).start();
                    
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        finally {
            try {
                if(serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
}
