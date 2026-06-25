package ru.pstu.lamsv2.subMethods.convertData.converForWeb;

import org.junit.jupiter.api.Test;
import ru.pstu.lamsv2.dto.application.statisticDTO.uniqueUsersForMethodsAggregation.UniqueUsersConvertDataDTO;
import ru.pstu.lamsv2.dto.getDataInDB.statisticDTO.microservicesStat.UniqueUsersForMethodStatDTO;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConvertUniqueUsersStatDataTest
{
    @Test
    void convertUniqueUsersStatGroupsByServiceAndActionAndKeepsUserList()
    {
        LocalDateTime firstDate = LocalDateTime.of(2026, 6, 11, 10, 0);
        LocalDateTime secondDate = LocalDateTime.of(2026, 6, 11, 11, 0);

        List<UniqueUsersConvertDataDTO> result = ConvertUniqueUsersStatData.convertUniqueUsersStat(List.of(
                new UniqueUsersForMethodStatDTO(
                        firstDate,
                        "auth-service",
                        "Login",
                        2L,
                        List.of("admin", "analyst"),
                        3.0
                ),
                new UniqueUsersForMethodStatDTO(
                        secondDate,
                        "auth-service",
                        "Login",
                        1L,
                        List.of("admin"),
                        2.0
                ),
                new UniqueUsersForMethodStatDTO(
                        firstDate,
                        "log-service",
                        "Search",
                        1L,
                        List.of("operator"),
                        1.0
                )
        ));

        assertThat(result).hasSize(2);

        UniqueUsersConvertDataDTO authService = result.stream()
                .filter(item -> item.getMicroserviceName().equals("auth-service"))
                .findFirst()
                .orElseThrow();

        assertThat(authService.getActionList()).hasSize(1);
        assertThat(authService.getActionList().get(0).getAction()).isEqualTo("Login");
        assertThat(authService.getActionList().get(0).getStatData())
                .extracting("date", "count", "users", "predict")
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple(firstDate, 2L, List.of("admin", "analyst"), 3.0),
                        org.assertj.core.groups.Tuple.tuple(secondDate, 1L, List.of("admin"), 2.0)
                );
    }

    @Test
    void convertUniqueUsersStatReturnsEmptyListForNullOrEmptyInput()
    {
        assertThat(ConvertUniqueUsersStatData.convertUniqueUsersStat(null)).isEmpty();
        assertThat(ConvertUniqueUsersStatData.convertUniqueUsersStat(List.of())).isEmpty();
    }
}
