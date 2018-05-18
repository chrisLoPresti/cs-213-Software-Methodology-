package com.rutcs.chrislopresti.photos27;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TagsManager {
    //private static HashMap<String,HashSet<String>> tagToImages = new HashMap<>();
    private static HashMap<String,String> imageToTags = new HashMap<>();

    private TagsManager(){}

    public static void updateTag(File img, String tagsString) {
        updateTag(img.getAbsolutePath(),tagsString);
    }

    public static void updateTag(String imgpath, String tagsString) {
        if(tagsString.isEmpty()){
            imageToTags.remove(imgpath);
        } else {
            imageToTags.put(imgpath, tagsString.toLowerCase());
        }
        /*String tags[] = tagsString.split(";");
        for (String tag: tags) {
            addWithCollosionManagement(tag,imgpath);
        }*/
    }

    public static String getTagString(Activity activity, String imgpath) {
        if(imageToTags.isEmpty()) {
            readTags(activity);
        }
        String toreturn = imageToTags.get(imgpath);

        if(toreturn == null){
            return " ";
        } else {
            return toreturn;
        }
    }

    public static ArrayList<HashMap<String,String>> searchTags (String tag) {
        ArrayList<HashMap<String,String>> paths = new ArrayList<>();
        for(Map.Entry<String,String> entry : imageToTags.entrySet()) {
            if(entry.getValue().contains(tag.toLowerCase())) {
                HashMap<String,String> toAdd = new HashMap<>();
                toAdd.put(MyFunc.KEY_PATH,entry.getKey());
                toAdd.put(MyFunc.KEY_ALBUM,entry.getKey().substring(0,entry.getKey().lastIndexOf("/")));
                paths.add(toAdd);
            }
        }
        return paths;
    }


    // TODO: Change this from storing in SharedPreferences to an SQLite Database.
    public static void writeTags(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.preference_file_tags), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String,String> element : imageToTags.entrySet()) {
            editor.putString(element.getKey(),element.getValue());
        }
        editor.apply();
    }

    public static void  writeTag(Activity activity, String path, String tags) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.preference_file_tags), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(tags.isEmpty()){
            editor.remove(path);
        } else {
            editor.putString(path, tags.toLowerCase());
        }
        editor.commit();

    }

    public static void readTags(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.preference_file_tags), Context.MODE_PRIVATE);
        imageToTags = (HashMap<String, String>) sharedPreferences.getAll();
    }
    /*private static void addWithCollosionManagement(String tag, String path) {
        if(tagToImages.get(tag)==null){
            tagToImages.put(tag,new HashSet<>());
        }
        tagToImages.get(tag).add(path);
    }*/

}
