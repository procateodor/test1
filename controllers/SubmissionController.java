package services.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import services.user.models.Submission;
import services.user.models.UrlsRequest;
import services.user.models.User;
import services.user.repositories.SubmissionRepository;
import services.user.repositories.UserRepository;
import services.user.utils.AuthUtil;
import services.user.utils.JwtTokenUtil;

import javax.transaction.Transactional;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/submissions", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
@Transactional
public class SubmissionController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthUtil authUtil;

    @GetMapping(value = "/all", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Submission> getSubmissions(@RequestHeader("authorization") String authorizationHeader) {
        authUtil.checkAuthenticated(authorizationHeader);

        UUID userId = UUID.fromString(jwtTokenUtil.getUserIdFromToken(authUtil.getToken(authorizationHeader)));

        return submissionRepository.findAllByUserId(userId);
    }

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void addSubmission(@RequestHeader("authorization") String authorizationHeader, @RequestBody UrlsRequest urlsRequest) {
        authUtil.checkAuthenticated(authorizationHeader);

        UUID userId = UUID.fromString(jwtTokenUtil.getUserIdFromToken(authUtil.getToken(authorizationHeader)));

        Optional<User> userOptional = userRepository.findById(userId);
        User user = null;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        }

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"
            );
        }

        Submission submission = new Submission(urlsRequest.getUrls(), userId);
        submissionRepository.save(submission);
    }

    @DeleteMapping(value = "/{submissionId}", produces = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubmission(@RequestHeader("authorization") String authorizationHeader, @PathVariable String submissionId) {
        authUtil.checkAuthenticated(authorizationHeader);

        UUID userId = UUID.fromString(jwtTokenUtil.getUserIdFromToken(authUtil.getToken(authorizationHeader)));

        Optional<Submission> submissionOptional = submissionRepository.findById(UUID.fromString(submissionId));
        Submission submission = null;

        if (submissionOptional.isPresent()) {
            submission = submissionOptional.get();
        }

        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Submission not found"
            );
        }

        if (!submission.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Submission not yours"
            );
        }

        submissionRepository.deleteById(UUID.fromString(submissionId));
    }

    @DeleteMapping(value = "/delete", produces = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllSubmissions(@RequestHeader("authorization") String authorizationHeader) {
        authUtil.checkAuthenticated(authorizationHeader);

        UUID userId = UUID.fromString(jwtTokenUtil.getUserIdFromToken(authUtil.getToken(authorizationHeader)));

        submissionRepository.deleteAllByUserId(userId);
    }
}
