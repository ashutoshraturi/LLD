package services;

import models.User;

public class UserService {
    public User createUser(int userId){
        return new User(userId);
    }
}
