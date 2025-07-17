package services;

import dao.UserDAO;
import models.User;

import java.util.List;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public boolean registerUser(User user) {
        // You could add extra validation here
        return userDAO.save(user);
    }

    public User loginUser(String email, String password) {
        return userDAO.findByEmailAndPassword(email, password);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
}
