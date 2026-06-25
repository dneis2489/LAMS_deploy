package ru.pstu.lamsv2.repositorys.userRepository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.interfaces.userInterfaces.RefreshTokenRepoInterface;

import java.time.LocalDateTime;
import java.util.*;

/**
    Репозиторий реализующий методы работы с RefreshToken. Содержит методы:
        1. Создание RefreshToken
        2. Проверка, какому пользователю принадлежит токен
        3. Проверка валидность токена
        4. Удаление токена для выхода из системы
        5. Удаление всех токенов у конкретного пользователя. Для деактивации всех аккаунтов
*/

@Repository
public class RefreshTokenRepository implements RefreshTokenRepoInterface
{

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RefreshTokenRepository(
            NamedParameterJdbcTemplate jdbcTemplate
    )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Добавление токена
    @Override
    public void create(
            UUID userId,
            String tokenHash,
            LocalDateTime expiresAt
    )
    {
        String sql = """
                INSERT INTO public.refresh_tokens(
                    user_id,
                    token_hash,
                    expires_at
                )
                VALUES (
                    :userId,
                    :tokenHash,
                    :expiresAt
                )
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("tokenHash", tokenHash);
        params.put("expiresAt", expiresAt);

        jdbcTemplate.update(sql, params);
    }

    //Проверяем, какому пользователю принадлежит токен
    @Override
    public Optional<UUID> findUserIdByValidTokenHash(String tokenHash)
    {

        String sql = """
            SELECT user_id
            FROM public.refresh_tokens
            WHERE token_hash = :tokenHash
              AND expires_at > CURRENT_TIMESTAMP
            """;

        Map<String, Object> params = new HashMap<>();
        params.put("tokenHash", tokenHash);

        List<UUID> result =
                jdbcTemplate.queryForList(
                        sql,
                        params,
                        UUID.class
                );

        return result.stream().findFirst();
    }

    //Проверка валидности токена
    @Override
    public boolean existsValidToken(
            UUID userId,
            String tokenHash
    )
    {
        String sql = """
                SELECT 1
                FROM public.refresh_tokens
                WHERE user_id = :userId
                  AND token_hash = :tokenHash
                  AND expires_at > CURRENT_TIMESTAMP
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("tokenHash", tokenHash);

        return !jdbcTemplate
                .queryForList(sql, params, Integer.class)
                .isEmpty();
    }

    //Удаление токена
    @Override
    public void deleteByTokenHash(String tokenHash)
    {
        String sql = """
                DELETE FROM public.refresh_tokens
                WHERE token_hash = :tokenHash
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("tokenHash", tokenHash);

        jdbcTemplate.update(sql, params);
    }

    //Удаление всех токенов у пользователя
    @Override
    public void deleteAllByUserId(UUID userId)
    {
        String sql = """
                DELETE FROM public.refresh_tokens
                WHERE user_id = :userId
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        jdbcTemplate.update(sql, params);
    }
}
