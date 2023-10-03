package me.tonoy.cashcard.common;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomString {
    private static final String ALPHA_NUM = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int DEFAULT_LENGTH = 12;
    private static final SecureRandom random = new SecureRandom();

    public static int randomInteger(final int maximum) {
        return Math.abs(random.nextInt() % maximum);
    }

    public static String next(final String prefix, final Optional<Integer> maybeLength) {
        final var length = maybeLength.orElse(DEFAULT_LENGTH);
        return prefix + "_" + IntStream.range(0, length)
                .mapToObj(m -> ALPHA_NUM.charAt(randomInteger(ALPHA_NUM.length())))
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
