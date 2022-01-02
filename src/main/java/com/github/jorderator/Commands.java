package com.github.jorderator;


import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.Invite;
import discord4j.core.object.entity.*;
import discord4j.core.object.entity.channel.*;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commands {

//    private static Pattern suggestPattern;
//    private static Pattern delsuggestPattern;
    private static Pattern testListPattern;

    private static Pattern addModPattern;
    private static Pattern removeModPattern;

    private static Pattern setPrefixPattern;

    private static Pattern setColourPattern;

    private static Pattern messageAllSantasPattern;
    private static Pattern messageSantaPattern;
    private static Pattern messagePartnerPattern;
    static {
//        suggestPattern = Pattern.compile("^\\" + State.prefix + "suggest (.+)$");
//        delsuggestPattern = Pattern.compile("^\\" + State.prefix + "delsuggest ([0-9]+)$");
        testListPattern = Pattern.compile("^\\" + State.prefix + "add-test (.+)$");

        addModPattern = Pattern.compile("^\\" + State.prefix + "add-mod (.+)$");
        removeModPattern = Pattern.compile("^\\" + State.prefix + "remove-mod ([0-9]+)$");

        setPrefixPattern = Pattern.compile("^\\" + State.prefix + "set-prefix (.+)$");

        setColourPattern = Pattern.compile("^\\" + State.prefix + "set-colour ([0-9]+) ([0-9]+) ([0-9]+)$");

        messageAllSantasPattern = Pattern.compile("^\\" + State.prefix + "announce-santas (.+)$");
        messageSantaPattern = Pattern.compile("^\\" + State.prefix + "message-santa (.+)$");
        messagePartnerPattern = Pattern.compile("^\\" + State.prefix + "message-partner (.+)$");
    }

    public static Boolean processCommands(String content, MessageCreateEvent event) {
//        System.out.println("processing command: " + content);
        MessageChannel channel = event.getMessage().getChannel().block();

        Matcher m;

        // help command
        if (content.equals(State.prefix + "help") || content.equals(Util.getMention(event.getClient().getSelfId()) + " help")) {
            channel.createEmbed(embedCreateSpec -> {
                        Util.formatEmbed(channel, embedCreateSpec);
                        embedCreateSpec.setTitle("comf-admin-bot help");
                        embedCreateSpec.setDescription("Commands etc for the bot. Current prefix is: `" + State.prefix + "`.");

                        embedCreateSpec.addField("Commands:", "-------------------------", false);

                        embedCreateSpec.addField(State.prefix + "help", "displays this, dumbass (can also be accessed with \"{@bot} help\")", true);
                        if (State.modListActive) embedCreateSpec.addField(State.prefix + "mod-info", "displays info and commands for minecraft mod list effort", true);
                        if (State.secretSantaActive) embedCreateSpec.addField(State.prefix + "secret-santa-info", "displays info and commands for the secret santa event", true);
                        embedCreateSpec.addField(State.prefix + "list-test", "prints contents of test list thing", true);
                        embedCreateSpec.addField(State.prefix + "add-test {stuff}", "adds {stuff} to test list", true);
                        embedCreateSpec.addField(State.prefix + "clear-test", "clears contents of test list", true);

                        if (event.getMessage().getAuthor().get().getId().equals(Snowflake.of(State.getID("ownerID")))) {
                            embedCreateSpec
                                    .addField("Owner stuff:", "-------------------------", false)
                                    .addField(State.prefix + "set-role-reactions", "applies role reaction to specified message", true)
                                    .addField(State.prefix + "get-invite {reason}", "generates one use invite with {reason}", true)
                                    .addField(State.prefix + "reload-state", "reload bot state from file", true)
                                    .addField(State.prefix + "set-colour {r} {g} {b}", "changes the default bot colour", true)
                                    .addField(State.prefix + "toggle-colour", "toggles whether the bot uses role colour or default colour in servers", true);
                        }
                    }
            ).block();

            return true;
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

            TextChannel testChannel = (TextChannel) channel;
            System.out.println("channel guild id: " + testChannel.getGuildId());

            //channel.createMessage("testing").block();
        }

        // test embed command
        if (content.equals(State.prefix + "test-embed")) {
            channel.createEmbed(embedCreateSpec -> {
                        Util.formatEmbed(channel, embedCreateSpec);
                        embedCreateSpec
                                .setTitle("comf-admin-bot test")
//                                .setColor(embedColour)
                                .setDescription("kinda shit lol")
                                .setThumbnail("https://cdn.discordapp.com/attachments/849262664457912323/881768693145681930/the-beast.jpg")
                                .addField("uhhhhhh", "some crap", false)
                                .addField("more crap", "yeh", true)
                                .addField("yet more", "idk", true);
//                                .setFooter(name, iconURL)
                    }
            ).block();
        }


        // ===== Owner only commands =====

        if (event.getMessage().getAuthor().get().getId().equals(Snowflake.of(State.getID("ownerID")))) {

            // initialise rules message reaction
            if (content.startsWith(State.prefix + "set-role-reactions")) {
                // user role
                Guild guild = event.getClient().getGuildById(Snowflake.of(State.getID("serverID"))).block();
                TextChannel textChannel = (TextChannel) guild.getChannelById(Snowflake.of(State.getID("rulesChannelID"))).block();
                Message message = textChannel.getMessageById(Snowflake.of(State.getID("rulesMessageID"))).block();
                message.addReaction(ReactionEmoji.codepoints("U+1F44D")).block();

                // secret santa role
                if (State.secretSantaActive) {
                    guild = event.getClient().getGuildById(Snowflake.of(State.getID("serverID"))).block();
                    textChannel = (TextChannel) guild.getChannelById(Snowflake.of(State.getID("secretSantaChannelID"))).block();
                    message = textChannel.getMessageById(Snowflake.of(State.getID("secretSantaMessageID"))).block();
                    message.addReaction(ReactionEmoji.codepoints("U+1F385")).block();
                }

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
                        .setUnique(true)
                        .setMaxUses(1)
                        .setMaxAge(0)
                ).block();

                String output = String.format("Invite generated with reason \"%1$s\":\n`%2$s`", reasonText, Util.getInviteUrl(invite.getCode()));
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

                return true;
            }


            if (State.secretSantaActive && content.equals(State.prefix + "begin-secret-santa")) {
                SecretSanta.beginSecretSanta();
                return true;
            }

            if (State.secretSantaActive && content.equals(State.prefix + "end-secret-santa")) {
                SecretSanta.endSecretSanta();
                return true;
            }

            m = messageAllSantasPattern.matcher(content);
            if (State.secretSantaActive && !State.secretSantaOptIn && m.find()) {
                String message = m.group(1);
                SecretSanta.messageAllSantas(message);

                channel.createEmbed(embedCreateSpec -> {
                    Util.formatEmbed(channel, embedCreateSpec);
                    embedCreateSpec.setTitle("Announcement sent successfully.");
                    embedCreateSpec.setDescription("Content:\n" + message);
                }).block();
            }


            m = setColourPattern.matcher(content);
            if (m.find()) {
                Integer red = Integer.parseInt(m.group(1));
                Integer green = Integer.parseInt(m.group(2));
                Integer blue = Integer.parseInt(m.group(3));

                red = (red > 255) ? 255 : red;
                green = (green > 255) ? 255 : green;
                blue = (blue > 255) ? 255 : blue;

                State.defaultEmbedColour = Color.of(red, green, blue);

                channel.createEmbed(embedCreateSpec -> {
                            Util.formatEmbed(channel, embedCreateSpec);
                            embedCreateSpec
                                    .setTitle("New colour!")
                                    .setDescription("<- Here's how the new colour looks.")
                                    .setColor(State.defaultEmbedColour);
                        }
                ).block();

                State.saveState();
            }
            else if (content.startsWith(State.prefix + "set-colour")) {
                channel.createEmbed(embedCreateSpec -> {
                            Util.formatEmbed(channel, embedCreateSpec);
                            embedCreateSpec
                                    .setTitle("Invalid colour syntax")
                                    .setDescription("Correct command looks like:\r\n`" +
                                            State.prefix + "set-colour 255 255 255`");
                        }
                ).block();
            }

            if (content.equals(State.prefix + "toggle-colour")) {
                State.useDefaultColour = ! State.useDefaultColour;

                if (event.getGuildId().isPresent()) {
                    Color colour = (State.useDefaultColour) ? State.defaultEmbedColour : event.getGuild().block().getSelfMember().block().getColor().block();

                    channel.createEmbed(embedCreateSpec -> {
                                Util.formatEmbed(channel, embedCreateSpec);
                                embedCreateSpec
                                        .setTitle("Embeds now using " + ((State.useDefaultColour) ? "default colour." : "role colour"))
                                        .setDescription("<- Embed colour will now look like this.")
                                        .setColor(colour);
                            }
                    ).block();
                }
                else {
                    channel.createEmbed(embedCreateSpec -> {
                                Util.formatEmbed(channel, embedCreateSpec);
                                embedCreateSpec
                                        .setTitle("Embeds now using " + ((State.useDefaultColour) ? "default colour." : "role colour"))
                                        .setDescription("Use `.help` in a server to see the change.");
                            }
                    ).block();
                }
            }
        }

        // ===== End owner only commands =====



        // ===== DM only commands =====

        if (channel.getType().equals(Channel.Type.DM)) {
            // Send message to secret santa
            m = messageSantaPattern.matcher(content);
            if (State.secretSantaActive && !State.secretSantaOptIn && m.find()) {
                User partner = event.getMessage().getAuthor().get();
                String message = m.group(1);
                SecretSanta.messageSecretSanta(partner.getId(), message);

                channel.createEmbed(embedCreateSpec -> {
                    Util.formatEmbed(channel, embedCreateSpec);
                    embedCreateSpec.setTitle("Message sent successfully.");
                    embedCreateSpec.setDescription("Content:\n" + message);
                }).block();
            }

            // Send message to secret santa partner
            m = messagePartnerPattern.matcher(content);
            if (State.secretSantaActive && !State.secretSantaOptIn && m.find()) {
                User santa = event.getMessage().getAuthor().get();
                String message = m.group(1);
                SecretSanta.messagePartner(santa.getId(), message);

                channel.createEmbed(embedCreateSpec -> {
                    Util.formatEmbed(channel, embedCreateSpec);
                    embedCreateSpec.setTitle("Message sent successfully.");
                    embedCreateSpec.setDescription("Content:\n" + message);
                }).block();
            }
        }

        // ===== End DM only commands =====



        // print info about secret santa event
        if (State.secretSantaActive && !State.secretSantaOptIn && content.equals(State.prefix + "secret-santa-info")) {
            channel.createEmbed(embedCreateSpec -> {
                Util.formatEmbed(channel, embedCreateSpec);
                embedCreateSpec
                        .setTitle("Secret Santa Info:")
                        .setDescription("See the other messages in announcements for info and rules surrounding the " +
                                "secret santa event. Here, help for the associated commands is stored. Almost all of " +
                                "these commands will only work in DMs.")
                        .addField("Commands:", "-------------------------", false)
                        .addField(State.prefix + "secret-santa-info", "This thing.", true)
                        .addField(State.prefix + "message-santa {text}", "Send a message to your secret santa.", true)
                        .addField(State.prefix + "message-partner {text}", "Send an anonymous message to your partner.", true);

                if (event.getMessage().getAuthor().get().getId().equals(Snowflake.of(State.getID("ownerID")))) {
                   embedCreateSpec
                           .addField("Owner only commands:", "-------------------------", false)
                           .addField(State.prefix + "begin-secret-santa", "Closes opting in/out and assigns everyone their secret santas.", true);
                }
            }).block();
        }

        // print info about minecraft mod thing
        if (State.modListActive && content.equals(State.prefix + "mod-info")) {
            channel.createEmbed(embedCreateSpec -> {
                        Util.formatEmbed(channel, embedCreateSpec);
                        embedCreateSpec
                                .setTitle("Mod List Info")
                                .setDescription("This is now the list of mods to be added to the server. Every now and then we'll " +
                                        "go through and add all these to the server. (Try not to explore too far because of this, " +
                                        "we want to leave stuff unloaded, so when we add mods we can still get their resources " +
                                        "without having to delete chunks from the world.)")
                                .addField("Commands:", "-------------------------", false)
                                .addField(State.prefix + "mod-info", "This thing.", true)
                                .addField(State.prefix + "mod-list", "List the current mod list.", true)
                                .addField(State.prefix + "add-mod {mod}", "Add a {mod} to the list.", true)
                                .addField(State.prefix + "remove-mod {id}", "Remove the mod with {id} in list view.", true);
                    }
            ).block();
             return true;
        }

        // print contents of minecraft mod list
        if (State.modListActive && content.equals(State.prefix + "mod-list")) {
            String tempListOutput = (State.modSuggestions.size() < 1? "Nothing yet": "");
            for (int i = 0; i < State.modSuggestions.size(); i++) {
                tempListOutput += "**" + (i+1) + "** - " + State.modSuggestions.get(i) + "\n";
            }
            final String listOutput = tempListOutput;

            channel.createEmbed(embedCreateSpec -> {
                        Util.formatEmbed(channel, embedCreateSpec);
                        embedCreateSpec
                                .setTitle("Current Mod List:")
                                .setDescription("The current server mod list includes:\n\n" + listOutput);
                    }
            ).block();

            return true;
        }

        // add mod to mod list
        m = addModPattern.matcher(content);
        if (State.modListActive && m.find()) {
            String value = m.group(1);
            State.modSuggestions.add(value);
            channel.createMessage("**" + value + "** added to mod list.").block();

            State.saveState();
            return true;
        }

        // remove mod by id from list
        m = removeModPattern.matcher(content);
        if (State.modListActive && m.find()) {
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

        return false;
    }

}
