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

## Plans

My next main goal is to rebuild the reaction role stuff. Continuing to hard-code 
every instance I need a reaction thing won't work, so I want to make something dynamic, 
preferably configurable with commands, not just editing `bot-status.json`.

As part of that I will probably overhaul the ID system to be more dynamic too, with 
commands to gather all the IDs for a given thing.

## Development

Bot token goes in `token.txt` in root of the repository, or next to jar file after
exported. Run `./gradlew build` to get started on the project, and `./gradlew jar`
to export fat jar for deployment.
