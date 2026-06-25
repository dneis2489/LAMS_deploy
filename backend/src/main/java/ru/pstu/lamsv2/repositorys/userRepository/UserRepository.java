package ru.pstu.lamsv2.repositorys.userRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.userDTO.UserDTO;
import ru.pstu.lamsv2.interfaces.userInterfaces.UserRepoInterface;

import java.util.*;

/**
    Репозиторий реализующий методы работы с пользователями. Содержит методы:
        1. Найти пользователя по email
        2. Найти пользователя по id
        3. Получить всех пользователей в системе
        4. Проверить, есть ли такой email в системе
        5. Добавление нового пользователя
        6. Обновление данных о пользовтеле
        7. Уладение пользователя по его id
*/

@Repository
public class UserRepository implements UserRepoInterface
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate)
    {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    //Найти пользователя по email
    @Override
    public List<UserDTO> findUserByEmail(String email)
    {
        String SQL = """
                SELECT u.id, u.email, u.username, u.password_hash, r.name, u.enabled, u.created_at, u.updated_at
                	FROM public.users u
                	LEFT JOIN public.roles r ON role_id = r.id
                	WHERE u.email = :email
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        return namedParameterJdbcTemplate.query(SQL, params, (rs, rowNum) -> new UserDTO(
                rs.getObject("id", UUID.class),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("name"),
                rs.getBoolean("enabled"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()

        ));
    }

    //Найти пользователя по id
    @Override
    public List<UserDTO> findById(UUID id)
    {
        String SQL = """
                SELECT u.id, u.email, u.username, u.password_hash, r.name, u.enabled, u.created_at, u.updated_at
                	FROM public.users u
                	LEFT JOIN public.roles r ON role_id = r.id
                	WHERE u.id = :id
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return namedParameterJdbcTemplate.query(SQL, params, (rs, rowNum) -> new UserDTO(
                rs.getObject("id", UUID.class),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("name"),
                rs.getBoolean("enabled"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()

        ));
    }

    //Найти всех пользователей
    @Override
    public List<UserDTO> findAll()
    {
        String SQL = """
                SELECT u.id, u.email, u.username, u.password_hash, r.name, u.enabled, u.created_at, u.updated_at
                	FROM public.users u
                	LEFT JOIN public.roles r ON role_id = r.id
                """;
        return jdbcTemplate.query(SQL, (rs, rowNum) -> new UserDTO(
                rs.getObject("id", UUID.class),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("name"),
                rs.getBoolean("enabled"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        ));
    }

    //Зарегистрирован ли данный email, если да, то вернет true
    @Override
    public boolean existsByEmail(String email)
    {
        String SQL = """
                SELECT 1
                	FROM public.users
                	WHERE email = :email
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        List<Integer> result = namedParameterJdbcTemplate.queryForList(SQL, params, Integer.class);
        return !result.isEmpty();
    }

    //Добавить нового пользователя
    @Override
    public UUID create(String email, String username, String passwordHash, Long roleId)
    {
        String SQL = """
                INSERT INTO public.users(email, username, password_hash, role_id)
                VALUES (:email, :username, :passwordHash, :roleId)
                RETURNING id
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("username", username);
        params.put("passwordHash", passwordHash);
        params.put("roleId", roleId);
        return namedParameterJdbcTemplate.queryForObject(SQL, params, UUID.class);
    }

    //Обновление данных о пользователе
    @Override
    public boolean update(UUID id, String email, String username, Long roleId, String passwordHash, boolean enabled)
    {
        String SQL = """
                UPDATE public.users
                SET 
                    email = :email,
                    username = :username,
                    role_id = :roleId,
                    password_hash = COALESCE(:passwordHash, password_hash),
                    enabled = :enabled,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = :id
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("username", username);
        params.put("roleId", roleId);
        params.put("passwordHash", passwordHash);
        params.put("enabled", enabled);
        params.put("id", id);
        return namedParameterJdbcTemplate.update(SQL, params) > 0;
    }

    //Удалить пользователя по id
    @Override
    public boolean delete(UUID id)
    {
        String SQL = """
                DELETE FROM public.users
                	WHERE id = :id
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return namedParameterJdbcTemplate.update(SQL, params) > 0;
    }
}
