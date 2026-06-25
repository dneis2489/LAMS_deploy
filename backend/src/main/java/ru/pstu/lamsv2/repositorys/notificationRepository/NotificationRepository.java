package ru.pstu.lamsv2.repositorys.notificationRepository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.notificationDTO.NotificationSettingDTO;
import ru.pstu.lamsv2.dto.getDataInDB.notificationDTO.PendingNotificationDTO;
import ru.pstu.lamsv2.enums.NotificationCategory;
import ru.pstu.lamsv2.interfaces.notificationInterfaces.NotificationRepositoryInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class NotificationRepository implements NotificationRepositoryInterface
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public NotificationRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<NotificationSettingDTO> getSettings(UUID userId)
    {
        String sql = """
                SELECT
                    nc.code,
                    nc.title,
                    COALESCE(uns.enabled, false) AS enabled
                FROM public.notification_categories nc
                LEFT JOIN public.user_notification_settings uns
                    ON uns.category_id = nc.id
                    AND uns.user_id = :user_id
                ORDER BY nc.id
                """;
        return namedParameterJdbcTemplate.query(
                sql,
                Map.of("user_id", userId),
                (rs, rowNum) -> new NotificationSettingDTO(
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getBoolean("enabled")
                )
        );
    }

    @Override
    public void updateSettings(UUID userId, List<NotificationCategory> enabledCategories)
    {
        String disableSql = """
                INSERT INTO public.user_notification_settings(user_id, category_id, enabled)
                SELECT :user_id, id, false
                FROM public.notification_categories
                ON CONFLICT (user_id, category_id) DO UPDATE
                    SET enabled = false,
                        updated_at = CURRENT_TIMESTAMP
                """;
        namedParameterJdbcTemplate.update(disableSql, Map.of("user_id", userId));

        if (enabledCategories == null || enabledCategories.isEmpty())
        {
            return;
        }

        String enableSql = """
                INSERT INTO public.user_notification_settings(user_id, category_id, enabled)
                SELECT :user_id, id, true
                FROM public.notification_categories
                WHERE code IN (:codes)
                ON CONFLICT (user_id, category_id) DO UPDATE
                    SET enabled = true,
                        updated_at = CURRENT_TIMESTAMP
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("codes", enabledCategories.stream().map(Enum::name).toList());
        namedParameterJdbcTemplate.update(enableSql, params);
    }

    @Override
    public void enqueue(NotificationCategory category, String subject, String body)
    {
        String sql = """
                INSERT INTO public.notification_outbox(category_id, subject, body)
                SELECT id, :subject, :body
                FROM public.notification_categories
                WHERE code = :category_code
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("category_code", category.name());
        params.put("subject", subject);
        params.put("body", body);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<PendingNotificationDTO> getPending(int limit)
    {
        String sql = """
                SELECT no.id, nc.code, no.subject, no.body
                FROM public.notification_outbox no
                JOIN public.notification_categories nc ON nc.id = no.category_id
                WHERE no.status = 'PENDING'
                  AND no.next_attempt_at <= CURRENT_TIMESTAMP
                ORDER BY no.created_at
                LIMIT :limit
                """;
        return namedParameterJdbcTemplate.query(
                sql,
                Map.of("limit", limit),
                (rs, rowNum) -> new PendingNotificationDTO(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("subject"),
                        rs.getString("body")
                )
        );
    }

    @Override
    public List<String> getRecipientEmails(String categoryCode)
    {
        String sql = """
                SELECT DISTINCT u.email
                FROM public.users u
                JOIN public.user_notification_settings uns ON uns.user_id = u.id
                JOIN public.notification_categories nc ON nc.id = uns.category_id
                WHERE u.enabled = true
                  AND uns.enabled = true
                  AND nc.code = :category_code
                """;
        return namedParameterJdbcTemplate.queryForList(sql, Map.of("category_code", categoryCode), String.class);
    }

    @Override
    public void markSent(long notificationId)
    {
        String sql = """
                UPDATE public.notification_outbox
                SET status = 'SENT',
                    sent_at = CURRENT_TIMESTAMP,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = :id
                """;
        namedParameterJdbcTemplate.update(sql, Map.of("id", notificationId));
    }

    @Override
    public void markFailed(long notificationId, String errorMessage)
    {
        String sql = """
                UPDATE public.notification_outbox
                SET attempts = attempts + 1,
                    status = CASE WHEN attempts + 1 >= 5 THEN 'FAILED' ELSE 'PENDING' END,
                    last_error = :last_error,
                    next_attempt_at = CURRENT_TIMESTAMP + make_interval(mins => LEAST(60, (attempts + 1) * 5)),
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = :id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", notificationId)
                .addValue("last_error", errorMessage == null ? null : errorMessage.substring(0, Math.min(errorMessage.length(), 1000)));
        namedParameterJdbcTemplate.update(sql, params);
    }
}
