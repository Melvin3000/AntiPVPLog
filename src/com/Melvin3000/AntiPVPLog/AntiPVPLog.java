package com.Melvin3000.AntiPVPLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiPVPLog extends JavaPlugin {

	public static JavaPlugin instance;
	public static HashMap<UUID, PVPLoggedPlayer> dummySkeletons = new HashMap<UUID, PVPLoggedPlayer>();
	
	public static List<String> activeWorlds = new ArrayList<String>();

	public void onEnable() {
		saveConfig();
		instance = this;
		activeWorlds = getActiveWorlds();
		
		getLogger().info("Active Worlds: " + activeWorlds.toString());
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerQuit(), this);
		pm.registerEvents(new EntityDeath(), this);
		pm.registerEvents(new PlayerJoin(), this);
		pm.registerEvents(new EntityDamage(), this);
		pm.registerEvents(new EntityDamageByEntity(), this);
		pm.registerEvents(new PlayerMove(), this);
		pm.registerEvents(new ChunkUnload(), this);

		getCommand("logout").setExecutor(new LogoutCommand());
	}

	public void onDisable() {

		/* Despawn pvp logged skeletons on restart (Looks like Rumpslem will get away with it this time) */
		for (UUID uuid : dummySkeletons.keySet()) {
			Entity entity = getServer().getEntity(uuid);
			if (entity != null) {
				entity.remove();
			}
		}

	}
	
	public List<String> getActiveWorlds() {
		return getConfig().getStringList("ActiveWorlds");
	}

}
