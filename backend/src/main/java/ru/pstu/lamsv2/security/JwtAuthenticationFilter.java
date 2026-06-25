package ru.pstu.lamsv2.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    )
    {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try
        {
            authenticateByToken(request, token);
        }
        catch (RuntimeException e)
        {
            SecurityContextHolder.clearContext();
            writeUnauthorizedResponse(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateByToken(
            HttpServletRequest request,
            String token
    )
    {
        String email = jwtService.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            CustomUserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtService.isTokenValid(token, userDetails))
            {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
    }

    private void writeUnauthorizedResponse(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException
    {
        String requestId = request.getHeader("X-Request-Id");
        if (requestId == null || requestId.isBlank())
        {
            requestId = UUID.randomUUID().toString();
        }

        String body = """
                {"timestamp":"%s","status":401,"error":"Unauthorized","code":"UNAUTHORIZED","message":"Некорректный или истёкший access token","path":"%s","requestId":"%s","validationErrors":[]}
                """.formatted(
                OffsetDateTime.now(),
                escapeJson(request.getRequestURI()),
                escapeJson(requestId)
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.getWriter().write(body);
    }

    private String escapeJson(String value)
    {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
