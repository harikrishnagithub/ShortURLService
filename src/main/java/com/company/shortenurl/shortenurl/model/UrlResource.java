package com.company.shortenurl.shortenurl.model;

import java.io.Serializable;

/**
 * Class for holding URL Entities
 */
public class UrlResource implements Serializable {

    private String shortUrl;
    private String originalUrl;

    /**
     *
     * @param shortUrl
     * @param originalUrl
     */
    public UrlResource(String shortUrl, String originalUrl) {
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
    }

    /**
     * default Constructors
     */
    public UrlResource() {

    }

    public String getShortUrl() {
        return shortUrl;
    }

    /**
     *
     * @param shortUrl
     */
    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    /**
     *
     * @param originalUrl
     */
    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    @Override
    public String toString() {
        return "URLResourse{"
                + "shortUrl='" + shortUrl + '\''
                + ", originalUrl='" + originalUrl
                + '\''
                + '}';
    }

}
