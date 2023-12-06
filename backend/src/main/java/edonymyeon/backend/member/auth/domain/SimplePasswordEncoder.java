package edonymyeon.backend.member.auth.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.NOT_SUPPORTED_ALGORITHM;
import static edonymyeon.backend.global.exception.ExceptionInformation.NOT_SUPPORTED_VERSION;

import edonymyeon.backend.global.exception.BusinessLogicException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.StringJoiner;
import org.springframework.stereotype.Component;

@Component
public class SimplePasswordEncoder implements PasswordEncoder {

    private static final String ALGORITHM = "SHA-256";
    private static final String VERSION = "SPE1";
    private static final String KEY_DELIMITER = "$";
    private static final int MAX_BOUND = Integer.MAX_VALUE;
    private static final int MAX_HEX = 0xff;

    private MessageDigest hashFunction = getHashFunctionInstance();

    private MessageDigest getHashFunctionInstance() {
        if (Objects.nonNull(hashFunction)) {
            return hashFunction;
        }
        try {
            hashFunction = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessLogicException(NOT_SUPPORTED_ALGORITHM);
        }
        return hashFunction;
    }

    @Override
    public String encode(final String rawPassword) {
        final int cost = generateCost();
        final String salt = generateSalt(VERSION, cost);

        final String encodedPassword = toHash64(salt + rawPassword);
        return appendEncodedInfo(encodedPassword, cost);
    }

    private int generateCost() {
        final SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt(MAX_BOUND) + 1;
    }

    private String generateSalt(String version, int cost) {
        validateVersion(version);
        return toHash64(Integer.toHexString(cost));
    }

    private void validateVersion(final String version) {
        if (VERSION.equals(version)) {
            return;
        }
        throw new BusinessLogicException(NOT_SUPPORTED_VERSION);
    }

    private String toHash64(String input) {
        byte[] encodedHash = hashFunction.digest(input.getBytes());

        final StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(MAX_HEX & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    private String appendEncodedInfo(final String encodedPassword, final int cost) {
        final StringJoiner stringJoiner = new StringJoiner(KEY_DELIMITER, KEY_DELIMITER, "");
        stringJoiner.add(VERSION);
        stringJoiner.add(Integer.toString(cost));
        stringJoiner.add(encodedPassword);
        return stringJoiner.toString();
    }

    @Override
    public boolean matches(final String rawPassword, final String encodedValue) {
        final String version = extractVersion(encodedValue);
        final int cost = extractCost(encodedValue);
        final String encodedPassword = extractEncodedPassword(encodedValue);

        final String salt = generateSalt(version, cost);
        return encodedPassword.equals(toHash64(salt + rawPassword));
    }

    private String extractVersion(final String encodedValue) {
        final int firstIndex = encodedValue.indexOf(KEY_DELIMITER);
        final int secondIndex = encodedValue.indexOf(KEY_DELIMITER, 1);

        return encodedValue.substring(firstIndex + 1, secondIndex);
    }

    private Integer extractCost(final String encodedValue) {
        final int secondIndex = encodedValue.indexOf(KEY_DELIMITER, 1);
        final int lastIndex = encodedValue.lastIndexOf(KEY_DELIMITER);

        return Integer.parseInt(encodedValue.substring(secondIndex + 1, lastIndex));
    }

    private String extractEncodedPassword(final String encodedValue) {
        final int lastIndex = encodedValue.lastIndexOf(KEY_DELIMITER);
        return encodedValue.substring(lastIndex + 1);
    }
}
