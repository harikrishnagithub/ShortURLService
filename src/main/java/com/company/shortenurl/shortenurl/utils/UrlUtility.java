package com.company.shortenurl.shortenurl.utils;

import org.apache.commons.text.RandomStringGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        sb.append(generateRandomCode());
        return sb.toString();
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
        String FILEPATH = "inputfile_"+System.currentTimeMillis()+".txt";
        File file = new File(FILEPATH);
        try {

            // Initialize a pointer
            // in file using OutputStream
            OutputStream
                    os
                    = new FileOutputStream(file);

            // Starts writing the bytes in it
            os.write(bytes);
            // Close the file
            os.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
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
