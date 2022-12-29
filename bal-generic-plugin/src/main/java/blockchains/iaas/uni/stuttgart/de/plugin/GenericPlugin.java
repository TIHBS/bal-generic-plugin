package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.IAdapterExtension;
import blockchains.iaas.uni.stuttgart.de.api.connectionprofiles.AbstractConnectionProfile;
import blockchains.iaas.uni.stuttgart.de.api.interfaces.BlockchainAdapter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class GenericPlugin extends Plugin {
    public GenericPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Extension
    public static class GenericAdapterImpl implements IAdapterExtension {

        @Override
        public BlockchainAdapter getAdapter(AbstractConnectionProfile abstractConnectionProfile) {
            GenericConnectionProfile genericConnectionProfile = (GenericConnectionProfile) abstractConnectionProfile;

            String nodeUrl = genericConnectionProfile.getNodeUrl();
            String keyFile = genericConnectionProfile.getKeyFile();

            // int averageBlockTimeSeconds = Integer.parseInt(parameters.get("averageBlockTimeSeconds"));
            return new GenericAdapter(nodeUrl, keyFile);
        }


        @Override
        public Class<? extends AbstractConnectionProfile> getConnectionProfileClass() {
            return GenericConnectionProfile.class;
        }

        @Override
        public String getConnectionProfileNamedType() {
            return "generic";
        }

        @Override
        public String getBlockChainId() {
            return "generic";
        }

    }

}
