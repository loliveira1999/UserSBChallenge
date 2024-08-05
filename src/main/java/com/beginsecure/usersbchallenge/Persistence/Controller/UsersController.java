package com.beginsecure.usersbchallenge.Persistence.Controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beginsecure.usersbchallenge.Persistence.Entity.AuditEntity;
import com.beginsecure.usersbchallenge.Persistence.Entity.UsersEntity;
import com.beginsecure.usersbchallenge.Persistence.Service.AuditService;
import com.beginsecure.usersbchallenge.Persistence.Service.UsersService;
import com.beginsecure.usersbchallenge.Util.Constants;
import com.beginsecure.usersbchallenge.Util.Functions;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(path="api/users")
public class UsersController {
    private final UsersService usersService;
    private final AuditService auditService;

    @Autowired
    public UsersController(UsersService usersService, AuditService auditService){
        this.usersService = usersService;
        this.auditService = auditService;
    }

    private JSONArray queryResultsToJsonArray(List<UsersEntity> users) {
        JSONArray queryArray = new JSONArray();
        if (users == null || users.isEmpty())
            return queryArray;

        for (UsersEntity user : users) {
            queryArray.put(user.toJsonObject());
        }
        return queryArray;
    }

    // GET USER
    @GetMapping("getUsers")
    public ResponseEntity<String> getUsers(@RequestBody String rawJsonRequest) {
        String requestURI = "api/users/getUsers";
        String processUUID = UUID.randomUUID().toString();
        AuditEntity audit = new AuditEntity(processUUID, Constants.AUDIT_PROCESS_NAME_GET_USER);
        Date startTime = new Date();
        JSONObject jsonDebug = Functions.initiateJsonDebug(startTime, requestURI, rawJsonRequest);
        auditService.beginAudit(audit, jsonDebug);
        

        JSONObject jsonOutput = new JSONObject();
        JSONObject jsonInput = null;

        if(rawJsonRequest == null || rawJsonRequest.isBlank()){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "JSON Input is empty.");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "JSON Input is empty...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        try{
            jsonInput = new JSONObject(rawJsonRequest);
            if(!jsonInput.has(Constants.JSON_P_CONTENT)){
                throw new Exception("Invalid JSON! Content was not found.");
            }
            jsonDebug.put(Constants.JSON_D_INPUT_PATH, jsonInput);
        }
        catch(Exception e){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "JSON Input is invalid or empty.");
            jsonDebug.put(Constants.JSON_D_EXCEPTION_PATH, e.getMessage());
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception occurred while mapping JSON Input...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        
        JSONObject jsonContent = jsonInput.getJSONObject(Constants.JSON_P_CONTENT);
        Integer userID = jsonContent.optIntegerObject(Constants.JSON_P_ID);
        if(userID <= 0){
           userID = null; 
        }
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted ID to Query...");

        List<UsersEntity> users = usersService.getUsers(userID);
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Queried Data...");
        jsonDebug = Functions.addDebugTrail(jsonDebug, users == null || users.isEmpty() ? "Did not found Users..." : "Found Users...");

        jsonOutput.put(Constants.JSON_O_MESSAGE_PATH, "Successfully Queried User/s");
        jsonOutput.put(Constants.JSON_OUTPUT_PATH, queryResultsToJsonArray(users));
        jsonDebug.put(Constants.JSON_OUTPUT_PATH, jsonOutput.getJSONArray(Constants.JSON_OUTPUT_PATH));
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Finished...");

