package ru.pstu.lamsv2.services.gantService;

import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.application.gantDTO.UserActivityData;
import ru.pstu.lamsv2.dto.application.gantDTO.UserActivityGrouppingDTO;
import ru.pstu.lamsv2.dto.getDataInDB.gantDTO.UserActivityRequestDTO;
import ru.pstu.lamsv2.interfaces.gantInterface.UserActivityGantRepoInterface;
import ru.pstu.lamsv2.interfaces.gantInterface.UserActivityGantServiceInterface;

import java.util.List;
import java.util.stream.Collectors;

/**
    Сервис реализующий методы получения данных для диаграмы Ганта активности пользователей
*/

@Service
public class UserActivityGantService implements UserActivityGantServiceInterface
{

    private final UserActivityGantRepoInterface  userActivityGantRepo;

    public UserActivityGantService(UserActivityGantRepoInterface userActivityGantRepo)
    {
        this.userActivityGantRepo = userActivityGantRepo;
    }

    //Получение данных по активности пользователей
    @Override
    public List<UserActivityGrouppingDTO> getUserActivityForGant()
    {
        List<UserActivityRequestDTO> data = userActivityGantRepo.getUserActivityForGant();
        return data.stream()
                .collect(Collectors.groupingBy(UserActivityRequestDTO::getUserName))
                .entrySet()
                .stream()
                .map(e -> new UserActivityGrouppingDTO(
                        e.getKey(),
                        e.getValue().size(),
                        e.getValue().stream()
                                .map(dto -> new UserActivityData(
                                        dto.getStartDate(),
                                        dto.getEndDate(),
                                        dto.getDuration(),
                                        dto.getMicroserviceName(),
                                        dto.getActionName()
                                ))
                                .toList()
                ))
                .toList();
    }
}

