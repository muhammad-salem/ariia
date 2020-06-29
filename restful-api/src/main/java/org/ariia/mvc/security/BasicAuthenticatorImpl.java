package org.ariia.mvc.security;

import com.sun.net.httpserver.BasicAuthenticator;

import java.util.Objects;

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
