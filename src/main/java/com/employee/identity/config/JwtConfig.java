package com.employee.identity.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtConfig {

    @Value("${jwt.private-key}")
    private Resource privateKeyResource;

    @Value("${jwt.public-key}")
    private Resource publicKeyResource;

    @Bean
    public RSAPrivateKey rsaPrivateKey()
            throws Exception {

        return PemUtils.readPrivateKey(
                privateKeyResource
        );
    }

    @Bean
    public RSAPublicKey rsaPublicKey()
            throws Exception {

        return PemUtils.readPublicKey(
                publicKeyResource
        );
    }

    @Bean
    public JwtEncoder jwtEncoder(
            RSAPublicKey publicKey,
            RSAPrivateKey privateKey
    ) {

        RSAKey rsaKey =
                new RSAKey.Builder(publicKey)
                        .privateKey(privateKey)
                        .build();

        JWKSource<SecurityContext> jwkSource =
                new ImmutableJWKSet<>(
                        new JWKSet(rsaKey)
                );

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(
            RSAPublicKey publicKey
    ) {

        return NimbusJwtDecoder
                .withPublicKey(publicKey)
                .build();
    }
}