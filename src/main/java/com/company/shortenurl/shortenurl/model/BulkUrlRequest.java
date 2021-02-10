package com.company.shortenurl.shortenurl.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * Class for holding the Bulk URL request.
 */
public class BulkUrlRequest implements Serializable {

    private MultipartFile file;
    private String name;

    BulkUrlRequest(MultipartFile file, String name) {
        this.file = file;
        this.name = name;
    }

    public BulkUrlRequest() {

    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BulkUrlRequest{"
                + "file="
                + file
                + ", name='" + name
                + '\''
                + '}';
    }
}
