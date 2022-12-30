package blockchains.iaas.uni.stuttgart.de.plugin;

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

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(post);
            logger.debug("POST API [{}] response code [{}]", url, response.getStatusLine().getStatusCode());
            result = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new Exception(result);
            }
            return result;

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

}
