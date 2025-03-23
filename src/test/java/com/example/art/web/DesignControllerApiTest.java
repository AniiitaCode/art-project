package com.example.art.web;

import com.example.art.design.model.ConstructionDesign;
import com.example.art.design.model.DecorationPebbles;
import com.example.art.design.model.DecorationPicture;
import com.example.art.design.model.FormDesign;
import com.example.art.design.service.DesignService;
import com.example.art.security.AuthenticationDetails;
import com.example.art.user.model.User;
import com.example.art.user.model.UserRole;
import com.example.art.user.service.UserService;
import com.example.art.wallet.model.Wallet;
import com.example.art.web.dto.DesignDecorationRequest;
import com.example.art.web.dto.DesignFormRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DesignController.class)
public class DesignControllerApiTest {

    @MockBean
    private UserService userService;

    @MockBean
    private DesignService designService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getDesignCreatePage() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        User user = User.builder()
                .id(userId)
                .firstName("Ani")
                .email("ani@abv.bg")
                .role(UserRole.USER)
                .password(authenticationDetails.getPassword())
                .username(authenticationDetails.getUsername())
                .wallet(new Wallet())
                .profilePicture("www.image.com")
                .build();

        when(userService.getById(userId)).thenReturn(user);

        MockHttpServletRequestBuilder request = get("/designs")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(view().name("design-create"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", user));

        verify(userService, times(1)).getById(userId);
    }

    @Test
    void getDesignFormPage() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        User user = User.builder()
                .id(userId)
                .firstName("Ani")
                .email("ani@abv.bg")
                .role(UserRole.USER)
                .password(authenticationDetails.getPassword())
                .username(authenticationDetails.getUsername())
                .wallet(new Wallet())
                .profilePicture("www.image.com")
                .build();

        when(userService.getById(userId)).thenReturn(user);

        MockHttpServletRequestBuilder request = get("/designs/nextPage")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(view().name("design-form"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", user))
                .andExpect(model().attributeExists("designFormRequest"));

        verify(userService, times(1)).getById(userId);
    }

    @Test
    void postDesignFormConfirm_happyPath() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        MockHttpServletRequestBuilder request = post("/designs/nextPage")
                .formField("form", String.valueOf(FormDesign.ALMOND))
                .formField("construction", String.valueOf(ConstructionDesign.YES))
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/designs/decoration"));
    }

    @Test
    void postDesignFormConfirm_hasErrors() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        MockHttpServletRequestBuilder request = post("/designs/nextPage")
                .formField("form", "")
                .formField("construction", String.valueOf(ConstructionDesign.YES))
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("design-form"));
    }

    @Test
    void getShowFormPage() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        MockHttpServletRequestBuilder request = get("/designs/forms")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("design-form-show"));
    }

    @Test
    void getDesignDecorationPage() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        User user = User.builder()
                .id(userId)
                .firstName("Ani")
                .email("ani@abv.bg")
                .role(UserRole.USER)
                .password(authenticationDetails.getPassword())
                .username(authenticationDetails.getUsername())
                .wallet(new Wallet())
                .profilePicture("www.image.com")
                .build();

        when(userService.getById(userId)).thenReturn(user);

        MockHttpServletRequestBuilder request = get("/designs/decoration")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("design-decoration"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attributeExists("designDecorationRequest"));

        verify(userService, times(1)).getById(userId);
    }

    @Test
    void postDesignDecorationConfirm_happyPath() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        User user = User.builder()
                .id(userId)
                .firstName("Ani")
                .email("ani@abv.bg")
                .role(UserRole.USER)
                .password(authenticationDetails.getPassword())
                .username(authenticationDetails.getUsername())
                .wallet(new Wallet())
                .profilePicture("www.image.com")
                .build();

        when(userService.getById(userId)).thenReturn(user);

        MockHttpServletRequestBuilder request = post("/designs/decoration")
                .formField("color", "02")
                .formField("pebbles", String.valueOf(DecorationPebbles.DIAMONDS))
                .formField("picture", String.valueOf(DecorationPicture.BEAR))
                .with(user(authenticationDetails))
                .with(csrf());

        DesignDecorationRequest designDecorationRequest =
                new DesignDecorationRequest();
        designDecorationRequest.setColor("02");
        designDecorationRequest.setPebbles(DecorationPebbles.DIAMONDS);
        designDecorationRequest.setPicture(DecorationPicture.BEAR);

        when(designService.saveDecoration(designDecorationRequest)).thenReturn(designDecorationRequest);

        DesignFormRequest designFormRequest = designService.getSavedForm();
        when(designService.getSavedForm()).thenReturn(designFormRequest);

        doNothing().when(designService).saveAllInformationDesign(designFormRequest, designDecorationRequest, user);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));


        verify(userService, times(1)).getById(userId);
        verify(designService, times(1)).saveDecoration(designDecorationRequest);
        verify(designService, times(1))
                .saveAllInformationDesign(designFormRequest, designDecorationRequest, user);
    }

    @Test
    void postDesignDecorationConfirm_hasErrors() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        MockHttpServletRequestBuilder request = post("/designs/decoration")
                .formField("color", "")
                .formField("pebbles", String.valueOf(DecorationPebbles.DIAMONDS))
                .formField("picture", String.valueOf(DecorationPicture.BEAR))
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("design-decoration"));

    }
}