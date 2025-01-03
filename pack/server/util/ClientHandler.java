package pack.server.util;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable{
    
    private Socket clientSocket;
    ServerProperties serverProperties;
    public ClientHandler(Socket clientSocket, ServerProperties serverProperties) {
        this.clientSocket = clientSocket;
        this.serverProperties = serverProperties;
    }
public void run() {
        String line = "";
        DataInputStream dataInputStream = null;
        String temp = "";
        String fileName = "";
        try {
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            fileName = dataInputStream.readUTF();
            try{
                while ((temp = dataInputStream.readUTF())!=null) {
                    if(temp.trim().isEmpty())
                        continue;
                    line = line.concat(temp.trim());
                }
            } catch (EOFException e) {
                System.out.println("EOF reached");
            }
            writeToFiles(serverProperties.getTargetDir(), fileName, line);
            if(dataInputStream != null) {
                dataInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     private void writeToFiles(String targetDir, String fileName, String payload) {
        
        File file = new File(targetDir.concat(File.separator).concat(fileName));
        try {
            if(file.createNewFile()){
                System.out.println("File created: ".concat(file.getName()));
            } else {
                System.out.println("File already exists. Skipping writing to file");
                return ;
            }
        } catch (IOException e) {
            System.out.println("Error creating file");
            e.printStackTrace();
        }        
        System.out.println("Writing to files");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(payload.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
