package fuad.hamidan.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fuad.hamidan.entity.Contact;
import fuad.hamidan.entity.User;
import fuad.hamidan.model.ContactResponse;
import fuad.hamidan.model.CreateContactRequest;
import fuad.hamidan.model.UpdateContactRequest;
import fuad.hamidan.model.WebResponse;
import fuad.hamidan.repository.ContactRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @BeforeEach
    void setUp() {

        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setName("admin");
        user.setUsername("admin");
        user.setToken("admin");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000L);
        user.setPassword(BCrypt.hashpw("admin", BCrypt.gensalt()));
        userRepository.save(user);
    }

    @Test
    void createContactSuccess() throws Exception {

        CreateContactRequest contact = new CreateContactRequest();
        contact.setFirstName("Fuad");
        contact.setLastName("Hamidan");
        contact.setEmail("fuad@gmail.com");
        contact.setPhone("343434343");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact))
                        .header("X-API-TOKEN", "admin")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertNull(response.getErrors());
            assertNotNull(response.getData());
            assertEquals(contact.getFirstName(), response.getData().getFirstName());
            assertEquals(contact.getLastName(), response.getData().getLastName());
            assertEquals(contact.getEmail(), response.getData().getEmail());
            assertEquals(contact.getPhone(), response.getData().getPhone());


            log.info("RESPONSE {}", response.getData().toString());
        });
    }

    @Test
    void createContactUnauthorized() throws Exception {

        CreateContactRequest contact = new CreateContactRequest();
        contact.setFirstName("Fuad");
        contact.setLastName("Hamidan");
        contact.setEmail("fuad@gmail.com");
        contact.setPhone("343434343");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertNotNull(response.getErrors());
            log.info("ERROR {}", response.getErrors());
            assertNull(response.getData());

        });
    }

    @Test
    void createContactBadRequest() throws Exception {

        CreateContactRequest contact = new CreateContactRequest();
        contact.setLastName("Hamidan");
        contact.setEmail("fuad@gmail.com");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact))
                        .header("X-API-TOKEN", "admin")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertNotNull(response.getErrors());
            log.info("ERROR {}", response.getErrors());
            assertNull(response.getData());

        });
    }

    @Test
    void getContactSuccess() throws Exception {

        User userDb = userRepository.findById("admin").orElse(null);

        Contact contact = new Contact();
        contact.setId("123123");
        contact.setFirstName("Fuad");
        contact.setLastName("Hamidan");
        contact.setEmail("fuad@gmail.com");
        contact.setPhone("343434343");
        contact.setUser(userDb);
        contactRepository.save(contact);

        mockMvc.perform(
                get("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "admin")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertNotNull(response.getData());
            assertEquals(contact.getFirstName(), response.getData().getFirstName());
        });
    }

    @Test
    void updateSuccess() throws Exception {
        User user = userRepository.findById("admin").orElse(null);
        Contact contact = new Contact();
        contact.setId("123123");
        contact.setFirstName("Fuad");
        contact.setLastName("Hamidan");
        contact.setEmail("fuad@gmail.com");
        contact.setPhone("343434343");
        contact.setUser(user);
        contactRepository.save(contact);

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("Hamidan");
        request.setLastName("Fuad");
        request.setEmail("fuad@gmail.com");
        request.setPhone("343434343");

        mockMvc.perform(
                put("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "admin")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getData());
            assertEquals(contact.getFirstName(), response.getData().getLastName());
            assertEquals(contact.getLastName(), response.getData().getFirstName());
            assertEquals(contact.getEmail(), response.getData().getEmail());
            assertEquals(contact.getPhone(), response.getData().getPhone());
        });
    }

    @Test
    void updateFailedBadRequest() throws Exception {
        User user = userRepository.findById("admin").orElse(null);
        Contact contact = new Contact();
        contact.setId("123123");
        contact.setFirstName("Fuad");
        contact.setLastName("Hamidan");
        contact.setEmail("fuad@gmail.com");
        contact.setPhone("343434343");
        contact.setUser(user);
        contactRepository.save(contact);

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("Hamidan");
        request.setLastName("Fuad");
        request.setEmail("fuad@gmail.com");

        mockMvc.perform(
                put("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "admin")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateFailedContactNotFound() throws Exception {
        User user = userRepository.findById("admin").orElse(null);
        Contact contact = new Contact();
        contact.setId("123123");
        contact.setFirstName("Fuad");
        contact.setLastName("Hamidan");
        contact.setEmail("fuad@gmail.com");
        contact.setPhone("343434343");
        contact.setUser(user);
        contactRepository.save(contact);

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("Hamidan");
        request.setLastName("Fuad");
        request.setEmail("fuad@gmail.com");
        request.setPhone("0865456734");

        mockMvc.perform(
                put("/api/contacts/765432")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "admin")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getData());
            assertNotNull(response.getErrors());
            log.info("ERROR {}", response.getErrors());
        });
    }

    @Test
    void updateFailedUnauthorized() throws Exception {
        User user = userRepository.findById("admin").orElse(null);
        Contact contact = new Contact();
        contact.setId("123123");
        contact.setFirstName("Fuad");
        contact.setLastName("Hamidan");
        contact.setEmail("fuad@gmail.com");
        contact.setPhone("343434343");
        contact.setUser(user);
        contactRepository.save(contact);

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("Hamidan");
        request.setLastName("Fuad");
        request.setEmail("fuad@gmail.com");
        request.setPhone("0899998989");

        mockMvc.perform(
                put("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void deleteSuccess() throws Exception {
        User user = userRepository.findById("admin").orElse(null);
        Contact contact = new Contact();
        contact.setId("123123");
        contact.setFirstName("Fuad");
        contact.setLastName("Hamidan");
        contact.setEmail("fuad@gmail.com");
        contact.setPhone("343434343");
        contact.setUser(user);
        contactRepository.save(contact);

        Contact contact2 = new Contact();
        contact2.setId("222222");
        contact2.setFirstName("Fuad");
        contact2.setLastName("Hamidan");
        contact2.setEmail("fuad@gmail.com");
        contact2.setPhone("343434343");
        contact2.setUser(user);
        contactRepository.save(contact2);

        mockMvc.perform(
                delete("/api/contacts/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "admin")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getData());
        });
    }

    @Test
    void deleteFailedContactNotFound() throws Exception {
        User user = userRepository.findById("admin").orElse(null);
        Contact contact = new Contact();
        contact.setId("123123");
        contact.setFirstName("Fuad");
        contact.setLastName("Hamidan");
        contact.setEmail("fuad@gmail.com");
        contact.setPhone("343434343");
        contact.setUser(user);
        contactRepository.save(contact);

        mockMvc.perform(
                delete("/api/contacts/222")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "admin")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
            assertEquals("Contact not found", response.getErrors());
        });
    }

    @Test
    void deleteFailedUnauthorized() throws Exception {
        User user = userRepository.findById("admin").orElse(null);
        Contact contact = new Contact();
        contact.setId("123123");
        contact.setFirstName("Fuad");
        contact.setLastName("Hamidan");
        contact.setEmail("fuad@gmail.com");
        contact.setPhone("343434343");
        contact.setUser(user);
        contactRepository.save(contact);

        mockMvc.perform(
                delete("/api/contacts/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
            assertEquals("Unauthorized", response.getErrors());
        });
    }

    @Test
    void searchSuccess() throws Exception {
        User user = userRepository.findById("admin").orElse(null);
        for (int i = 1; i<=50; i++){
            Contact contact = new Contact();
            contact.setId("22" + i);
            contact.setFirstName("Fuad " + i);
            contact.setLastName("Hamidan"  + i);
            contact.setEmail("fuad" + i +"@gmail.com");
            contact.setPhone("343434343" + i);
            contact.setUser(user);
            contactRepository.save(contact);
        }

        mockMvc.perform(
                get("/api/contacts")
                        .param("name", "Fuad 2")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "admin")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getData());
            log.info("DATA: {}", response.getData());
        });
    }
}
