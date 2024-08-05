package com.beginsecure.usersbchallenge.Persistence.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
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
        return usersRepository.getUserByID(userID);
    }
    
    public List<UsersEntity> getUsers(Integer userID){
        if(userID == null)
            return usersRepository.getAllUsers();
        else{
            UsersEntity user = usersRepository.getUserByID(userID);
            return user == null ? new ArrayList<UsersEntity>() : Collections.singletonList(user);
        }
    }

    public UsersEntity insertUser(UsersEntity user){
        return usersRepository.saveAndFlush(user);
    }

    public UsersEntity updateUser(UsersEntity user){
        if(user.getID() == null)
            return null;
        return usersRepository.saveAndFlush(user);
    }

    public void deleteUser(Integer ID){
        if(ID == null)
            return;
            usersRepository.deleteById(ID);
    }

}
