package dikanev.nikita.core.service.server.URLParameter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpGetParameter implements Parameter {

    private Map<String, List<String>> parameters;

    public HttpGetParameter() {
        this.parameters = new HashMap<>();
    }

    public HttpGetParameter(String parameters) throws UnsupportedEncodingException {
        this.parameters = getMapFromHttpGet(parameters);
    }

    public HttpGetParameter(String key, List<String> parameter) {
        if (parameter == null || parameter.isEmpty()) {
            parameters = new HashMap<>();
        } else {
            this.parameters = new HashMap<>(Map.of(key, parameter));
        }
    }

    public HttpGetParameter(Map<String, String[]> parameterMap) {
        parameters = new HashMap<>();
        parameterMap.forEach((k, v) -> parameters.put(k, new ArrayList<>(List.of(v))));
    }

    @Override
    public Parameter getParameter(String parameter) {
        return new HttpGetParameter(parameter, parameters.get(parameter));
    }

    @Override
    public String getContent() {
        return mapToGetString(parameters);
    }

    @Override
    public String getContent(String param) {
        return listToGetString(param, parameters.get(param));
    }

    public List<String> get(String parameter) {
        return parameters.get(parameter);
    }

    @Override
    public List<String> getOrDefault(String key, List<String> def) {
        List<String> lst = get(key);
        return lst != null ? lst : def;
    }

    @Override
    public String getF(String parameter) {
        List<String> lst = parameters.get(parameter);
        if (lst != null && lst.size() > 0) {
            return lst.get(0);
        }
        return null;
    }

    @Override
    public String getFOrDefault(String parameter, String defaultValue) {
        String param = getF(parameter);
        return param != null ? param : defaultValue;
    }

    @Override
    public List<Integer> getInt(String param) {
        List<String> strList = get(param);
        if (strList == null) {
            return null;
        }

        List<Integer> intList = new ArrayList<>();
        strList.forEach(it -> intList.add(Integer.valueOf(it)));

        return intList;
    }

    @Override
    public List<Integer> getIntOrDefault(String param, List<Integer> def) {
        List<Integer> lst = getInt(param);
        return lst != null ? lst : def;
    }

    @Override
    public int getIntF(String param) {
        return Integer.valueOf(getF(param));
    }

    @Override
    public int getIntFOrDefault(String param, int def) {
        try {
            return getIntF(param);
        } catch (Exception e) {
            return def;
        }
    }

    @Override
    public boolean contains(String param) {
        return parameters.containsKey(param);
    }

    @Override
    public boolean containsAll(List<String> params) {
        for (String param : params) {
            if (!parameters.containsKey(param)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAllVal(String param, List<String> vals) {
        List<String> lst = get(param);
        if (lst == null || vals == null || lst.size() != vals.size()) {
            return false;
        }

            for (String temp : vals) {
                if (!lst.contains(temp)) {
                    return false;
                }
            }
        return true;
    }

    @Override
    public boolean containsVal(String param, String val) {
        List<String> lst = get(param);
        if (lst == null) {
            return false;
        }

        return lst.contains(val);
    }

    @Override
    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    private static Map<String, List<String>> getMapFromHttpGet(String query) throws UnsupportedEncodingException {
        final Map<String, List<String>> query_pairs = new HashMap<>();
        final String[] pairs = query.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            if (value != null) {
                List<String> parameter = query_pairs.getOrDefault(key, new ArrayList<>());
                parameter.add(value);
                query_pairs.put(key, parameter);
            }
        }

        return query_pairs;
    }

    private static String mapToGetString(Map<String, List<String>> params) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            for (String val : entry.getValue()) {
                builder.append("&").append(entry.getKey()).append("=").append(entry.getValue() != null ? escape(val) : "");
            }
        }

        return builder.toString();
    }

    private static String listToGetString(String key, List<String> params) {
        StringBuilder builder = new StringBuilder();
        for (String param : params) {
            builder.append("&").append(key).append("=").append(param != null ? escape(param) : "");
        }
        return builder.toString();
    }

    private static String escape(String urlData) {
        try {
            return URLEncoder.encode(urlData, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
