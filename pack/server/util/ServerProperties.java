package pack.server.util;

import java.util.Properties;

public class ServerProperties {
    private String targetDir;
    private int serverPort;

    public ServerProperties(Properties configProperties) {
        this.serverPort = Integer.parseInt(configProperties.getProperty("server_port"));
        this.targetDir = configProperties.getProperty("target_dir");
    }

    public Integer getServerPort() {
        return serverPort;
    }
    public String getTargetDir() {
        return targetDir;
    }

}
