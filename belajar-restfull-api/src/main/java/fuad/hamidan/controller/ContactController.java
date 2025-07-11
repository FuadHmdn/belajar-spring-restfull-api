package fuad.hamidan.controller;

import fuad.hamidan.entity.User;
import fuad.hamidan.model.*;
import fuad.hamidan.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping(
            path = "/api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request){
        ContactResponse response = contactService.createContact(user, request);
        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/api/contacts/{idContact}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> get(User user, @PathVariable("idContact") String idContact){
        ContactResponse response = contactService.get(user, idContact);
        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @PutMapping(
            path = "/api/contacts/{idContact}"
    )
    public WebResponse<ContactResponse> update(User user, @RequestBody UpdateContactRequest request, @PathVariable("idContact") String idContact){
        ContactResponse response = contactService.update(user, request, idContact);
        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "/api/contacts/{idContact}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user, @PathVariable("idContact") String idContact){
        contactService.delete(user, idContact);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ContactResponse>> search(User user,
                                                     @RequestParam(value = "name", required = false) String name,
                                                     @RequestParam(value = "email", required = false) String email,
                                                     @RequestParam(value = "phone", required = false) String phone,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0")Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10")Integer size
    ){
        SearchContactRequest request = new SearchContactRequest(name, email, phone, page, size);
        Page<ContactResponse> contactResponse = contactService.search(user, request);
        return WebResponse.<List<ContactResponse>>builder()
                .data(contactResponse.getContent())
                .page(PagingResponse.builder()
                        .currentPage(contactResponse.getNumber())
                        .totalPage(contactResponse.getTotalPages())
                        .size(contactResponse.getSize())
                        .build()
                )
                .build();
    }
}
