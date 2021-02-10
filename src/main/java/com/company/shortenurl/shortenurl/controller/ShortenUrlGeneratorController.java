package com.company.shortenurl.shortenurl.controller;

import com.company.shortenurl.shortenurl.exceptions.InvalidHashTypeException;
import com.company.shortenurl.shortenurl.exceptions.InvalidShortUrlException;
import com.company.shortenurl.shortenurl.exceptions.NotImplementedException;
import com.company.shortenurl.shortenurl.model.BulkUrlRequest;
import com.company.shortenurl.shortenurl.model.BulkUrlRequestBuilder;
import com.company.shortenurl.shortenurl.model.JobDefinition;
import com.company.shortenurl.shortenurl.model.UrlResource;
import com.company.shortenurl.shortenurl.service.ShortenUrlGeneratorService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/urlModifier")
@RestController
@Api(
        tags = "URL Shortening Service",
        description = "Provides the ability for a user to generate the shorter URL from the Full URL.\n"
                + " This Api Provides tiny URL generator functionality for single URL and it also have\n"
                + " capability to process bulk URLs contains in a file. When user sends a single URL \n"
                + " it generate the response with shorten URL and Full URL.\n"
                + "  \n"
                + " When user wanted to process bulk URLs then it has a capability to process it asynchronously.\n"
                + " Here user will upload the file which has contains URL's then API provides Job id. User can track\n"
                + " the processing of the Job based on job id. It provides the status of the job and any failed URLS in it.\n"
                + "\n"
                + " Example usage:\n"
                + " - createShortUrl : Accept the Full URL as a request and responded with Tiny URL.\n"
                + " - getOriginalUrl : Send the short URL and it Returns the Full URL. \n"
                + " - processBulkUrls : Upload the file which has  more URL's then it returns the Job id.\n"
                + " - getAllActiveJobs : It return the all in progress jobs with failed URL'S \n"
                + " - fetchShortUrls : Fetch the all short URL's stored in database.\n"
                + " - getJob : Fetch the job based on job id.\n"
                + " - getShortUrlsForJob : Fetch successful URL's for bulk Processing.\n"
)
public class ShortenUrlGeneratorController {
    @Autowired
    private ShortenUrlGeneratorService shortenUrlGeneratorService;

    public ShortenUrlGeneratorService getShortenUrlGeneratorService() {
        return shortenUrlGeneratorService;
    }

    public void setShortenUrlGeneratorService(ShortenUrlGeneratorService shortenUrlGeneratorService) {
        this.shortenUrlGeneratorService = shortenUrlGeneratorService;
    }

    @GetMapping("/{shortenUrl}")
    public @ResponseBody
    UrlResource getOriginalUrl(@PathVariable String shortenUrl) {
        UrlResource urlResource = shortenUrlGeneratorService.getOriginalUrl(shortenUrl);
        if (urlResource == null) {
            throw new InvalidShortUrlException("Invalid short URL. Please sent the correct one.");
        }
        return urlResource;
    }

    @PostMapping
    public @ResponseBody
    UrlResource createShortUrl(@RequestBody String originalUrl) {
            return shortenUrlGeneratorService.createShortUrl(originalUrl);
    }

    @GetMapping(path = "/allShorturls/", params = {"offset", "limit"})
    public @ResponseBody
    List<UrlResource> fetchShortUrls(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) throws InvalidShortUrlException {
        return shortenUrlGeneratorService.findAll(offset, limit);
    }

    @PostMapping("/uploadFile")
    public @ResponseBody
    JobDefinition processBulkUrls(@RequestPart("file") MultipartFile file) throws InvalidHashTypeException, NotImplementedException, IOException {
        BulkUrlRequest request = new BulkUrlRequestBuilder().createBulkURLRequest();
        request.setFile(file);
        return  shortenUrlGeneratorService.processBulkUrls(request);
    }

    @GetMapping("/allactivejobs")
    public @ResponseBody
    List<JobDefinition> getAllActiveJobs() throws InvalidShortUrlException {
        return shortenUrlGeneratorService.getAllActiveJobs();
    }

    @GetMapping("/job/")
    public @ResponseBody
    JobDefinition getJob(@RequestParam("jobid") String jobId) throws InvalidShortUrlException {
        return shortenUrlGeneratorService.findJob(jobId);
    }

    @GetMapping(path = "/shorturls/", params = {"jobid"})
    public @ResponseBody
    List<UrlResource> getShortUrlsForJob(@RequestParam("jobid") String jobId) throws InvalidShortUrlException {
        return shortenUrlGeneratorService.getShortUrlsForJob(jobId);
    }
}
