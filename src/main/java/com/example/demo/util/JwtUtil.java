package com.example.demo.util;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	/*
	 * The specified key byte array is 48 bits which is not secure enough for any
	 * JWT HMAC-SHA algorithm. The JWT JWA Specification (RFC 7518, Section 3.2)
	 * states that keys used with HMAC-SHA algorithms MUST have a size >= 256 bits
	 * (the key size must be greater than or equal to the hash output size).
	 * Consider using the
	 * io.jsonwebtoken.security.Keys#secretKeyFor(SignatureAlgorithm) method to
	 * create a key guaranteed to be secure enough for your preferred HMAC-SHA
	 * algorithm. See https://tools.ietf.org/html/rfc7518#section-3.2 for more
	 * information.
	 * 
	 */
	@Value("${jwt.signing.secret:secretsecretsecretsecretsecretsecretsecretsecretsecretsecret}")
	private String secret;

	static final int JWT_TOKEN_VALIDITY = 60 * 60;

	public String getUsernameFromToken(String jwtToken) {
		return getClaimFromToken(jwtToken, Claims::getSubject);
	}

	public Date getExpirationFromToken(String jwtToken) {
		return getClaimFromToken(jwtToken, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {

		return claimsResolver.apply(getAllClaimsFromToken(token));

	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(secret.getBytes()).build().parseClaimsJws(token).getBody();
	}

	// check expiry
	public boolean validateToken(String jwtToken, UserDetails userDetails) {
		return !isTokenExpired(jwtToken);
	}

	private boolean isTokenExpired(String jwtToken) {
		return getExpirationFromToken(jwtToken).before(new Date());
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> extraClaims = new HashMap<>();
		return doGenerate(extraClaims, userDetails.getUsername());
	}

	private String doGenerate(Map<String, Object> extraClaims, String subject) {
		return Jwts.builder().setClaims(extraClaims).setSubject(subject).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				// key should be 256 bytes because we used hmac sha 256 algorithm
				.signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256).compact();

	}

	public ObjectNode decode(String token, boolean verifySign,Optional<String> encodedSecretkey) throws JsonProcessingException {
		String[] chunks = token.split("\\.");
		Base64.Decoder decoder = Base64.getUrlDecoder();
		

		String header = new String(decoder.decode(chunks[0]));
		String payload = new String(decoder.decode(chunks[1]));
//		String sign = new String(decoder.decode(chunks[2]));
		
		JsonNode headerObj = new ObjectMapper().readTree(header);
		JsonNode payloadObj = new ObjectMapper().readTree(payload);
		
		ObjectNode resp = new ObjectMapper().createObjectNode();
		resp.set("headers", headerObj);
		resp.set("payload", payloadObj);
//		resp.put("sign", sign);
		
		if (verifySign&& encodedSecretkey.isPresent()) {
			String decodedSecret=new String(decoder.decode(encodedSecretkey.get()));
			System.out.println("decoded secret: "+decodedSecret);
			String signAlgo = headerObj.get("alg").asText();
			SignatureAlgorithm algorithm = SignatureAlgorithm.valueOf(signAlgo);
//			SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), signAlgo);
//			Keys.hmacShaKeyFor(secretKey.getBytes()) internally calls line 114 SecretkeySpec Method only
			DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(algorithm,
					Keys.hmacShaKeyFor(decodedSecret.getBytes()), Decoders.BASE64URL);
			
			System.out.println(decoder.decode(chunks[2]));
			
			String jwtWithoutSignature=chunks[0] + "." + chunks[1];
			if (!validator.isValid(jwtWithoutSignature, chunks[2])) {
				resp.put("signValid", false);
			} else {
				resp.put("signValid", true);
			}
		}
		return resp;
	}

}
