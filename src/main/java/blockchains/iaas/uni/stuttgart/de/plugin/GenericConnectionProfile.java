package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.connectionprofiles.AbstractConnectionProfile;

public class GenericConnectionProfile extends AbstractConnectionProfile {


    public GenericConnectionProfile() {
    }

    private String remotePluginUrl;
    
    public String getRemotePluginUrl() {
        return remotePluginUrl;
    }

    public void setRemotePluginUrl(String remotePluginUrl) {
        this.remotePluginUrl = remotePluginUrl;
    }
}
