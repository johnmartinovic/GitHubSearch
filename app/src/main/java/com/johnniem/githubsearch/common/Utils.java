package com.johnniem.githubsearch.common;

/**
 * CGeneral helping tools
 */

public class Utils {

    public static String getNextLinkFromHeaderLink(String headerLink){
        if (headerLink == null) return null;

        int posNextBorder = headerLink.indexOf(">; rel=\"next\"");
        if ((posNextBorder < 0))
            return null;

        String nextLink = headerLink.substring(1, posNextBorder);

        return nextLink;
    }

}
