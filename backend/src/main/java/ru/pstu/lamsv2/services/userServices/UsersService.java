package ru.pstu.lamsv2.services.userServices;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.pstu.lamsv2.dto.application.userDTO.AuthResponseDTO;
import ru.pstu.lamsv2.dto.application.userDTO.CreateUserDTO;
import ru.pstu.lamsv2.dto.application.userDTO.LoginDTO;
import ru.pstu.lamsv2.dto.application.userDTO.RefreshTokenDTO;
import ru.pstu.lamsv2.dto.getDataInDB.userDTO.RoleDTO;
import ru.pstu.lamsv2.dto.getDataInDB.userDTO.UserDTO;
import ru.pstu.lamsv2.exception.BadRequestException;
import ru.pstu.lamsv2.exception.ConflictException;
import ru.pstu.lamsv2.exception.NotFoundException;
import ru.pstu.lamsv2.interfaces.userInterfaces.RefreshTokenServiceInterface;
import ru.pstu.lamsv2.interfaces.userInterfaces.RoleRepoInterface;
import ru.pstu.lamsv2.interfaces.userInterfaces.UserRepoInterface;
import ru.pstu.lamsv2.interfaces.userInterfaces.UsersServiceInterface;
import ru.pstu.lamsv2.security.CustomUserDetails;
import ru.pstu.lamsv2.security.CustomUserDetailsService;
import ru.pstu.lamsv2.security.JwtService;

import java.util.List;
import java.util.UUID;

@Service
public class UsersService implements UsersServiceInterface
{
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepoInterface userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepoInterface roleRepo;
    private final RefreshTokenServiceInterface refreshTokenService;

    public UsersService(
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService,
            UserRepoInterface userRepo,
            PasswordEncoder passwordEncoder,
            RoleRepoInterface roleRepo,
            RefreshTokenServiceInterface refreshTokenService
    )
    {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public AuthResponseDTO login(LoginDTO request)
    {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getEmail());

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        String refreshToken =
                refreshTokenService.createRefreshToken(userDetails.getId());

        return new AuthResponseDTO(accessToken, refreshToken);
    }

    @Override
    public AuthResponseDTO refresh(RefreshTokenDTO request)
    {
        return refreshTokenService.refreshTokens(request.getRefreshToken());
    }

    @Override
    public boolean addUser(CreateUserDTO request)
    {
        if (userRepo.existsByEmail(request.getEmail()))
        {
            throw new ConflictException("Пользователь с таким email уже существует");
        }

        if (!roleRepo.existsById(request.getRoleId()))
        {
            throw new BadRequestException("Роль с таким id не существует");
        }

        if (!request.getPassword().equals(request.getConfirmPassword()))
        {
            throw new BadRequestException("Пароли не совпадают");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        UUID userId = userRepo.create(
                request.getEmail(),
                request.getUsername(),
                passwordHash,
                request.getRoleId()
        );

        return userId != null;
    }

    @Override
    public List<UserDTO> findUserByEmail(String email)
    {
        return userRepo.findUserByEmail(email);
    }

    @Override
    public List<UserDTO> findById(UUID id)
    {
        return userRepo.findById(id);
    }

    @Override
    public List<UserDTO> findAllUsers()
    {
        return userRepo.findAll();
    }

    @Override
    public boolean update(UUID id, String email, String username, Long roleId, String password, boolean enabled)
    {
        if (!roleRepo.existsById(roleId))
        {
            throw new BadRequestException("Роль с таким id не существует");
        }

        String passwordHash = password == null || password.isBlank()
                ? null
                : passwordEncoder.encode(password);

        boolean updated = userRepo.update(
                id,
                email,
                username,
                roleId,
                passwordHash,
                enabled
        );

        if (!updated)
        {
            throw new NotFoundException("Пользователь не найден");
        }

        return true;
    }

    @Override
    public boolean delete(UUID id)
    {
        boolean deleted = userRepo.delete(id);
        if (!deleted)
        {
            throw new NotFoundException("Пользователь не найден");
        }

        return true;
    }

    @Override
    public List<RoleDTO> findAllRoles()
    {
        return roleRepo.findAll();
    }

    public void logout(RefreshTokenDTO request)
    {
        refreshTokenService.logout(request.getRefreshToken());
    }
}
