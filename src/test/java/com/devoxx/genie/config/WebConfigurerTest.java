package com.devoxx.genie.config;

import com.devoxx.genie.config.filter.CachingHttpHeadersFilter;
import io.github.jhipster.config.JHipsterConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the {@link WebConfigurer} class.
 */
class WebConfigurerTest {

    private WebConfigurer webConfigurer;

    private MockServletContext servletContext;

    private MockEnvironment env;

    private HipsterConfiguration props;

    @BeforeEach
    void setup() {
        servletContext = spy(new MockServletContext());
        doReturn(mock(FilterRegistration.Dynamic.class))
            .when(servletContext).addFilter(anyString(), any(Filter.class));

        env = new MockEnvironment();
        props = new HipsterConfiguration();

        webConfigurer = new WebConfigurer(env, props);
    }

    @Test
    void startUpProdServletContext() throws ServletException {
        env.setActiveProfiles(JHipsterConstants.SPRING_PROFILE_PRODUCTION);
        webConfigurer.onStartup(servletContext);

        verify(servletContext, atLeastOnce()).addFilter(eq("cachingHttpHeadersFilter"), any(CachingHttpHeadersFilter.class));
    }

    @Test
    void startUpDevServletContext() throws ServletException {
        env.setActiveProfiles(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT);
        webConfigurer.onStartup(servletContext);

        verify(servletContext, never()).addFilter(eq("cachingHttpHeadersFilter"), any(CachingHttpHeadersFilter.class));
    }

    @Test
    void customizeServletContainer() {
        env.setActiveProfiles(JHipsterConstants.SPRING_PROFILE_PRODUCTION);
        UndertowServletWebServerFactory container = new UndertowServletWebServerFactory();
        webConfigurer.customize(container);
        assertThat(container.getMimeMappings().get("html")).isEqualTo("text/html;charset=utf-8");
        assertThat(container.getMimeMappings().get("json")).isEqualTo("text/html;charset=utf-8");
        if (container.getDocumentRoot() != null) {
            assertThat(container.getDocumentRoot()).isEqualTo(new File("target/classes/static/"));
        }
    }

    @Test
    void corsFilterOnApiPath() throws Exception {
        props.getCors().setAllowedOrigins(List.of("other.domain.com"));
        props.getCors().setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        props.getCors().setAllowedHeaders(List.of("*"));
        props.getCors().setMaxAge(1800L);
        props.getCors().setAllowCredentials(true);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController())
            .addFilters(webConfigurer.corsFilter())
            .build();

        mockMvc.perform(
            options("/api/test-cors")
                .header(HttpHeaders.ORIGIN, "other.domain.com")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "other.domain.com"))
            .andExpect(header().string(HttpHeaders.VARY, "Origin"))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE"))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "1800"));

        mockMvc.perform(
            get("/api/test-cors")
                .header(HttpHeaders.ORIGIN, "other.domain.com"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "other.domain.com"));
    }

    @Test
    void corsFilterOnOtherPath() throws Exception {
        props.getCors().setAllowedOrigins(List.of("other.domain.com"));
        props.getCors().setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        props.getCors().setAllowedHeaders(List.of("*"));
        props.getCors().setMaxAge(1800L);
        props.getCors().setAllowCredentials(true);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController())
            .addFilters(webConfigurer.corsFilter())
            .build();

        mockMvc.perform(
            get("/test/test-cors")
                .header(HttpHeaders.ORIGIN, "other.domain.com"))
            .andExpect(status().isOk())
            .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void corsFilterDeactivated() throws Exception {
        props.getCors().setAllowedOrigins(null);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController())
            .addFilters(webConfigurer.corsFilter())
            .build();

        mockMvc.perform(
            get("/api/test-cors")
                .header(HttpHeaders.ORIGIN, "other.domain.com"))
            .andExpect(status().isOk())
            .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void corsFilterDeactivated2() throws Exception {
        props.getCors().setAllowedOrigins(new ArrayList<>());

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController())
            .addFilters(webConfigurer.corsFilter())
            .build();

        mockMvc.perform(
            get("/api/test-cors")
                .header(HttpHeaders.ORIGIN, "other.domain.com"))
            .andExpect(status().isOk())
            .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}
