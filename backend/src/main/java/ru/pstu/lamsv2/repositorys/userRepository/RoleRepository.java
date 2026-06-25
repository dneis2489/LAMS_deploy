package ru.pstu.lamsv2.repositorys.userRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.userDTO.RoleDTO;
import ru.pstu.lamsv2.interfaces.userInterfaces.RoleRepoInterface;

import java.util.List;

/**
    Репозиторий реализующий методы работы с ролями пользователей. Содержит методы:
        1. Найти все роли
        2. Проверить существование роли по её id
*/

//Репозиторий для работы с ролями пользователей
@Repository
public class RoleRepository implements RoleRepoInterface
{
    private final JdbcTemplate jdbcTemplate;

    public RoleRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Найти все роли
    @Override
    public List<RoleDTO> findAll()
    {
        String sql = "select * from public.roles;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new RoleDTO(
                rs.getLong("id"),
                rs.getString("name")
        ));
    }

    //Проверить существование роли по её id
    @Override
    public boolean existsById(Long id)
    {
        String sql = """
            SELECT 1
            FROM public.roles
            WHERE id = ?
            """;

        List<Integer> result =
                jdbcTemplate.queryForList(
                        sql,
                        Integer.class,
                        id
                );

        return !result.isEmpty();
    }
}
