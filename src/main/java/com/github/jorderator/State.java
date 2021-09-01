package com.github.jorderator;


import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class State {

    private static String jsonStateFilePath = "bot-state.json";

    public static Map<String, Object> ids;
    static {
        ids = new HashMap<>();
    }

    public static String prefix = ".";
    public static Color embedColor = new Color(15, 58, 23);

//    public static Boolean stinkyToggle;
//    public static Boolean messageToggle;

    public static ArrayList<String> modSuggestions;
    static {
        modSuggestions = new ArrayList<>();
    }

    public static ArrayList<String> test;
    static {
        test = new ArrayList<>();
    }




    public static long getID(String name) {
//        return Snowflake.of((long) ids.get(name));
        return (long) ids.get(name);
    }

    public static long[] getIDList(String name) {
//        return Snowflake.of((long) ids.get(name));
        return (long[]) ids.get(name);
    }



    public static void saveState() {
        JSONObject botState = new JSONObject();

        Map<String, Object> tempIds = new HashMap<>(ids);
        tempIds.remove("ownerID");
        botState.put("ids", tempIds);

        botState.put("prefix", prefix);

        botState.put("modSuggestions", modSuggestions);
        botState.put("testList", test);

        try {
            FileWriter jsonFileWriter = new FileWriter(jsonStateFilePath);
            jsonFileWriter.write(botState.toString());
            jsonFileWriter.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void loadState() throws IOException {
        File jsonFile = new File(jsonStateFilePath);

        if (jsonFile.exists()) {
            Scanner jsonFileReader = new Scanner(jsonFile);

            if (jsonFileReader.hasNext()) {
                JSONObject botState = new JSONObject(jsonFileReader.nextLine());

                if (botState.has("ids")) ids = botState.getJSONObject("ids").toMap();
                ids.put("ownerID", Main.client.getApplicationInfo().block().getOwnerId().asLong());

                if (botState.has("prefix")) prefix = botState.getString("prefix");

                if (botState.has("modSuggestions")) {
                    JSONArray modSuggestionsTempArray = botState.getJSONArray("modSuggestions");
                    for (int i = 0; i < modSuggestionsTempArray.length(); i++) {
                        modSuggestions.add(modSuggestionsTempArray.getString(i));
                    }
                }

                if (botState.has("testList")) {
                    JSONArray testTempArray = botState.getJSONArray("testList");
                    for (int i = 0; i < testTempArray.length(); i++) {
                        test.add(testTempArray.getString(i));
                    }
                }
            }
        }
    }
}
