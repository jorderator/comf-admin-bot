package com.github.jorderator;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SecretSanta {

    public static Map<Long, Long> secretSantas;
    static {
        secretSantas = new HashMap<>();
    }

    public static void beginSecretSanta() {
        ArrayList<Member> unassignedSantas = new ArrayList<>();
        ArrayList<Member> unassignedPartners = new ArrayList<>();

        Guild guild = Main.client.getGuildById(Snowflake.of(State.getID("serverID"))).block();

        for (Member member : guild.requestMembers().toIterable()) {
            if (member.getRoleIds().contains(Snowflake.of(State.getID("secretSantaRoleID")))) {
                unassignedSantas.add(member);
            }
        }
        unassignedPartners = new ArrayList<>(unassignedSantas);

        Random rand = new Random();

        while (unassignedSantas.size() > 0) {
            Member santa = unassignedSantas.get(rand.nextInt(unassignedSantas.size()));
            ArrayList<Member> tempPartners = new ArrayList<>(unassignedPartners);
            tempPartners.remove(santa);
            Member partner = tempPartners.get(rand.nextInt(tempPartners.size()));

            secretSantas.put(santa.getId().asLong(), partner.getId().asLong());

            unassignedSantas.remove(santa);
            unassignedPartners.remove(partner);
        }

        State.secretSantaOptIn = false;
        State.saveState();

        System.out.println("Secret santas assigned");

        TextChannel announcementChannel = guild.getSystemChannel().block();
        Role secretSantaRole = guild.getRoleById(Snowflake.of(State.getID("secretSantaRoleID"))).block();
        announcementChannel.createMessage(messageCreateSpec ->
                messageCreateSpec.setContent(secretSantaRole.getMention()).addEmbed(embedCreateSpec -> {
                    Util.formatEmbed(announcementChannel, embedCreateSpec);
                    embedCreateSpec
                            .setTitle("The secret santa has begun")
                            .setDescription("Everyone with the " + secretSantaRole.getMention() + " role has now been " +
                                    "assigned their partner, and will be informed in a DM from this bot.\n**You have until the " +
                                    "22nd to organise your gift for that person.**\n**The upper limit on price is $25**, but you " +
                                    "are under no obligation to spend that much. \n\n**You can anonymously message that person " +
                                    "with `.message-partner {text}` in the bot's dms, and they can respond with `.message-santa " +
                                    "{text}`, also in the bot's dms.** " +
                                    "It is highly *highly* recommended to send your secret santa (`.message-santa`) a " +
                                    "wishlist of 2 to 5 items to give them an idea of what to get. No one should have to " +
                                    "stress about what to get for this, so please do this." +
                                    "\n\nAnd most importantly, have fun!");
                }
        )).block();
//        announcementChannel.createMessage(secretSantaRole.getMention()).block();

        for (Map.Entry<Long, Long> secretSanta : secretSantas.entrySet()) {
            PrivateChannel secretSantaPrivateChannel = Main.client.getUserById(Snowflake.of(secretSanta.getKey())).block().getPrivateChannel().block();
            User partner = Main.client.getUserById(Snowflake.of(secretSanta.getValue())).block();

            secretSantaPrivateChannel.createEmbed(embedCreateSpec -> {
                        Util.formatEmbed(secretSantaPrivateChannel, embedCreateSpec);
                        embedCreateSpec
                                .setTitle("Your partner for the secret santa is:")
                                .setDescription(Util.getMention(partner) + "!!\n\n" +
                                        "You can message them with `.message-partner {text}`, and whoever has you can be " +
                                        "messaged with `.message-santa {text}`. Please send them a wishlist when you can.")
                                .setThumbnail(partner.getAvatarUrl());
                    }
            ).block();
        }
    }

    public static void endSecretSanta() {
        Guild guild = Main.client.getGuildById(Snowflake.of(State.getID("serverID"))).block();
        TextChannel announcementChannel = guild.getSystemChannel().block();
        Role secretSantaRole = guild.getRoleById(Snowflake.of(State.getID("secretSantaRoleID"))).block();

        announcementChannel.createMessage(messageCreateSpec ->
                messageCreateSpec
                        .setContent(secretSantaRole.getMention())
                        .addEmbed(embedCreateSpec -> {
                            Util.formatEmbed(announcementChannel, embedCreateSpec);
                            embedCreateSpec
                                    .setTitle("The secret santa has finished!")
                                    .setDescription("Whenever everyone is ready, we will get in vc tonight to reveal partners and exchange gifts. " +
                                            "Please all " + secretSantaRole.getMention() + " join, so make sure we organise a time that works " +
                                            "for you.");
                        })
        ).block();
    }



    public static void messageAllSantas(String content) {
        System.out.println("Sending announcement \"" + content + "\" to all santas.");

        for (Map.Entry<Long, Long> secretSanta : secretSantas.entrySet()) {
            PrivateChannel secretSantaPrivateChannel = Main.client.getUserById(Snowflake.of(secretSanta.getKey())).block().getPrivateChannel().block();

            secretSantaPrivateChannel.createEmbed(embedCreateSpec -> {
                Util.formatEmbed(secretSantaPrivateChannel, embedCreateSpec);
                embedCreateSpec
                        .setTitle("Secret Santa Announcement:")
                        .setDescription(content);
            }).block();
        }
    }

    public static void messagePartner(Snowflake senderID, String content) {
        PrivateChannel channel = Main.client.getUserById(Snowflake.of(secretSantas.get(senderID.asLong()))).block().getPrivateChannel().block();
        channel.createEmbed(embedCreateSpec -> {
            Util.formatEmbed(channel, embedCreateSpec);
            embedCreateSpec.setTitle("Your secret santa says:");
            embedCreateSpec.setDescription(content);
        }).block();
    }

    public static void messageSecretSanta(Snowflake senderID, String content) {
        User santa = Main.client.getUserById(Snowflake.of(getSecretSanta(senderID.asLong()))).block();
        PrivateChannel channel = santa.getPrivateChannel().block();
        channel.createEmbed(embedCreateSpec -> {
            Util.formatEmbed(channel, embedCreateSpec);
            embedCreateSpec.setTitle("Your partner says:");
            embedCreateSpec.setDescription(Util.getMention(senderID) + ": " + content);
        }).block();
    }



    public static Long getSecretSanta(Long partnerID) {
        for (Map.Entry<Long,Long> person : SecretSanta.secretSantas.entrySet()) {
            if (person.getValue().equals(partnerID)) {
                return person.getKey();
            }
        }

        return null;
    }
}
