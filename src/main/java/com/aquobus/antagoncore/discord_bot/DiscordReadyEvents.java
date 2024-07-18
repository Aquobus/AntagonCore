/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.aquobus.antagoncore.discord_bot;

import com.aquobus.antagoncore.AntagonCore;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import org.bukkit.Bukkit;

public class DiscordReadyEvents {

    private volatile boolean init = false;
    public void ready() {
        DiscordCommands discordCommands = new DiscordCommands();
        Bukkit.getPluginManager().registerEvents(discordCommands, AntagonCore.plugin);
        DiscordSRV.api.addSlashCommandProvider(discordCommands);
        discordCommands.reload();
    }

//    public DiscordReadyEvents() {
//        init = false;
//        if (DiscordSRV.isReady) {
//            init = true;
//            ready();
//        }
//    }

    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onDiscordReady(DiscordReadyEvent event) {
        if (!init) {
            init = true;
            ready();
        }
    }

}
