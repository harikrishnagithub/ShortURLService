# ShortenURLService
# Overview
   Short URL service provides the ability for a user to generate the shorter URL from the Full URL.This Api Provides tiny URL generator functionality for single URL and it also have capability to process bulk URLs contains in a file. When user sends a single URL it generate the response with shorten URL and Full URL. User wanted to process bulk URLs then it has a capability to process it asynchronously. Here user will upload the file which has contains URL’s then API provides Job id. User can track the processing of the Job based on job id. It provides the status of the job and any failed URLS in it.
 
 ## Documentation
 
 # Rest
 **create Short Url** : Accept the Full URL as a request and responded with Tiny URL. Here you can replace the %OriginalUrl% with the actual URL and ping it in CLI.
 
 `curl -X POST "http://localhost:9080/urlModifier" -H "accept: */*" -H "Content-Type: application/json" -d "\"%OriginalUrl%\""`
 
 Response
 
 `{
    "originalUrl": "string",
    "shortUrl": "string"
  }`
 
 **Get Original Url** : This end point is for Send the short URL and it Returns the Full URL. In below request you can replace the %shorturl% with the generated short url.
 If short url is not valid then system throws the message as "Invalid short URL. Please sent the correct one."
 
 `curl -X GET "http://localhost:9080/urlModifier/%shorturl%}" -H "accept: */*"`
 
 Response
 `{
    "originalUrl": "string",
    "shortUrl": "string"
  }`
 
 **fetch Short Urls** : Fetch the all short URL’s stored in data base. This end point has given the limitation on fetching the no of short url's. if we have millions of records then this endpoint will allow us to fetch only limited number. it will help to implement the pagination in front end.
  
  `curl -X GET "http://localhost:9080/urlModifier/allShorturls/?limit=1&offset=0" -H "accept: */*"`
 
 Response
 
 `[
    {
      "originalUrl": "string",
      "shortUrl": "string"
    }
  ]`
  
 **process Bulk Urls** : Upload the file which has more URL’s then it returns the Job id. Once file is uploaded then it gives the response immediately with jod id. We have implemented the Message Queue which will process the file asynchronously.
 
 `curl -X POST "http://localhost:9080/urlModifier/uploadFile" -H "accept: */*" -H "Content-Type: multipart/form-data" -F "file=@texttt2.txt;type=text/plain"`
 
 Response
 
 `{
    "createdDate": "2020-07-22T00:18:23.850Z",
    "failedUrls": [
      "string"
    ],
    "file": "string",
    "fileName": "string",
    "jobId": 0,
    "status": "PUBLISHED",
    "successUrls": [
      {
        "originalUrl": "string",
        "shortUrl": "string"
      }
    ]
  }`
  
 **get All Active Jobs** : It return the all in progress jobs. API return the response with failed and success urls.
 
 `curl -X GET "http://localhost:9080/urlModifier/allactivejobs" -H "accept: */*"`
 
 Response
 
 `[
    {
      "createdDate": "2020-07-22T00:22:39.164Z",
      "failedUrls": [
        "string"
      ],
      "file": "string",
      "fileName": "string",
      "jobId": 0,
      "status": "PUBLISHED",
      "successUrls": [
        {
          "originalUrl": "string",
          "shortUrl": "string"
        }
      ]
    }
  ]`
 
 **get Job** : Fetch the job based on job id.
 
 `curl -X GET "http://localhost:9080/urlModifier/?jobid=1234" -H "accept: */*"`
 
 Response
 
 `{
    "createdDate": "2020-07-22T00:25:45.165Z",
    "failedUrls": [
      "string"
    ],
    "file": "string",
    "fileName": "string",
    "jobId": 0,
    "status": "PUBLISHED",
    "successUrls": [
      {
        "originalUrl": "string",
        "shortUrl": "string"
      }
    ]
  }`
  
 **get Short Urls For Job** : Fetch successful URL’s for the specific file. Here need to pass the job id as request parameter.
   
   `curl -X GET "http://localhost:9080/urlModifier/shorturls/?jobid=hhj" -H "accept: */*"`
   
   Response
   `[
      {
        "originalUrl": "string",
        "shortUrl": "string"
      }
    ]`
 
 #Approach:
  **Generating Unique code** 
  
              I have written a code to generate a unique 6 digit alphanumeric code for short URL. To generate the code we have 
          appended the url with system timestamp, ip address and base64 alphanumeric random code. Then for this unique URL generated
          the hash code based on configured hash code generation types like MURMUR3, CRC32 etc.
          
          Now generated 6 digit base64 encoded string with the above hash code. i have provide the configuration to selete the type of hashing.
       
       `application.hashing.hashtype=MURMUR3`
       
       In simple words,
           Original URL + System Timestamp + ip address + Random alphanumeric number ---> Hash code(MURMUR3 hashcode) --> Base 64 code(6 digit)
       Once unique code is generated it will check against the database if key is not used then it will proceed to save. If unique code is already used
       then it will generate the new code untill code is available to use.
       
       
       System has a feature to upload a file which has more number of URLs. I have developed asynchronoues process to process the bulk urls. 
       
       Here I have used **Redis messaging Queue** which simple and high perfomant messaging service. Once file uploaded through "/UPloadfile"
       endpoint it will create a job in database and publish the job id to message publisher.  
       Then Message listerner pick the message and fetch the job from  database then it start processing. Here file will be processed in chunks.
       Chunk size is configured in applciation.properties. 
     
     `application.custom.processslotsize =1000`
     
         It will pick first 1000 urls then process it and store it in database the it will pick another 1000 and so on. I have provided the end point 
      "urlModifier/?jobid=1234" to trach the job. it gives all failed and success urls in that job.
         One more end point "urlModifier/allactivejobs" to fetch all active jobs.
      "urlModifier/shorturls/?jobid='abdhw2'" endpoint is for fetching the all generated short url from the perticulat file.
      
   #Data Base
      Here we need to store only short url and original url i preper to choose noSQL Key-value pair database. I have selected the Redis key-value pair database.
      **Advantages :**
      
      1. Redis allows storing key and value pairs as large as 512 MB.
      2. Redis uses its own hashing mechanism called Redis Hashing
      3. Redis offers data replication.
      4. Redis offers a pub/sub messaging system.
      **Messaging Queue**
         Redis offers a pub/sub messaging system. we can develop a high-performing messaging application using the Redis pub/sub mechanism using any language of your choice.
   
