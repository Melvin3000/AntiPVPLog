package com.Melvin3000.AntiPVPLog;

import java.util.Arrays;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeath implements Listener {

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {

		LivingEntity entity = event.getEntity();

		/* If Entity is Dummy skeleton drop pvp logged players items */
		if (AntiPVPLog.dummySkeletons.containsKey(entity.getUniqueId())) {

			PVPLoggedPlayer dummy = AntiPVPLog.dummySkeletons.get(entity.getUniqueId());

			event.setDroppedExp(dummy.getXpDropped());
			event.getDrops().clear();
			event.getDrops().addAll(Arrays.asList(dummy.getInventory().getStorageContents()));
			event.getDrops().addAll(Arrays.asList(dummy.getInventory().getArmorContents()));

			killOfflinePlayer(dummy);
		}

	}

	/**
	 * Save player UUID to kill them when they rejoin next 
	 * @param player player to save
	 */
	public void killOfflinePlayer(PVPLoggedPlayer player) {

		AntiPVPLog.dummySkeletons.remove(player.getSkeleton().getUniqueId());
		AntiPVPLog.instance.getServer().broadcastMessage(player.getName() + " should not have pvp logged");
		AntiPVPLog.instance.getLogger().info(player.getName() + " died after pvp logging.");

		/* Lazy way to save UUIDs, could be replaced with SQL or something */
		FileConfiguration config = AntiPVPLog.instance.getConfig();
		config.set(player.getUuid().toString(), true);
		AntiPVPLog.instance.saveConfig();
	}

}
