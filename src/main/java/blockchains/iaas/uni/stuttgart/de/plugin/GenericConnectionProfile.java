package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.connectionprofiles.AbstractConnectionProfile;

public class GenericConnectionProfile extends AbstractConnectionProfile {


    public GenericConnectionProfile() {
    }

    private String nodeUrl;
    private String remotePluginUrl;
    private String keyFile;

    public GenericConnectionProfile(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public String getKeyFile() {
        return keyFile;
    }

    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }

    public String getRemotePluginUrl() {
        return remotePluginUrl;
    }

    public void setRemotePluginUrl(String remotePluginUrl) {
        this.remotePluginUrl = remotePluginUrl;
    }
}
