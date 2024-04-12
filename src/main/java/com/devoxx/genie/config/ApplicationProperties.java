package com.devoxx.genie.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Properties specific to Genie.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private Oauth2 oauth2;
    private final FirebaseSdk firebaseSdk = new FirebaseSdk();

    public Oauth2 getOauth2() {
        return oauth2;
    }

    public void setOauth2(Oauth2 oauth2) {
        this.oauth2 = oauth2;
    }

    public static class Oauth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();

        public List<String> getAuthorizedRedirectUris() {
            return authorizedRedirectUris;
        }

        public void setAuthorizedRedirectUris(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
        }
    }

    public FirebaseSdk getFirebaseSdk() {
        return firebaseSdk;
    }

    public static class FirebaseSdk {

        private String base64Credentials;

        public String getBase64Credentials() {
            return base64Credentials;
        }

        public void setBase64Credentials(String base64Credentials) {
            this.base64Credentials = base64Credentials;
        }

        public boolean isBase64CredentialsPresent() {
            return this.base64Credentials != null;
        }
    }
}
