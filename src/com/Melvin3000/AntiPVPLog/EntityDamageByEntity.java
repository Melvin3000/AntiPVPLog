package com.Melvin3000.AntiPVPLog;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntity implements Listener {

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		/* Disable non player damage to offline players (Primarily stops wolves) */
		if (AntiPVPLog.dummySkeletons.containsKey(event.getEntity().getUniqueId())) {

			if (!(event.getDamager() instanceof Player)) {
				event.setCancelled(true);
			}
		}

	}

}
