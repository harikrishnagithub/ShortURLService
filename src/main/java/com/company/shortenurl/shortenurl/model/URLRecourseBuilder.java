package com.company.shortenurl.shortenurl.model;

public class URLRecourseBuilder {
    private String shortUrl;
    private String originalUrl;

    public URLRecourseBuilder setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
        return this;
    }

    public URLRecourseBuilder setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
        return this;
    }

    public UrlResource createURLResourse() {
        return new UrlResource(shortUrl, originalUrl);
    }
}