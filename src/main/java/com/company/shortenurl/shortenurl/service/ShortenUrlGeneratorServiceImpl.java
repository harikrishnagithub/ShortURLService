package com.company.shortenurl.shortenurl.service;

import com.company.shortenurl.shortenurl.exceptions.FailedToProcessFileException;
import com.company.shortenurl.shortenurl.exceptions.FailedToCreateShortURLException;
import com.company.shortenurl.shortenurl.exceptions.InvalidHashTypeException;
import com.company.shortenurl.shortenurl.exceptions.InvalidJobIdentifierException;
import com.company.shortenurl.shortenurl.exceptions.InvalidShortUrlException;
import com.company.shortenurl.shortenurl.exceptions.NotImplementedException;
import com.company.shortenurl.shortenurl.hash.HashFunctionFactory;
import com.company.shortenurl.shortenurl.model.BulkUrlRequest;
import com.company.shortenurl.shortenurl.model.JobDefinition;
import com.company.shortenurl.shortenurl.model.JobStatus;
import com.company.shortenurl.shortenurl.model.URLRecourseBuilder;
import com.company.shortenurl.shortenurl.model.UrlResource;
import com.company.shortenurl.shortenurl.queue.MessagePublisherImpl;
import com.company.shortenurl.shortenurl.repository.BulkUrlProcessingJobRepository;
import com.company.shortenurl.shortenurl.repository.UrlShorteningRepository;
import com.company.shortenurl.shortenurl.utils.UrlUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Service class having business logic to generate the shorten URL's.
 */
@Service
public class ShortenUrlGeneratorServiceImpl implements ShortenUrlGeneratorService {

    @Autowired
    private HashFunctionFactory hashFunctionFactory;
    @Autowired
    private MessagePublisherImpl messagePublisherImpl;

    @Autowired
    private UrlShorteningRepository urlShorteningRepository;
    @Autowired
    private BulkUrlProcessingJobRepository bulkURLProcessingJobRepository;

    /**
     * Method to retrieve the original URL based on short url
     * @param shortUrl
     * @return
     */
    @Override
    public UrlResource getOriginalUrl(String shortUrl)  {
        try {
            UrlResource urlResourceEntity = urlShorteningRepository.find(shortUrl);
            return urlResourceEntity;
        }
        catch (Exception e) {
            throw new InvalidShortUrlException();
        }
    }

    /**
     * Method is for creating the short URLs from Original Url.
     * @param originalUrl
     * @return
     */
    @Override
    public UrlResource createShortUrl(String originalUrl) {
        if (!UrlUtility.isValidUrl(originalUrl)) {
            throw new FailedToCreateShortURLException("invalid Url. Please send the valid URL.");
        }
        try {
            UrlResource urlResourceEntity = getUrlRecourse(originalUrl);
            urlResourceEntity.setOriginalUrl(originalUrl);
            urlShorteningRepository.add(urlResourceEntity);
            return urlResourceEntity;
        }
        catch (InvalidHashTypeException | NotImplementedException ex) {
            throw  new FailedToCreateShortURLException(ex);
        }
    }

    /**
     * This method is for generating Unique id and create URL Resource entity.
     * @param url
     * @return
     * @throws InvalidHashTypeException
     * @throws NotImplementedException
     */
    private UrlResource getUrlRecourse(String originalUrl) throws InvalidHashTypeException, NotImplementedException {
        Boolean isKeyAvailableToUse = false;
        UrlResource urlResource = null;
        while (!isKeyAvailableToUse) {
            String shortUrl = generateUniqueCode(originalUrl);
            urlResource = urlShorteningRepository.find(shortUrl);
            if (urlResource == null) {
                urlResource = new URLRecourseBuilder().createURLResourse();
                urlResource.setShortUrl(shortUrl);
                urlResource.setOriginalUrl(originalUrl);
                isKeyAvailableToUse = true;
            }
        }
        return urlResource;
    }

    /**
     * This  method is to generate the Unique code for short URL.
     * Unique code generation is like below
     * concatenating full URL + timestamp + System IP address + Random Base64 unique code
     * then generate the Hashcode based on configured Hashing Type.
     * now generate 6 degit unique code by using the generated hashcode.
     * simplifying to URL+TImestamp+IP address+Random base64 code ---> Hashcode --> generate Base 64 unique code.
     * @param url
     * @return
     * @throws InvalidHashTypeException
     * @throws NotImplementedException
     */
    private String generateUniqueCode(String originalUrl) throws InvalidHashTypeException, NotImplementedException {
        String shortUrl;
        //generate uniqueUrl like URL+TImestamp+IP address+Random base64 code
        String uniqueURl = UrlUtility.getUniqueURl(originalUrl);
        //Hash code for configuring hashing Technique
        String hashCode = hashFunctionFactory.getHashGeneratorFactory().hash64(uniqueURl);
        //Generate 6 digit code.
        Base64 base64 = new Base64();
        shortUrl = new String(base64.encode(hashCode.getBytes(), 0, 5), StandardCharsets.UTF_8);
        return shortUrl;
    }

