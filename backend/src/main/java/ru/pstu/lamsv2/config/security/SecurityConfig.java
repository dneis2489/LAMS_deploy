package ru.pstu.lamsv2.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.pstu.lamsv2.security.JwtAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
    Подключаем JWT-фильтр для запросов к Spring Security и отключит сессии.
    Фильтры необходимы для разгранечения доступа к эндпоинтам
*/

@Configuration
@EnableWebSecurity
public class SecurityConfig
{

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    )
    {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    //Фильтр доступа эндпоинтов
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception
    {

        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                //Устанавливаем для приложения политику STATELESS -> каждый запрос должен сам приносить JWT токен
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                //Подключаем AuthenticationProvider для проверки почты и пароля
                .authenticationProvider(authenticationProvider)

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/lams/login",
                                "/swagger-ui/index.html",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll() //Открытые эндпоинты

                        .requestMatchers(
                                "/lams/addUser",
                                "/lams/findByEmail",
                                "/lams/findById",
                                "/lams/findAllUsers",
                                "/lams/deleteUser",
                                "/lams/findAllRoles",
                                "/lams/updateUser"
                        ).hasAuthority("ROLE_SUPER_ADMIN") //Эндпоинты только для суперадмина

                        .anyRequest().authenticated() //Остальные эндпоинты доступны всем с валидным JWT
                )

                //Подключаем фильтр
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception
    {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {

        CorsConfiguration configuration = new CorsConfiguration();

        //адреса фронтенда, которым разрешено обращаться к backend
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        configuration.setExposedHeaders(List.of(
                "Authorization"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}
