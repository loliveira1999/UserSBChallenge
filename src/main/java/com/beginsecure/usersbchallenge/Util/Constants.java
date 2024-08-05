package com.beginsecure.usersbchallenge.Util;

public class Constants {
    public static final String APP_NAME = "UserSBChallenge";

    // JSON DEBUG
    public static final String JSON_D_RAW_INPUT_PATH    = "RawInput";
    public static final String JSON_D_INPUT_PATH        = "Input";
    public static final String JSON_D_EXCEPTION_PATH    = "Exception";
    public static final String JSON_D_STARTDATE_PATH    = "StartDate";
    public static final String JSON_D_TRAIL_PATH        = "AuditTrail";
    public static final String JSON_D_URI_PATH          = "URI";
    public static final String JSON_D_OUTPUT_PATH       = "FinalOutput";

    // JSON OUTPUT
    public static final String JSON_OUTPUT_PATH     = "Output";
    public static final String JSON_O_ERROR_PATH    = "Error";
    public static final String JSON_O_MESSAGE_PATH  = "Message";


    // JSON INPUT PARAMETERS
    public static final String JSON_P_CONTENT       = "Content";
    public static final String JSON_P_ID            = "id";
    public static final String JSON_P_NAME          = "name";
    public static final String JSON_P_EMAIL         = "email";
    public static final String JSON_P_PASSWORD      = "password";
    public static final String JSON_P_BIRTHDATE     = "birthdate";
    public static final String JSON_P_ISACTIVE      = "isActive";

    // FORMATS & PATTERNS
    public static final String DATE_FORMAT              = "yyyy-MM-dd";
    public static final String DATE_TIME_HOUR_FORMAT    = "yyyy-MM-dd HH";
    public static final String DATE_TIME_MIN_FORMAT     = "yyyy-MM-dd HH:mm";
    public static final String DATE_TIME_SEC_FORMAT     = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_MSEC_FORMAT    = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String EMAIL_PATTERN            = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+(\\.[a-zA-Z]+)+$";
    public static final int PASSWORD_MIN_LEN            = 8;

    // AUDIT
    public static final String AUDIT_PROCESS_NAME_DELETE_USER   = "DELETE_USER";
    public static final String AUDIT_PROCESS_NAME_UPDATE_USER   = "UPDATE_USER";
    public static final String AUDIT_PROCESS_NAME_GET_USER      = "GET_USER";
    public static final String AUDIT_PROCESS_NAME_INSERT_USER   = "INSERT_USER";
    
    public static final String AUDIT_STATUS_BEGIN       = "BEGIN";
    public static final String AUDIT_STATUS_END         = "END";
    public static final String AUDIT_STATUS_EXCEPTION   = "EXCEPTION";

    // DB 
    public static final String DB_NAME  = "UserSBChallenge";

    
    // API TOKEN
    public static final String API_TOKEN_FILE_PATH = "Config/apiToken.txt";


}
