package com.beginsecure.usersbchallenge.Persistence.Controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
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

    private String exceptionOutput(JSONObject jsonOutput, String newOutputMsg, 
                                    JSONObject jsonDebug, String exceptionMsg, AuditEntity audit){
        // json output
        jsonOutput.put(Constants.JSON_O_ERROR_PATH, newOutputMsg);
        // json debug
        jsonDebug.put(Constants.JSON_OUTPUT_PATH, jsonOutput);
        jsonDebug.put(Constants.JSON_D_EXCEPTION_PATH, exceptionMsg);
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception Ocurred!");
        // insert audit
        this.auditService.exceptionAudit(audit, jsonDebug);
        return jsonOutput.toString();
    }

    private String successOutput(JSONObject jsonOutput, String newOutputMsg, JSONArray outputArray,
                                    JSONObject jsonDebug, AuditEntity audit){
        // json output
        jsonOutput.put(Constants.JSON_O_MESSAGE_PATH, newOutputMsg);
        jsonOutput.put(Constants.JSON_OUTPUT_PATH, outputArray);
        // json debug
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Finished...");
        jsonDebug.put(Constants.JSON_D_OUTPUT_PATH, jsonOutput);
        // insert audit
        this.auditService.endAudit(audit, jsonDebug);
        return jsonOutput.toString();
    }

    // GET USER
    @GetMapping("getUsers")
    public ResponseEntity<String> getUsers(@RequestBody String rawJsonRequest) {
        String finalOutputStr = null;
        String requestURI = "api/users/getUsers";
        String processUUID = UUID.randomUUID().toString();
        AuditEntity audit = new AuditEntity(processUUID, Constants.AUDIT_PROCESS_NAME_GET_USER);
        Date startTime = new Date();
        JSONObject jsonDebug = Functions.initiateJsonDebug(startTime, requestURI, rawJsonRequest);
        this.auditService.beginAudit(audit, jsonDebug);
        

        JSONObject jsonOutput = new JSONObject();
        JSONObject jsonInput = null;

        if(rawJsonRequest == null || rawJsonRequest.isBlank()){
            String outputMsg = "JSON Input is empty.";
            String exceptionMsg = outputMsg;
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        try{
            jsonInput = new JSONObject(rawJsonRequest);
            if(!jsonInput.has(Constants.JSON_P_CONTENT)){
                throw new Exception("Invalid JSON! Content was not found.");
            }
            jsonDebug.put(Constants.JSON_D_INPUT_PATH, jsonInput);
        }
        catch(Exception e){
            String outputMsg = "JSON Input is invalid or empty.";
            String exceptionMsg = e.getMessage();
            finalOutputStr =exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        
        JSONObject jsonContent = jsonInput.getJSONObject(Constants.JSON_P_CONTENT);
        Integer userID = jsonContent.optIntegerObject(Constants.JSON_P_ID);
        if(userID <= 0){
           userID = null;
        }
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted ID to Query...");

        try{
            List<UsersEntity> users = this.usersService.getUsers(userID);
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Queried Data...");
            jsonDebug = Functions.addDebugTrail(jsonDebug, 
                users == null || users.isEmpty() ? "Did not found Users..." : "Found Users...");
            String newOutputMsg = "Successfully Fetched User/s";
            finalOutputStr = successOutput(jsonOutput, newOutputMsg, 
                queryResultsToJsonArray(users), jsonDebug, audit);
        }
        catch(Exception e){
            String outputMsg = "Error while Fetching User/s!";
            String exceptionMsg = e.getMessage();
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        return ResponseEntity.ok(finalOutputStr);
    }

    // UPDATE USER
    @PutMapping(path="updateUser")
    public ResponseEntity<String> updateUser(@RequestBody String rawJsonRequest) {
        String finalOutputStr = null;
        String requestURI = "api/users/updateUser";
        Date startTime = new Date();
        String processUUID = UUID.randomUUID().toString();
        AuditEntity audit = new AuditEntity(processUUID, Constants.AUDIT_PROCESS_NAME_UPDATE_USER);

        JSONObject jsonDebug = Functions.initiateJsonDebug(startTime, requestURI, rawJsonRequest);
        this.auditService.beginAudit(audit, jsonDebug);

        JSONObject jsonOutput = new JSONObject();
        JSONObject jsonInput = null;

        if(rawJsonRequest == null || rawJsonRequest.isBlank()){
            String outputMsg = "JSON Input is empty.";
            String exceptionMsg = outputMsg;
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        
        try{
            jsonInput = new JSONObject(rawJsonRequest);
            if(!jsonInput.has(Constants.JSON_P_CONTENT)){
                throw new Exception("Invalid JSON! Content was not found.");
            }
            jsonDebug.put(Constants.JSON_D_INPUT_PATH, jsonInput);
        }
        catch(Exception e){
            String outputMsg = "JSON Input is invalid or empty.";
            String exceptionMsg = e.getMessage();
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        JSONObject jsonContent = jsonInput.getJSONObject(Constants.JSON_P_CONTENT);
        if(jsonContent.keySet().size() == 0){
            String outputMsg = "JSON Input does not contain enough content to update a User.";
            String exceptionMsg = "Exception occurred while extracting Data from JSON...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        Integer userID = jsonContent.optIntegerObject(Constants.JSON_P_ID);
        if(userID == null|| userID <= 0){
            String outputMsg = "JSON Input does not contain an ID to Update.";
            String exceptionMsg = "Exception occurred while extracting Data from JSON...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted ID to Update...");

        UsersEntity user = this.usersService.getUserByID(userID);
        if(user == null){
            String outputMsg = "User to Update was not found. Provided ID is Invalid!";
            String exceptionMsg = "Exception when querying User...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Queried User to Update...");

        // updates user with json content (only specified and valid values, invalid values are put to null)
        try {
            user.updateUser(jsonContent);
        } catch (NoSuchAlgorithmException | JSONException e) {
            String outputMsg = "Error Updating User!";
            String exceptionMsg = e.getMessage();
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        // verify email
        if(user.getEmail() == null){
            String outputMsg = "Provided Email is not valid! It has an invalid format!";
            String exceptionMsg = "Exception when mapping updated User Data...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        // verify password
        if(user.getPassword() == null){
            String outputMsg = "Provided Password is not valid! It must have at least " 
                + Constants.PASSWORD_MIN_LEN + " characters!";
            String exceptionMsg = "Exception when mapping updated User Data...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        // verify for NULL values
        if(user.isUserInvalid()){
            String outputMsg = "Provided updated values are not valid!";
            String exceptionMsg = "Exception when mapping updated User Data...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        // verify unique email
        if(this.usersService.isEmailInUse(user.getEmail(), userID)){
            String outputMsg = "Provided Email is already associated with a different account!";
            String exceptionMsg = "Exception when mapping updated User Data...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted Data to update User...");
        // update user in db
        try{
            user = this.usersService.updateUser(user);
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Updated Data...");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Retrieved Inserted Data...");

            String newOutputMsg = "Successfully Updated User";
            // user.setPassword(null);
            finalOutputStr = successOutput(jsonOutput, newOutputMsg, 
                queryResultsToJsonArray(Collections.singletonList(user)), jsonDebug, audit);
        }
        catch(Exception e){
            String outputMsg = "Error while Updating User!";
            String exceptionMsg = e.getMessage();
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        
        return ResponseEntity.ok(finalOutputStr);
    }

    // "DELETE" USER
    @PutMapping(path="deleteUser")
    public ResponseEntity<String> deleteUser(@RequestBody String rawJsonRequest) {
        String finalOutputStr = null;
        String requestURI = "api/users/deleteUser";
        String processUUID = UUID.randomUUID().toString();
        AuditEntity audit = new AuditEntity(processUUID, Constants.AUDIT_PROCESS_NAME_DELETE_USER);
        Date startTime = new Date();
        JSONObject jsonDebug = Functions.initiateJsonDebug(startTime, requestURI, rawJsonRequest);
        this.auditService.beginAudit(audit, jsonDebug);
        

        JSONObject jsonOutput = new JSONObject();
        JSONObject jsonInput = null;

        if(rawJsonRequest == null || rawJsonRequest.isBlank()){
            String outputMsg = "JSON Input is empty.";
            String exceptionMsg = outputMsg;
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        try{
            jsonInput = new JSONObject(rawJsonRequest);
            if(!jsonInput.has(Constants.JSON_P_CONTENT)){
                throw new Exception("Invalid JSON! Content was not found.");
            }
            jsonDebug.put(Constants.JSON_D_INPUT_PATH, jsonInput);
        }
        catch(Exception e){
            String outputMsg = "JSON Input is invalid or empty.";
            String exceptionMsg = e.getMessage();
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        
        JSONObject jsonContent = jsonInput.getJSONObject(Constants.JSON_P_CONTENT);
        Integer userID = jsonContent.optIntegerObject(Constants.JSON_P_ID);
        if(userID == null|| userID <= 0){
            String outputMsg = "JSON Input does not contain an ID to Delete.";
            String exceptionMsg = "Exception occurred while extracting ID from JSON...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted ID to Query...");

        UsersEntity user = this.usersService.getActiveUserByID(userID);
        if(user == null){
            String outputMsg = "User to Delete was not found!";
            String exceptionMsg = "Exception when querying User...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Queried User to Delete...");

        try{
            user.setIsActive(false);        
            user = this.usersService.updateUser(user);
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Deactivated User...");
            String newOutputMsg = "Successfully Deleted User";
            finalOutputStr = successOutput(jsonOutput, newOutputMsg, 
                queryResultsToJsonArray(Collections.singletonList(user)), jsonDebug, audit);
        }
        catch(Exception e){
            String outputMsg = "Error while Deleting User!";
            String exceptionMsg = e.getMessage();
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        return ResponseEntity.ok(finalOutputStr);
    }

    // CREATE USER
    @PutMapping(path="createUser")
    public ResponseEntity<String> createUser(@RequestBody String rawJsonRequest) {
        String finalOutputStr = null;
        String requestURI = "api/users/createUser";
        Date startTime = new Date();
        String processUUID = UUID.randomUUID().toString();
        AuditEntity audit = new AuditEntity(processUUID, Constants.AUDIT_PROCESS_NAME_INSERT_USER);

        JSONObject jsonDebug = Functions.initiateJsonDebug(startTime, requestURI, rawJsonRequest);
        this.auditService.beginAudit(audit, jsonDebug);

        JSONObject jsonOutput = new JSONObject();
        JSONObject jsonInput = null;

        if(rawJsonRequest == null || rawJsonRequest.isBlank()){
            String outputMsg = "JSON Input is empty.";
            String exceptionMsg = outputMsg;
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        
        try{
            jsonInput = new JSONObject(rawJsonRequest);
            if(!jsonInput.has(Constants.JSON_P_CONTENT)){
                throw new Exception("Invalid JSON! Content was not found.");
            }
            jsonDebug.put(Constants.JSON_D_INPUT_PATH, jsonInput);
        }
        catch(Exception e){
            String outputMsg = "JSON Input is invalid or empty.";
            String exceptionMsg = e.getMessage();
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        JSONObject jsonContent = jsonInput.getJSONObject(Constants.JSON_P_CONTENT);
        if(jsonContent.keySet().size() == 0){
            String outputMsg = "JSON Input does not contain enough content to create a User.";
            String exceptionMsg = "Exception occurred while extracting Data from JSON...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }

        // creates user with json content
        UsersEntity user = null;
        try {
            user = new UsersEntity(jsonContent);
        } catch (NoSuchAlgorithmException | JSONException e) {
            String outputMsg = "Error Creating User!";
            String exceptionMsg = e.getMessage();
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        // verify email
        if(user.getEmail() == null){
            String outputMsg = "Provided Email is not valid! It has an invalid format!";
            String exceptionMsg = "Exception when mapping updated User Data...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        // verify password
        if(user.getPassword() == null){
            String outputMsg = "Provided Password is not valid! It must have at least " + Constants.PASSWORD_MIN_LEN + " characters!";
            String exceptionMsg = "Exception when mapping updated User Data...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        // verify NULL values inside user
        if(user.isUserInvalid()){
            String outputMsg = "Provided updated values are not valid!";
            String exceptionMsg = "Exception when mapping updated User Data...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        // verify unique email
        if(this.usersService.isEmailInUse(user.getEmail(), null)){
            String outputMsg = "Provided Email is already associated with a different account!";
            String exceptionMsg = "Exception when mapping updated User Data...";
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }

        jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted Data to create User...");
        
        // insert user in db
        try{
            user = this.usersService.insertUser(user);
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Inserted Data...");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Retrieved Inserted Data...");

            String newOutputMsg = "Successfully Created User";
            finalOutputStr = successOutput(jsonOutput, newOutputMsg, 
                queryResultsToJsonArray(Collections.singletonList(user)), jsonDebug, audit);
        }
        catch(Exception e){
            String outputMsg = "Error while Creating User!";
            String exceptionMsg = e.getMessage();
            finalOutputStr = exceptionOutput(jsonOutput, outputMsg, jsonDebug, exceptionMsg, audit);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalOutputStr);
        }
        return ResponseEntity.ok(finalOutputStr);
    }
}

