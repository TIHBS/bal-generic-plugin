import blockchains.iaas.uni.stuttgart.de.api.model.*;
import blockchains.iaas.uni.stuttgart.de.plugin.GenericAdapter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TestGenericAdapterFlowTests {
    private static final Logger logger = LoggerFactory.getLogger(TestGenericAdapterFlowTests.class.getName());

    private GenericAdapter adapter;

    @BeforeEach
    public void setUp() {
        adapter = new GenericAdapter("http://localhost:7878", false, 10);
    }

    @Test
    public void testExecute() throws ExecutionException, InterruptedException {
        String smartContractPath = "0xf8d6e0586b0a20c7/Example";
        String functionIdentifier = "setValues";
        List<String> typeArguments = new ArrayList<>();
        List<Parameter> inputs = new ArrayList<>();

        inputs.add(new Parameter("name", "{\"type\":\"string\"}", "Example NFT"));
        inputs.add(new Parameter("newBooleanVar", "{\"type\":\"boolean\"}", "true"));
        inputs.add(new Parameter("newInt8Var", "{\"type\":\"integer\",\"maximum\": \"127\", \"minimum\": \"-128\"}", "-100"));
        inputs.add(new Parameter("newUInt128Var", "{\"type\":\"integer\",\"maximum\": \"340282366920938463463374607431768211455\", \"minimum\": \"0\"}", "1000"));

        List<Parameter> outputs = new ArrayList<>();

        double requiredConfidence = 0;
        long timeout = 10000000;
        List<String> signers = new ArrayList<>();
        long minimumNumberOfSignatures = 0;
        String signature = "";
        String signer = "";

        List<ImmutablePair<String, String>> signatures = new ArrayList<>();
        signatures.add(new ImmutablePair<>("test", "test"));

        Transaction result = adapter.invokeSmartContract(smartContractPath, functionIdentifier, typeArguments,
                inputs, outputs, requiredConfidence, timeout, signature, signer, signers, signatures, minimumNumberOfSignatures).get();

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
