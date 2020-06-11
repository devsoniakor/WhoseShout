package com.mad.whosetreat;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.libraries.places.api.Places;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mad.whosetreat.model.Group;
import com.mad.whosetreat.model.Groups;
import com.mad.whosetreat.model.Person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by noche on 2017-06-06.
 */

public class WhoseTreatApplication extends Application {

    public static final String GROUPS_KEY = "groups_shared_pref_key";
    public static final String NAMES_KEY = "All people";
    public static final String FLAG_KEY = "Flag";
    private static final String ALL_NAMES = "All names set";
    private static final String ALL_GROUP_NAMES = "All group names set";
    private SharedPreferences sharedPreferences;
    public static String API_KEY;


    @Override
    public void onCreate() {
        super.onCreate();
        API_KEY = getResources().getString(R.string.google_api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY, Locale.US);
        }
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }
        if (sharedPreferences.getString(GROUPS_KEY, "").equals("")) {
            List<Group> groupList = new ArrayList<>();
            List<Person> personList = new ArrayList<>();
            HashSet<String> names = new HashSet<>();
            Groups groups = new Groups();
            Group group = new Group();
            groups.setGroups(groupList);
            group.setPerson(personList);
            saveAllNames(group);
            saveAllGroups(groups);
            saveAllSet(names);
        }
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public Groups getGroupsFromSharedPreferences() {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(sharedPreferences.getString(GROUPS_KEY, ""), Groups.class);
    }

    public Group getGroupFromSharedPreferences() {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(sharedPreferences.getString(NAMES_KEY, ""), Group.class);
    }

    public HashSet<String> getNamesSet() {
        return (HashSet<String>) sharedPreferences.getStringSet(ALL_NAMES, new HashSet<String>());
    }

    public HashSet<String> getGroupNameSet() {
        return (HashSet<String>) sharedPreferences.getStringSet(ALL_GROUP_NAMES, new HashSet<String>());
    }

    public boolean getTreat() {
        return sharedPreferences.getBoolean(FLAG_KEY, false);
    }

    public void setTreat(Boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FLAG_KEY, flag);
        editor.apply();
    }

    public void saveAllGroups(Groups groups) {
        Gson gson = new GsonBuilder().create();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(GROUPS_KEY, gson.toJson(groups));
        editor.apply();
    }

    public void saveAllNames(Group people) {
        Gson gson = new GsonBuilder().create();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAMES_KEY, gson.toJson(people));
        editor.apply();
    }

    public void saveAllGroupNamesSet(HashSet<String> set) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(ALL_GROUP_NAMES, set);
        editor.apply();
    }

    public void saveAllSet(HashSet<String> set) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(ALL_NAMES, set);
        editor.apply();
    }
}
