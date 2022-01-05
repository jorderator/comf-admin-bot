package com.github.jorderator;


import discord4j.rest.util.Color;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class State {

    private static String jsonStateFilePath = "bot-state.json";

    public static Map<String, Object> ids;
    static {
        ids = new HashMap<>();
    }

    public static String prefix = ".";
    public static Color defaultEmbedColour = Color.of(255, 172, 9);
    public static Boolean useDefaultColour = false;

    public static ArrayList<String> test;
    static {
        test = new ArrayList<>();
    }


    public static Boolean modListActive = false;

    public static ArrayList<String> modSuggestions;
    static {
        modSuggestions = new ArrayList<>();
    }


    public static Boolean secretSantaActive = false;
    public static Boolean secretSantaOptIn = false;




    public static Long getID(String name) {
//        return Snowflake.of((long) ids.get(name));
        return (Long) ids.get(name);
    }

    public static ArrayList<Long> getIDList(String name) {
//        return Snowflake.of((long) ids.get(name));
        return (ArrayList<Long>) ids.get(name);
    }




    public static void saveState() {
        JSONObject botState = new JSONObject();

        botState.put("reactions", Reactions.toJSON());

        Map<String, Object> tempIds = new HashMap<>(ids);
        tempIds.remove("ownerID");
        botState.put("ids", tempIds);

        botState.put("prefix", prefix);

        botState.put("defaultEmbedColour", defaultEmbedColour.getRGB());
        botState.put("useDefaultColour", useDefaultColour);

        botState.put("modListActive", modListActive);
        botState.put("modSuggestions", modSuggestions);

        botState.put("secretSantaActive", secretSantaActive);
        botState.put("secretSantaOptIn", secretSantaOptIn);
        botState.put("secretSantas", SecretSanta.secretSantas);

        botState.put("testList", test);

        try {
            FileWriter jsonFileWriter = new FileWriter(jsonStateFilePath);
            jsonFileWriter.write(botState.toString());
            jsonFileWriter.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred while writing the state file.");
            e.printStackTrace();
        }
    }

    public static void loadState() throws IOException {
        File jsonFile = new File(jsonStateFilePath);

        if (jsonFile.exists()) {
            Scanner jsonFileReader = new Scanner(jsonFile);

            if (jsonFileReader.hasNext()) {
                JSONObject botState = new JSONObject(jsonFileReader.nextLine());

                if (botState.has("reactions")) Reactions.fromJSON(botState.getJSONArray("reactions"));

                if (botState.has("ids")) ids = botState.getJSONObject("ids").toMap();
                ids.put("ownerID", Main.client.getApplicationInfo().block().getOwnerId().asLong());

                if (botState.has("prefix")) prefix = botState.getString("prefix");

                if (botState.has("defaultEmbedColour")) defaultEmbedColour = Color.of(botState.getInt("defaultEmbedColour"));
                if (botState.has("useDefaultColour")) useDefaultColour = botState.getBoolean("useDefaultColour");

                if (botState.has("modListActive")) modListActive = botState.getBoolean("modListActive");

                if (botState.has("modSuggestions")) {
                    modSuggestions.clear();
                    JSONArray modSuggestionsTempArray = botState.getJSONArray("modSuggestions");
                    for (int i = 0; i < modSuggestionsTempArray.length(); i++) {
                        modSuggestions.add(modSuggestionsTempArray.getString(i));
                    }
                }


                if (botState.has("secretSantaActive")) secretSantaActive = botState.getBoolean("secretSantaActive");
                if (botState.has("secretSantaOptIn")) secretSantaOptIn = botState.getBoolean("secretSantaOptIn");

                if (botState.has("secretSantas")) {
                    SecretSanta.secretSantas.clear();
                    for (Map.Entry<String, Object> person : botState.getJSONObject("secretSantas").toMap().entrySet()) {
                        try {
                            SecretSanta.secretSantas.put(Long.parseLong(person.getKey()), Long.parseLong(person.getValue().toString()));
                        }
                        catch (NumberFormatException e) {
                            System.out.println("While reading bot_state.json, a secret santa entry was not a long.");
                            e.printStackTrace();
                        }
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
