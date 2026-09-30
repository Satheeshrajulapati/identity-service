//package com.employee.identity;
//
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.util.Base64;
//
//public class RsaKeyGenerator {
//
//    private static final Path KEY_DIRECTORY =
//            Path.of("src/main/resources/keys");
//
//    public static void main(String[] args) throws Exception {
//
//        KeyPairGenerator keyPairGenerator =
//                KeyPairGenerator.getInstance("RSA");
//
//        keyPairGenerator.initialize(2048);
//
//        KeyPair keyPair =
//                keyPairGenerator.generateKeyPair();
//
//        Files.createDirectories(KEY_DIRECTORY);
//
//        writePem(
//                "PRIVATE KEY",
//                keyPair.getPrivate().getEncoded(),
//                KEY_DIRECTORY.resolve("private.pem")
//        );
//
//        writePem(
//                "PUBLIC KEY",
//                keyPair.getPublic().getEncoded(),
//                KEY_DIRECTORY.resolve("public.pem")
//        );
//
//        System.out.println("RSA key pair generated successfully.");
//        System.out.println(
//                "Private key: " +
//                        KEY_DIRECTORY.resolve("private.pem")
//        );
//        System.out.println(
//                "Public key: " +
//                        KEY_DIRECTORY.resolve("public.pem")
//        );
//    }
//
//    private static void writePem(
//            String type,
//            byte[] encoded,
//            Path path
//    ) throws Exception {
//
//        String base64 =
//                Base64.getMimeEncoder(
//                        64,
//                        System.lineSeparator()
//                                .getBytes(StandardCharsets.UTF_8)
//                ).encodeToString(encoded);
//
//        String pem =
//                "-----BEGIN " + type + "-----"
//                        + System.lineSeparator()
//                        + base64
//                        + System.lineSeparator()
//                        + "-----END " + type + "-----"
//                        + System.lineSeparator();
//
//        Files.writeString(
//                path,
//                pem,
//                StandardCharsets.UTF_8
//        );
//    }
//}