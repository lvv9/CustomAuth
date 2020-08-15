package me.liuweiqiang.customauth.common.config.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public class TicketAuthToken extends AbstractAuthenticationToken {

    private static String credentialsName;
    private static String principalName;

    private Map<String, String> token;

    public TicketAuthToken(Map<String, String> token) {
        super(null);
        this.token = token;
        this.setAuthenticated(false);
    }

    public TicketAuthToken(Map<String, String> token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        super.setAuthenticated(true);
    }

    public static void setCredentialsName(String credentialsName) {
        TicketAuthToken.credentialsName = credentialsName;
    }

    public static void setPrincipalName(String principalName) {
        TicketAuthToken.principalName = principalName;
    }

    public Map<String, String> getToken() {
        return token;
    }

    @Override
    public String getCredentials() {
        return token.get(credentialsName);
    }

    @Override
    public String getPrincipal() {
        return token.get(principalName);
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        token.remove(credentialsName);
    }
}
