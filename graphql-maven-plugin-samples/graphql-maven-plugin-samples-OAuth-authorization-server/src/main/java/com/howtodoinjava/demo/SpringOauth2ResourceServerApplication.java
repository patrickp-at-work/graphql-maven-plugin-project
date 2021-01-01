package com.howtodoinjava.demo;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * To get the access token from the command line:
 * 
 * <PRE>
curl -X POST "http://localhost:8181/oauth/token" -i -d "grant_type=client_credentials" -u "theClientId:theClientSecret"
 * </PRE>
 * 
 * The response is, for instance:
 * 
 * <PRE>
{"access_token":"e47db1c1-b1d6-4c93-91d4-44c606bf25ee","token_type":"bearer","refresh_token":"dcaa4f20-a35d-429c-8d2c-2613541916a4","expires_in":4999,"scope":"read_profile_info"}
 * </PRE>
 * 
 * A GraphQL query could be:
 * 
 * <PRE>
curl -X POST http://localhost:8180/graphql -i -d "{\"query\":\"query{withoutParameters{name}}\",\"variables\":null,\"operationName\":null}" -H "authorization: Bearer aa9aa23f-7679-4513-b286-07ca583b2773" --noproxy "*"
 * </PRE>
 * 
 * @see https://aaronparecki.com/oauth-2-simplified/
 * @see https://howtodoinjava.com/spring-boot2/oauth2-auth-server/
 * @see https://www.javainuse.com/spring/springboot-oauth2-password-grant
 * @see https://www.javainuse.com/spring/springboot-oauth2-client-grant
 * @see https://www.baeldung.com/spring-security-oauth-resource-server
 * 
 *      Below: allows to use oAuth on the client side, with the Jersy client ?
 * @see https://www.baeldung.com/spring-security-social-login-jersey
 * 
 * @author etienne-sf
 */
@SpringBootApplication
@RestController
public class SpringOauth2ResourceServerApplication extends WebSecurityConfigurerAdapter {

	/** Logger for this class */
	Logger logger = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) {
		SpringApplication.run(SpringOauth2ResourceServerApplication.class, args);
	}

	/**
	 * This method will be used to check if the user has a valid token to access the resource.
	 *
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/validateUser", produces = MediaType.APPLICATION_JSON_VALUE)
	@GetMapping
	@PostMapping
	public String user(Principal user) {
		logger.debug("Received a request on /validateUser, with Principal '{}'", user);
		// return (user == null) ? new GenericPrincipal("no name", "no password", Arrays.asList("USER")) : user;
		// return (user == null) ? "no name" : user.getName();
		return "{\"active=\": true, \"scope\": \"USER\"}"; // Let's always say yes
	}

}
