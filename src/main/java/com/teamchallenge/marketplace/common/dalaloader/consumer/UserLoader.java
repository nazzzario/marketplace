package com.teamchallenge.marketplace.common.dalaloader.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.marketplace.user.dto.request.UserRequestDto;
import com.teamchallenge.marketplace.user.dto.response.UserResponseDto;
import com.teamchallenge.marketplace.user.mapper.UserMapper;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import com.teamchallenge.marketplace.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLoader implements Consumer<List<Map<String, Object>>> {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void accept(List<Map<String, Object>> maps) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        maps.stream().filter(x -> x.containsKey("users"))
                .forEach(x ->
                        ((List<HashMap>) x.get("users")).forEach(y -> {
                            UserRequestDto userRequestDto = mapper.convertValue(y, UserRequestDto.class);

                            userService.createUser(userRequestDto);
                        })
                );

    }
}
