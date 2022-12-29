package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.exceptions.*;
import blockchains.iaas.uni.stuttgart.de.api.interfaces.BlockchainAdapter;
import blockchains.iaas.uni.stuttgart.de.api.model.*;
import blockchains.iaas.uni.stuttgart.de.api.utils.SmartContractPathParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Observable;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class GenericAdapter implements BlockchainAdapter {

    private String nodeUrl;

    private static final Logger logger = LoggerFactory.getLogger(GenericAdapter.class.getName());


    public GenericAdapter(String nodeUrl, String keyFile) {
        this.nodeUrl = nodeUrl;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> example = objectMapper.readValue(new File(keyFile), Map.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
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
        return null;
        // tx.complete();
    }

    private static CompletionException wrapAtposExceptions(Throwable e) {
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
