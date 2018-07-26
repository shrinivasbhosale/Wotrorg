package shree.firebaseandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Shrinivas on 10-07-2018.
 */

public class ProjectManager {

    SharedPreferences projectprefs;
    SharedPreferences.Editor projecteditor;
    Context projectctx;
    private static final String PROJECTRESULT = "result";

    private static final String PROJECTACTIVITYRESULT = "projectactivityresult";
    private static final String SUBACTIVITYRESULT = "subactivityresult";
    private static final String COMPLETEDTASKRESULT = "completedtaskresult";
    private static final String DAILOGACTRESULT = "dailogactresult";


    public ProjectManager(Context ctx){
        this.projectctx = ctx;
        projectprefs = ctx.getSharedPreferences("myappprojects", Context.MODE_PRIVATE);
        projecteditor = projectprefs.edit();
    }

    public static String getRESULT() {
        return PROJECTRESULT;
    }

    public void setRESULT(String value) {
        projectprefs.edit().putString(PROJECTRESULT, value).apply();
    }

    public static String getPROJECTACTIVITYRESULT() {
        return PROJECTACTIVITYRESULT;
    }

    public void setPROJECTACTIVITYRESULT(String value) {
        projectprefs.edit().putString(PROJECTACTIVITYRESULT, value).apply();
    }


    public static String getSUBACTIVITYRESULT() {
        return PROJECTACTIVITYRESULT;
    }

    public void setSUBACTIVITYRESULT(String value) {
        projectprefs.edit().putString(SUBACTIVITYRESULT, value).apply();
    }

    public void setCOMPLETEDTASKRESULT(String value) {
        projectprefs.edit().putString(COMPLETEDTASKRESULT, value).apply();
    }

    public void setDAILOGACTRESULT(String value) {
        projectprefs.edit().putString(DAILOGACTRESULT, value).apply();
    }

}
