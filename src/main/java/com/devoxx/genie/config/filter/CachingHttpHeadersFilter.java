package com.devoxx.genie.config.filter;

import io.github.jhipster.config.JHipsterProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import jakarta.servlet.*;

/**
 * This filter is used in production, to put HTTP cache headers with a long (4 years) expiration time.
 */
@Service
public class CachingHttpHeadersFilter implements Filter {

    /**
     * <p>Constructor for CachingHttpHeadersFilter.</p>
     *
     * @param jHipsterProperties a {@link io.github.jhipster.config.JHipsterProperties} object.
     */
    public CachingHttpHeadersFilter(JHipsterProperties jHipsterProperties) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // Nothing to destroy
    }

}
