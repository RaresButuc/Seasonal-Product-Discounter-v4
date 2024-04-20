package com.codecool.seasonalproductdiscounter.service.authentication;

import com.codecool.seasonalproductdiscounter.model.users.User;
import com.codecool.seasonalproductdiscounter.service.users.UserRepository;

public class AuthenticationServiceImpl implements AuthenticationService {
    //    private final Map<String, String> userNamesToPasswords = new HashMap<>() {{
//        put("user1", "1234");
//        put("user2", "4567");
//        put("admin", "admin");
//    }};
//
//    public boolean authenticate(User user) {
//        return userNamesToPasswords.entrySet()
//                .stream()
//                .anyMatch(entry -> entry.getKey().equals(user.userName())
//                        && entry.getValue().equals(user.password()));
//    }
    private final UserRepository userRepository;

    public AuthenticationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean authenticate(User user) {
        return userRepository.getUsers()
                .stream()
                .anyMatch(entry -> entry.userName().equals(user.userName())
                        && entry.password().equals(user.password()));
    }
}

