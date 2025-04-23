package ie.gti.asdl.rey.gtirecord.desktop.util;

/**
 * @author Andrei Levchenko
 */
public class GtiStringUtils {

    public static String stripHtmlTags(String html) {
        return html.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
    }

}
