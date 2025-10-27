package com.quizserver.service.user;

import com.quizserver.enteties.User;

public interface UserService {

    User createUser(User user);

    Boolean hasUserWithEmail(String email);

    User login(User user);
}
