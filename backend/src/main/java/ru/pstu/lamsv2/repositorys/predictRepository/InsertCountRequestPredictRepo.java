package ru.pstu.lamsv2.repositorys.predictRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.interfaces.predictInterface.repoInterface.PredictCountRequestRepoInterface;

import java.sql.Timestamp;
import java.util.List;

/**
    Репозиторий реализующий методы обновления прогноза по общему количеству запросов. Включает в себя методы:
        1. Обновление таблицы данных с градацией по часам
        2. Обновление таблицы данных с градацией по дням
        3. Обновление таблицы данных с градацией по месяцам
*/

@Repository
public class InsertCountRequestPredictRepo implements PredictCountRequestRepoInterface
{
    private final JdbcTemplate jdbcTemplate;
    public InsertCountRequestPredictRepo(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Обновить прогноз по общему количеству запросов с градацией по часам
    @Override
    public void updateTotalCountRequestWithHourInDB(List<DataFormatFromPredictDTO> forecastList)
    {
        String sql = """
            INSERT INTO public.total_count_request_stats_hour
                (
                    stat_hour,
                    predict
                )
                VALUES (?, ?)
            
                ON CONFLICT(stat_hour)
                DO UPDATE SET
                    predict = EXCLUDED.predict
        """;

        jdbcTemplate.batchUpdate(
                sql,
                forecastList,
                forecastList.size(),
                (ps, dto) -> {

                    ps.setTimestamp(
                            1,
                            Timestamp.valueOf(dto.getDate())
                    );

                    ps.setDouble(
                            2,
                            dto.getData()
                    );
                }
        );
    }

    //Обновить прогноз по общему количеству запросов с градацией по дням
    @Override
    public void updateTotalCountRequestWithDayInDB(List<DataFormatFromPredictDTO> forecastList)
    {
        String sql = """
            INSERT INTO public.total_count_request_stats_day
                (
                    stat_day,
                    predict
                )
                VALUES (?, ?)
            
                ON CONFLICT(stat_day)
                DO UPDATE SET
                    predict = EXCLUDED.predict
        """;

        jdbcTemplate.batchUpdate(
                sql,
                forecastList,
                forecastList.size(),
                (ps, dto) -> {

                    ps.setTimestamp(
                            1,
                            Timestamp.valueOf(dto.getDate())
                    );

                    ps.setDouble(
                            2,
                            dto.getData()
                    );
                }
        );
    }

    //Обновить прогноз по общему количеству запросов с градацией по месяцам
    @Override
    public void updateTotalCountRequestWithMonthInDB(List<DataFormatFromPredictDTO> forecastList)
    {
        String sql = """
            INSERT INTO public.total_count_request_stats_month
                (
                    stat_month,
                    predict
                )
                VALUES (?, ?)
            
                ON CONFLICT(stat_month)
                DO UPDATE SET
                    predict = EXCLUDED.predict
        """;

        jdbcTemplate.batchUpdate(
                sql,
                forecastList,
                forecastList.size(),
                (ps, dto) -> {

                    ps.setTimestamp(
                            1,
                            Timestamp.valueOf(dto.getDate())
                    );

                    ps.setDouble(
                            2,
                            dto.getData()
                    );
                }
        );
    }
}
