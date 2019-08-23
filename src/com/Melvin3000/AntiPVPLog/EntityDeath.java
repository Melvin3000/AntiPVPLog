package com.Melvin3000.AntiPVPLog;

import java.util.Arrays;

import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.NBTTagCompound;

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
			
			Player killer = entity.getKiller();
			if (killer != null) {
				showDeathMessage(dummy, killer);
	
				/* Clean up statistics */
				killer.incrementStatistic(Statistic.PLAYER_KILLS);
				killer.decrementStatistic(Statistic.KILL_ENTITY, EntityType.SKELETON);
			} else {
				/* This shouldn't happen but check anyway and write an error if it does */
				AntiPVPLog.instance.getLogger().warning("PVP Logged skeleton died somehow without player involvement");
			}
		}

	}

	/**
	 * Save player UUID to kill them when they rejoin next 
	 * @param player player to save
	 */
	public void killOfflinePlayer(PVPLoggedPlayer player) {

		AntiPVPLog.dummySkeletons.remove(player.getSkeleton().getUniqueId());
		//AntiPVPLog.instance.getServer().broadcastMessage(player.getName() + " should not have PvP logged");
		AntiPVPLog.instance.getLogger().info(player.getName() + " died after PvP logging.");

		/* Lazy way to save UUIDs, could be replaced with SQL or something */
		FileConfiguration config = AntiPVPLog.instance.getConfig();
		config.set("PVPLoggers." + player.getUuid().toString(), true);
		AntiPVPLog.instance.saveConfig();
	}


	/**
	 * Prints death message using format
	 * Player PvP logged to escape Killer using [weapon]
	 */
	public void showDeathMessage(PVPLoggedPlayer player, Player killer) {

		TextComponent deathMessage = new TextComponent(player.getName() + " PvP logged to escape " + killer.getName());

		/* Show weapon in detail if one was used */
		ItemStack weaponUsed = killer.getInventory().getItemInMainHand();
		if (weaponUsed.hasItemMeta() && weaponUsed.getItemMeta().hasDisplayName()) {
			deathMessage.addExtra(" using ");
			deathMessage.addExtra(getHoverableItemComponent(weaponUsed));
		}
		
		for (Player p : AntiPVPLog.instance.getServer().getOnlinePlayers()) {
			p.spigot().sendMessage(deathMessage);
		}
	}

	/**
	 * @param item ItemStack to show on hover
	 * @return Returns a death message in the format of [weapon name]
	 */
	public TextComponent getHoverableItemComponent(ItemStack item) {

		TextComponent info = new TextComponent("[");
		info.setColor(ChatColor.AQUA);
		TextComponent itemName = new TextComponent(item.getItemMeta().getDisplayName());
		itemName.setItalic(true);
		info.addExtra(itemName);
		info.addExtra("]");

		/* Convert item to JSON using nms trickery to get hoverable item json
		 * https://www.spigotmc.org/threads/tut-item-tooltips-with-the-chatcomponent-api.65964 */
		String nbtInfoString = CraftItemStack.asNMSCopy(item).save(new NBTTagCompound()).toString();
		info.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(nbtInfoString).create()));

		return info;
	}

}
