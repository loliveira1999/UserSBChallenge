package com.beginsecure.usersbchallenge.Util;

import org.json.JSONObject;
import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Functions{
    public static Integer stringToInteger(String str){
        return str == null ? null : Integer.valueOf(str);
    }

    public static Date stringToDate(String str, String dateFormat){
        if(str == null || str.isBlank())
            return null;
        if(dateFormat == null || dateFormat.isBlank())
            dateFormat = Constants.DATE_TIME_SEC_FORMAT;
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Date dateTime = null;
        try {
            dateTime = formatter.parse(str);
            System.out.println("Converted Date Time: " + dateTime.toString());
        } catch (ParseException e) {
            System.out.println("Invalid Date Format: " + e.getMessage());
        }
        return dateTime;
    }

    public static String dateTimeToString(Date date, String dateFormat){
        if(date == null)
            return null;
        if(dateFormat == null || dateFormat.isBlank())
            dateFormat = Constants.DATE_TIME_SEC_FORMAT;
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }

    public static String formatStringDateTime(String str, String dateFormat){
        if(str == null || str.isBlank())
            return null;
        if(dateFormat == null || dateFormat.isBlank())
            dateFormat = Constants.DATE_TIME_SEC_FORMAT;
        Date date = Functions.stringToDate(str, dateFormat);
        return date == null ? null : date.toString();
    }

    public static String setEmail(String email){
        if(!Functions.isEmailValid(email))
            return null;
        return email.trim();
    }

    public static String setPassword(String password){
        if(!Functions.isPasswordValid(password))
            return null;
        return password.trim();
    }

    public static String concatStrings(String[] strs){
        if(strs == null || strs.length == 0)
            return null;
        String finalStr = "";
        for(String s : strs){
            finalStr += s == null ? "" : s;
        }
        return finalStr.trim();
    }

    public static String stringifyHours(Float fHours){
        if(fHours == null || fHours.isNaN())
            return null;
        String sHours = null;
        int iHours = fHours.intValue();
        int minutes = (int) ((fHours.floatValue() - iHours) * 60);
        sHours = Functions.concatStrings(new String[]{
            iHours == 0 && minutes != 0 ? "" : iHours + "h ",
            minutes == 0 ? "" : minutes + "m"
        });
        return sHours.isBlank() ? null : sHours;
    }

    public static JSONObject addDebugTrail(JSONObject jsonDebug, String newTrailMsg) {
        // DateTimeFormatter for formatting the timestamp
        Date startTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_TIME_MSEC_FORMAT);
        // Initialize jsonDebug if it is null
        if (jsonDebug == null) {
            jsonDebug = new JSONObject();
        }
        // Get or create the AuditTrail array
        JSONArray auditTrail = jsonDebug.optJSONArray(Constants.JSON_D_TRAIL_PATH);
        if (auditTrail == null) {
            auditTrail = new JSONArray();
            jsonDebug.put(Constants.JSON_D_TRAIL_PATH, auditTrail);
        }

        // Add the new trail message with timestamp to the array
        auditTrail.put("[" + formatter.format(startTime) + "] " + newTrailMsg);

        // Return the updated jsonDebug object
        return jsonDebug;
    }

    public static JSONObject initiateJsonDebug(Date startTime, String requestURI, String rawInput){
        startTime = startTime == null ? new Date() : startTime;
        JSONObject jsonDebug = new JSONObject()
            .put(Constants.JSON_D_STARTDATE_PATH, Functions.dateTimeToString(startTime, Constants.DATE_TIME_MSEC_FORMAT))
            .put(Constants.JSON_D_URI_PATH, Functions.defaultValue(requestURI, JSONObject.NULL))
            .put(Constants.JSON_D_TRAIL_PATH, new JSONArray())
            .put(Constants.JSON_D_RAW_INPUT_PATH, Functions.defaultValue(rawInput, JSONObject.NULL)
        );
        jsonDebug = Functions.addDebugTrail(jsonDebug, "Initiated...");
        return jsonDebug;
    }

    public static Object defaultValue(Object value, Object defaultValue){
        return value == null ? defaultValue : value;
    }
    

    public static boolean isEmailValid(String email){
        if(email == null || email.isBlank())
            return false;
        Pattern pattern = Pattern.compile(Constants.EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        boolean matchFound = matcher.find();
        if(matchFound)
            return true;
        else 
            return false;
    }

    public static boolean isPasswordValid(String password){
        if(password == null || password.isBlank() || password.length() < Constants.PASSWORD_MIN_LEN)
            return false;
        return true;
    }
}