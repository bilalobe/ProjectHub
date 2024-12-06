package com.projecthub.ui.viewmodels;

import java.util.List;

import org.springframework.stereotype.Component;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.service.UserService;

import jakarta.annotation.PostConstruct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Component
public class UserViewModel {

    private final ObservableList<AppUserSummary> users = FXCollections.observableArrayList();

    
    private UserService userService;

    public UserViewModel() {
    }

    @PostConstruct
    public void init() {
        loadUsers();
    }

    public ObservableList<AppUserSummary> getUsers() {
        return users;
    }

    public void loadUsers() {
        users.clear();
        List<AppUserSummary> userList = userService.getAllUsers();
        users.addAll(userList);
    }

    public void addUser(AppUserSummary user) {
        userService.saveUser(user, "default");
        users.add(user);
    }

    public void updateUser(AppUserSummary user) {
        userService.saveUser(user, "default");
        int index = users.indexOf(user);
        if (index >= 0) {
            users.set(index, user);
        }
    }

    public void deleteUser(AppUserSummary user) {
        userService.deleteUser(user.getId());
        users.remove(user);
    }
}