package fuad.hamidan.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fuad.hamidan.entity.User;
import fuad.hamidan.model.LoginUserRequest;
import fuad.hamidan.model.TokenResponse;
import fuad.hamidan.model.WebResponse;
import fuad.hamidan.repository.UserRepository;
import fuad.hamidan.security.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void loginFailed() throws Exception {

        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("admin");
        loginUserRequest.setPassword("admin");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    void loginFailedWrongPassword() throws Exception {

        User user = new User();
        user.setName("admin");
        user.setUsername("admin");
        user.setPassword(BCrypt.hashpw("admin", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("admin");
        loginUserRequest.setPassword("salah");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    void loginSuccess() throws Exception {

        User user = new User();
        user.setName("admin");
        user.setUsername("admin");
        user.setPassword(BCrypt.hashpw("admin", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("admin");
        loginUserRequest.setPassword("admin");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo(result ->{
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });


            Assertions.assertNotNull(response.getData());
            Assertions.assertNull(response.getErrors());
            Assertions.assertNotNull(response.getData().getExpiredAt());
            Assertions.assertNotNull(response.getData().getToken());

            User userDb = userRepository.findById("admin").orElse(null);
            Assertions.assertNotNull(userDb);
            Assertions.assertEquals(response.getData().getToken(), userDb.getToken());
            Assertions.assertEquals(response.getData().getExpiredAt(), userDb.getTokenExpiredAt());
        });
    }

    @Test
    void logoutSuccess() throws Exception{
        User user = new User();
        user.setName("admin");
        user.setUsername("admin");
        user.setToken("admin");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000L);
        user.setPassword(BCrypt.hashpw("admin", BCrypt.gensalt()));
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/auth/logout")
                        .header("X-API-TOKEN", "admin")
        ).andExpectAll(
                status().isOk()
        ).andExpectAll( result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getData());
            Assertions.assertEquals("OK", response.getData());

            User userDb = userRepository.findById("admin").orElse(null);

            Assertions.assertNotNull(userDb);
            Assertions.assertNull(userDb.getToken());
            Assertions.assertNull(userDb.getTokenExpiredAt());
        });
    }

    @Test
    void logoutUnauthorizedTokenNotSend() throws Exception{
        User user = new User();
        user.setName("admin");
        user.setUsername("admin");
        user.setToken("admin");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000L);
        user.setPassword(BCrypt.hashpw("admin", BCrypt.gensalt()));
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/auth/logout")
        ).andExpectAll(
                status().isUnauthorized()
        ).andExpectAll( result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getErrors());
        });
    }

}