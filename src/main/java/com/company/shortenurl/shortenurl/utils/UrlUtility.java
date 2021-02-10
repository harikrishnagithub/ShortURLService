package com.company.shortenurl.shortenurl.utils;

import org.apache.commons.text.RandomStringGenerator;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Utility class for performing operations.
 */
public class UrlUtility {
    /**
     * Method for generating the unique URL by concatenating
     * full URL + timestamp + System IP address + Random Base64 unique code
     * @param url
     * @return
     */
    public static String getUniqueURl(String url) {
        StringBuilder sb = new StringBuilder(url);
        sb.append(System.currentTimeMillis());
        try {
            InetAddress systemIP = InetAddress.getLocalHost();
            sb.append(systemIP.getHostAddress());
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        sb.append(generateRandomCode());
        return sb.toString();
    }

    /**
     * Method validate the URL is valid or not.
     * @param fullUrl
     * @return
     */
    public static Boolean isValidUrl(String fullUrl) {
        try {
            //String urlPattern = "^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
            //return fullUrl.matches(urlPattern);
            new URL(fullUrl).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }

    /**
     *  Method for Encoding the bytes to string.
     * @param bytes
     * @return
     */
    public static String getEncodedString(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }

    /**
     * Method for decoding the String to bytes.
     * @param string
     * @return
     */
    public static byte[] getDecodedByteArray(String string) {
        return Base64.getDecoder().decode(string.getBytes());
    }

    /**
     * Method for converting the bytes to File.
     * @param bytes
     * @return
     */
    public static File writeByte(byte[] bytes) {
        String filePath = "Inputfile_" + System.currentTimeMillis() + ".txt";
        File file = new File(filePath);
        try {
            Path path = Paths.get(filePath);
            Files.write(path, bytes).getFileSystem();
        }
        catch (Exception e) {
            System.out.println("Exception during parsing the file: " + e);
        }
        return file;
    }

    /**
     * Generating the random code based on base 64.
     * @return
     */
    public static String generateRandomCode() {
        String alphaNumerics = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .selectFrom(alphaNumerics.toCharArray())
                .build();
        return generator.generate(6, 10);
    }

}
