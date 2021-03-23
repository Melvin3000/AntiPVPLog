package com.Melvin3000.AntiPVPLog;

import java.util.HashSet;
import java.util.UUID;

public class JailPlugin {
    // java needs a lateinit thing that throws a runtime :(
    public static HashSet<UUID> JAILED_MAP = null;

    public static void initJail() {
        try {
            final Class<?> jailClass = Class.forName("net.simpvp.Jail.Jail");

            // This cast is safe I swear
            JAILED_MAP = (HashSet<UUID>) jailClass.getDeclaredField("jailed_players").get(null);
        // how many exceptions can we get??!!
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            AntiPVPLog.instance.getLogger().info("Jail plugin not found");
        }
    }

    /**
     * This will always return false if the jailed plugin is not loaded
     */
    public static boolean isPlayerJailed(UUID uuid) {
        if (JAILED_MAP == null) {
            return false;
        } else {
            return JAILED_MAP.contains(uuid);
        }
    }

    /**
     * This is here in case it is useful
     */
    public static boolean jailedPluginPresent() {
        try {
            Class.forName("net.simpvp.Jail.Jail");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
