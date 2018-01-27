package com.Melvin3000.AntiPVPLog;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PVPLoggedPlayer {

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

		/* Simulates dropped player XP as per: https://minecraft.gamepedia.com/Experience#Behavior */
		this.xpDropped = player.getLevel() * 7;
		this.xpDropped = (xpDropped > 100) ? 100 : xpDropped;

		AntiPVPLog.dummySkeletons.put(skeleton.getUniqueId(), this);
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
