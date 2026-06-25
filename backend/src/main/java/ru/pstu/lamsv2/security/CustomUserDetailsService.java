package ru.pstu.lamsv2.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.pstu.lamsv2.dto.getDataInDB.userDTO.UserDTO;
import ru.pstu.lamsv2.interfaces.userInterfaces.UserRepoInterface;

import java.util.List;
import java.util.UUID;

/**
    Создает для пользователя объект UserDetails при авторизации по логину
*/

@Component
public class CustomUserDetailsService implements UserDetailsService
{

    private final UserRepoInterface userRepository;

    public CustomUserDetailsService(UserRepoInterface userRepository)
    {
        this.userRepository = userRepository;
    }

    //При авторизации пользователя, вызывается loadUserByUsername(String email) для создания
    // под пользователя объекта CustomUserDetails необходимого для Spring Security
    @Override
    public CustomUserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException
    {

        UserDTO user = userRepository.findUserByEmail(email)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + email
                        )
                );

        return buildUserDetails(user);
    }

    //Получение данных о пользователе по его id
    public CustomUserDetails loadUserById(UUID id)
    {

        UserDTO user = userRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with id: " + id
                        )
                );

        return buildUserDetails(user);
    }

    //Создание CustomUserDetails для пользователя по его id
    private CustomUserDetails buildUserDetails(
            UserDTO user
    )
    {
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getRole(),
                user.isEnabled(),
                List.of(
                        new SimpleGrantedAuthority(
                                user.getRole()
                        )
                )
        );
    }
}
