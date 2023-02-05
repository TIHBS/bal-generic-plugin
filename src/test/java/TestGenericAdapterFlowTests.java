import blockchains.iaas.uni.stuttgart.de.api.exceptions.InvalidScipParameterException;
import blockchains.iaas.uni.stuttgart.de.api.model.*;
import blockchains.iaas.uni.stuttgart.de.plugin.GenericAdapter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestGenericAdapterFlowTests {
    private static final Logger logger = LoggerFactory.getLogger(TestGenericAdapterFlowTests.class.getName());

    private GenericAdapter adapter;

    @BeforeEach
    public void setUp() {
        adapter = new GenericAdapter("http://localhost:7878", false, 10);
    }

    @Test
    public void testExecute() throws ExecutionException, InterruptedException {

        // Example.cdc contract available in BAL-tests
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
    public void testExecuteError() throws ExecutionException, InterruptedException {

        // Example.cdc contract available in BAL-tests
        String smartContractPath = "0xf8d6e0586b0a20c7/Example";
        String functionIdentifier = "setValues";
        List<String> typeArguments = new ArrayList<>();
        List<Parameter> inputs = new ArrayList<>();

        inputs.add(new Parameter("name", "{\"type\":\"string\"}", "Example NFT"));
        inputs.add(new Parameter("newBooleanVar", "{\"type\":\"boolean\"}", "true"));
        inputs.add(new Parameter("newInt8Var", "{\"type\":\"integer\",\"maximum\": \"127\", \"minimum\": \"-128\"}", "-1000"));
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
        Throwable exception = assertThrows(ExecutionException.class, () -> adapter.invokeSmartContract(smartContractPath, functionIdentifier, typeArguments,
                inputs, outputs, requiredConfidence, timeout, signature, signer, signers, signatures, minimumNumberOfSignatures).get());


        assertEquals("\"Transaction arguments are invalid.\"", exception.getCause().getMessage());
    }

    @Test
    public void testExecuteError2() throws ExecutionException, InterruptedException {

        // Example.cdc contract available in BAL-tests
        String smartContractPath = "0xf8d6e0586b0a20c7/Example";
        String functionIdentifier = "setValues";
        List<String> typeArguments = new ArrayList<>();
        List<Parameter> inputs = new ArrayList<>();

        inputs.add(new Parameter("name", "{\"type\":\"string\"}", "Example NFT"));
        inputs.add(new Parameter("newBooleanVar", "{\"type\":\"boolean\"}", "true"));
        inputs.add(new Parameter("newInt8Var", "{\"type\":\"integer\",\"maximum\": \"127\", \"minimum\": \"-1280\"}", "-1000"));
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
        Throwable exception = assertThrows(ExecutionException.class, () -> adapter.invokeSmartContract(smartContractPath, functionIdentifier, typeArguments,
                inputs, outputs, requiredConfidence, timeout, signature, signer, signers, signatures, minimumNumberOfSignatures).get());


        assertEquals("\"Unrecognized integer type {\\\"type\\\":\\\"integer\\\",\\\"maximum\\\":\\\"127\\\",\\\"minimum\\\":\\\"-1280\\\"}!\"", exception.getCause().getMessage());

    }
}
