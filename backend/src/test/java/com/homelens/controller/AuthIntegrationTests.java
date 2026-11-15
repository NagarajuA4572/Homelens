package com.homelens.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homelens.dto.LoginRequest;
import com.homelens.dto.RegisterRequest;
import com.homelens.model.Role;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "newbuyer@homelens.com",
                "SecretPass123!",
                "New Buyer",
                "+1-555-444-3333",
                Role.ROLE_BUYER
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.email").value("newbuyer@homelens.com"))
                .andExpect(jsonPath("$.fullName").value("New Buyer"))
                .andExpect(jsonPath("$.role").value("ROLE_BUYER"));
    }

    @Test
    void testRegisterDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "duplicate@homelens.com",
                "SecretPass123!",
                "First User",
                "+1-555-444-1111",
                Role.ROLE_BUYER
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Attempt second registration with same email
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already registered")));
    }

    @Test
    void testLoginSuccess() throws Exception {
        RegisterRequest registerReq = new RegisterRequest(
                "agent.login@homelens.com",
                "StrongPassword123!",
                "Agent Smith",
                "+1-555-888-9999",
                Role.ROLE_AGENT
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = new LoginRequest("agent.login@homelens.com", "StrongPassword123!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role").value("ROLE_AGENT"));
    }

    @Test
    void testLoginInvalidPassword() throws Exception {
        LoginRequest loginReq = new LoginRequest("nonexistent@homelens.com", "WrongPassword!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetCurrentUserProfileWithJwt() throws Exception {
        RegisterRequest registerReq = new RegisterRequest(
                "profile.test@homelens.com",
                "Password123!",
                "Profile Tester",
                "+1-555-777-6666",
                Role.ROLE_BUYER
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode responseNode = objectMapper.readTree(result.getResponse().getContentAsString());
        String jwtToken = responseNode.get("token").asText();

        // Call protected /api/auth/me with Bearer token
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("profile.test@homelens.com"))
                .andExpect(jsonPath("$.fullName").value("Profile Tester"))
                .andExpect(jsonPath("$.role").value("ROLE_BUYER"));
    }

    @Test
    void testGetCurrentUserWithoutJwt() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
