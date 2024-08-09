package com.beginsecure.usersbchallenge.Persistence.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.json.JSONException;
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
        return this.usersRepository.getUserByID(userID);
    }

    public UsersEntity getActiveUserByID(Integer userID){
        return this.usersRepository.getActiveUserByID(userID);
    }

    public boolean isEmailInUse(String userEmail, Integer userID){
        // ID can be null
        List<UsersEntity> users = this.usersRepository.getUserWEmail(userEmail, userID, PageRequest.of(0, 1));
        return !users.isEmpty();
    }
    
    public List<UsersEntity> getUsers(Integer userID) throws Exception{
        if(userID == null)
            return this.usersRepository.getAllUsers();
        if(userID < 1){
            throw new Exception("Invalid User ID!");
        }
        else{
            UsersEntity user = this.usersRepository.getUserByID(userID);
            return user == null ? List.of() : List.of(user);
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public UsersEntity insertUserWithJsonContent(JSONObject jsonUser) throws Exception{
        // creates user with JSON Values. Invalid Values will be set to NULL
        UsersEntity user = new UsersEntity(jsonUser);
        // checks if User is Invalid
        this.throwExceptionIfInvalidUser(user);
        try{
            this.usersRepository.save(user);
        }
        catch(Exception e){
            throw new Exception("Error Inserting User!");
        }
        return user;
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
        // checks if User is Invalid
        this.throwExceptionIfInvalidUser(user);
        try{
            this.usersRepository.save(user);
        }
        catch(Exception e){
            throw new Exception("Error Updating User!");
        }
        return user;
    }

    @Transactional(rollbackOn = Exception.class)
    public UsersEntity deactivateUser(Integer userID) throws Exception{      
        if(userID == null|| userID <= 0){
            throw new Exception("Invalid User ID!");
        }
        UsersEntity user = this.getActiveUserByID(userID);
        if(user == null){
            throw new Exception("Active User with the provided ID was not found!");
        }
        user.deactivatesUser();
        try{
            this.usersRepository.save(user);
        }
        catch(Exception e){
            throw new Exception("Error Deactivating User!");
        }
        return user;
    }

    public void throwExceptionIfInvalidUser(UsersEntity user) throws Exception{
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
            throw new Exception("User Values are not valid!");
        }
        // verify unique email
        if(this.isEmailInUse(user.getEmail(), user.getID())){
            throw new Exception("User Email is already associated with a different account!");
        }
    }
}
