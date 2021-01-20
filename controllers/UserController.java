package services.user.controllers;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import services.user.models.AuthRequest;
import services.user.models.User;
import services.user.repositories.UserRepository;
import services.user.utils.AuthUtil;
import services.user.utils.JwtTokenUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/users", method = {RequestMethod.GET, RequestMethod.POST})
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping(value = "/auth", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public  Map<String, String> auth(@RequestBody AuthRequest authRequest) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://api.github.com/user"))
                    .header("authorization", "token " + authRequest.getAccessToken())
                    .build();

            HttpResponse<String> gitResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = gitResponse.body();

            JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            JSONObject userData = (JSONObject) parser.parse(body);

            User user = userRepository.findByEmail(userData.getAsString("email"));

            if (user == null) {
                user = new User(userData.getAsString("name"), userData.getAsString("email"), userData.getAsString("avatar_url"), authRequest.getAccessToken());
                userRepository.save(user);
            }

            String token = jwtTokenUtil.generateToken(user);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Server error!"
            );
        }
    }

    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public User getDetails(@RequestHeader("authorization") String authorizationHeader) {
        authUtil.checkAuthenticated(authorizationHeader);

        Optional<User> userOptional = userRepository.findById(UUID.fromString(jwtTokenUtil.getUserIdFromToken(authUtil.getToken(authorizationHeader))));
        User user = null;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        }

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"
            );
        }

        return user;
    }
}
