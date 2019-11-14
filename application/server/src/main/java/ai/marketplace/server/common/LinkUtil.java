package ai.marketplace.server.common;


//https://www.baeldung.com formats Link header
public class LinkUtil {
    public static String createLinkHeader(String uri, String rel) {
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }
}
