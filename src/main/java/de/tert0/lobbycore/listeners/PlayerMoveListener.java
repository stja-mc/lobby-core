package de.tert0.lobbycore.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.tert0.lobbycore.LobbyCore;
import de.tert0.lobbycore.portals.Portal;
import de.tert0.lobbycore.portals.Position;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PlayerMoveListener implements Listener {
    private final Logger logger;
    private final String channel;
    private final String world;
    private final List<Portal> portals;
    private final Set<UUID> playersInPortal;
    public PlayerMoveListener(String channel, FileConfiguration config) {
        this.logger = LobbyCore.getInstance().getLogger();
        this.channel = channel;
        this.portals = new ArrayList<>();
        this.playersInPortal = new HashSet<>();

        List<Map<?, ?>> portalsConfig = config.getMapList("portals");
        this.world = config.getString("lobby-world", "world");

        for(Map<?, ?> portalConfig : portalsConfig) {
            String destination = (String) portalConfig.get("destination");
            Position corner1 = new Position((String) Objects.requireNonNull(portalConfig.get("corner1")));
            Position corner2 = new Position((String) Objects.requireNonNull(portalConfig.get("corner2")));

            Portal portal = new Portal(corner1, corner2, world, destination);
            this.portals.add(portal);
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.hasChangedPosition()) return;
        if(!event.hasChangedBlock()) return;
        if(!event.getPlayer().getWorld().getName().equals(this.world)) return;

        Player player = event.getPlayer();
        Position position = new Position(event.getTo());

        List<Portal> collisions = this.portals.stream()
                .filter(portal -> portal.isColliding(position))
                .collect(Collectors.toList());

        if(!collisions.isEmpty()) {
            if(this.playersInPortal.contains(player.getUniqueId())) return;
            if(collisions.size() > 1) {
                this.logger.warning("There are multiple portals colliding at: " + position);
            }

            Portal portal = collisions.get(0);

            ByteArrayDataOutput data = ByteStreams.newDataOutput();
            data.writeUTF(portal.destination);
            data.writeUTF(player.getUniqueId().toString());

            player.sendPluginMessage(LobbyCore.getInstance(), this.channel, data.toByteArray());

            this.playersInPortal.add(player.getUniqueId());
        } else {
            this.playersInPortal.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.playersInPortal.remove(event.getPlayer().getUniqueId());
    }
}
