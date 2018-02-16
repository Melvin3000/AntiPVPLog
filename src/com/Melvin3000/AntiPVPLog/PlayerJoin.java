package com.Melvin3000.AntiPVPLog;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		/* Kill players who PVP Logged the last time they left server */
		FileConfiguration config = AntiPVPLog.instance.getConfig();
		
		if (config.contains("PVPLoggers." + player.getUniqueId().toString())) {

			player.getInventory().clear(); // Clearing inventory first stops dupe glitch using a totem of undying
			player.setLevel(0);
			player.setHealth(0);
			player.sendMessage(ChatColor.DARK_RED + "You died from PvP logging");

			/* Delete player from list of to-be-killed players */
			config.set("PVPLoggers." + player.getUniqueId().toString(), null);
			AntiPVPLog.instance.saveConfig();
		}

		/* Despawn dummy skeleton if it is still alive and update health */
		else {
			UUID found = null;
			for (UUID uuid : AntiPVPLog.dummySkeletons.keySet()) {
				PVPLoggedPlayer dummy = AntiPVPLog.dummySkeletons.get(uuid);

				if (dummy.getUuid().equals(player.getUniqueId())) {
					Skeleton spooky = dummy.getSkeleton();
					player.setHealth(spooky.getHealth());
					spooky.remove();
					found = spooky.getUniqueId();
					break;
				}

			}

			if (found != null) {
				AntiPVPLog.dummySkeletons.remove(found);
			}
		}

	}
}
