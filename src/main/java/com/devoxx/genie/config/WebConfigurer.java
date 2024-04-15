package com.devoxx.genie.config;

import com.devoxx.genie.config.filter.CachingHttpHeadersFilter;
import io.github.jhipster.config.JHipsterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;

import static java.net.URLDecoder.decode;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;

    private final HipsterConfiguration hipsterConfiguration;

    public WebConfigurer(Environment env, HipsterConfiguration hipsterConfiguration) {
        this.env = env;
        this.hipsterConfiguration = hipsterConfiguration;
    }

    @Override
    public void onStartup(jakarta.servlet.ServletContext servletContext) {
        if (env.getActiveProfiles().length != 0) {
            LOGGER.info("Web application configuration, using profiles: {}", (Object[]) env.getActiveProfiles());
        }

        EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_PRODUCTION))) {
            initCachingHttpHeadersFilter(servletContext, disps);
        }
        LOGGER.info("Web application fully configured");
    }

    /**
     * Customize the Servlet engine: Mime types, the document root, the cache.
     */
    @Override
    public void customize(WebServerFactory server) {
        setMimeMappings(server);
        // When running in an IDE or with ./mvnw spring-boot:run, set location of the static web assets.
        setLocationForStaticAssets(server);
    }

    private void setMimeMappings(WebServerFactory server) {
        if (server instanceof ConfigurableServletWebServerFactory servletWebServer) {
            MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
            // IE issue, see https://github.com/jhipster/generator-jhipster/pull/711
            mappings.add("html", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
            // CloudFoundry issue, see https://github.com/cloudfoundry/gorouter/issues/64
            mappings.add("json", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
            servletWebServer.setMimeMappings(mappings);
        }
    }

    private void setLocationForStaticAssets(WebServerFactory server) {
        if (server instanceof ConfigurableServletWebServerFactory servletWebServer) {
            File root;
            String prefixPath = resolvePathPrefix();
            root = new File(prefixPath + "target/classes/static/");
            if (root.exists() && root.isDirectory()) {
                servletWebServer.setDocumentRoot(root);
            }
        }
    }

    /**
     * Resolve path prefix to static resources.
     */
    private String resolvePathPrefix() {
        String fullExecutablePath;
        fullExecutablePath = decode(this.getClass().getResource("").getPath(), StandardCharsets.UTF_8);
        String rootPath = Paths.get(".").toUri().normalize().getPath();
        String extractedPath = fullExecutablePath.replace(rootPath, "");
        int extractionEndIndex = extractedPath.indexOf("target/");
        if (extractionEndIndex <= 0) {
            return "";
        }
        return extractedPath.substring(0, extractionEndIndex);
    }

    /**
     * Initializes the caching HTTP Headers Filter.
     */
    private void initCachingHttpHeadersFilter(ServletContext servletContext,
                                              EnumSet<DispatcherType> dispatcherTypes) {
        LOGGER.debug("Registering Caching HTTP Headers Filter");

        CachingHttpHeadersFilter myCachingHttpHeadersFilter = new CachingHttpHeadersFilter(hipsterConfiguration);
        FilterRegistration.Dynamic cachingHttpHeadersFilter = servletContext.addFilter("cachingHttpHeadersFilter", myCachingHttpHeadersFilter);

        if (cachingHttpHeadersFilter != null) {
            cachingHttpHeadersFilter.addMappingForUrlPatterns(dispatcherTypes, true, "/i18n/*");
            cachingHttpHeadersFilter.addMappingForUrlPatterns(dispatcherTypes, true, "/content/*");
            cachingHttpHeadersFilter.addMappingForUrlPatterns(dispatcherTypes, true, "/app/*");
            cachingHttpHeadersFilter.setAsyncSupported(true);
        }
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = hipsterConfiguration.getCors();

        Optional<List<String>> allowedOrigins = Optional.ofNullable(config).map(CorsConfiguration::getAllowedOrigins);
        if (allowedOrigins.isPresent() && !allowedOrigins.get().isEmpty()) {
            LOGGER.debug("Registering CORS filter - allowed origins : {}", allowedOrigins);
            registerCorsConfigForPattern(source, config, "/api/**");
            registerCorsConfigForPattern(source, config, "/management/**");
            registerCorsConfigForPattern(source, config, "/v2/api-docs");
        }

        return new CorsFilter(source);
    }

    private void registerCorsConfigForPattern(UrlBasedCorsConfigurationSource source, CorsConfiguration config, String pattern) {
        source.registerCorsConfiguration(pattern, config);
    }
}