    /**
     * Method to create the short url from bulk URL's.
     * @param jobId
     * @param urlList
     * @throws InvalidHashTypeException
     * @throws NotImplementedException
     */
    @Override
    public void bulkGenerateShortenUrl(String jobId, List<String> urlList) throws InvalidHashTypeException, NotImplementedException {
        final List<UrlResource> urlRecourseList = new ArrayList<>();
        List<String> failedUrlList = new ArrayList<String>();
        urlList.stream().forEach(url -> {
            try {
                if (UrlUtility.isValidUrl(url)) {
                    urlRecourseList.add(getUrlRecourse(url));
                }
                else {
                    failedUrlList.add(url);
                }
            }
            catch (Exception e) {
                failedUrlList.add(url);
                e.printStackTrace();
            }
        });
        urlShorteningRepository.addAll(urlRecourseList);
        JobDefinition jobDefinition = bulkURLProcessingJobRepository.find(jobId);
        if (jobId != null && !urlRecourseList.isEmpty()) {
            jobDefinition.setSuccessUrls(urlRecourseList);
            bulkURLProcessingJobRepository.update(jobDefinition);
        }
        //updating the failed List of URL's.
        if (jobId != null && !failedUrlList.isEmpty()) {
            jobDefinition.setFailedUrls(failedUrlList);
            bulkURLProcessingJobRepository.update(jobDefinition);
        }

    }

    /**
     * Method to process the bulk URL with in the file.
     * @param source
     * @throws InvalidHashTypeException
     * @throws NotImplementedException
     * @throws IOException
     */
    @Override
    public JobDefinition processBulkUrls(BulkUrlRequest source) throws IOException {
        if (source == null) {
            return null;
        }
        /** Logic to generate the Unique Key for job ID.
         * Here checking the job id against Database. if
         * jobid is not used then isKeyAvailableToUse will become true
         * then proceed to use it.
         *
        */
        try {
            MultipartFile file = source.getFile();
            Boolean isKeyAvailableToUse = false;
            JobDefinition jobDefinition = null;
            Long jobId = 0L;
            while (!isKeyAvailableToUse) {
                Random random = new Random();
                jobId = Math.abs(random.nextLong());
                jobDefinition = bulkURLProcessingJobRepository.find(String.valueOf(jobId));
                if (jobDefinition == null) {
                    isKeyAvailableToUse = true;
                }
            }
            jobDefinition = new JobDefinition();
            jobDefinition.setJobId(String.valueOf(jobId));
            jobDefinition.setFile(UrlUtility.getEncodedString(file.getBytes()));
            jobDefinition.setStatus(JobStatus.QUEUED);
            jobDefinition.setFileName(file.getOriginalFilename());
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            jobDefinition.setCreatedDate(date);
            bulkURLProcessingJobRepository.add(jobDefinition);
            ObjectMapper obj = new ObjectMapper();
            jobDefinition.setFile(null);
            String message = obj.writeValueAsString(jobDefinition);
            messagePublisherImpl.publish(message);
        return jobDefinition;
        }
        catch (IOException io) {
            throw new FailedToProcessFileException("Failed to Process the File. Please check the file.");
        }
    }

    /**
     * Method to fetch the list of URL resources with in the limit.
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<UrlResource> findAll(Integer offset, Integer limit) {
        return urlShorteningRepository.findAllWithLimit(offset, limit);
    }

    /**
     * Method to fetch all in progress jobs.
     * @return
     */
    @Override
    public List<JobDefinition> getAllActiveJobs() {
        return bulkURLProcessingJobRepository.findActiveJobs();
    }

    /**
     * Method to fetch all in progress jobs.
     * @param jobId
     * @return
     */
    @Override
    public JobDefinition findJob(String jobId) {
        return bulkURLProcessingJobRepository.find(jobId);
    }

    /**
     *  Method to fetch Short URL's basaed on job.
     * @param jobId
     * @return
     */
    @Override
   public List<UrlResource> getShortUrlsForJob(String jobId) {
       JobDefinition jobDefinition = bulkURLProcessingJobRepository.find(jobId);
       if (jobDefinition == null) {
           throw new InvalidJobIdentifierException("Provided Job id is invalid.");
       }
       return jobDefinition.getSuccessUrls();
    }
}
