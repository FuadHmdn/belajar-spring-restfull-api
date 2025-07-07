package fuad.hamidan.controller;

import fuad.hamidan.entity.User;
import fuad.hamidan.model.ContactResponse;
import fuad.hamidan.model.CreateContactRequest;
import fuad.hamidan.model.UpdateContactRequest;
import fuad.hamidan.model.WebResponse;
import fuad.hamidan.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
}
