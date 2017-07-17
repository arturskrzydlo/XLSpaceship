package com.xebia.security;

/**
 * Created by artur.skrzydlo on 2017-07-11.
 */
public interface EncryptionService {

    String encryptString(String input);

    boolean checkPassword(String plainPassword, String encryptedPassword);
}
