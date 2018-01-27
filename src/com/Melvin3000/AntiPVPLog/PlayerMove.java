package com.Melvin3000.AntiPVPLog;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		
		/* Only stop a logout if player moved to an entirely new block, ignore small movements and the camera */
		if (!event.getFrom().getBlock().equals(event.getTo().getBlock())) {
			LogoutCheck.cancelLogout(event.getPlayer());
		}
		
	}
	
}
