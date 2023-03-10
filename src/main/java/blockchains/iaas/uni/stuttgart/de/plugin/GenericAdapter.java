package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.exceptions.*;
import blockchains.iaas.uni.stuttgart.de.api.interfaces.BlockchainAdapter;
import blockchains.iaas.uni.stuttgart.de.api.model.*;
import blockchains.iaas.uni.stuttgart.de.api.utils.SmartContractPathParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.reactivex.Observable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InsufficientResourcesException;
import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class GenericAdapter implements BlockchainAdapter {

    private HashMap<String, String> eventSubscriptionLastSearchTimeMapping = new HashMap();

    private static final Logger logger = LoggerFactory.getLogger(GenericAdapter.class.getName());
    private static String serverUrl = null;
    private static boolean canHandleDelegatedSubscription = false;

    private static long scanInterval = 10;

//    public GenericAdapter(String keyFile) {
//        if (keyFile != null) {
//            ObjectMapper objectMapper = new ObjectMapper();
//            try {
//                Map<String, String> example = objectMapper.readValue(new File(keyFile), Map.class);
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//    }

    public GenericAdapter(String remotePluginUrl, boolean canHandleDelegatedSubscription, long scanInterval) {

        serverUrl = remotePluginUrl;
        this.canHandleDelegatedSubscription = canHandleDelegatedSubscription;
        this.scanInterval = scanInterval;
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
                                                              long timeoutMillis,
                                                              String signature,
                                                              String signer,
                                                              List<String> signers,
                                                              List<ImmutablePair<String, String>> signatures,
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
                m.put("timeout", timeoutMillis);
                m.put("signers", signers);
                m.put("signatures", signatures);
                m.put("minimumNumberOfSignatures", minimumNumberOfSignatures);

                String json = new ObjectMapper().writeValueAsString(m);
                String api = this.serverUrl + "/invoke";
                String result = Utils.sendPostRequest(api, json);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(result, Map.class);

                logger.info("Transaction hash: " + map.get("transactionHash"));
                return CompletableFuture.completedFuture(map.get("transactionHash"));
            } catch (Exception e) {
                throw mapPluginException(e);
            }
        }).thenApply((txhash) -> {
            Transaction tx = new Transaction();
            tx.setState(TransactionState.CONFIRMED);
            tx.setReturnValues(new ArrayList<>());
            return tx;
        });
//        .exceptionally(e -> {
//            logger.info("Invocation failed with exception : " + e.getMessage());
//            throw new CompletionException(e);
//        })
    }

    private static CompletionException wrapExceptions(Throwable e) {
        return new CompletionException(mapPluginException(e));
    }

    private static BalException mapPluginException(Throwable e) {
        BalException result;

        if (e instanceof BalException)
            result = (BalException) e;
        else if (e.getCause() instanceof IOException)
            result = new BlockchainNodeUnreachableException(e.getMessage());
        else if (e.getCause() instanceof RuntimeException)
            result = new InvalidTransactionException(e.getMessage());
        else {
            logger.error("Unexpected exception was thrown!");
            result = new InvalidTransactionException(e.getMessage());
        }
        return result;
    }


    @Override
    public Observable<Occurrence> subscribeToEvent(String smartContractAddress, String eventIdentifier,
                                                   List<Parameter> outputParameters, double degreeOfConfidence, String filter) throws BalException {
        String key = smartContractAddress + "::" + eventIdentifier;
        if (!eventSubscriptionLastSearchTimeMapping.containsKey(key)) {
            eventSubscriptionLastSearchTimeMapping.put(key, String.valueOf(System.currentTimeMillis()));
        }

        return Observable.interval(0, scanInterval, TimeUnit.SECONDS).map((t) -> {
            String end = String.valueOf(System.currentTimeMillis());

            String start = eventSubscriptionLastSearchTimeMapping.get(key);

            Map<String, Object> m = new HashMap<>();
            m.put("eventIdentifier", eventIdentifier);

            m.put("filter", filter);

            Map<String, String> timeFrame = new HashMap<>();
            timeFrame.put("from", start);
            timeFrame.put("to", end);
            m.put("timeframe", timeFrame);
            m.put("parameters", outputParameters);

            String json = new ObjectMapper().writeValueAsString(m);


            String api = serverUrl + "/query";
            String result = Utils.sendPostRequest(api, json);

            ObjectMapper mapper = new ObjectMapper();

            eventSubscriptionLastSearchTimeMapping.replace(key, end);

            Occurrence[] occurrencesArray = mapper.readValue(result, Occurrence[].class);
            List<Occurrence> occurrences = Arrays.asList(occurrencesArray);
            logger.info("Subscribe query result size {} time frame [{},{}]", occurrences.size(), start, end);

            return occurrences;
        }).flatMapIterable(x -> x);

    }

    @Override
    public CompletableFuture<QueryResult> queryEvents(String smartContractAddress, String eventIdentifier, List<String> typeArguments,
                                                      List<Parameter> outputParameters, String filter, TimeFrame timeFrame) throws BalException {


        try {
            String json = null;
            Map<String, Object> m = new HashMap<>();
            m.put("eventIdentifier", eventIdentifier);
            m.put("smartContractPath", smartContractAddress);

            m.put("outputParameters", outputParameters);
            m.put("filter", filter);
            m.put("timeframe", timeFrame);

            json = new ObjectMapper().writeValueAsString(m);

            String api = this.serverUrl + "/query";
            String result = Utils.sendPostRequest(api, json);

            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            List<Occurrence> occurrences = objectMapper.readValue(result, typeFactory.constructCollectionType(List.class, Occurrence.class));
            QueryResult queryResult = new QueryResult();
            queryResult.setOccurrences(occurrences);
            return CompletableFuture.completedFuture(queryResult);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String testConnection() {

        return null;
    }

    @Override
    public CompletableFuture<Transaction> tryReplaceInvocation(String correlationId, String smartContractPath,
                                                               String functionIdentifier,
                                                               List<String> typeArguments,
                                                               List<Parameter> inputs,
                                                               List<Parameter> outputs,
                                                               double requiredConfidence,
                                                               String signature,
                                                               String signer,
                                                               List<String> signers,
                                                               long minimumNumberOfSignatures) {
        return null;
    }

    @Override
    public boolean tryCancelInvocation(String s) {
        return false;
    }


    @Override
    public boolean canHandleDelegatedSubscription() {
        return canHandleDelegatedSubscription;
    }

    @Override
    public boolean delegatedSubscribe(String functionIdentifier,
                                      String eventIdentifier,
                                      List<Parameter> outputParameters,
                                      double degreeOfConfidence,
                                      String filter,
                                      String callbackUrl,
                                      String correlationId) {
        return false;
    }

    @Override
    public boolean delegatedUnsubscribe(String smartContractPath,
                                        String functionIdentifier,
                                        String eventIdentifier,
                                        List<String> typeArguments,
                                        List<Parameter> parameters,
                                        String correlationId) {
        return false;
    }
}
