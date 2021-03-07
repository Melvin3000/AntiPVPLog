package com.Melvin3000.AntiPVPLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LogoutCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to do that.");
			return true;
		}
		Player player = (Player) sender;

		if (!AntiPVPLog.activeWorlds.contains(player.getWorld().getName())) {
			player.kickPlayer("Safely logged out");
			return true;
		}

		scheduleLogout(player);
		return true;
	}

	/**
	 * Schedule a logout for given player
	 * @param player Player to log out
	 */
	private static void scheduleLogout(final Player player) {

		if (LogoutCheck.isInsideSpawn(player.getLocation())) {
			player.sendMessage(ChatColor.AQUA + "You can log out safely at any time while inside spawn.");
			return;
		}

		BukkitRunnable r = new BukkitRunnable() {
			public void run() {
				LogoutCheck.loggingOut.remove(player.getUniqueId());
				LogoutCheck.canLogout.add(player.getUniqueId());
				player.kickPlayer("Safely logged out");
			}
		};

		if (LogoutCheck.loggingOut.putIfAbsent(player.getUniqueId(), r) != null) {
			player.sendMessage(ChatColor.RED + "You have already scheduled /logout!");
			return;
		}

		player.sendMessage(ChatColor.AQUA + "Safely logging out in " + LogoutCheck.LOGOUT_COOLDOWN + " seconds.");

		r.runTaskLater(AntiPVPLog.instance, LogoutCheck.LOGOUT_COOLDOWN * 20L);
	}
}
