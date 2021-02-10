package com.company.shortenurl.shortenurl.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Class for holding Job definitions
 */
public class JobDefinition implements Serializable {

    private String jobId;
    private String file;
    private String fileName;
    private JobStatus status;
    private List<String> failedUrls;
    private List<UrlResource> successUrls;
    private Date createdDate;

    public JobDefinition() {
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public List<String> getFailedUrls() {
        return failedUrls;
    }

    public void setFailedUrls(List<String> failedUrls) {
        this.failedUrls = failedUrls;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<UrlResource> getSuccessUrls() {
        return successUrls;
    }

    public void setSuccessUrls(List<UrlResource> successUrls) {
        this.successUrls = successUrls;
    }
}
