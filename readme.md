# comf-admin-bot

An admin bot for my friend group 'comfort zone' server. 

It will have pretty much just whatever features we need or I feel like adding. It's
original purpose was just to manage invites and the reaction-role system, so anything else
is bonus.

Some features and settings are toggled through the `bot-state.json` file that is generated 
by the application, so where there isn't a command, it might be in that, applied with 
`.reload-state` in discord. (This is dumb, I will change this)

The initial commit times on this are a bit misleading because I may or may not have had 
to rebase everything to remove hard-coded IDs before making it public... oops.

## Features

The primary features of the bot, among more minor things, are as follows:

 - Single use invite generation with optional "reason" text for audit logs.
 - Dynamic role-reaction system, for creating roles self-assignable by reactions on a message.
 - Secret Santa functions, for a yearly group christmas event. This includes random assigning 
    of partners, anonymous messaging, and self management of participation through the role-
    reaction system.
 - Persistent storage of state to a generated and editable JSON file.

## Plans

I want to continue refactoring the project, moving commands or sets of commands to separate 
class files, remove the old ID system as it stands, with commands to make more dynamic the last 
required parts such as primary server ID, announcement channels, etc. 

I also want to continue developing the reaction system, including to add response-type functions, 
such as a message with multiple reactions serving as buttons or options to be selected. This 
will also entail a system for multi-stage commands and functions, not just a single response to 
a single command, but a process of multiple responses with persistent state.

Also better documentation, commenting etc. The eternal struggle.

## Development

Bot token goes in `token.txt` in root of the repository, or next to jar file after
exported. Run `./gradlew build` to get started on the project, and `./gradlew jar`
to export fat jar for deployment.
