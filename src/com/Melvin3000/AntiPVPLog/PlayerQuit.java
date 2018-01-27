package com.Melvin3000.AntiPVPLog;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		Player player = event.getPlayer();
		
		if (LogoutCheck.loggingOut.contains(player.getUniqueId())) {
			LogoutCheck.loggingOut.remove(player.getUniqueId());
			return;
		}
		
		if (LogoutCheck.isInsideSpawn(player.getLocation())) {
			return;
		}
		
		if (player.getGameMode() != GameMode.SURVIVAL) {
			return;
		}
		
		else {
			new PVPLoggedPlayer(event.getPlayer());
		}		
	}
}
