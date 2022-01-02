package com.github.jorderator;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

public class Util {
    public static String getMention(Snowflake id) {
        return String.format("<@!%s>", id.asString());
    }

    public static String getMention(User user) {
        return String.format("<@!%s>", user.getId().asString());
    }

    public static String getMention(Member member) {
        return member.getNicknameMention();
    }


    public static String getInviteUrl(String inviteCode) {
        return "https://discord.gg/" + inviteCode;
    }


    public static EmbedCreateSpec formatEmbed(MessageChannel channel, EmbedCreateSpec embedCreateSpec) {
        String name;
        String iconURL = channel.getClient().getSelf().block().getAvatarUrl();
        Color colour;

        if (channel.getType().equals(Channel.Type.DM)) {
            name = channel.getClient().getSelf().block().getUsername();
            colour = State.defaultEmbedColour;
        }
        else {
            TextChannel tempChannel = (TextChannel) channel;

            name = tempChannel.getGuild().block().getSelfMember().block().getDisplayName();
            if (!State.useDefaultColour)
                colour = tempChannel.getGuild().block().getSelfMember().block().getColor().block();
            else
                colour = State.defaultEmbedColour;
        }


        embedCreateSpec.setColor(colour);
        embedCreateSpec.setFooter(name, iconURL);

        return embedCreateSpec;
    }
}
