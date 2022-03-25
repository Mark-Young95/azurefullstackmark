package com.revature.AzureFullStackMark.beans.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.AzureFullStackMark.beans.models.User;
import com.revature.AzureFullStackMark.beans.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepo userRepo;
    private final ObjectMapper mapper;

    @Autowired
    public UserController(UserRepo userRepo, ObjectMapper mapper) {
        this.userRepo = userRepo;
        this.mapper = mapper;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Boolean> postNewUser(@RequestBody User user) {
        System.out.println("at least reached the code");
        String errorMsg = "";
        if (areCredentialsValid(user)) {
            try {
                // changing to lowercase so that two usernames that are the same with different cases won't both be accepted
                user.setUsername(user.getUsername().toLowerCase());

                ExampleMatcher em = ExampleMatcher.matching()
                        .withIgnorePaths("userId", "password", "count")
                        .withMatcher("username", ignoreCase());

                Example<User> example = Example.of(user, em);

                if (userRepo.exists(example)) {
                    System.out.println("username already exists");
                    errorMsg = "username already exists";
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("errorMsg", errorMsg);
                    return ResponseEntity.ok().headers(headers).body(false);
                }

                // save the user in the database
                user.setCount(0);
                userRepo.save(user);
            } catch (Exception e) {
                // inform failed result
                System.out.println("most likely failed to connect?");
                errorMsg = "most likely failed to connect?";
                HttpHeaders headers = new HttpHeaders();
                headers.set("errorMsg", errorMsg);
                return ResponseEntity.ok().headers(headers).body(false);
            }
            // inform successful result
            return ResponseEntity.ok().body(true);
        } else {
            System.out.println("username and password fields must not be empty");
            errorMsg = "username and password fields must not be empty";
            HttpHeaders headers = new HttpHeaders();
            headers.set("errorMsg", errorMsg);
            return ResponseEntity.ok().headers(headers).body(false);
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<String> getUserByUsername(@RequestBody User user) throws JsonProcessingException {
        User tempUser;
        String errorMsg = "";
        if (user.getUsername() == null || user.getPassword() == null) {
            tempUser = null;
            errorMsg = "username and password fields must not be empty";
        } else {
            ExampleMatcher em = ExampleMatcher.matching()
                    .withIgnorePaths("userId", "password", "count")
                    .withMatcher("username", ignoreCase());

            Example<User> example = Example.of(user, em);

            if (userRepo.exists(example)) {
                Optional<User> optionalUser = userRepo.findOne(example);

                // password checking
                if (user.getPassword().equals(optionalUser.get().getPassword())) {
                    tempUser = optionalUser.get();
                } else {
                    tempUser = null;
                    errorMsg = "password is incorrect";
                }
            } else {
                tempUser = null;
                errorMsg = "username does not exist";
            }
        }



        if (tempUser != null) {
            return ResponseEntity.ok().body(mapper.writeValueAsString(tempUser));
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.set("errorMsg", errorMsg);
            return ResponseEntity.badRequest().headers(headers).build();
        }
    }

    @GetMapping
    public ResponseEntity<String> getUserById(@RequestBody Integer userId) throws JsonProcessingException {
        Optional<User> user = userRepo.findById(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(mapper.writeValueAsString(user.get()));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("errorMsg", "not a valid user");
        return ResponseEntity.badRequest().headers(headers).build();
    }

    @PostMapping(value = "/update")
    public Boolean updateCount(@RequestBody User user) {
        Optional<User> foundUser = userRepo.findById(user.getUserId());
        if (foundUser.isPresent()) {
            foundUser.get().setCount(user.getCount());
            userRepo.save(foundUser.get());
            return true;
        }
        return false;
    }

    public boolean areCredentialsValid(User user) {
        if (user.getUsername() == null || user.getPassword() == null || user.getUsername().equals("") || user.getPassword().equals("")) {
            return false;
        }
        return true;
    }
}
