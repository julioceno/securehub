package com.securehub.auth.application.util;

import java.util.concurrent.ThreadLocalRandom;

public final class GenerateCode {
    public static String generateCode(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = ThreadLocalRandom.current().nextInt(10);
            sb.append(digit);
        }

        return sb.toString();
    }

    public static String generateCode() {
        return generateCode(6);
    }
}
