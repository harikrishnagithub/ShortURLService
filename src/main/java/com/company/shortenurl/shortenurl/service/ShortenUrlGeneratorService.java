package com.company.shortenurl.shortenurl.service;

import com.company.shortenurl.shortenurl.exceptions.InvalidHashTypeException;
import com.company.shortenurl.shortenurl.exceptions.NotImplementedException;
import com.company.shortenurl.shortenurl.model.BulkUrlRequest;
import com.company.shortenurl.shortenurl.model.JobDefinition;
import com.company.shortenurl.shortenurl.model.UrlResource;

import java.io.IOException;
import java.util.List;

/**
 * Interface for shorten URL generation service.
 */
public interface ShortenUrlGeneratorService {
    /**
     *  Method to retrieve the original URL based on short url
     * @param shortUrl
     * @return
     * @throws InvalidShortUrlException
     */
    UrlResource getOriginalUrl(String shortUrl);

    /**
     * Method is for creating the short URLs from Original Url.
     * @param originalUrl
     * @return
     */
    UrlResource createShortUrl(String originalUrl);

    /**
     * Method to process the bulk URL with in the file.
     * @param url
     * @return
     * @throws InvalidHashTypeException
     * @throws NotImplementedException
     * @throws IOException
     */
    JobDefinition processBulkUrls(BulkUrlRequest url) throws InvalidHashTypeException, NotImplementedException, IOException;

    /**
     *   Method to create the short url from bulk URL's.
     * @param jobId
     * @param urlList
     * @throws InvalidHashTypeException
     * @throws NotImplementedException
     */
    void bulkGenerateShortenUrl(String jobId, List<String> urlList) throws InvalidHashTypeException, NotImplementedException;

    /**
     *
     * @param offset
     * @param limit
     * @return
     */
    List<UrlResource> findAll(Integer offset, Integer limit);

    /**
     * Method to fetch all Active jobs.
     * @return
     */
    List<JobDefinition> getAllActiveJobs();

    /**
     * Method to fetch job.
     * @param jobId
     * @return
     */
    JobDefinition findJob(String jobId);

    /**
     * Method to fetch job.
     * @param jobId
     * @return
     */
    List<UrlResource> getShortUrlsForJob(String jobId);
}
