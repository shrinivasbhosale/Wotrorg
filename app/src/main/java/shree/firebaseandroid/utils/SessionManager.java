package shree.firebaseandroid.utils;
import android.content.Context;
import android.content.SharedPreferences;

import shree.firebaseandroid.Login;


/**
 * Created by paym on 2/3/17.
 */

public class SessionManager {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    private static final String USER_ID = "user_id";
    private static final String MOBILE_NO = "mobile_no";
    private static final String EMAIL_ID = "emailAddress";
    private static final String NAME = "name";
    private static final String SUR_NAME = "surName";
    private static final String TENANT_ID = "tenantId";
    private static final String ADDRESS = "address";
    private static final String USER_ROLE = "userRole";
    private static final String LOCATIONS = "locationName";
    private static final String CREATION_TIME = "creationTime";
    private static final String CREATOR_ID = "creatorUserId";
    private static final String PROJECT_NAME = "project_name";

    public SessionManager(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("myapp", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedin(boolean logggedin){
        editor.putBoolean("loggedInmode",logggedin);
        editor.commit();
    }

    public boolean loggedin(){
        return prefs.getBoolean("loggedInmode", false);
    }

    public void setMobile(String value) {
        prefs.edit().putString(MOBILE_NO, value).apply();
    }

    public String getMobile() {
        return prefs.getString(MOBILE_NO, null);
    }

    public void setUserId(String value) {
        prefs.edit().putString(USER_ID, value).apply();
    }

    public String getUserId() {
        return prefs.getString(USER_ID, null);
    }

    public void setEmailId(String value) {
        prefs.edit().putString(EMAIL_ID, value).apply();
    }

    public String getEmailId() {
        return prefs.getString(EMAIL_ID, null);
    }

    public void setName(String value) {
        prefs.edit().putString(NAME, value).apply();
    }

    public String getName() {
        return prefs.getString(NAME, null);
    }

    public void setTenantId(String value) {
        prefs.edit().putString(TENANT_ID, value).apply();
    }

    public String getTenantId() {
        return prefs.getString(TENANT_ID, null);
    }


    public void setAddress(String value) {
        prefs.edit().putString(ADDRESS, value).apply();
    }

    public String getAddress() {
        return prefs.getString(ADDRESS, null);
    }


    public void setUserRole(String value) {
        prefs.edit().putString(USER_ROLE, value).apply();
    }

    public String getUserRole() {
        return prefs.getString(USER_ROLE, null);
    }


    public void setLocations(String value) {
        prefs.edit().putString(LOCATIONS, value).apply();
    }

    public String getLocations() {
        return prefs.getString(LOCATIONS, null);
    }


    public void setCreationTime(String value) {
        prefs.edit().putString(CREATION_TIME, value).apply();
    }

    public String getCreationTime() {
        return prefs.getString(CREATION_TIME, null);
    }

    public void setCreatorId(String value) {
        prefs.edit().putString(CREATOR_ID, value).apply();
    }

    public String getCreatorId() {
        return prefs.getString(CREATOR_ID, null);
    }

    public static String getProjectName() {
        return PROJECT_NAME;
    }

    public void setProjectName(String value) {
        prefs.edit().putString(PROJECT_NAME, value).apply();
    }
}
