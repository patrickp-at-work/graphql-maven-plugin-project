package com.howtodoinjava.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {

	// Added, thanks to https://stackoverflow.com/questions/52194081/spring-boot-oauth-unsupported-grant-type
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
				.allowFormAuthenticationForClients();
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients//
				.inMemory()//
				.withClient("theClientId")//
				.secret(passwordEncoder.encode("theClientSecret"))//
				.authorizedGrantTypes("client_credentials", "password", "authorization_code", "refresh_token")//
				.authorities("GRAPHQL").scopes("read_profile_info")
		// .resourceIds("oauth2-resource").redirectUris("http://localhost:8081/login")
		// .accessTokenValiditySeconds(5000).refreshTokenValiditySeconds(50000)
		;
	}

	// Added, thanks to https://stackoverflow.com/questions/52194081/spring-boot-oauth-unsupported-grant-type
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints.authenticationManager(authenticationManager);
	}
}
