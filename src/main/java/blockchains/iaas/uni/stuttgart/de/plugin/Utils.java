package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.exceptions.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InsufficientResourcesException;
import javax.naming.OperationNotSupportedException;
import java.io.IOException;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class.getName());

    public static String sendGetRequest(String url) {
        String result;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {

            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(request);

            try {

                // Get HttpResponse Status
                logger.debug("GET API [{}] response code [{}]", url, response.getStatusLine().getStatusCode());   // 200
                assert response.getStatusLine().getStatusCode() == 200;
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity);
                    return result;
                }

            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static String sendPostRequest(String url, String json) throws Exception {
        String result = "";
        HttpPost post = new HttpPost(url);
        post.addHeader("content-type", "application/json");

        // send a JSON data
        post.setEntity(new StringEntity(json.toString()));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(post);
        logger.debug("POST API [{}] response code [{}]", url, response.getStatusLine().getStatusCode());
        result = EntityUtils.toString(response.getEntity());
        if (response.getStatusLine().getStatusCode() != 200) {
            throw mapException(result);
        }
        return result;
    }

    private static BalException mapException(String text) {
        BalException result;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(text);
            int errorCode = jsonNode.get("errorCode").asInt();
            String errorMessage = jsonNode.get("errorMessage").toString();

            switch (errorCode) {
                case ExceptionCode.ExecutionError:
                    return new InvokeSmartContractFunctionRevoke(errorMessage);
                case ExceptionCode.InsufficientFunds:
                    return new InsufficientFundsException(errorMessage);
                case ExceptionCode.InvocationError:
                    return new InvokeSmartContractFunctionFailure(errorMessage);
                case ExceptionCode.ReplaceRejectedError:
                    return new ReplaceRejectedError(errorMessage);
                case ExceptionCode.CancelRejectedError:
                    return new CancelRejectedError(errorMessage);
                case ExceptionCode.InvalidParameters:
                    return new ParameterException(errorMessage);
                case ExceptionCode.InvalidScipParam:
                    return new InvalidScipParameterException(errorMessage);
                default:
                    return new UnknownException();
            }
        } catch (JsonProcessingException ex) {
            return new UnknownException();
        }
    }
}
