package org.ariia.mvc.security;

import java.util.Objects;

import com.sun.net.httpserver.BasicAuthenticator;

public class BasicAuthenticatorImpl extends BasicAuthenticator {

	private UserAuthenticator authenticator;
	public BasicAuthenticatorImpl(String realm, UserAuthenticator authenticator) {
		super(realm);
		this.authenticator = Objects.requireNonNull(authenticator);
	}

	@Override
	public boolean checkCredentials(String username, String password) {
		return authenticator.checkCredentials("Basic", username, password);
	}

}
