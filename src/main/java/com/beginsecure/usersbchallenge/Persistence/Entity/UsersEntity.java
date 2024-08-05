package com.beginsecure.usersbchallenge.Persistence.Entity;

import java.util.Date;

import org.json.JSONObject;

import com.beginsecure.usersbchallenge.Util.Constants;
import com.beginsecure.usersbchallenge.Util.Functions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name="users", schema=Constants.DB_NAME)
public class UsersEntity {
    @Column(name="id")
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer ID;

    @Column(name="name", nullable=false, length=100)
    private String name;

    @Column(name="email", nullable=false, length=100)
    private String email;

    @Column(name="password", nullable=false, length=255)
    private String password;

    @Column(name="birthdate", nullable=false)
    private Date birthdate;

    @Column(name="created_on", nullable=false)
    private Date createdOn;
    
    @Column(name="updated_on", nullable=false)
    private Date updatedOn;

    @Column(name="is_active", nullable=false)
    private Boolean isActive;

    // CONSTRUCTORS
    // default
    public UsersEntity() {}

    // json
    public UsersEntity(JSONObject jsonContent) {
        // extract data from JSON
        this.name = jsonContent.optString(Constants.JSON_P_NAME);
        this.email = jsonContent.optString(Constants.JSON_P_EMAIL);
        this.birthdate = Functions.stringToDate(
            jsonContent.getString(Constants.JSON_P_BIRTHDATE),
            Constants.DATE_FORMAT);

        this.createdOn = Functions.stringToDate(
            new Date().toString(),
            Constants.DATE_TIME_MSEC_FORMAT);

        this.updatedOn = this.createdOn;
        this.isActive = true;
    }

    // UPDATE
    public void updateUser(JSONObject jsonContent){
        // only updates attributes specified in the JSON

        // name
        if(jsonContent.has(Constants.JSON_P_NAME)){
            this.name = jsonContent.optString(Constants.JSON_P_NAME);
        }
        // email
        if(jsonContent.has(Constants.JSON_P_EMAIL)){
            this.email = Functions.setEmail(jsonContent.optString(Constants.JSON_P_EMAIL));
        }
        // birthdate
        if(jsonContent.has(Constants.JSON_P_BIRTHDATE)){
            this.birthdate = Functions.stringToDate(
                jsonContent.optString(Constants.JSON_P_BIRTHDATE),
                Constants.DATE_FORMAT);
        }
        // updated on
        // this.updatedOn = Functions.stringToDate(
        //     new Date().toString(),
        //     Constants.DATE_TIME_MSEC_FORMAT);
        this.updatedOn = new Date();

        // is active
        if(jsonContent.has(Constants.JSON_P_ISACTIVE)){
            this.isActive = (Boolean) Functions.defaultValue(
                jsonContent.optBoolean(Constants.JSON_P_ISACTIVE), 
                Boolean.FALSE);
        }
    }

    // TO JSON
    public JSONObject toJsonObject() {
        return new JSONObject()
            .put("ID", Functions.defaultValue(this.ID, JSONObject.NULL))
            .put("name", Functions.defaultValue(this.name, JSONObject.NULL))
            .put("email", Functions.defaultValue(this.email, JSONObject.NULL))
            // password missing
            .put("birthdate", Functions.defaultValue(
                                Functions.dateTimeToString(this.birthdate, Constants.DATE_FORMAT), 
                                JSONObject.NULL))
            .put("createdOn", Functions.defaultValue(
                                Functions.dateTimeToString(this.createdOn, Constants.DATE_TIME_MSEC_FORMAT), 
                                JSONObject.NULL))
            .put("updatedOn", Functions.defaultValue(
                                Functions.dateTimeToString(this.updatedOn, Constants.DATE_TIME_MSEC_FORMAT), 
                                JSONObject.NULL))
            .put("isActive", Functions.defaultValue(this.isActive, JSONObject.NULL));
    }

    // GETTERS / SETTERS
    public Integer getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = new Date();
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn() {
        this.updatedOn = new Date();
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
