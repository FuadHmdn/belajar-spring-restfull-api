package fuad.hamidan.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fuad.hamidan.entity.User;
import fuad.hamidan.model.RegisterUserRequest;
import fuad.hamidan.model.UserResponse;
import fuad.hamidan.model.UserUpdateRequest;
import fuad.hamidan.model.WebResponse;
import fuad.hamidan.repository.UserRepository;
import fuad.hamidan.security.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerSuccess() throws Exception{

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("admin");
        request.setPassword("admin");
        request.setName("Admin");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo( result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("OK", response.getData());
            log.info("RESPONSE {}", response);
        });
    }

    @Test
    void registerIsBadRequest() throws Exception{

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setName("");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo( result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserUnauthorized() throws Exception{
        mockMvc.perform(
                get("/api/user/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "salah")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo( result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserUnauthorizedTokenNotSend() throws Exception{
        mockMvc.perform(
                get("/api/user/current")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo( result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserUnauthorizedTokenExpired() throws Exception{

        User user = new User();
        user.setName("Fuad");
        user.setUsername("fuad");
        user.setPassword(BCrypt.hashpw("fuad", BCrypt.gensalt()));
        user.setToken("fuad");
        user.setTokenExpiredAt(System.currentTimeMillis() - 100000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/user/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "fuad")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo( result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserSuccess() throws Exception{

        User user = new User();
        user.setName("Fuad");
        user.setUsername("fuad");
        user.setPassword(BCrypt.hashpw("fuad", BCrypt.gensalt()));
        user.setToken("fuad");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/user/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "fuad")
        ).andExpectAll(
                status().isOk()
        ).andDo( result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertEquals(response.getData().getUsername(), user.getUsername());
        });
    }

    @Test
    void updateUserSuccess() throws Exception{
        User user = new User();
        user.setName("Fuad");
        user.setUsername("fuad");
        user.setPassword(BCrypt.hashpw("fuad", BCrypt.gensalt()));
        user.setToken("fuad");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000L);
        userRepository.save(user);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("hamidan");
        request.setPassword("hamidan");

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "fuad")
        ).andExpectAll(
                status().isOk()
        ).andDo( result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertNotNull(response.getData());

            User userDb = userRepository.findById("fuad").orElse(null);
            assertNotNull(userDb);
            assertTrue(BCrypt.checkpw(request.getPassword(), userDb.getPassword()));
        });
    }
}