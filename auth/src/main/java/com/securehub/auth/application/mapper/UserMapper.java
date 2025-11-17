package com.securehub.auth.application.mapper;

import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
    User toEntity(UserDTO dto);
}