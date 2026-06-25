package ru.pstu.lamsv2.repositorys.predictRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pstu.lamsv2.dto.getDataInDB.predictDTO.DataFormatFromPredictDTO;
import ru.pstu.lamsv2.interfaces.predictInterface.repoInterface.PredictRequestStatusRepoInterface;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
    Репозиторий реализующий методы обновления прогноза по общему количеству статусов запросов. Включает в себя методы:
 1. Обновление таблицы данных с градацией по часам
 2. Обновление таблицы данных с градацией по дням
 3. Обновление таблицы данных с градацией по месяцам
*/

@Repository
public class InsertRequestStatusPredictRepo implements PredictRequestStatusRepoInterface
{

    private final JdbcTemplate jdbcTemplate;
    public InsertRequestStatusPredictRepo(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Обновить прогноз по статусу выполнения запросов с градацией по часам
    @Override
    public void updateTotalRequestStatusWithHourInDB(Map<Integer, List<DataFormatFromPredictDTO>> forecastList)
    {
        String sql = """
            INSERT INTO public.total_request_status_stats_hour
            (
                stat_hour,
                request_status_id,
                predict
            )
            VALUES (
                ?,
                (
                    SELECT id
                    FROM public.request_status
                    WHERE request_status_code = ?
                ),
                ?
            )
            ON CONFLICT (stat_hour, request_status_id)
            DO UPDATE SET
                predict = EXCLUDED.predict
    """;

        List<Object[]> batchArgs = new ArrayList<>();

        forecastList.forEach((statusCode, forecasts) -> {
            for (DataFormatFromPredictDTO forecast : forecasts) {
                batchArgs.add(new Object[]{
                        Timestamp.valueOf(forecast.getDate()),
                        statusCode,
                        forecast.getData()
                });
            }
        });

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    //Обновить прогноз по статусу выполнения запросов с градацией по дням
    @Override
    public void updateTotalRequestStatusWithDayInDB(Map<Integer, List<DataFormatFromPredictDTO>> forecastList)
    {
        String sql = """
            INSERT INTO public.total_request_status_stats_day
            (
                stat_day,
                request_status_id,
                predict
            )
            VALUES (
                ?,
                (
                    SELECT id
                    FROM public.request_status
                    WHERE request_status_code = ?
                ),
                ?
            )
            ON CONFLICT (stat_day, request_status_id)
            DO UPDATE SET
                predict = EXCLUDED.predict
    """;

        List<Object[]> batchArgs = new ArrayList<>();

        forecastList.forEach((statusCode, forecasts) -> {
            for (DataFormatFromPredictDTO forecast : forecasts) {
                batchArgs.add(new Object[]{
                        Timestamp.valueOf(forecast.getDate()),
                        statusCode,
                        forecast.getData()
                });
            }
        });

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    //Обновить прогноз по статусу выполнения запросов с градацией по месяцам
    @Override
    public void updateTotalRequestStatusWithMonthInDB(Map<Integer, List<DataFormatFromPredictDTO>> forecastList)
    {
        String sql = """
            INSERT INTO public.total_request_status_stats_month
            (
                stat_month,
                request_status_id,
                predict
            )
            VALUES (
                ?,
                (
                    SELECT id
                    FROM public.request_status
                    WHERE request_status_code = ?
                ),
                ?
            )
            ON CONFLICT (stat_month, request_status_id)
            DO UPDATE SET
                predict = EXCLUDED.predict
    """;

        List<Object[]> batchArgs = new ArrayList<>();

        forecastList.forEach((statusCode, forecasts) -> {
            for (DataFormatFromPredictDTO forecast : forecasts) {
                batchArgs.add(new Object[]{
                        Timestamp.valueOf(forecast.getDate()),
                        statusCode,
                        forecast.getData()
                });
            }
        });

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
