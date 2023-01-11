import blockchains.iaas.uni.stuttgart.de.api.model.*;
import blockchains.iaas.uni.stuttgart.de.plugin.GenericAdapter;
import io.reactivex.Observable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TestGenericAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TestGenericAdapter.class.getName());

    private GenericAdapter adapter;

    @BeforeEach
    public void setUp() {
        adapter = new GenericAdapter("http://localhost:8585");
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
        String signature = "";
        String signer = "";
        Transaction result = adapter.invokeSmartContract(smartContractPath, functionIdentifier, typeArguments,
                inputs, outputs, requiredConfidence, timeout, signature, signer, signers, minimumNumberOfSignatures).get();

        assert result.getState() == TransactionState.CONFIRMED;
        assert result != null;

    }

    @Test
    public void testQueryEvents() throws ExecutionException, InterruptedException {
        String smartContractPath = "0x2/devnet_nft";
        String eventIdentifier = "0x2::devnet_nft::MintNFTEvent";
        TimeFrame timeFrame = new TimeFrame("0", "1672411415694000");
        String filter = "";
        List<Parameter> outputs = new ArrayList<>();

        double requiredConfidence = 0;
        long timeout = 10000000;
        List<String> signers = new ArrayList<>();
        long minimumNumberOfSignatures = 0;
        List<String> typeArguments = new ArrayList<>();
        QueryResult result = adapter.queryEvents(smartContractPath, eventIdentifier, typeArguments, outputs,
                filter, timeFrame).get();

        assert result != null;
        assert result.getOccurrences().size() != 0;

    }

    @Test
    public void testSubscribe() throws ExecutionException, InterruptedException {
        String smartContractPath = "0x2/devnet_nft";
        String eventIdentifier = "0x2::devnet_nft::MintNFTEvent";
        String filter = "";
        List<Parameter> outputs = new ArrayList<>();

        double requiredConfidence = 0;
        adapter.subscribeToEvent(smartContractPath, eventIdentifier, outputs, requiredConfidence, filter)
                .blockingSubscribe(occurrence -> {
                    if (occurrence != null) {
                        logger.info("detected occurrence! {}", occurrence.getIsoTimestamp());
                        for (Parameter p : occurrence.getParameters()) {
                            logger.info("Parameter: Name: [{}] Type: [{}] Value: [{}]", p.getName(), p.getType(), p.getValue());

                        }
                    } else {
                        logger.error("detected occurrence is null!");
                    }
                });
    }
}
