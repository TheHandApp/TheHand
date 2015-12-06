package com.tianyaqu.thehand.app.utils;

/**
 * Created by Alex on 2015/11/9.
 */
public class ImageUtil {
    public static String stripImageAttrs(String body, boolean keepImage) {

        if (keepImage) {
            body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
            body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
        } else {
            body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
        }
        return body;
    }
}
