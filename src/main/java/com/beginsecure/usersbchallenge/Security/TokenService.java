package com.beginsecure.usersbchallenge.Security;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// import com.auth0.jwt.JWT;
// import com.auth0.jwt.algorithms.Algorithm;
// import com.auth0.jwt.exceptions.JWTCreationException;
// import com.auth0.jwt.exceptions.JWTVerificationException;
// import com.auth0.jwt.interfaces.Claim;
import com.beginsecure.usersbchallenge.Util.Constants;

import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors

// @Service
// public class TokenService{
//     @Value("${api.security.token.secret}")
//     public String secret;
    
//     public TokenService(){}

//     public String generateAndExportToken() throws Exception{
//         try{
//             Algorithm algoSecret = Algorithm.HMAC256(this.secret);
//             String token = JWT.create()
//                             .withIssuer(Constants.APP_NAME)
//                             .withIssuedAt(new Date())
//                             .withExpiresAt(generateExpirationDate())
//                             .sign(algoSecret);

//             exportAPIToken(token);
//             return token;
//         }
//         catch(JWTCreationException e){
//             throw new RuntimeException("Errow while generating API Token");
//         }
//     }

//     public String validateToken(String token){
//         if(token == null || token.isBlank())
//             return null;
//         try{
//             Algorithm algoSecret = Algorithm.HMAC256(this.secret);
//             return JWT.require(algoSecret)
//                     .withIssuer(Constants.APP_NAME)
//                     .build()
//                     .verify(token)
//                     .getSubject();
//         }
//         catch(JWTVerificationException e){
//             return null;
//         }
//     }

//     public Date generateExpirationDate(){
//         Calendar calendar = Calendar.getInstance();
//         calendar.setTime(new Date());
//         calendar.add(Calendar.HOUR_OF_DAY, 2);
//         return calendar.getTime();
//     }

//     public void exportAPIToken(String token){
//         try {
//             FileWriter myWriter = new FileWriter(Constants.API_TOKEN_FILE_PATH);
//             myWriter.write(token);
//             myWriter.close();
//             System.out.println("Exported API Token to file " + Constants.API_TOKEN_FILE_PATH);
//         } catch (IOException e) {
//             System.out.println("An error occurred while exporting API Token.");
//             e.printStackTrace();
//         }
//     }
// }