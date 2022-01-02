// ===============================================================================================
//  Project Information:
//   {blurb here}
//
//  General todo list:
//  - LOTS MORE SEPARATION. Seperate file handling and runtime state, separate command classes in
//     separate package, separate modular reaction role handling, separate primary features
//     (secret santa, mod list, etc), go nuts. Embed handling to Util even?
//  - Bot status stuff
//  - Also this intro thing
// ===============================================================================================


package com.github.jorderator;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static GatewayDiscordClient client;

    public static void onReady(ReadyEvent event) {
        System.out.println("ready bitches");
//        ActivityUpdateRequest activity = Activity.playing("asdf");
//        activity.
//        client.updatePresence();
    }

    public static void main(String[] args) {
        System.out.println("we're at least starting");
        Scanner in = new Scanner(System.in);

        // Get discord api token from a 'token.txt' file, in current working path
        // TODO: Implement token system better (maybe? I guess maybe commandline argument?)
        String token = "";
        try {
            File tokenFile = new File("token.txt");
            Scanner fileReader = new Scanner(tokenFile);
            token = fileReader.nextLine();
            fileReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Token file not found, please place discord bot token into a token.txt file next to the executable.");
            e.printStackTrace();
            System.exit(1);
        }
        if (token.equals("")) {
            System.out.println("Token not found in token.txt, please place the bot token in token.txt");
            System.exit(1);
        }

        System.out.println("we got the token I guess");

        client = DiscordClientBuilder.create(token)
                .build()
                .login()
                .block();

        System.out.println("logged in?");

        try {
            State.loadState();
        }
        catch (IOException e) {
            System.out.println("An error occurred while loading json file.");
            e.printStackTrace();
            System.out.println("Exiting...");
            System.exit(1);
        }

        System.out.println("loaded state");

//        State.ids.put("ownerID", client.getApplicationInfo().block().getOwnerId().asLong());

        System.out.println("owner id: " + State.getID("ownerID"));


//        BotSettings.updateStatus();
//
//        System.out.println("status set");


        // Assigning required listeners
        client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(Listeners::messageCreated);

        client.getEventDispatcher().on(ReactionAddEvent.class).subscribe(Listeners::reactionAdded);
        client.getEventDispatcher().on(ReactionRemoveEvent.class).subscribe(Listeners::reactionRemoved);

        client.getEventDispatcher().on(MemberJoinEvent.class).subscribe(Listeners::memberJoined);
        client.getEventDispatcher().on(MemberLeaveEvent.class).subscribe(Listeners::memberLeft);

        client.getEventDispatcher().on(ReadyEvent.class).subscribe(Main::onReady);

        System.out.println("listeners attached");


        client.onDisconnect().block();

        // Exiting the bot
//        while (true) {
//            System.out.print("\n enter 'stop' to close the bot: ");
//            if (in.nextLine().equals("stop")) {
//                saveState();
//                System.exit(0);
//            }
//        }
    }
}
