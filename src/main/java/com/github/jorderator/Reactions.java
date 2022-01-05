package com.github.jorderator;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.discordjson.json.EmojiData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Reactions {

    public static ArrayList<Map<String, Object>> reactions;
    static {
        reactions = new ArrayList<>();
//        Map<String, Object> entry = new HashMap<>();
//        entry.put("test", "deez nuts");
//        entry.put("more", new ArrayList<>(Arrays.asList("assorted", "entries")));
//        entry.put("num test", 1234684564L);
//        reactions.add(entry);
    }

//    public static String getReactionID(String name) {
//        return reactions.get(0).get("test").getClass().getTypeName();
//    }

    public static void processReaction(MessageEvent _event) {
        if (_event.getClass() == ReactionAddEvent.class) {
            ReactionAddEvent event = (ReactionAddEvent) _event;

            for (Map<String, Object> reaction : reactions) {
                if (checkReaction(event.getMessageId(), event.getChannelId(), event.getEmoji(), reaction)) {
                    System.out.println(event.getUser().block().getTag() + " pressed this reaction: " + reaction);

                    if (reaction.get("type").toString().equals("role"))
                        processAddRoleReaction(event, reaction);
                }
            }
//            event.getChannel().block().createMessage(toJSON().toString()).block();
//            event.getChannel().block().createMessage(reactions.get(0).get("num test").getClass().getTypeName()).block();
        }

        else if (_event.getClass() == ReactionRemoveEvent.class) {
            ReactionRemoveEvent event = (ReactionRemoveEvent) _event;

            for (Map<String, Object> reaction : reactions) {
                if (checkReaction(event.getMessageId(), event.getChannelId(), event.getEmoji(), reaction)) {
                    System.out.println(event.getUser().block().getTag() + " removed this reaction: " + reaction);

                    if (reaction.get("type").toString().equals("role") && reaction.get("removable").equals(true))
                        processRemoveRoleReaction(event, reaction);
                }

            }
        }

    }

    private static void processAddRoleReaction(ReactionAddEvent event, Map<String, Object> reaction) {
        Member member = event.getMember().get();
        String rolesGiven = "";

//        System.out.println(reaction.get("roleIDs").getClass().getTypeName());
        if (reaction.get("roleIDs").getClass() != ArrayList.class) {
            System.out.println("Role list is an invalid format");
            return;
        }

        for (Long roleID : (ArrayList<Long>) reaction.get("roleIDs")) {
            Role role;
            try {
                role = event.getGuild().block().getRoleById(Snowflake.of(roleID)).block();
            }
            catch (NullPointerException e) {
                System.out.print("uhh, shit, no role for " + roleID + " boss");
                continue;
            }

            if (member.getRoleIds().contains(Snowflake.of(roleID))) {
                System.out.println(member.getTag() + " already had the role \"" + role.getName() + "\"");
                continue;
            }

            member.addRole(Snowflake.of(roleID)).block();

            rolesGiven += " \"" + role.getName() + "\"";
        }

        System.out.println(member.getTag() + " given roles:" + rolesGiven);
    }

    private static void processRemoveRoleReaction(ReactionRemoveEvent event, Map<String, Object> reaction) {
        Member member = event.getUser().block().asMember(event.getGuildId().get()).block();
        String rolesRemoved = "";

//        System.out.println(reaction.get("roleIDs").getClass().getTypeName());
        if (reaction.get("roleIDs").getClass() != ArrayList.class) {
            System.out.println("Role list is an invalid format");
            return;
        }

        for (Long roleID : (ArrayList<Long>) reaction.get("roleIDs")) {
            Role role;
            try {
                role = event.getGuild().block().getRoleById(Snowflake.of(roleID)).block();
            }
            catch (NullPointerException e) {
                System.out.print("uhh, shit, no role for " + roleID + " boss");
                continue;
            }

            if (!member.getRoleIds().contains(Snowflake.of(roleID))) {
                System.out.println(member.getTag() + " doesn't have the role \"" + role.getName() + "\"");
                continue;
            }

            member.removeRole(Snowflake.of(roleID)).block();

            rolesRemoved += " \"" + role.getName() + "\"";
        }

        System.out.println(member.getTag() + " removed roles:" + rolesRemoved);
    }
//    if (State.secretSantaActive && State.secretSantaOptIn && event.getMessageId().asLong() == State.getID("secretSantaMessageID") && event.getEmoji().equals(ReactionEmoji.codepoints("U+1F385"))) {
//        Snowflake roleID = Snowflake.of(State.getID("secretSantaRoleID"));
//        Member member = event.getUser().block().asMember(event.getGuildId().get()).block();
//
//        System.out.printf("%s removed their reaction from the secret santa message.%n", member.getTag());
//
//        if (!member.getRoleIds().contains(roleID)) {
//            System.out.println(member.getDisplayName() + " already had the role removed.");
//            return;
//        }
//
//        Role role = event.getGuild().block().getRoleById(roleID).block();
//        if (role.equals(null)) {
//            System.out.printf("uhh, shit, no role for %s boss%n", role);
//            return;
//        }
//
//        member.removeRole(roleID).block();
//    }


    private static Boolean checkReaction(Snowflake messageID, Snowflake channelID, ReactionEmoji emoji, Map<String, Object> reaction) {
        if (messageID.asLong() != ((Long) reaction.get("messageID")) &&
                channelID.asLong() != ((Long) reaction.get("channelID")))
            return false;

        if (emoji.asCustomEmoji().isPresent() && !emoji.asCustomEmoji().get().asFormat().equals(reaction.get("emoji").toString()))
            return false;

        if (emoji.asUnicodeEmoji().isPresent() && !emoji.asUnicodeEmoji().get().getRaw().equals(reaction.get("emoji").toString()))
            return false;

        return true;
    }


    public static void addRoleReaction(Message message, Set<Snowflake> roles, ReactionEmoji emoji, Boolean removable) {
        message.addReaction(emoji).block();

        Map<String, Object> entry = new HashMap<>();
        ArrayList<Long> roleIDs = new ArrayList<>();

        for (Snowflake role : roles) {
            roleIDs.add(role.asLong());
        }

        entry.put("type", "role");
        entry.put("serverID", message.getGuild().block().getId().asLong());
        entry.put("channelID", message.getChannelId().asLong());
        entry.put("messageID", message.getId().asLong());
        if (emoji.asCustomEmoji().isPresent())
            entry.put("emoji", emoji.asCustomEmoji().get().asFormat());
        else
            entry.put("emoji", emoji.asUnicodeEmoji().get().getRaw());
        entry.put("roleIDs", roleIDs);
        entry.put("removable", removable);

        reactions.add(entry);
    }

    public static JSONArray toJSON() {
        JSONArray reactionsJSON = new JSONArray();

        for (Map<String, Object> reaction : reactions) {
            reactionsJSON.put(reaction);
        }

        return reactionsJSON;
    }

    public static void fromJSON(JSONArray JSONreactions) {
        for (Object JSONreaction : JSONreactions) {
            reactions.add(((JSONObject) JSONreaction).toMap());
        }
    }

}
