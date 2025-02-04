package io.quarkus.oidc.runtime;

import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.oidc.common.runtime.OidcCommonUtils;

public class OidcConfigPropertySupplier implements Supplier<String> {
    private static final String AUTH_SERVER_URL_CONFIG_KEY = "quarkus.oidc.auth-server-url";
    private static final String END_SESSION_PATH_KEY = "quarkus.oidc.end-session-path";
    private String oidcConfigProperty;
    private String defaultValue;
    private boolean urlProperty;

    public OidcConfigPropertySupplier() {

    }

    public OidcConfigPropertySupplier(String oidcConfigProperty) {
        this(oidcConfigProperty, null);
    }

    public OidcConfigPropertySupplier(String oidcConfigProperty, String defaultValue) {
        this(oidcConfigProperty, defaultValue, false);
    }

    public OidcConfigPropertySupplier(String oidcConfigProperty, String defaultValue, boolean urlProperty) {
        this.oidcConfigProperty = oidcConfigProperty;
        this.defaultValue = defaultValue;
        this.urlProperty = urlProperty;
    }

    @Override
    public String get() {
        if (defaultValue != null || END_SESSION_PATH_KEY.equals(oidcConfigProperty)) {
            Optional<String> value = ConfigProvider.getConfig().getOptionalValue(oidcConfigProperty, String.class);
            if (value.isPresent()) {
                return checkUrlProperty(value.get());
            }
            return defaultValue;
        } else {
            return checkUrlProperty(ConfigProvider.getConfig().getValue(oidcConfigProperty, String.class));
        }
    }

    private String checkUrlProperty(String value) {
        if (urlProperty && !value.startsWith("http:")) {
            String authServerUrl = ConfigProvider.getConfig().getValue(AUTH_SERVER_URL_CONFIG_KEY, String.class);
            return OidcCommonUtils.getOidcEndpointUrl(authServerUrl, Optional.of(value));
        } else {
            return value;
        }
    }

    public String getOidcConfigProperty() {
        return oidcConfigProperty;
    }

    public void setOidcConfigProperty(String oidcConfigProperty) {
        this.oidcConfigProperty = oidcConfigProperty;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isUrlProperty() {
        return urlProperty;
    }

    public void setUrlProperty(boolean urlProperty) {
        this.urlProperty = urlProperty;
    }

}
