package com.beginsecure.usersbchallenge.Persistence.Entity;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.beginsecure.usersbchallenge.Util.Constants;
import com.beginsecure.usersbchallenge.Util.Functions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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

    @Column(name="password_salt", nullable=false, length=255)
    private String passwordSalt;

    @Column(name="birthdate", nullable=false)
    private Date birthdate;

    @Column(name="created_on", nullable=false)
    private Date createdOn;
    
    @Column(name="updated_on", nullable=false)
    private Date updatedOn;

    @Column(name="is_active", nullable=false)
    private Boolean isActive;

    @Transient
    private Integer age;

    // CONSTRUCTORS
    // default
    public UsersEntity() {}

    // json
    public UsersEntity(JSONObject jsonContent) throws NoSuchAlgorithmException, JSONException {
        // only fills attributes specified in the JSON
        // id
        this.ID = null;
        // name
        if(jsonContent.has(Constants.JSON_P_NAME)){
            this.name = jsonContent.getString(Constants.JSON_P_NAME);
        }
        // email
        if(jsonContent.has(Constants.JSON_P_EMAIL)){
            this.email = Functions.setEmail(jsonContent.getString(Constants.JSON_P_EMAIL));
        }
        // password
        if(jsonContent.has(Constants.JSON_P_PASSWORD)){
            this.setPassword(jsonContent.getString(Constants.JSON_P_PASSWORD));
        }
        // birthdate
        if(jsonContent.has(Constants.JSON_P_BIRTHDATE)){
            this.birthdate = Functions.stringToDate(
                jsonContent.optString(Constants.JSON_P_BIRTHDATE),
                Constants.DATE_FORMAT);
        }
        // created on
        this.createdOn = new Date();
        // updated on
        this.updatedOn = this.createdOn;
        // is active
        this.isActive = true;
    }

    // json
    public void updateUser(JSONObject jsonContent) throws NoSuchAlgorithmException, JSONException {
        // only fills attributes specified in the JSON
        // ID
        if(jsonContent.has(Constants.JSON_P_ID)){
            this.ID = jsonContent.getInt(Constants.JSON_P_ID);
        }
        // name
        if(jsonContent.has(Constants.JSON_P_NAME)){
            this.name = jsonContent.getString(Constants.JSON_P_NAME);
        }
        // email
        if(jsonContent.has(Constants.JSON_P_EMAIL)){
            this.email = Functions.setEmail(jsonContent.getString(Constants.JSON_P_EMAIL));
        }
        // password
        if(jsonContent.has(Constants.JSON_P_PASSWORD)){
           this.setPassword(jsonContent.getString(Constants.JSON_P_PASSWORD));
        }
        // birthdate
        if(jsonContent.has(Constants.JSON_P_BIRTHDATE)){
            this.birthdate = Functions.stringToDate(
                jsonContent.optString(Constants.JSON_P_BIRTHDATE),
                Constants.DATE_FORMAT);
        }
        // is active
        if(jsonContent.has(Constants.JSON_P_ISACTIVE)){
            this.isActive = (Boolean) Functions.defaultValue(
                jsonContent.optBooleanObject(Constants.JSON_P_ISACTIVE),
                Boolean.FALSE);
        }
        // updated on
        this.updatedOn = new Date();
    }

    // verify if recently created user is invalid (contains null values)
    public boolean isUserInvalid(){
        return 
            (this.name == null || this.name.isBlank())
            || (this.email == null || this.email.isBlank())
            || (this.password == null || this.password.isBlank())
            || (this.passwordSalt == null || this.passwordSalt.isBlank())
            || this.birthdate == null;
    }

    // TO JSON
    public JSONObject toJsonObject() {
        return new JSONObject()
            .put("ID", Functions.defaultValue(this.ID, JSONObject.NULL))
            .put("name", Functions.defaultValue(this.name, JSONObject.NULL))
            .put("email", Functions.defaultValue(this.email, JSONObject.NULL))
            .put("password", Functions.defaultValue(this.password, JSONObject.NULL))
            .put("passwordSalt", Functions.defaultValue(this.passwordSalt, JSONObject.NULL))
            .put("birthdate", Functions.defaultValue(
                                Functions.dateTimeToString(this.birthdate, Constants.DATE_FORMAT), 
                                JSONObject.NULL))
            .put("age", Functions.defaultValue(this.getAge(), JSONObject.NULL))
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

    public void setID(Integer ID) {
        this.ID = ID;
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

    public void setPassword(String password) throws NoSuchAlgorithmException {
        if(Functions.isPasswordValid(password)){
            this.passwordSalt = Functions.encodeHash(Functions.generateSalt());
            this.password = Functions.hashAndEncodePassword(this.passwordSalt, password);
        }
        else{
            this.passwordSalt = null;
            this.password = null;
        }
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

    public void setCreatedOn() {
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

    public Integer getAge() {
        if(this.birthdate == null)
            return null;
        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(this.birthdate);

        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(new Date());

        int age = todayCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

        // Adjust if birthdate hasn't occurred yet this year
        if (todayCal.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
