package com.xebia.security;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by artur.skrzydlo on 2017-07-11.
 */
@Service
public class EncryptionServiceImpl implements EncryptionService {


    private StrongPasswordEncryptor passwordEncryptor;

    @Autowired
    public void setPasswordEncryptor(StrongPasswordEncryptor passwordEncryptor) {
        this.passwordEncryptor = passwordEncryptor;
    }

    @Override
    public String encryptString(String input) {
        return passwordEncryptor.encryptPassword(input);
    }

    @Override
    public boolean checkPassword(String plainPassword, String encryptedPassword) {
        return passwordEncryptor.checkPassword(plainPassword, encryptedPassword);
    }
}
