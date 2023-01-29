package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.connectionprofiles.AbstractConnectionProfile;

public class GenericConnectionProfile extends AbstractConnectionProfile {


    public GenericConnectionProfile() {
    }

    private String remotePluginUrl;
    private boolean canHandleDelegatedSubscription;

    private long scanIntervalForSubscription;
    public boolean canHandleDelegatedSubscription() {
        return canHandleDelegatedSubscription;
    }

    public void setCanHandleDelegatedSubscription(boolean canHandleDelegatedSubscription) {
        this.canHandleDelegatedSubscription = canHandleDelegatedSubscription;
    }


    public String getRemotePluginUrl() {
        return remotePluginUrl;
    }

    public void setRemotePluginUrl(String remotePluginUrl) {
        this.remotePluginUrl = remotePluginUrl;
    }

    public long getScanIntervalForSubscription() {
        return scanIntervalForSubscription;
    }

    public void setScanIntervalForSubscription(long scanIntervalForSubscription) {
        this.scanIntervalForSubscription = scanIntervalForSubscription;
    }
}
