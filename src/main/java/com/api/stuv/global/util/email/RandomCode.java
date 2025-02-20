package com.api.stuv.global.util.email;

import java.security.SecureRandom;

public class RandomCode {
    public static String getRandomCode() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
        int length = 10;

        SecureRandom random = new SecureRandom();
        StringBuilder randomCode = new StringBuilder();

        for(int i = 0 ; i < length ; i++){
            int index = random.nextInt(alphabet.length());
            randomCode.append(alphabet.charAt(index));
        }

        return randomCode.toString();
    }

}
