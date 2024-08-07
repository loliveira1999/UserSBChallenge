package com.beginsecure.usersbchallenge.Persistence.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.beginsecure.usersbchallenge.Persistence.Entity.UsersEntity;
import com.beginsecure.usersbchallenge.Persistence.Repository.UsersRepository;

@Service
public class UsersService {
    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository){
        this.usersRepository = usersRepository;
    }

    public UsersEntity getUserByID(Integer userID){
        if(userID == null)
            return null;
        return usersRepository.getUserByID(userID);
    }

    public UsersEntity getActiveUserByID(Integer userID){
        return usersRepository.getActiveUserByID(userID);
    }

    public boolean isEmailInUse(String userEmail, Integer userID){
        // ID can be null
        List<UsersEntity> users = usersRepository.getUserWEmail(userEmail, userID, PageRequest.of(0, 1));
        return !users.isEmpty();
    }
    
    public List<UsersEntity> getUsers(Integer userID){
        if(userID == null)
            return usersRepository.getAllUsers();
        else{
            UsersEntity user = usersRepository.getUserByID(userID);
            return user == null ? new ArrayList<UsersEntity>() : List.of(user);
        }
    }

    public UsersEntity insertUser(UsersEntity user){
        user.setID(null);
        user.setCreatedOn();
        user.setUpdatedOn();
        return usersRepository.saveAndFlush(user);
    }

    public UsersEntity updateUser(UsersEntity user){
        if(user.getID() == null)
            return null;
        user.setUpdatedOn();
        return usersRepository.saveAndFlush(user);
    }
}
