package ru.pstu.lamsv2.services.userServices;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.application.userDTO.AuthResponseDTO;
import ru.pstu.lamsv2.exception.UnauthorizedException;
import ru.pstu.lamsv2.interfaces.userInterfaces.RefreshTokenRepoInterface;
import ru.pstu.lamsv2.interfaces.userInterfaces.RefreshTokenServiceInterface;
import ru.pstu.lamsv2.security.CustomUserDetails;
import ru.pstu.lamsv2.security.CustomUserDetailsService;
import ru.pstu.lamsv2.security.JwtProperties;
import ru.pstu.lamsv2.security.JwtService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenService implements RefreshTokenServiceInterface
{
    private final RefreshTokenRepoInterface refreshTokenRepository;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepoInterface refreshTokenRepository,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService,
            JwtProperties jwtProperties
    )
    {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public String createRefreshToken(UUID userId)
    {
        String refreshToken = generateRandomToken();
        String tokenHash = hashToken(refreshToken);

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusSeconds(jwtProperties.refreshTokenExpirationMs() / 1000);

        refreshTokenRepository.create(
                userId,
                tokenHash,
                expiresAt
        );

        return refreshToken;
    }

    @Override
    public String hashToken(String token)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Ошибка при хешировании refresh token", e);
        }
    }

    private String generateRandomToken()
    {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    @Override
    public AuthResponseDTO refreshTokens(String refreshToken)
    {
        String oldTokenHash = hashToken(refreshToken);

        UUID userId =
                refreshTokenRepository
                        .findUserIdByValidTokenHash(oldTokenHash)
                        .orElseThrow(() ->
                                new UnauthorizedException(
                                        "Refresh token недействителен или истёк"
                                )
                        );

        refreshTokenRepository.deleteByTokenHash(oldTokenHash);

        CustomUserDetails userDetails =
                userDetailsService.loadUserById(userId);

        String newAccessToken =
                jwtService.generateAccessToken(userDetails);

        String newRefreshToken =
                createRefreshToken(userId);

        return new AuthResponseDTO(
                newAccessToken,
                newRefreshToken
        );
    }

    @Override
    public void logout(String refreshToken)
    {
        String tokenHash = hashToken(refreshToken);
        refreshTokenRepository.deleteByTokenHash(tokenHash);
    }
}
