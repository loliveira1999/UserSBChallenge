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
import org.springframework.web.bind.annotation.RequestParam;


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
            jsonOutput.put(Constants.JSON_ERROR_PATH, "JSON Input is empty.");
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
            jsonOutput.put(Constants.JSON_ERROR_PATH, "JSON Input is invalid or empty.");
            jsonDebug.put(Constants.JSON_D_EXCEPTION_PATH, e.getMessage());
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception occurred while mapping JSON Input...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        
        JSONObject jsonContent = jsonInput.getJSONObject(Constants.JSON_P_CONTENT);
        Integer userID = jsonContent.optIntegerObject(Constants.JSON_P_ID);
        System.out.println("ID:" + userID);
        if(userID <= 0){
           userID = null; 
        }
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted ID to Query...");

        List<UsersEntity> users = usersService.getUsers(userID);
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Queried Data...");
        jsonDebug = Functions.addDebugTrail(jsonDebug, users == null || users.isEmpty() ? "Did not found Users..." : "Found Users...");


        jsonOutput.put(Constants.JSON_OUTPUT_PATH, queryResultsToJsonArray(users));
        jsonDebug.put(Constants.JSON_OUTPUT_PATH, jsonOutput.getJSONArray(Constants.JSON_OUTPUT_PATH));
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Finished...");

        auditService.endAudit(audit, jsonDebug);
        return ResponseEntity.ok(jsonOutput.toString());
    }

    // UPDATE USER
    @PutMapping(path="updateUsers")
    public ResponseEntity<String> updateUsers(@RequestBody String rawJsonRequest) {
        String requestURI = "api/users/updateUsers";
        Date startTime = new Date();
        String processUUID = UUID.randomUUID().toString();
        AuditEntity audit = new AuditEntity(processUUID, Constants.AUDIT_PROCESS_NAME_UPDATE_USER);

        JSONObject jsonDebug = Functions.initiateJsonDebug(startTime, requestURI, rawJsonRequest);
        auditService.beginAudit(audit, jsonDebug);

        JSONObject jsonOutput = new JSONObject();
        JSONObject jsonInput = null;

        if(rawJsonRequest == null || rawJsonRequest.isBlank()){
            jsonOutput.put(Constants.JSON_ERROR_PATH, "JSON Input is empty.");
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
            jsonOutput.put(Constants.JSON_ERROR_PATH, "JSON Input is invalid or empty.");
            jsonDebug.put(Constants.JSON_D_EXCEPTION_PATH, e.getMessage());
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception occurred while extracting Data from JSON Input...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        JSONObject jsonContent = jsonInput.getJSONObject(Constants.JSON_P_CONTENT);
        if(jsonContent.keySet().size() == 0){
            jsonDebug.put(Constants.JSON_ERROR_PATH, "JSON Input does not contain enough content to insert a User.");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception occurred while extracting Data from JSON...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        Integer userID = jsonContent.optIntegerObject(Constants.JSON_P_ID);
        if(userID == null|| userID <= 0){
            jsonOutput.put(Constants.JSON_ERROR_PATH, "JSON Input does not contain an ID to Update.");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception occurred while extracting ID from JSON...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted ID to Update...");

        UsersEntity user = usersService.getUserByID(userID);
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Queried User to Update...");

        user.updateUser(jsonContent);
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Extracted Data to Update...");
        
        user = usersService.updateUser(user);
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Updated Data...");
        if(user.getID() == null){
            jsonDebug.put(Constants.JSON_ERROR_PATH, "User was not inserted successfully.");
            jsonDebug = Functions.addDebugTrail(jsonDebug, "Exception when retrieving inserted data...");
            auditService.exceptionAudit(audit, jsonDebug);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonOutput.toString());
        }

        jsonOutput.put(Constants.JSON_OUTPUT_PATH, user.toJsonObject());
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Retrieved Inserted Data...");
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Finished...");
        
        jsonDebug.put(Constants.JSON_OUTPUT_PATH, jsonOutput.getJSONObject(Constants.JSON_OUTPUT_PATH));

        auditService.endAudit(audit, jsonDebug);
        return ResponseEntity.ok(jsonOutput.toString());
    }

        

}
