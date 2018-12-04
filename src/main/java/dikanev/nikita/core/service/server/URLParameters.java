package dikanev.nikita.core.service.server;

import com.sun.istack.internal.NotNull;
import dikanev.nikita.core.api.exceptions.ApiException;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class URLParameters {

    private static final Logger LOG = LoggerFactory.getLogger(URLParameters.class);

    private String baseParameters;
    private String transactionParameters = null;

    private boolean throwExceptionIfGetNull = false;

    private Map<String, List<String>> parameters;

    public URLParameters(String parameters) throws ApiException {
        try {
            this.baseParameters = parameters;
            this.parameters = getUrlParameters(parameters);
        } catch (Exception e) {
            LOG.warn("Url encoding exception");
            throw new ApiException(400, "Invalid URL parameters.", e.getMessage());
        }

    }

    public String[] get(String key) throws InvalidParametersException {
        List<String> params = parameters.get(key);
        if (params == null) {
            if (throwExceptionIfGetNull) {
                throw new InvalidParametersException("Params " + key + " not found." );
            }
            return null;
        }

        return (String[]) params.toArray();
    }

    public String getFirst(String key) throws InvalidParametersException {
        List<String> params = parameters.get(key);
        throwIfParameterNull(key, params);

        return (params != null && params.size() > 0 ? params.get(0) : null);
    }

    public Integer getFirstInt(String key) throws InvalidParametersException {
        List<String> params = parameters.get(key);
        throwIfParameterNull(key, params);

        return (params != null && params.size() > 0 ? Integer.valueOf(params.get(0)) : null);
    }

    public Boolean getFirstBoolean(String key) throws InvalidParametersException {
        List<String> params = parameters.get(key);
        throwIfParameterNull(key, params);

        return (params != null && params.size() > 0 ? Boolean.valueOf(params.get(0)) : null);
    }

    private void throwIfParameterNull(String key, List<String> params) throws InvalidParametersException {
        if ((params == null || params.size() == 0) && throwExceptionIfGetNull) {
            throw new InvalidParametersException("Params " + key + " not found.");
        }
    }

    public URLParameters set(String key, @NotNull String... val) {
        if (val == null ||  val.length == 0) {
            return this;
        }

        parameters.computeIfAbsent(key, k -> new ArrayList<>()).addAll(Arrays.asList(val));
        return this;
    }

    public boolean contains(@NotNull String... keys){
        if (keys == null || keys.length == 0) {
            return false;
        }

        for (String key : keys) {
            if (!parameters.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAndEquels(@NotNull String... v) {
        if (v == null || v.length % 2 != 0 || v.length == 0) {
            return false;
        }

        for (int i = 0; i < v.length; i += 2) {
            List<String> params = parameters.get(v[i]);

            if (params == null) {
                if (v[i + 1] != null) {
                    return false;
                }
            } else if (!params.contains(v[i + 1])) {
                return false;
            }
        }

        return true;
    }

    public String getURL() {
        return baseParameters;
    }

    public URLParameters transaction(){
        transactionParameters = mapToGetString(parameters);
        return this;
    }

    public URLParameters rollback(){
        if (transactionParameters != null) {
            try {
                parameters = getUrlParameters(transactionParameters);
            } catch (UnsupportedEncodingException ignore) {
            }
        }
        return this;
    }

    public URLParameters commit(){
        baseParameters = mapToGetString(parameters);
        return this;
    }

    public URLParameters endTransaction(){
        transactionParameters = null;
        return this;
    }

    private String mapToGetString(Map<String, List<String>> parameters) {
        //todo: сделать. Реализация есть в боте.
        return null;
    }

    private static Map<String, List<String>> getUrlParameters(String query) throws UnsupportedEncodingException {
        //todo: сделать. Реализация есть в боте.
        return null;
    }

}