# Main Classes
   [ShortenUrlGeneratorController](com/company/shortenurl/shortenurl/controller/ShortenUrlGeneratorController.java) Controller class
 
   [HashFunctionFactory](com/company/shortenurl/shortenurl/hash/HashFunctionFactory.java) to generate the object for type of hash based on configuration.
   
   [ShortenUrlGeneratorService](com/company/shortenurl/shortenurl/service/ShortenUrlGeneratorService.java) Business logic to generate short urls
           
           UrlResource getUrlRecourse(String originalUrl): returns the short URL for the given original url.
           
           String generateUniqueCode(String originalUrl): Method for generating the unique code for short url.   
      
   [UrlShorteningRepository](com/company/shortenurl/shortenurl/repository/UrlShorteningRepository.java) Repository class which deal with DAO layer.
         
   [BulkUrlProcessingJobRepository](com/company/shortenurl/shortenurl/repository/BulkUrlProcessingJobRepository.java) Repository class for job processing which deal with DAO layer.
  
   **Entities**  
         
   [UrlResource](com/company/shortenurl/shortenurl/model/JobDefinition.java) 
      
   [BulkUrlRequest](com/company/shortenurl/shortenurl/model/JobDefinition.java) 
    
   [JobDefinition](com/company/shortenurl/shortenurl/model/JobDefinition.java) 
   
   
   **Good to Have**  
   
   1.  Purge policy if records are more than 30 days.
   2.  Make cloud native  we can select blob storage 
   3.  Insights: analytics on frequently accessed URLS and regions.
    
# Build Environment
    
    Steps:
       1. download or clone the repository from git hub.
       
          `$ gradle clean build`
       2. download Redis and extract the zip file. i have attahched the file redis-2.4.5-win32-win64. you can down it either from here or download it form redis website.
       3. start the server under /64bit/redis-server.bat.
       4. Start the application server.
           `gradle bootrun`
  **Swagger ENABLED**
  
   Link to access the swagger : http://localhost:9080/swagger-ui.html
