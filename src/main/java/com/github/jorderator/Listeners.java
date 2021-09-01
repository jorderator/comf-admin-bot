package com.github.jorderator;


import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.*;
import discord4j.core.object.entity.channel.MessageChannel;

import java.math.BigInteger;


// Class for handling responses to misc events
public class Listeners {

    // Event code for server member joining
    public static void memberJoined(MemberJoinEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild().block();

        System.out.printf("Member joined: '%s'%n", member.getTag());
        guild.getSystemChannel().block().createMessage(String.format("welcome to whatever this is, %s\n(sorry I type so much holy shit)", member.getMention())).block();
    }

    // Event code for member leaving server (kick or not)
    public static void memberLeft(MemberLeaveEvent event) {
        User user = event.getUser();
        Guild guild = event.getGuild().block();

        System.out.printf("User left: '%s'%n", user.getTag());
//        MessageChannel channel = guild.getSystemChannel().block();
        guild.getSystemChannel().block().createMessage(String.format("see you later %s", user.getMention())).block();
    }

    public static void messageCreated(MessageCreateEvent event) {
        final Message message = event.getMessage();

        if (message.getAuthor().isPresent()) {
//            System.out.println("message received: " + message.getContent());
//            System.out.println("author: " + message.getAuthor().get().getTag());
            if (message.getAuthor().get().getId().equals(Main.client.getSelf().block().getId())) {
                return;
            }

            // Commands:
            if (Commands.processCommands(message.getContent(), event))
                return;

            // User responses:
//            if (BotSettings.stinkyToggle)
//                Responses.respondToUser(event.getMessageAuthor().getId(), event);
//
//            if (BotSettings.messageToggle)
//                Responses.respondToMessage(event.getMessageContent(), event);


        }
    }

    public static void reactionAdded(ReactionAddEvent event) {
        if (event.getChannelId().asLong() == State.getID("rulesChannelID") && event.getUserId().asLong() != Main.client.getSelfId().asLong()) {
            Guild guild = event.getGuild().block();
            Member member = event.getMember().get();

            System.out.printf("%s added reaction to rules message.%n", member.getTag());

            if (member.getRoleIds().contains(Snowflake.of((State.getIDList("defaultRoleIDs"))[0]))) {
                System.out.println(member.getDisplayName() + " already had the role.");
                return;
            }

            for (long defaultRoleID : State.getIDList("defaultRoleIDs")) {
                Role defaultRole = guild.getRoleById(Snowflake.of(defaultRoleID)).block();
//                System.out.println(defaultRole);
                if (defaultRole.equals(null)) {
                    System.out.printf("uhh, shit, no role for %s boss%n", defaultRoleID);
                    return;
                }

                member.addRole(Snowflake.of(defaultRoleID)).block();
            }

            System.out.println("roles assigned for " + member.getTag());
        }
    }


    //  Response generating methods
    // Method to handle user-specific responses
    public static void respondToUser(Long userId, MessageCreateEvent event) {
        // example code:
        /*
        if (userId == 000000000000000000L) {
            event.getChannel().sendMessage("hey " + event.getMessageAuthor().getDisplayName() + "... shush nerd");
        }
         */

    }

    // Method for more general message responses, such as elements of text or links, etc
    public static void respondToMessage(String content, MessageCreateEvent event) {
        // example code:
        /*
        if (content.toLowerCase().contains("testing") && event.getMessageAuthor().isRegularUser()) {
            event.getChannel().sendMessage("hello, yes? testing?");
        }
         */
    }

}
