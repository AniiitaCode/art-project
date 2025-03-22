package com.example.art.web;

import com.example.art.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getIndexView_shouldIndexPage() throws Exception {

        MockHttpServletRequestBuilder request = get("/");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getRegisterView_shouldRegisterPage() throws Exception {

        MockHttpServletRequestBuilder request = get("/register");

    mockMvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(view().name("register"))
            .andExpect(model().attributeExists(("registerRequest")));
    }

    @Test
    void getLoginView_shouldLoginPage() throws Exception {

        MockHttpServletRequestBuilder request = get("/login");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists(("loginRequest")));
    }

    @Test
    void getLoginView_shouldLoginPageWithParam() throws Exception {

        MockHttpServletRequestBuilder request = get("/login")
                .param("error", "");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists(("loginRequest")))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void postLoginView_shouldHappyPath() throws Exception {

        MockHttpServletRequestBuilder request = post("/register")
                .formField("firstName", "Ani")
                .formField("email", "ani@abv.bg")
                .formField("username", "ani123")
                .formField("password", "123123")
                .formField("confirmPassword", "123123")
                        .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).register(any());
    }

    @Test
    void postLoginView_shouldHasError() throws Exception {

        MockHttpServletRequestBuilder request = post("/register")
                .formField("firstName", "")
                .formField("email", "ani@abv.bg")
                .formField("username", "ani123")
                .formField("password", "123123")
                .formField("confirmPassword", "123123")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).register(any());
    }

    @Test
    void postLoginView_shouldDifferentPassword() throws Exception {

        MockHttpServletRequestBuilder request = post("/register")
                .formField("firstName", "")
                .formField("email", "ani@abv.bg")
                .formField("username", "ani123")
                .formField("password", "123456")
                .formField("confirmPassword", "123123")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).register(any());
    }



}
