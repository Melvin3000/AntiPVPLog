package com.Melvin3000.AntiPVPLog;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectOutputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.Level;

public class PVPLoggedPlayer {
	private static Logger INVENTORY_LOGGER = LogManager.getLogger("InventoryLog");
	private static final Marker INVENTORY_MARKER = MarkerManager.getMarker("INVENTORY");

	private UUID uuid;
	private Skeleton skeleton;
	private String name;

	private int xpDropped;
	private PlayerInventory inventory;

	/**
	 * Stores data on pvp logged player
	 * @param player player who pvp logged
	 */
	public PVPLoggedPlayer(Player player) {

		this.uuid = player.getUniqueId();
		this.inventory = player.getInventory();
		this.name = player.getName();
		this.skeleton = spawnDummySkeleton(player);

		logInventory(player);

		/* Simulates dropped player XP as per: https://minecraft.gamepedia.com/Experience#Behavior */
		this.xpDropped = player.getLevel() * 7;
		this.xpDropped = (xpDropped > 100) ? 100 : xpDropped;

		AntiPVPLog.dummySkeletons.put(skeleton.getUniqueId(), this);
		scheduleRemoval();
	}

	/**
	 * Spawns dummy skeleton with inventory and location of player
	 * @param player player who pvp logged
	 * @return UUID of created skeleton
	 */
	private Skeleton spawnDummySkeleton(Player player) {

		/* Skeletons throw an error if you attempt to give them more than 20 hearts (maybe possible with golden apples) */
		double health = (player.getHealth() > 20) ? 20 : player.getHealth();

		ItemStack[] armor = inventory.getArmorContents();
		ItemStack mainhand = inventory.getItemInMainHand();
		ItemStack offhand = inventory.getItemInOffHand();

		/* If player had no helmet then replace it with a button (invisible helmet but stops sunlight damage) */
		ItemStack placeholderHelmet = new ItemStack(Material.STONE_BUTTON, 1);
		armor[3] = (armor[3] == null) ? placeholderHelmet : armor[3];

		Skeleton spooky = (Skeleton) player.getWorld().spawnEntity(player.getLocation(), EntityType.SKELETON);

		spooky.setCustomName(player.getName());
		spooky.setCustomNameVisible(true);
		spooky.setAI(false);
		spooky.setCanPickupItems(false);
		spooky.setHealth(health);
		spooky.setRemoveWhenFarAway(false);

		spooky.getEquipment().setArmorContents(armor);
		spooky.getEquipment().setItemInMainHand(mainhand);
		spooky.getEquipment().setItemInOffHand(offhand);

		spooky.getEquipment().setHelmetDropChance(0.0f);
		spooky.getEquipment().setChestplateDropChance(0.0f);
		spooky.getEquipment().setLeggingsDropChance(0.0f);
		spooky.getEquipment().setBootsDropChance(0.0f);
		spooky.getEquipment().setItemInMainHandDropChance(0.0f);
		spooky.getEquipment().setItemInOffHandDropChance(0.0f);

		return spooky;
	}

	/**
	 * Schedule this dummy skeleton to be removed after the logout cooldown
	 */
	private void scheduleRemoval() {

		AntiPVPLog.instance.getServer().getScheduler().runTaskLater(AntiPVPLog.instance, new Runnable() {
			public void run() {

				if (AntiPVPLog.dummySkeletons.keySet().contains(skeleton.getUniqueId())) {
					despawn();
				}

			}
		}, LogoutCheck.LOGOUT_COOLDOWN * 20L);
	}

	/**
	 * Log inventory, just in case as per: https://github.com/C4K3/Events/blob/master/src/EventCommand.java#L80
	 */
	private void logInventory(Player player) {
		if (!INVENTORY_LOGGER.isDebugEnabled()) {
			return;
		}

		try {
			ByteArrayOutputStream bytearray = new ByteArrayOutputStream();
			OutputStream base64 = Base64.getEncoder().wrap(bytearray);
			BukkitObjectOutputStream bukkitstream = new BukkitObjectOutputStream(base64);

			int len = 0;
			for (ItemStack itemStack : inventory.getContents()) {
				if (itemStack == null) {
					continue;
				}
				len += 1;
			}
			bukkitstream.writeInt(len);

			for (ItemStack itemStack : inventory.getContents()) {
				if (itemStack == null) {
					continue;
				}

				bukkitstream.writeObject(itemStack);
			}

			bukkitstream.close();
			base64.close();

			INVENTORY_LOGGER.debug(INVENTORY_MARKER, String.format("%s potentially PvP logged: %d %s", name, player.getLevel(), bytearray.toString()));
		} catch (Exception e) {
			AntiPVPLog.instance.getLogger().warning("Failed to log inventory: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Despawns skeleton and removes reference from plugin
	 */
	public void despawn() {
		skeleton.remove();
		AntiPVPLog.dummySkeletons.remove(skeleton.getUniqueId());
		AntiPVPLog.instance.getLogger().info(name + "'s skeleton logged out safely.");
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Skeleton getSkeleton() {
		return skeleton;
	}

	public void setSkeleton(Skeleton skeleton) {
		this.skeleton = skeleton;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getXpDropped() {
		return xpDropped;
	}

	public void setXpDropped(int xpDropped) {
		this.xpDropped = xpDropped;
	}

	public PlayerInventory getInventory() {
		return inventory;
	}

	public void setInventory(PlayerInventory inventory) {
		this.inventory = inventory;
	}

}
