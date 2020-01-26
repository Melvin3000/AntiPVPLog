package com.Melvin3000.AntiPVPLog;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntity implements Listener {

	private final static HashSet<EntityType> projectiles = new HashSet<EntityType>(Arrays.asList(
			EntityType.ARROW,
			EntityType.SPECTRAL_ARROW,
			EntityType.TRIDENT
	));

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		/* Disable non player damage to offline players (Primarily stops wolves) */
		if (AntiPVPLog.dummySkeletons.containsKey(event.getEntity().getUniqueId())) {

			boolean cancel = true;
			Entity damager = event.getDamager();
			if (damager instanceof Player) {
				cancel = false;
			}

			else if (projectiles.contains(damager.getType())) {
				Projectile projectile = (Projectile) damager;
				if (projectile.getShooter() instanceof Player) {
					cancel = false;
				}
			}
			event.setCancelled(cancel);
		}
	}

}
