package com.ee5.mobile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;

public class PasswordProtection {

    private static byte[] hashPassword(String passwordToHash, byte[] salt){

        byte[] hashedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            hashedPassword = md.digest(passwordToHash.getBytes());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return hashedPassword;
    }

    private static byte[] getRandomSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return salt;
    }

    public static boolean checkPassword(String password, String storedPassword, String storedSalt){
        byte[] salt = Base64.getDecoder().decode(storedSalt);
        byte[] hashedPassword = hashPassword(password, salt);
        String hashedPasswordString = Base64.getEncoder().encodeToString(hashedPassword);

        if (hashedPasswordString.equals(storedPassword)) return true;
        else return false;
    }

    public static ArrayList<String> createPassword(String password){
        byte[] salt = getRandomSalt();
        byte[] hashedPassword = hashPassword(password, salt);
        String saltString = Base64.getEncoder().encodeToString(salt);
        String hashedPasswordString = Base64.getEncoder().encodeToString(hashedPassword);
        ArrayList<String> newSecurePassword = new ArrayList<String>();
        newSecurePassword.add(hashedPasswordString);
        newSecurePassword.add(saltString);
        return newSecurePassword;
    }

}