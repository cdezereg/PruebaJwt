package cl.dp.signup.security.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;

@Component
public class JwtUtils {

	@Value("${cl.dp.jwtSecret}")
	private String jwtSecret;

	@Value("${cl.dp.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String generateJwtToken(String email, String password) {

		return Jwts.builder()
				.setSubject(email+":"+password)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public String getCoordFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			return false;
	    } catch (MalformedJwtException e) {
			return false;
	    } catch (ExpiredJwtException e) {
			return false;
	    } catch (UnsupportedJwtException e) {
			return false;
	    } catch (IllegalArgumentException e) {
			return false;
	    }
	}

}