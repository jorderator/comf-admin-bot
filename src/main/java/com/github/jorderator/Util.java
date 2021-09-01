package com.github.jorderator;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;

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
}
