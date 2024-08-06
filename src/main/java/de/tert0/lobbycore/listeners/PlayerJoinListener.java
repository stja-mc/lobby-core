package de.tert0.lobbycore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
    }
}