        auditService.endAudit(audit, jsonDebug);
        return ResponseEntity.ok(jsonOutput.toString());
    }

    // UPDATE USER
    @PutMapping(path="updateUser")
    public ResponseEntity<String> updateUser(@RequestBody String rawJsonRequest) {
        String requestURI = "api/users/updateUser";
        Date startTime = new Date();
        String processUUID = UUID.randomUUID().toString();
        AuditEntity audit = new AuditEntity(processUUID, Constants.AUDIT_PROCESS_NAME_UPDATE_USER);

        JSONObject jsonDebug = Functions.initiateJsonDebug(startTime, requestURI, rawJsonRequest);
        auditService.beginAudit(audit, jsonDebug);

        JSONObject jsonOutput = new JSONObject();
        JSONObject jsonInput = null;

        if(rawJsonRequest == null || rawJsonRequest.isBlank()){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "JSON Input is empty.");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "JSON Input is empty...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        
        try{
            jsonInput = new JSONObject(rawJsonRequest);
            if(!jsonInput.has(Constants.JSON_P_CONTENT)){
                throw new Exception("Invalid JSON! Content was not found.");
            }
            jsonDebug.put(Constants.JSON_D_INPUT_PATH, jsonInput);
        }
        catch(Exception e){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "JSON Input is invalid or empty.");
            jsonDebug.put(Constants.JSON_D_EXCEPTION_PATH, e.getMessage());
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception occurred while extracting Data from JSON Input...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        JSONObject jsonContent = jsonInput.getJSONObject(Constants.JSON_P_CONTENT);
        if(jsonContent.keySet().size() == 0){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "JSON Input does not contain enough content to update a User.");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception occurred while extracting Data from JSON...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        Integer userID = jsonContent.optIntegerObject(Constants.JSON_P_ID);
        if(userID == null|| userID <= 0){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "JSON Input does not contain an ID to Update.");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception occurred while extracting ID from JSON...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted ID to Update...");

        UsersEntity user = usersService.getUserByID(userID);
        if(user == null){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "User to Update was not found. Provided ID is Invalid!");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception when querying User...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Queried User to Update...");

        // updates user with json content (only specified and valid values, invalid values are put to null)
        user.updateUser(jsonContent);
        // verify email
        if(user.getEmail() == null){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "Provided Email is not valid! It has an invalid format!");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception when mapping updated User Data...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        // verify password
        if(user.getPassword() == null){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "Provided Password is not valid! It must have at least " 
                + Constants.PASSWORD_MIN_LEN + " characters!");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception when mapping updated User Data...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        // verify for NULL values
        if(user.isUserValid()){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "Provided updated values are not valid!");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception when mapping updated User Data...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        // verify unique email
        if(usersService.getUserWSameEmail(userID, user.getEmail()) != null){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "Provided Email is already associated with a different account!");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception when mapping updated User Data...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }

        jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted Data to update User...");
        
        // update user in db
        user = usersService.updateUser(user);
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Updated Data...");

        jsonOutput.put(Constants.JSON_OUTPUT_PATH, user.toJsonObject());
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Retrieved Inserted Data...");
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Finished...");
        
        jsonOutput.put(Constants.JSON_O_MESSAGE_PATH, "Successfully Updated User");
        jsonDebug.put(Constants.JSON_OUTPUT_PATH, jsonOutput.getJSONObject(Constants.JSON_OUTPUT_PATH));

        auditService.endAudit(audit, jsonDebug);
        return ResponseEntity.ok(jsonOutput.toString());
    }

    // "DELETE" USER
    @PutMapping(path="deleteUser")
    public ResponseEntity<String> deleteUser(@RequestBody String rawJsonRequest) {
        String requestURI = "api/users/deleteUser";
        String processUUID = UUID.randomUUID().toString();
        AuditEntity audit = new AuditEntity(processUUID, Constants.AUDIT_PROCESS_NAME_GET_USER);
        Date startTime = new Date();
        JSONObject jsonDebug = Functions.initiateJsonDebug(startTime, requestURI, rawJsonRequest);
        auditService.beginAudit(audit, jsonDebug);
        

        JSONObject jsonOutput = new JSONObject();
        JSONObject jsonInput = null;

        if(rawJsonRequest == null || rawJsonRequest.isBlank()){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "JSON Input is empty.");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "JSON Input is empty...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        try{
            jsonInput = new JSONObject(rawJsonRequest);
            if(!jsonInput.has(Constants.JSON_P_CONTENT)){
                throw new Exception("Invalid JSON! Content was not found.");
            }
            jsonDebug.put(Constants.JSON_D_INPUT_PATH, jsonInput);
        }
        catch(Exception e){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "JSON Input is invalid or empty.");
            jsonDebug.put(Constants.JSON_D_EXCEPTION_PATH, e.getMessage());
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception occurred while mapping JSON Input...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        
        JSONObject jsonContent = jsonInput.getJSONObject(Constants.JSON_P_CONTENT);
        Integer userID = jsonContent.optIntegerObject(Constants.JSON_P_ID);
        if(userID == null|| userID <= 0){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "JSON Input does not contain an ID to Delete.");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception occurred while extracting ID from JSON...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted ID to Query...");

        UsersEntity user = usersService.getActiveUserByID(userID);
        if(user == null){
            jsonOutput.put(Constants.JSON_O_ERROR_PATH, "User to Delete was not found.");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception when querying User...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Queried User to Delete...");

        user.setIsActive(false);
        
        user = usersService.updateUser(user);
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Deactivated User...");

        jsonOutput.put(Constants.JSON_O_MESSAGE_PATH, "Successfully Deleted User");
        jsonOutput.put(Constants.JSON_OUTPUT_PATH, user.toJsonObject());
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Finished...");
        
        jsonDebug.put(Constants.JSON_OUTPUT_PATH, jsonOutput.getJSONObject(Constants.JSON_OUTPUT_PATH));

        auditService.endAudit(audit, jsonDebug);
        return ResponseEntity.ok(jsonOutput.toString());
    }

        // UPDATE USER
        @PutMapping(path="createUser")
        public ResponseEntity<String> createUser(@RequestBody String rawJsonRequest) {
            String requestURI = "api/users/createUser";
            Date startTime = new Date();
            String processUUID = UUID.randomUUID().toString();
            AuditEntity audit = new AuditEntity(processUUID, Constants.AUDIT_PROCESS_NAME_UPDATE_USER);
    
            JSONObject jsonDebug = Functions.initiateJsonDebug(startTime, requestURI, rawJsonRequest);
            auditService.beginAudit(audit, jsonDebug);
    
            JSONObject jsonOutput = new JSONObject();
            JSONObject jsonInput = null;
    
            if(rawJsonRequest == null || rawJsonRequest.isBlank()){
                jsonOutput.put(Constants.JSON_O_ERROR_PATH, "JSON Input is empty.");
                jsonDebug = Functions.addDebugTrail(jsonDebug, "JSON Input is empty...");
                auditService.exceptionAudit(audit, jsonDebug);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
            }
            
            try{
                jsonInput = new JSONObject(rawJsonRequest);
                if(!jsonInput.has(Constants.JSON_P_CONTENT)){
                    throw new Exception("Invalid JSON! Content was not found.");
                }
                jsonDebug.put(Constants.JSON_D_INPUT_PATH, jsonInput);
            }
            catch(Exception e){
                jsonOutput.put(Constants.JSON_O_ERROR_PATH, "JSON Input is invalid or empty.");
                jsonDebug.put(Constants.JSON_D_EXCEPTION_PATH, e.getMessage());
                jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception occurred while extracting Data from JSON Input...");
                auditService.exceptionAudit(audit, jsonDebug);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
            }
            JSONObject jsonContent = jsonInput.getJSONObject(Constants.JSON_P_CONTENT);
            if(jsonContent.keySet().size() == 0){
                jsonOutput.put(Constants.JSON_O_ERROR_PATH, "JSON Input does not contain enough content to create a User.");
                jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception occurred while extracting Data from JSON...");
                auditService.exceptionAudit(audit, jsonDebug);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
            }
            Integer userID = jsonContent.optIntegerObject(Constants.JSON_P_ID);
            if(userID == null|| userID <= 0){
                jsonOutput.put(Constants.JSON_O_ERROR_PATH, "JSON Input does not contain an ID to Update.");
                jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception occurred while extracting ID from JSON...");
                auditService.exceptionAudit(audit, jsonDebug);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
            }
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted ID to Update...");
    
            UsersEntity user = usersService.getUserByID(userID);
            if(user == null){
                jsonOutput.put(Constants.JSON_O_ERROR_PATH, "User to Update was not found. Provided ID is Invalid!");
                jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception when querying User...");
                auditService.exceptionAudit(audit, jsonDebug);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
            }
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Queried User to Update...");
    
            // updates user with json content (only specified and valid values, invalid values are put to null)
            user.updateUser(jsonContent);
            // verify email
            if(user.getEmail() == null){
                jsonOutput.put(Constants.JSON_O_ERROR_PATH, "Provided Email is not valid! It has an invalid format!");
                jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception when mapping updated User Data...");
                auditService.exceptionAudit(audit, jsonDebug);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
            }
            // verify password
            if(user.getPassword() == null){
                jsonOutput.put(Constants.JSON_O_ERROR_PATH, "Provided Password is not valid! It must have at least " 
                    + Constants.PASSWORD_MIN_LEN + " characters!");
                jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception when mapping updated User Data...");
                auditService.exceptionAudit(audit, jsonDebug);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
            }
            // verify for NULL values
            if(user.isUserValid()){
                jsonOutput.put(Constants.JSON_O_ERROR_PATH, "Provided updated values are not valid!");
                jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception when mapping updated User Data...");
                auditService.exceptionAudit(audit, jsonDebug);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
            }
            // verify unique email
            if(usersService.getUserWSameEmail(userID, user.getEmail()) != null){
                jsonOutput.put(Constants.JSON_O_ERROR_PATH, "Provided Email is already associated with a different account!");
                jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception when mapping updated User Data...");
                auditService.exceptionAudit(audit, jsonDebug);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
            }
    
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted Data to update User...");
            
            // update user in db
            user = usersService.updateUser(user);
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Updated Data...");
    
            jsonOutput.put(Constants.JSON_OUTPUT_PATH, user.toJsonObject());
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Retrieved Inserted Data...");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Finished...");
            
            jsonOutput.put(Constants.JSON_O_MESSAGE_PATH, "Successfully Updated User");
            jsonDebug.put(Constants.JSON_OUTPUT_PATH, jsonOutput.getJSONObject(Constants.JSON_OUTPUT_PATH));
    
            auditService.endAudit(audit, jsonDebug);
            return ResponseEntity.ok(jsonOutput.toString());
        }
}

