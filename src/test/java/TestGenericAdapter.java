import blockchains.iaas.uni.stuttgart.de.api.model.Parameter;
import blockchains.iaas.uni.stuttgart.de.api.model.Transaction;
import blockchains.iaas.uni.stuttgart.de.api.model.TransactionState;
import blockchains.iaas.uni.stuttgart.de.plugin.GenericAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TestGenericAdapter {

    private GenericAdapter adapter;

    @BeforeEach
    public void setUp() {
        adapter = new GenericAdapter(null);
    }

    @Test
    public void testExecute() throws ExecutionException, InterruptedException {
        String smartContractPath = "0x2/devnet_nft";
        String functionIdentifier = "mint";
        List<String> typeArguments = new ArrayList<>();
        List<Parameter> inputs = new ArrayList<>();

        inputs.add(new Parameter("name", "string", "Example NFT"));
        inputs.add(new Parameter("description", "string", "An NFT created for testing"));
        inputs.add(new Parameter("ipfs", "string", "ipfs://bafkreibngqhl3gaa7daob4i2vccziay2jjlp435cf66vhono7nrvww53ty"));

        List<Parameter> outputs = new ArrayList<>();

        double requiredConfidence = 0;
        long timeout = 10000000;
        List<String> signers = new ArrayList<>();
        long minimumNumberOfSignatures = 0;
        Transaction result = adapter.invokeSmartContract(smartContractPath, functionIdentifier, typeArguments,
                inputs, outputs, requiredConfidence, timeout, signers, minimumNumberOfSignatures).get();

        assert result.getState() == TransactionState.CONFIRMED;
        assert result != null;

    }
}
