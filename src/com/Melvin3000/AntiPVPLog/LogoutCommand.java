package com.Melvin3000.AntiPVPLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LogoutCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to do that.");
			return true;
		}

		Player player = (Player) sender;
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

		player.sendMessage(ChatColor.AQUA + "Safely logging out in " + LogoutCheck.LOGOUT_COOLDOWN + " seconds.");

		LogoutCheck.loggingOut.add(player.getUniqueId());

		AntiPVPLog.instance.getServer().getScheduler().runTaskLater(AntiPVPLog.instance, new Runnable() {
			public void run() {
				if (LogoutCheck.loggingOut.contains(player.getUniqueId())) {
					player.kickPlayer("Safely logged out");
				}
			}
		}, LogoutCheck.LOGOUT_COOLDOWN * 20L);
	}
}
