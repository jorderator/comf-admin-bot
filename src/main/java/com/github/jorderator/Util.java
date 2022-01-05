package com.github.jorderator;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    public static Optional<Message> getMessage(String messageLink) {
        Pattern linkPattern = Pattern.compile("^https://discord.com/channels/(@me|[0-9]+)/([0-9]+)/([0-9]+)$");
        Matcher linkMatch = linkPattern.matcher(messageLink);

        if (linkMatch.find()) {
            if (linkMatch.group(1).equals("@me")) {
                PrivateChannel channel = (PrivateChannel) Main.client.getChannelById(Snowflake.of(linkMatch.group(2))).block();
                Message message = channel.getMessageById(Snowflake.of(linkMatch.group(3))).block();

                return Optional.ofNullable(message);
            }
            else {
                Guild guild = Main.client.getGuildById(Snowflake.of(linkMatch.group(1))).block();
                TextChannel channel = (TextChannel) guild.getChannelById(Snowflake.of(linkMatch.group(2))).block();
                Message message = channel.getMessageById(Snowflake.of(linkMatch.group(3))).block();

                return Optional.ofNullable(message);
            }
        }

        return Optional.ofNullable(null);
    }


    public static ReactionEmoji getEmoji(String stringEmoji) {
        // Unicode emoji patterns: https://gist.github.com/Vexs/9e4c14d41161590ca94d0d21e456fef0
        // info about discord emojis: https://anidiots.guide/coding-guides/using-emojis/

        Pattern guildEmojiPattern = Pattern.compile("<(|a):(.+):([0-9]+)>");
        Matcher guildEmojiMatcher = guildEmojiPattern.matcher(stringEmoji);

        if (guildEmojiMatcher.find()) {
            // TODO: may need a try-catch here? if it's a nitro thing? try this with sayo later
            return ReactionEmoji.of(Long.parseLong(guildEmojiMatcher.group(3)), guildEmojiMatcher.group(2), (!guildEmojiMatcher.group(1).isEmpty()));
        } else {
            return ReactionEmoji.unicode(stringEmoji);
        }

//        return Optional.ofNullable(ReactionEmoji.unicode("a"));
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
