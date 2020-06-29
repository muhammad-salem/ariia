package org.ariia.mvc.security;

public interface UserAuthenticator {

    /**
     * check Credentials for the giving user name and password
     *
     * @param type     the Authentication Type as
     *                 "Basic", "Bearer", "Digest", "HOBA", "Mutual", "Negotiate" and "OAuth"
     * @param username the provided user name
     * @param password the check against password
     * @return boolean, true >> user exist with correct password, false other cases
     */
    boolean checkCredentials(String type, String username, String password);


    /**
     * check Credentials for the giving Authentication Type with its authorization token
     *
     * @param type               the Authentication Type as
     *                           "Basic", "Bearer", "Digest", "HOBA", "Mutual", "Negotiate" and "OAuth"
     * @param authorizationToken the authorization token from "Authorization" request header
     * @return boolean, true >> user exist with correct password, false other cases
     */
    boolean checkCredentials(String type, String authorizationToken);

}
