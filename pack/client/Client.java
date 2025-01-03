package pack.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import pack.client.util.ClientProperties;
import pack.client.util.InputFilter;

public class Client {
    static InputFilter inputFilter;

    private static void sendToServer(String serverAddress, Integer port, String fileName, String fileContent) throws IOException {

        try {
            Socket socket = new Socket(serverAddress, port);
            System.out.println("Connected to server");
            DataOutputStream serverOutStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("Sending data to server");
            System.out.println("File Name: " + fileName);
            System.out.println("File Content: " + fileContent);
            serverOutStream.writeUTF(fileName);
            serverOutStream.writeUTF(fileContent);
            serverOutStream.flush();
            serverOutStream.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error sending data to server");
            throw e;
        }
    }

    private static ClientProperties getProperties(String[] args){
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
        ClientProperties clientProperties = new ClientProperties(properties);
        return clientProperties;
    } 


    public static void main(String[] args) {
        ClientProperties clientProperties = getProperties(args);
       

        inputFilter = new InputFilter(clientProperties.getKeyFilterPattern());

        File monitoredDir  = new File(clientProperties.getMonitoredDir());

        while (true) {
            
            File[] files = monitoredDir.listFiles();
            if(files!=null && files.length>0){
                for(File file: files){
                    Map<String, String> fileContentMap = new HashMap<>();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = br.readLine()) != null) {
                            
                            String[] keyValue = line.split(":", 2);
                            if (keyValue.length == 2) {
                                fileContentMap.put(keyValue[0].trim(), keyValue[1].trim());
                            }
                        }
                            String payload = fileContentMap.entrySet()
                            .stream()
                            .map(k->k.getKey()+":"+k.getValue())
                            .filter(entry->inputFilter.filter(entry))
                            .collect(Collectors.joining("\n"));

                            System.out.println("Payload ready:"+payload);

                            sendToServer(clientProperties.getServerAddress(), clientProperties.getServerPort(),  file.getName(), payload);                                
                            br.close();
                            file.delete();
                        
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        continue;
                    }
                }
            }else{
                System.out.println("No files to process");
            }
            try {
                System.out.println("Sleeping for : " + clientProperties.getFileScanInterval());
                Thread.sleep(clientProperties.getFileScanInterval());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}