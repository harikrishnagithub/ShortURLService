package com.company.shortenurl.shortenurl.hash;

public enum HashFunctionType {

    MURMUR3("MURMUR3"),
    SHA1("SHA1"),
    SHA256("SHA256"),
    CRC32("CRC32"),
    MD5("MD5");

    private String type;

    HashFunctionType(String type) {
        this.type = type;
    }

    public static HashFunctionType valueOfType(String value) {
        for (HashFunctionType result : values()) {
            if (result.getType().equalsIgnoreCase(value)) {
                return result;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

}
