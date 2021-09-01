package com.github.jorderator;


import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.Invite;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.CategorizableChannel;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.InviteCreateSpec;
import discord4j.rest.util.Color;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commands {

//    private static Pattern suggestPattern;
//    private static Pattern delsuggestPattern;
    private static Pattern testListPattern;

    private static Pattern addModPattern;
    private static Pattern removeModPattern;

    private static Pattern setPrefixPattern;
    static {
//        suggestPattern = Pattern.compile("^\\" + State.prefix + "suggest (.+)$");
//        delsuggestPattern = Pattern.compile("^\\" + State.prefix + "delsuggest ([0-9]+)$");
        testListPattern = Pattern.compile("^\\" + State.prefix + "add-test (.+)$");

        addModPattern = Pattern.compile("^\\" + State.prefix + "add-mod (.+)$");
        removeModPattern = Pattern.compile("^\\" + State.prefix + "remove-mod ([0-9]+)$");

        setPrefixPattern = Pattern.compile("^\\" + State.prefix + "set-prefix (.+)$");
    }

    public static Boolean processCommands(String content, MessageCreateEvent event) {
//        System.out.println("processing command: " + content);
        MessageChannel channel = event.getMessage().getChannel().block();
        String iconURL = event.getClient().getSelf().block().getAvatarUrl();

        // stupid shit to avoid null pointer on guild when message is in dms, while
        //  still having the colour accessible to lambdas
        Color tempEmbedColour;
        String tempName;
        try {
            tempEmbedColour = event.getGuild().block().getSelfMember().block().getColor().block();
            tempName = event.getGuild().block().getSelfMember().block().getDisplayName();
        }
        catch (NullPointerException e) {
            tempEmbedColour = Color.of(255, 255, 255);
            tempName = event.getClient().getSelf().block().getUsername();
        }
        final String name = tempName;
        final Color embedColour = tempEmbedColour;

        Matcher m;

        // help command
        if (content.equals(State.prefix + "help") || content.equals(Util.getMention(event.getClient().getSelfId()) + " help")) {
            channel.createEmbed(embedCreateSpec -> embedCreateSpec
                    .setTitle("comf-admin-bot help")
                    .setColor(embedColour)
                    .setDescription("Commands etc for the bot. Current prefix is: `" + State.prefix + "`.")
                    .addField("Commands:", "-------------------------", false)
                    .addField("help", "displays this, dumbass", true)
                    .addField("mod-info", "displays info and commands for minecraft mod list effort", true)
                    .addField("list-test", "prints contents of test list thing", true)
                    .addField("add-test {stuff}", "adds {stuff} to test list", true)
                    .addField("clear-test", "clears contents of test list", true)
                    .addField("Owner stuff:", "-------------------------", false)
                    .addField("set-role-reaction", "applies role reaction to specified message", true)
                    .addField("get-invite {reason}", "generates one use invite with {reason}", true)
                    .addField("reload-state", "reload bot state from file", true)
                    .setFooter(name, iconURL)
            ).block();
        }

        // set prefix command
        // TODO: continue this, fix the problem with the regex patterns not updating
//        m = setPrefixPattern.matcher(content);
//        if (m.find()) {
//            String value = m.group(1);
//            MessageChannel channel = event.getMessage().getChannel().block();
//
//            if (value.length() > 0) {
//                State.prefix = value;
//                channel.createMessage("Prefix updated to `" + value + "`").block();
//
//                State.saveState();
//            }
//            else {
//                channel.createMessage("Invalid prefix").block();
//            }
//
//            return true;
//        }

        // general test message command
        if (content.equals(State.prefix + "test")) {
            channel.createMessage("testing").block();
        }

        // test embed command
        if (content.equals(State.prefix + "test-embed")) {
            channel.createEmbed(embedCreateSpec -> embedCreateSpec
                    .setTitle("comf-admin-bot test")
                    .setColor(embedColour)
                    .setDescription("kinda shit lol")
                    .setThumbnail("https://cdn.discordapp.com/attachments/849262664457912323/881768693145681930/the-beast.jpg")
                    .addField("uhhhhhh", "some crap", false)
                    .addField("more crap", "yeh", true)
                    .addField("yet more", "idk", true)
                    .setFooter(name, iconURL)
            ).block();
        }

        // owner only commands
        if (event.getMessage().getAuthor().get().getId().equals(Snowflake.of(State.getID("ownerID")))) {
            // initialise rules message reaction
            if (content.startsWith(State.prefix + "set-role-reaction")) {
                Guild guild = event.getClient().getGuildById(Snowflake.of(State.getID("serverID"))).block();
                TextChannel rulesChannel = (TextChannel) guild.getChannelById(Snowflake.of(State.getID("rulesChannelID"))).block();
                Message message = rulesChannel.getLastMessage().block();
                message.addReaction(ReactionEmoji.codepoints("U+1F44D")).block();

                return true;
            }

            // generate single use invite to server
            // TODO: make this actually unique, like python (see test server audit log)
            if (content.startsWith(State.prefix + "get-invite")) {
                String tempReasonText = "";
                int commandLength = (State.prefix + "get-invite ").length();
                if (content.length() > commandLength) tempReasonText = content.substring(commandLength);
                final String reasonText = tempReasonText;


                Guild guild =  event.getClient().getGuildById(Snowflake.of(State.getID("serverID"))).block();
                CategorizableChannel infoChannel = (CategorizableChannel) guild.getChannelById(Snowflake.of(State.getID("infoChannelID"))).block();
                Invite invite = infoChannel.createInvite(inviteCreateSpec -> inviteCreateSpec
                        .setReason(reasonText)
                        .setMaxUses(1)
                        .setMaxAge(0)
                        .setUnique(true)
                ).block();

                String output = String.format("Invite generated with reason \"%1$s\":\n`%2$s`", reasonText, invite.getCode());
                System.out.println(output);
                event.getMessage().getChannel().block().createMessage(output).block();

                return true;
            }

            // reload bot state from .json file
            if (content.equals(State.prefix + "reload-state")) {
                try {
                    State.loadState();
                }
                catch (IOException e) {
                    channel.createMessage("An error occured while reloading, exiting...").block();
                    System.out.println("An error occurred while loading json file.");
                    e.printStackTrace();
                    System.out.println("Exiting...");
                    System.exit(1);
                }

                channel.createMessage("Bot state reloaded.").block();
                System.out.println("state reloaded");
            }
        }


        // print info about minecraft mod thing
        if (content.equals(State.prefix + "mod-info")) {
            channel.createEmbed(embedCreateSpec -> embedCreateSpec
                    .setTitle("Mod List Info")
                    .setColor(embedColour)
                    .setDescription("This is the list of mods to be included in a modded minecraft server we'll run. " +
                            "Use the commands below to interact with the list, and at the end we'll set up a server " +
                            "as much as we can get working.")
                    .addField("Commands:", "-------------------------", false)
                    .addField(State.prefix + "mod-info", "This thing.", true)
                    .addField(State.prefix + "mod-list", "List the current mod list.", true)
                    .addField(State.prefix + "add-mod {mod}", "Add a {mod} to the list.", true)
                    .addField(State.prefix + "remove-mod {id}", "Remove the mod with {id} in list view.", true)
                    .setFooter(name, iconURL)
            ).block();
             return true;
        }

        // print contents of minecraft mod list
        if (content.equals(State.prefix + "mod-list")) {
            String tempListOutput = (State.modSuggestions.size() < 1? "Nothing yet": "");
            for (int i = 0; i < State.modSuggestions.size(); i++) {
                tempListOutput += "**" + (i+1) + "** - " + State.modSuggestions.get(i) + "\n";
            }
            final String listOutput = tempListOutput;

            channel.createEmbed(embedCreateSpec -> embedCreateSpec
                    .setTitle("Current Mod List:")
                    .setColor(embedColour)
                    .setDescription("The current server mod list includes:\n\n" + listOutput)
                    .setFooter(name, iconURL)
            ).block();

            return true;
        }

        // add mod to mod list
        m = addModPattern.matcher(content);
        if (m.find()) {
            String value = m.group(1);
            State.modSuggestions.add(value);
            channel.createMessage("**" + value + "** added to mod list.").block();

            State.saveState();
            return true;
        }

        // remove mod by id from list
        m = removeModPattern.matcher(content);
        if (m.find()) {
            int id = Integer.parseInt(m.group(1)) - 1;

            try {
                String removedMod = State.modSuggestions.remove(id);
                channel.createMessage("**" + removedMod + "** deleted from mod list.").block();

                State.saveState();
            }
            catch (IndexOutOfBoundsException e) {
                channel.createMessage("There is no mod with that ID.").block();
            }

            return true;
        }


        // print contents of test list
        if (content.equals(State.prefix + "list-test")) {
            String output = "**Test list content:**\n";

            for (String entry : State.test) {
                output += " - " + entry + "\n";
            }

            if (output.length() > 2000) channel.createMessage("ah shit, the list is longer than discord can handle");
            else channel.createMessage(output).block();

            return true;
        }

        // add entry to test list
        m = testListPattern.matcher(content);
        if (m.find()) {
            String value = m.group(1);
            State.test.add(value);
            channel.createMessage(value + " added to test list").block();

            State.saveState();
            return true;
        }

        // clear contents of test list
        if (content.equals(State.prefix + "clear-test")) {
            State.test.clear();

            channel.createMessage("test list cleared").block();

            State.saveState();
            return true;
        }

//        if (content.equals(BotSettings.prefix + "help")) {
//            EmbedBuilder helpEmbed = new EmbedBuilder()
//                    .setTitle("Stinky-bot help:")
//                    .setColor(BotSettings.embedColor)
//                    .setDescription("Command list for stinky-bot.")
//                    .addField("Commands:", "---------------")
//                    .addInlineField(".help", "displays this page")
//                    .addInlineField(".toggle stinky", "toggle whether to respond to certain user messages")
//                    .addInlineField(".toggle message", "toggle whether to respond to message contents")
//                    .addInlineField(".invite", "get the bot invite link, to invite it to a server")
//                    .addInlineField(".suggestions", "list bot feature suggestions")
//                    .addInlineField(".suggest [text]", "submit a feature suggestion for the bot")
//                    .addInlineField(".delsuggest [id]", "delete one of the bot suggestions")
//                    .setFooter("stinky-bot", event.getApi().getYourself().getAvatar());
//
//            event.getChannel().sendMessage(helpEmbed);
//            return true;
//        }
//
//        if (content.equals(BotSettings.prefix + "toggle stinky")) {
//            BotSettings.stinkyToggle = !BotSettings.stinkyToggle;
//            event.getChannel().sendMessage("stinky-detecting toggled to " + (BotSettings.stinkyToggle? "on": "off"));
//
//            Main.saveState();
//            BotSettings.updateStatus();
//
//            return true;
//        }
//
//        if (content.equals(BotSettings.prefix + "toggle message")) {
//            BotSettings.messageToggle = !BotSettings.messageToggle;
//            event.getChannel().sendMessage("message-detecting toggled to " + (BotSettings.messageToggle? "on": "off"));
//
//            Main.saveState();
//            return true;
//        }
//
//        if (content.equals(BotSettings.prefix + "invite")) {
//            event.getChannel().sendMessage("Invite me to a server with: " + Main.api.createBotInvite());
//
//            return true;
//        }
//
//        if (content.equals(BotSettings.prefix + "suggestions")) {
//            String suggestionsString = (BotSettings.suggestions.size() < 1? "Nothing here currently": "");
//            for (int i = 0; i < BotSettings.suggestions.size(); i++) {
//                suggestionsString = suggestionsString + "\n " + (i+1) + " - " + BotSettings.suggestions.get(i);
//            }
//
//            EmbedBuilder suggestionsEmbed = new EmbedBuilder()
//                    .setTitle("Suggestions list:")
//                    .setColor(BotSettings.embedColor)
//                    .setFooter("stinky-bot", event.getApi().getYourself().getAvatar())
//                    .setDescription("List of suggested bot features/ideas.")
//                    .addField("Suggestions:", suggestionsString);
//
//            event.getChannel().sendMessage(suggestionsEmbed);
//
//            return true;
//        }
//
//        // .suggest
//        m = suggestPattern.matcher(content);
//        if (m.find()) {
//            String value = m.group(1);
//            BotSettings.suggestions.add(value);
//            event.getChannel().sendMessage('"' + value + "\" added to suggestions.");
//
//            Main.saveState();
//            return true;
//        }
//
//        // .del[ete]suggest
//        m = delsuggestPattern.matcher(content);
//        if (m.find()) {
//            int id = Integer.parseInt(m.group(1)) - 1;
//
//            try {
//                String suggestionText = BotSettings.suggestions.remove(id);
//                event.getChannel().sendMessage('"' + suggestionText + "\" deleted from suggestions.");
//
//                Main.saveState();
//            }
//            catch (IndexOutOfBoundsException e) {
//                event.getChannel().sendMessage("There is no suggestion with that id.");
//            }
//
//            return true;
//        }

        return false;
    }

}
