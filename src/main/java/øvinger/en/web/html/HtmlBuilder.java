package øvinger.en.web.html;

import java.nio.charset.StandardCharsets;

/**
 * Quick and dirty builder class for building HTML-documents
 */
public class HtmlBuilder {

    private StringBuilder builder;

    /**
     * Creates a html-document builder with the given header and
     * some default header-tags. Following appends will be written
     * from the top of the body.
     */
    public HtmlBuilder(String title) {
        builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n<html lang=\"en\" dir=\"ltr\">\n<head><meta charset=\"utf-8\">");
        builder.append("<title>").append(title).append("</title></head><body>");
    }

    /**
     * Appends the given text to the html-document
     */
    public HtmlBuilder append(String s) {
        builder.append(s);
        return this;
    }

    /**
     * Completes the document with the body- and
     * html-closing tags and returns the data.
     */
    public byte[] complete() {
        builder.append("</body></html>");
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }
}