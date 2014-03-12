package de.newsarea.homecockpit.fsuipc2http.controller.converter;

public class NewsareaStatusMediaTypeConverter {

    public static String toFromUrlEncoded(String status) {
        StringBuilder out = new StringBuilder();
        out.append("status").append("=").append(status);
        return out.toString();
    }

}
