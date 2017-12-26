package store.dicks.com.storelocator.network;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by vinay on 12/22/17.
 */

public class NetworkResponseParser {

    private static NetworkResponseParser responseParser;

    private NetworkResponseParser() {

    }

    public static NetworkResponseParser getInstance() {
        if (null == responseParser) {
            responseParser = new NetworkResponseParser();
        }
        return responseParser;
    }

    public Serializable parse(String response, Class dataModel) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jp = null;
        Serializable result = null;

        try {
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            jp = jsonFactory.createParser(response);
            // Added (Serializable) as Android Studio wouldn't compile this line otherwise. - BCrider
            result = (Serializable) objectMapper.readValue(jp, dataModel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
