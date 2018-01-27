package com.Melvin3000.AntiPVPLog;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class EntityDamage implements Listener {

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {

		/* Disable offline damage by sources other than entities */
		if (event.getCause() != DamageCause.ENTITY_ATTACK && AntiPVPLog.dummySkeletons.containsKey(event.getEntity().getUniqueId())) {
			event.setCancelled(true);
			return;
		}

		/* Cancel any logouts */
		if (event.getEntity() instanceof Player) {
			LogoutCheck.cancelLogout((Player) event.getEntity());
		}
		
	}

}
