package com.employee.identity.config;

import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class PemUtils {

    private PemUtils() {
    }

    public static RSAPrivateKey readPrivateKey(
            Resource resource
    ) throws Exception {

        String key = readResource(resource)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded =
                Base64.getDecoder().decode(key);

        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(decoded);

        KeyFactory keyFactory =
                KeyFactory.getInstance("RSA");

        return (RSAPrivateKey)
                keyFactory.generatePrivate(keySpec);
    }

    public static RSAPublicKey readPublicKey(
            Resource resource
    ) throws Exception {

        String key = readResource(resource)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded =
                Base64.getDecoder().decode(key);

        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(decoded);

        KeyFactory keyFactory =
                KeyFactory.getInstance("RSA");

        return (RSAPublicKey)
                keyFactory.generatePublic(keySpec);
    }

    private static String readResource(
            Resource resource
    ) throws Exception {

        return new String(
                resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
    }
}