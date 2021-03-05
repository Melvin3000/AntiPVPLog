package com.Melvin3000.AntiPVPLog;

import java.util.HashSet;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LogoutCheck {
	
	public static int LOGOUT_COOLDOWN = 10;
	public static HashMap<UUID, BukkitRunnable> loggingOut = new HashMap<UUID, BukkitRunnable>();
	public static HashSet<UUID> canLogout = new HashSet<UUID>();
	
	private static int minx = -31;
	private static int minz = -31;
	private static int maxx = 31;
	private static int maxz = 31;
	private static String worldname = "world";

	/**
	 * Checks if given location is inside the spawn
	 * @param loc Location to check for
	 * @return true if yes, else false
	 */
	public static boolean isInsideSpawn(Location loc) {	
		if (!loc.getWorld().getName().equals(worldname)) {
			return false;
		}
		if (loc.getBlockX() < minx || loc.getBlockX() > maxx) {
			return false;
		}
		if (loc.getBlockZ() < minz || loc.getBlockZ() > maxz) {
			return false;
		}
		return true;
	}

	/**
	 * Cancel a logout (if it exists) from given player
	 * @param player
	 */
	public static void cancelLogout(Player player) {
		BukkitRunnable r = loggingOut.remove(player.getUniqueId());
		if (r != null) {
			r.cancel();
			player.sendMessage(ChatColor.RED + "Logout canceled.");
		}
	}
}
