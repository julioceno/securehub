package com.securehub.auth.application.port.out;

public interface TokenEncryptorPort {
    String encrypt(String raw) throws Exception;
    boolean compare(String raw, String encrypted) throws Exception;
}
