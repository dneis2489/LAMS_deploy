package ru.pstu.lamsv2.repositorys.predictRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.interfaces.predictInterface.repoInterface.PredictDurationRepoInterface;

import java.sql.Timestamp;
import java.util.List;

/**
    Репозиторий реализующий методы обновления прогноза по длительности выполнения запросов. Включает в себя методы:
        1. Обновление таблицы данных с градацией по часам
        2. Обновление таблицы данных с градацией по дням
        3. Обновление таблицы данных с градацией по месяцам
*/

@Repository
public class InsertDurationPredictRepo implements PredictDurationRepoInterface
{
    private final JdbcTemplate jdbcTemplate;
    public InsertDurationPredictRepo(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Обновить прогноз по длительности выполнения запросов с градацией по часам
    @Override
    public void updateTotalDurationWithHourInDB(List<DataFormatFromPredictDTO> forecastList)
    {
        String sql = """
            INSERT INTO public.total_duration_stats_hour
                (
                    stat_hour,
                    avg_predict
                )
                VALUES (?, ?)
            
                ON CONFLICT(stat_hour)
                DO UPDATE SET
                    avg_predict = EXCLUDED.avg_predict;
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
    public void updateTotalDurationWithDayInDB(List<DataFormatFromPredictDTO> forecastList)
    {
        String sql = """
            INSERT INTO public.total_duration_stats_day
                (
                    stat_day,
                    avg_predict
                )
                VALUES (?, ?)
            
                ON CONFLICT(stat_day)
                DO UPDATE SET
                    avg_predict = EXCLUDED.avg_predict;
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
    public void updateTotalDurationWithMonthInDB(List<DataFormatFromPredictDTO> forecastList)
    {
        String sql = """
            INSERT INTO public.total_duration_stats_month
                (
                    stat_month,
                    avg_predict
                )
                VALUES (?, ?)
            
                ON CONFLICT(stat_month)
                DO UPDATE SET
                    avg_predict = EXCLUDED.avg_predict;
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
