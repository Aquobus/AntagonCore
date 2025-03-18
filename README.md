<p align="left">
    <a href="https://www.gnu.org/licenses/gpl-3.0" alt=GPLv3>
        <img src="https://img.shields.io/badge/License-GPLv3-blue.svg?style=flat-square" /></a>
    <a href="https://github.com/Aquobus/AntagonCore/pulse" alt="Activity">
        <img src="https://img.shields.io/github/commit-activity/m/Aquobus/AntagonCore?style=flat-square" /></a>
    <a href="https://discord.gg/5tQ5PqKet6">
        <img src="https://img.shields.io/discord/946499645020979210?style=flat-square&logo=discord&logoColor=white"
            alt="Chat on Discord"></a>
</p>

## About project
AntagonCore is a core plugin for minecraft server Antagon.
It uses API of these plugins to extend some functionallity:
AntagonCore dependencies:
- [DiscordSRV](https://github.com/DiscordSRV/DiscordSRV) [SpigotMC](https://www.spigotmc.org/resources/discordsrv.18494/)
- [LuckPerms](https://github.com/LuckPerms/LuckPerms) [SpigotMC](https://www.spigotmc.org/resources/kingdomsx.77670/)

## Features
[antiElytra](src/main/java/com/aquobus/antagoncore/modules/antiElytra/ElytraListener.java):
> Disables elytra to player while raining.
> Disables elytra if luckperms condition is met.

(beta) [betterLeaves](src/main/java/com/aquobus/antagoncore/modules/betterLeaves/BetterLeaves.java):
> Allows player walk through leaves and fall through them, except leaves which hasn't blocks below

[discord_bot](src/main/java/com/aquobus/antagoncore/modules/discord_bot):
> Extend DiscordSRV functional
[fastDirtPath](src/main/java/com/aquobus/antagoncore/modules/fastDirtPath/FastDirtPath.java):
> Player running on dirt_path block gain speed effect and summon particles on feet

[fastMinecarts](src/main/java/com/aquobus/antagoncore/modules/fastMinecarts/FastMinecarts.java):
> Extend minecart speed looking on block below

[kingdoms](src/main/java/com/aquobus/antagoncore/modules/kingdoms):
> Check the code

[lightningToGlass](src/main/java/com/aquobus/antagoncore/modules/lightningToGlass/LightningToGlass.java):
> Replace sand with glass where lightning_bolt was spawn

[luckperms](src/main/java/com/aquobus/antagoncore/modules/luckperms):
> Security fixes

[resourcePackSafeLoad](src/main/java/com/aquobus/antagoncore/modules/resourcePackSafeLoad):
> Gives blind, invincibility and cancel move for player while they loading server resource 

[villagerTransportation](src/main/java/com/aquobus/antagoncore/modules/villagerTransportation):
> Extend minecart vehicle functional by allowing llamas and camels to ride villagers
> Thanks [Villager Transportation datapack](https://modrinth.com/datapack/villager-transportation) for the idea

## Building
To build this plugin download source code with some IDE (we use IntelliJ IDEA) and build using default Maven package
If you build plugin you can drop it in your server "plugins" folder 

## Conspiration
Thanks to datapack creator [Jaffthry](https://modrinth.com/user/Jaffthry) which datapack we used as idea for [villagerTransportation](src/main/java/com/aquobus/antagoncore/modules/villagerTransportation) module
Thanks to UltimaAddons which code we modify to adjust for our ideas in [kingdoms](src/main/java/com/aquobus/antagoncore/modules/kingdoms) module

## License
Copyright (C) 2024 by Antagon

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
