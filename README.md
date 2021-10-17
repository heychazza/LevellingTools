# LevellingTools

LevellingTools is the ultimate way to make your server stand out from the rest. Majority of servers sell OP pickaxes which ruins the fun for those who cannot obtain these.. this plugin completely changes that.

All players must mine blocks (gaining exp from each block) to level up their tool across various levels, which automatically change to better enchants over time.

This has been tried and tested with players, who've expressed how much more they prefer this system over OP-based pickaxes like other servers. Don't miss out on the chance to make your server stand out from the rest!

## Installation
If you're a [Patreon](https://patreon.com/mcplugins) member, just head to #levellingtools in the Discord Server to access the precompiled versions. If you aren't, you'll need to compile it yourself.
1. If you already haven't, you'll need maven and git.
2. Then `git clone` this repository to your local computer.
3. Change into the directory and run `mvn clean install`.
4. The pre-compiled jar will be in the `target` folder.

## Commands
* **/levellingtools** (help menu) - levellingtools.help
* **/levellingtools reload** (reload config) - levellingtools.config
* **/levellingtools xp [player]** (get players xp) - levellingtools.xp

Aliases: [`ltools`, `tools`, `tool`, `lt`]

## API
If you're a developer, you may hook into the plugin using our custom events as listed below.

Package: gg.plugins.levellingtools.api

#### ToolJoinEvent
This is fired when the player joins the server, and is given the levelling tool if one isn't present in their inventory.
* getPlayer() - get player who called event.
* getItem() - get levelling tool they have.
* getSlot() - get slot number the tool is in.
* isCancelled() - check if event is cancelled.
* setCancelled(boolean) - set event to be cancelled.

#### ToolPreMineEvent
This is fired when the player starts breaking a block.
* getPlayer() - get player who called event.
* getItem() - get levelling tool they have.
* getBlock() - get block they are about to break.
* getPlayerData() - get the players data.
* getTool() - get the levelling tool object.
* isCancelled() - check if event is cancelled.
* setCancelled(boolean) - set event to be cancelled.

#### ToolMineEvent
This is fired when the player breaks a block.
* getPlayer() - get player who called event.
* getItem() - get levelling tool they have.
* getBlock() - get block that was broken.
* getPlayerData() - get the players data.
* getTool() - get the levelling tool object.
* isCancelled() - check if event is cancelled.
* setCancelled(boolean) - set event to be cancelled.

#### ToolLevelUpEvent
This is fired when the player levels up their tool.
* getPlayer() - get player who called event.
* getItem() - get levelling tool they have.
* getBlock() - get block that was broken.
* getPlayerData() - get the new players data.
* getTool() - get the new levelling tool object.
* isCancelled() - check if event is cancelled.
* setCancelled(boolean) - set event to be cancelled.

## Placeholders

If you have PlaceholderAPI installed, you may use the following variables in any supported plugin:
* %levellingtools_level% (e.g. 1)
* %levellingtools_blocks% (e.g. 200)
* %levellingtools_progress% (e.g. 100)
* %levellingtools_progress_bar% (e.g. [:::::::::])

## License

We take licensing seriously, and hope you will also do the same. Please read our license by clicking [here](https://github.com/mcplugins/LevellingTools/blob/master/LICENSE), before using **LevellingTools**.

## Support

Due to our resources being free, we only offer support to our Patreon members.
