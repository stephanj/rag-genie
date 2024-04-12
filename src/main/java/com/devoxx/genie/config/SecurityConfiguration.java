package com.devoxx.genie.config;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.io.IOException;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Import(SecurityProblemSupport.class)
@ConditionalOnWebApplication
public class SecurityConfiguration {

    private final CorsFilter corsFilter;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserDetailsService userDetailsService;
    private final SecurityProblemSupport problemSupport;

    public SecurityConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder,
                                 UserDetailsService userDetailsService,
                                 CorsFilter corsFilter,
                                 SecurityProblemSupport problemSupport) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDetailsService = userDetailsService;
        this.corsFilter = corsFilter;
        this.problemSupport = problemSupport;
    }

    @PostConstruct
    public void init() {
        try {
            authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
        throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OncePerRequestFilter securityResponseFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(@NotNull @org.jetbrains.annotations.NotNull HttpServletRequest request,
                                            @NotNull @org.jetbrains.annotations.NotNull HttpServletResponse response,
                                            @NotNull @org.jetbrains.annotations.NotNull FilterChain filterChain) throws ServletException, IOException {
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {

        http
            .cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedPage("/403")
                .accessDeniedHandler(problemSupport)
                .authenticationEntryPoint(problemSupport))
            .headers(headers -> headers.frameOptions(withDefaults()))
            .httpBasic(withDefaults())
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(authorizeRequests ->
                // prettier-ignore
                authorizeRequests
                    // Always ordered from the most specific to the least one
                    .requestMatchers(mvc.pattern("/api/validate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/**")).permitAll()
                    .requestMatchers(mvc.pattern("/app/**")).permitAll()
                    .requestMatchers(mvc.pattern("/assets/**")).permitAll()
                    .requestMatchers(mvc.pattern("/content/**")).permitAll()
                    .requestMatchers(mvc.pattern("/i18n/**")).permitAll()
                    .requestMatchers(mvc.pattern("/swagger-ui/**")).permitAll()
                    .requestMatchers(mvc.pattern("/test/**")).permitAll()
                    .requestMatchers(mvc.pattern("/v3/api-docs/**")).permitAll()
                    .requestMatchers(mvc.pattern("/**")).permitAll()
            )
            .addFilterAfter(securityResponseFilter(), BasicAuthenticationFilter.class)
            .requestCache(RequestCacheConfigurer::disable);

        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
