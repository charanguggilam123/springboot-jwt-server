package com.example.demo.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.security.Keys;

public class Sample {

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
	
		String token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.i4Ae11pWgPMr2C-0xF51DPxiMRgCkhpMU6m8KtiBuPo";
		String secretKey="secretsecretsecretsecretsecretsecretsecretsecretsecretsecret";
		String[] chunks = token.split("\\.");
		String jwtWithoutSignature = chunks[0] + "." + chunks[1];

		
		Mac sha256Hmac = Mac.getInstance("HmacSHA256");
//		SecretKeySpec secretKeyspec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
//        sha256Hmac.init(secretKeyspec);
		sha256Hmac.init(Keys.hmacShaKeyFor(secretKey.getBytes(Charset.forName("US-ASCII"))));
        
        byte[] signedBytes = sha256Hmac.doFinal(jwtWithoutSignature.getBytes(Charset.forName("US-ASCII")));
        String signedBytesEncoded =Base64.getUrlEncoder().encodeToString(signedBytes);
        System.out.println(signedBytesEncoded.equals(chunks[2]));
        System.out.println(signedBytesEncoded);
        System.out.println(chunks[2]);
//        System.out.println(new String(signedBytesEncoded));
//        System.out.println(  hmacSha256(jwtWithoutSignature, secretKey));
        System.out.println(validator(secretKey));

	}
	
	
	private static boolean validator(String secret) throws NoSuchAlgorithmException, InvalidKeyException {
		System.out.println("in validatorrr");
		String token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.i4Ae11pWgPMr2C-0xF51DPxiMRgCkhpMU6m8KtiBuPo";
		 byte[] hash = secret.getBytes(StandardCharsets.UTF_8);//digest.digest(secret.getBytes(StandardCharsets.UTF_8));

		 String[] chunks = token.split("\\.");
			String jwtWithoutSignature = chunks[0] + "." + chunks[1];
         Mac sha256Hmac = Mac.getInstance("HmacSHA256");
         SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
        sha256Hmac.init(secretKey);

         byte[] signedBytes = sha256Hmac.doFinal(jwtWithoutSignature.getBytes(StandardCharsets.UTF_8));
         String signedBytesEncoded =Base64.getUrlEncoder().withoutPadding().encodeToString(signedBytes);
         System.out.println(signedBytesEncoded);
		
         return false;
		
	}
	/*private static HttpEntity<ObjectNode> prepareEntity() {
		// TODO Auto-generated method stub
		ObjectNode req = new ObjectMapper().createObjectNode();
		MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", "Bearer "+"token");
		return new HttpEntity<>(req, headers);
	}*/

}
