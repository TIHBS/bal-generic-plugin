package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.exceptions.*;
import blockchains.iaas.uni.stuttgart.de.api.interfaces.BlockchainAdapter;
import blockchains.iaas.uni.stuttgart.de.api.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class GenericAdapter implements BlockchainAdapter {

    private String serverUrl = "http://localhost:8585";

    private static final Logger logger = LoggerFactory.getLogger(GenericAdapter.class.getName());


    public GenericAdapter(String keyFile) {
        if (keyFile != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, String> example = objectMapper.readValue(new File(keyFile), Map.class);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }


    @Override
    public CompletableFuture<Transaction> submitTransaction(String s, BigDecimal bigDecimal, double v) throws InvalidTransactionException, NotSupportedException {
        return null;
    }

    @Override
    public Observable<Transaction> receiveTransactions(String s, double v) throws NotSupportedException {
        return null;
    }

    @Override
    public CompletableFuture<TransactionState> ensureTransactionState(String s, double v) throws NotSupportedException {
        return null;
    }

    @Override
    public CompletableFuture<TransactionState> detectOrphanedTransaction(String s) throws NotSupportedException {
        return null;
    }

    @Override
    public CompletableFuture<Transaction> invokeSmartContract(String smartContractPath,
                                                              String functionIdentifier,
                                                              List<String> typeArguments,
                                                              List<Parameter> inputs,
                                                              List<Parameter> outputs,
                                                              double requiredConfidence,
                                                              long timeout,
                                                              List<String> signers,
                                                              long minimumNumberOfSignatures) throws BalException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> m = new HashMap<>();
                m.put("functionIdentifier", functionIdentifier);
                m.put("smartContractPath", smartContractPath);

                m.put("inputs", inputs);
                m.put("typeArguments", typeArguments);
                m.put("outputs", outputs);
                m.put("requiredConfidence", requiredConfidence);
                m.put("timeout", timeout);
                m.put("signers", signers);
                m.put("minimumNumberOfSignatures", minimumNumberOfSignatures);

                String json = new ObjectMapper().writeValueAsString(m);
                String api = this.serverUrl + "/execute";
                String result = Utils.sendPostRequest(api, json);

                ObjectMapper mapper = new ObjectMapper();
                Map<String,Object> map = mapper.readValue(result, Map.class);

                logger.info("Transaction hash: " + map.get("transactionHash"));
                return CompletableFuture.completedFuture(map.get("transactionHash"));
            } catch (Exception e) {
                throw new CompletionException(wrapExceptions(e));
            }
        }).thenApply((txhash) -> {
            Transaction tx = new Transaction();
            tx.setState(TransactionState.CONFIRMED);
            tx.setReturnValues(new ArrayList<>());
            return tx;
        }).exceptionally(e -> {
            logger.info("Invocation failed with exception : " + e.getMessage());
            throw wrapExceptions(e);
        });
    }

    private static CompletionException wrapExceptions(Throwable e) {
        return new CompletionException(mapException(e));
    }

    @Override
    public Observable<Occurrence> subscribeToEvent(String smartContractAddress, String eventIdentifier,
                                                   List<Parameter> outputParameters, double degreeOfConfidence, String filter) throws BalException {
        return null;

    }

    @Override
    public CompletableFuture<QueryResult> queryEvents(String smartContractAddress, String eventIdentifier,
                                                      List<Parameter> outputParameters, String filter, TimeFrame timeFrame) throws BalException {

        return null;
    }

    @Override
    public String testConnection() {

        return null;
    }

    @Override
    public boolean signInvocation(String s, String s1) {

        return false;
    }

    @Override
    public List<Transaction> getPendingInvocations() {
        return null;
    }

    @Override
    public CompletableFuture<Transaction> tryReplaceInvocation(String s, String s1, String s2, List<String> list, List<Parameter> list1, List<Parameter> list2, double v, List<String> list3, long l) {
        return null;
    }

    @Override
    public void tryCancelInvocation(String s) {

    }

    private static BalException mapException(Throwable e) {
        BalException result;

        if (e instanceof BalException)
            result = (BalException) e;
        else if (e.getCause() instanceof BalException)
            result = (BalException) e.getCause();
        else if (e.getCause() instanceof IOException)
            result = new BlockchainNodeUnreachableException(e.getMessage());
        else if (e instanceof IllegalArgumentException || e instanceof OperationNotSupportedException)
            result = new InvokeSmartContractFunctionFailure(e.getMessage());
        else if (e.getCause() instanceof RuntimeException)
            result = new InvalidTransactionException(e.getMessage());
        else {
            logger.error("Unexpected exception was thrown!");
            result = new InvalidTransactionException(e.getMessage());
        }

        return result;
    }
}
