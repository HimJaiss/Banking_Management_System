package com.otpgenerator;
import java.security.SecureRandom;

public class OTPGenerator {

    private static final String DIGITS = "0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateOTP(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        return otp.toString();
    }

    public static void main(String[] args) {
        String otp = generateOTP(6); // Generate a 6-digit OTP
        System.out.println("Your OTP is: " + otp);
    }
}

