package fuad.hamidan.controller;

import fuad.hamidan.entity.User;
import fuad.hamidan.model.RegisterUserRequest;
import fuad.hamidan.model.UserResponse;
import fuad.hamidan.model.UserUpdateRequest;
import fuad.hamidan.model.WebResponse;
import fuad.hamidan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(
            path = "/api/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserRequest request) {
        userService.register(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/api/user/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> get(User user) {
        UserResponse response = userService.get(user);
        return WebResponse.<UserResponse>builder().data(response).build();
    }

    @PatchMapping(
            path = "/api/users/current",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private WebResponse<UserResponse> update(User user, @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.update(user, request);
        return WebResponse.<UserResponse>builder().data(response).build();
    }


}