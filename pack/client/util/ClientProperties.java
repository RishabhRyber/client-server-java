package pack.client.util;

import java.util.Properties;

public class ClientProperties {
    private String monitoredDir;
    private String keyFilterPattern;
    private String serverAddress;
    private int serverPort;
    private int fileScanInterval;

    public ClientProperties(Properties configProperties) {
        this.monitoredDir = configProperties.getProperty("monitored_dir");
        this.keyFilterPattern = configProperties.getProperty("key_filter_pattern");
        this.serverAddress = configProperties.getProperty("server_address");
        this.fileScanInterval = Integer.parseInt(configProperties.getProperty("files_scan_interval"));
        this.serverPort = Integer.parseInt(configProperties.getProperty("server_port"));

    }


    public int getServerPort() {
        return serverPort;
    }
    public String getMonitoredDir() {
        return monitoredDir;
    }
    public String getKeyFilterPattern() {
        return keyFilterPattern;
    }
    public String getServerAddress() {
        return serverAddress;
    }
    public int getFileScanInterval() {
        return fileScanInterval;
    }




}
