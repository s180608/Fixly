package com.fixly.fixlybackend;
import org.springframework.http.HttpHeaders;
import com.fixly.fixlybackend.service.JwtService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void servicesShouldBePublic() throws Exception {

        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk());
    }

    @Test
    void customerShouldNotAccessAllUsers() throws Exception {

        String token = jwtService.generateToken(
                "role.test@fixly.com",
                "CUSTOMER"
        );

        mockMvc.perform(
                        get("/api/users")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldAccessAllUsers() throws Exception {

        String token = jwtService.generateToken(
                "security.test@fixly.com",
                "ADMIN"
        );

        mockMvc.perform(
                        get("/api/users")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void usersShouldRequireAuthentication() throws Exception {

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }
}