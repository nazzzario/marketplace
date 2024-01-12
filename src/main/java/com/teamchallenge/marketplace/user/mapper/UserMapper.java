package com.teamchallenge.marketplace.user.mapper;

import com.teamchallenge.marketplace.user.dto.request.UserPatchRequestDto;
import com.teamchallenge.marketplace.user.dto.request.UserRequestDto;
import com.teamchallenge.marketplace.user.dto.response.UserResponseDto;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    UserEntity toEntity(UserRequestDto requestDto);

    UserResponseDto toResponseDto(UserEntity userEntity);


    @Mapping(target = "role", ignore = true)
    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void patchMerge(UserPatchRequestDto requestDto, @MappingTarget UserEntity userEntity);

    default UserRequestDto oathToUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("name");
        String password = oAuth2User.getAttribute("sub");
        String phone = "";
        return new UserRequestDto(username, email, phone, password);
    }
}
