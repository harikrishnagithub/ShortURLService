package com.company.shortenurl.shortenurl.model;

import org.springframework.web.multipart.MultipartFile;

/**
 * Class for  bulk url request builder.
 */
public class BulkUrlRequestBuilder {
    private MultipartFile file;
    private String name;

    public BulkUrlRequestBuilder setFile(MultipartFile file) {
        this.file = file;
        return this;
    }

    public BulkUrlRequestBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public BulkUrlRequest createBulkURLRequest() {
        return new BulkUrlRequest(file, name);
    }
}