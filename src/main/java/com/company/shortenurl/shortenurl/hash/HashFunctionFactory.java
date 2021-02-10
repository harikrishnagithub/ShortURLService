package com.company.shortenurl.shortenurl.hash;

import com.company.shortenurl.shortenurl.exceptions.InvalidHashTypeException;
import com.company.shortenurl.shortenurl.exceptions.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import static com.company.shortenurl.shortenurl.hash.HashFunctionType.SHA1;
import static com.company.shortenurl.shortenurl.hash.HashFunctionType.SHA256;

/**
 * Factory class for selecting the type of Hashing Techinique.
 */
@Component
@EnableConfigurationProperties
public class HashFunctionFactory {

    @Value("${application.hashing.hashtype}")
    private String hashType;

    /**
     *   Factory method to create a object based on hashing type
     * @return
     * @throws InvalidHashTypeException
     * @throws NotImplementedException
     */
    public HashFunction getHashGeneratorFactory() throws InvalidHashTypeException, NotImplementedException {
        if (hashType == null) {
            hashType = HashFunctionType.MURMUR3.getType();
        }
        HashFunctionType type = HashFunctionType.valueOfType(hashType);
        switch (type) {
            case CRC32:
                return new CRC32HashFunctionImplementation();
            case MURMUR3:
                return new MurmurHash3Implementation();
            case SHA1:
                throw new NotImplementedException(SHA1 + " is not Implemented yet. It is a upcoming Feature");
            case SHA256:
                throw new NotImplementedException(SHA256 + " is not Implemented yet. It is a upcoming Feature");
            default:
                throw new InvalidHashTypeException("Invalid hash type Configured. Please change the configuration. ");
        }
    }

    public String getHashType() {
        return hashType;
    }

    public void setHashType(String hashType) {
        this.hashType = hashType;
    }
}
