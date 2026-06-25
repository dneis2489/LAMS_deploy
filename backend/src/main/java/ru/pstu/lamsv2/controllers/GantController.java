package ru.pstu.lamsv2.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pstu.lamsv2.dto.application.gantDTO.UserActivityGrouppingDTO;
import ru.pstu.lamsv2.interfaces.gantInterface.UserActivityGantServiceInterface;

import java.util.List;

/**
    Контроллер получения данных для дигаграммы ганта активности пользователей
*/

@RestController
@RequestMapping("lams")
public class GantController
{
    private final UserActivityGantServiceInterface  userActivityGantService;

    public GantController(UserActivityGantServiceInterface userActivityGantService)
    {
        this.userActivityGantService = userActivityGantService;
    }

    //Получение данных для диаграммы ганта по пользователям
    @GetMapping("/getGant")
    public List<UserActivityGrouppingDTO> getGant()
    {
        return userActivityGantService.getUserActivityForGant();
    }
}
