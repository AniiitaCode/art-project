package com.example.art.web;

import com.example.art.user.model.User;
import com.example.art.web.dto.UserEditRequest;
import com.example.art.web.mapper.DtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {

    @Test
    void whenMappingUserToUserEditRequest() {
        User user = User.builder()
                .firstName("Ani")
                .email("ani@abv.bg")
                .username("ani123")
                .profilePicture("www.image.com")
                .build();

        UserEditRequest userEditRequest = DtoMapper.mapUserToUserEditRequest(user);

        assertEquals(user.getFirstName(), userEditRequest.getFirstName());
        assertEquals(user.getEmail(), userEditRequest.getEmail());
        assertEquals(user.getUsername(), userEditRequest.getUsername());
        assertEquals(user.getProfilePicture(), userEditRequest.getProfilePicture());
    }
}