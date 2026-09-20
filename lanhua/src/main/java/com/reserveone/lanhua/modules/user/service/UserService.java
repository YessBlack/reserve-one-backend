package com.reserveone.lanhua.modules.user.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.reserveone.lanhua.modules.user.dto.UserRequestDto;
import com.reserveone.lanhua.modules.user.dto.UserResponseDto;
import com.reserveone.lanhua.modules.user.entity.Rol;
import com.reserveone.lanhua.modules.user.entity.User;
import com.reserveone.lanhua.modules.user.repository.RolRepository;
import com.reserveone.lanhua.modules.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponseDto> listUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponseDto saveUser(UserRequestDto dto) {
        Long roleId = (dto.getIdRol() != null) ? dto.getIdRol() : 2L;
        Rol rol = rolRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        User user = new User();
        user.setNameUser(dto.getNameUser());
        user.setLastNameUser(dto.getLastNameUser());
        user.setEmailUser(dto.getEmailUser());
        user.setPasswordUser(passwordEncoder.encode(dto.getPasswordUser()));
        user.setRol(rol);

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public UserResponseDto updateUser(Long id, UserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        user.setNameUser(dto.getNameUser());
        user.setLastNameUser(dto.getLastNameUser());
        user.setEmailUser(dto.getEmailUser());

        if (dto.getPasswordUser() != null && !dto.getPasswordUser().isEmpty()) {
            user.setPasswordUser(passwordEncoder.encode(dto.getPasswordUser()));
        }

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return mapToResponse(user);
    }

    private UserResponseDto mapToResponse(User user) {
        UserResponseDto response = new UserResponseDto();
        response.setIdUser(user.getIdUser());
        response.setNameUser(user.getNameUser());
        response.setLastNameUser(user.getLastNameUser());
        response.setEmailUser(user.getEmailUser());
        response.setNameRol(user.getRol().getNameRol());
        response.setCreationDate(user.getCreationDate());
        response.setUpdateDate(user.getUpdateDate());

        if (user.getMembership() != null) {
            response.setMembershipName(user.getMembership().getName());
        }

        response.setMembershipEndDate(user.getMembershipEndDate());
        response.setHasActiveMembership(user.hasActiveMembership());

        return response;
    }
}