package org.tpc.form_builder.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
@Log4j2
public class JWTUtils {

    @Value("${application.security.jwt-secret}")
    private String SECRET;

    private static final long expirationTime = 1000 * 60 * 60 * 24;  // 1 HOUR

    public String generateToken(String username) throws Exception{
        JWSSigner signer = new MACSigner(SECRET.getBytes());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issueTime(new Date())
                .issuer("TPC-Form-Builder")
                .expirationTime(new Date(System.currentTimeMillis() + expirationTime))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    public boolean validateToken(String token) {
        try {
            // Parse the token
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Build the verifier
            JWSVerifier verifier = new MACVerifier(SECRET.getBytes());

            // Verify the signature && validate expiration
            return signedJWT.verify(verifier) && !isTokenExpired(signedJWT);
        }
        catch (ParseException | JOSEException e) {
            log.error("Error validating token", e);
            return false;
        }
    }

    @SneakyThrows
    public boolean isTokenExpired(SignedJWT signedJWT) {
       Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
       return expiration.before(new Date());
    }

    public String extractUsername(String token) throws Exception{
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getSubject();
    }
}
