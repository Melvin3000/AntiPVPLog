package com.Melvin3000.AntiPVPLog;

import java.util.Iterator;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnload implements Listener {

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {

		if (!AntiPVPLog.activeWorlds.contains(event.getWorld().getName())) {
			return;
		}

		/* Despawn any skeletons before chunk unloads */
		Iterator<PVPLoggedPlayer> iterator = AntiPVPLog.dummySkeletons.values().iterator();
		while (iterator.hasNext()) {

			PVPLoggedPlayer spooky = iterator.next();
			if (spooky.getSkeleton().getLocation().getChunk().equals(event.getChunk())) {
				spooky.despawn();
			}
		}

	}
}
