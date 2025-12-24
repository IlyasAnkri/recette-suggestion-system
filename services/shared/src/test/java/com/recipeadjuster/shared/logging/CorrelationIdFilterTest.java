package com.recipeadjuster.shared.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrelationIdFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private CorrelationIdFilter filter;

    @BeforeEach
    void setUp() {
        filter = new CorrelationIdFilter();
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldUseExistingCorrelationIdFromHeader() throws ServletException, IOException {
        String existingCorrelationId = "existing-correlation-id";
        when(request.getHeader("X-Correlation-ID")).thenReturn(existingCorrelationId);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(MDC.get("correlationId")).isNull(); // MDC cleared after filter
    }

    @Test
    void shouldGenerateCorrelationIdWhenNotPresent() throws ServletException, IOException {
        when(request.getHeader("X-Correlation-ID")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(MDC.get("correlationId")).isNull(); // MDC cleared after filter
    }

    @Test
    void shouldClearMdcAfterFilterChain() throws ServletException, IOException {
        when(request.getHeader("X-Correlation-ID")).thenReturn("test-id");

        filter.doFilter(request, response, filterChain);

        assertThat(MDC.get("correlationId")).isNull();
    }
}
