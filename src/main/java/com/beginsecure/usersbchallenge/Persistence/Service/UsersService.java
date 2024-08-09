package com.beginsecure.usersbchallenge.Persistence.Service;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.beginsecure.usersbchallenge.Persistence.Entity.UsersEntity;
import com.beginsecure.usersbchallenge.Persistence.Repository.UsersRepository;
import com.beginsecure.usersbchallenge.Util.Constants;

import jakarta.transaction.Transactional;

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

    // @Transactional
    public UsersEntity insertUser(UsersEntity user){
        user.setID(null);
        user.setCreatedOn();
        user.setUpdatedOn();
        return usersRepository.save(user);
    }

    @Transactional(rollbackOn = Exception.class)
    public UsersEntity updateUserWithJsonContent(JSONObject jsonUser) throws Exception{
        Integer userID = jsonUser.optIntegerObject(Constants.JSON_P_ID);
        if(userID == null|| userID <= 0){
            throw new Exception("Invalid User ID!");
        }
        UsersEntity user = this.getUserByID(userID);
        if(user == null){
            throw new Exception("User with the provided ID not found!");
        }
        // Update User with New Values. Invalid Values will be set to NULL
        user.updateUser(jsonUser);
        // BUG: If any of the below ifs are triggered, the auditService tries to save a User for some reason...
        // verify email (invalid email)
        if(user.getEmail() == null){
            throw new Exception("User Email is not valid!");
        }
        // verify password (invalid password)
        if(user.getPassword() == null){
            throw new Exception("User Password is not valid! It must have at least " 
                + Constants.PASSWORD_MIN_LEN + " characters!");
        }
        // verify NULL values inside user
        if(user.isUserInvalid()){
            throw new Exception("User Updated Values are not valid!");
        }
        // verify unique email
        if(this.isEmailInUse(user.getEmail(), user.getID())){
            throw new Exception("User Email is already associated with a different account!");
        }
        user.setUpdatedOn();
        usersRepository.save(user);
        return user;
    }

    // @Transactional
    public UsersEntity deactivateUser(UsersEntity user) throws Exception{         
        user.setIsActive(false);
        user.setUpdatedOn();
        return usersRepository.save(user);
    }
}
