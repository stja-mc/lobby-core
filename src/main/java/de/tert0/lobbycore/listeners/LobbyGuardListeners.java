package de.tert0.lobbycore.listeners;

import de.tert0.lobbycore.repositories.BuildModeRepository;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LobbyGuardListeners implements Listener {
    private final BuildModeRepository buildModeRepository;
    private final ConfigurationSection lobbyguardConfig;

    public LobbyGuardListeners(BuildModeRepository buildModeRepository, ConfigurationSection lobbyguardConfig) {
        this.buildModeRepository = buildModeRepository;
        this.lobbyguardConfig = lobbyguardConfig;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(!lobbyguardConfig.getBoolean("prevent-break-block", true)) return;
        if(this.buildModeRepository.isInBuilderMode(event.getPlayer().getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!lobbyguardConfig.getBoolean("prevent-break-place", true)) return;
        if(this.buildModeRepository.isInBuilderMode(event.getPlayer().getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!lobbyguardConfig.getBoolean("prevent-interaction", true)) return;
        if(this.buildModeRepository.isInBuilderMode(event.getPlayer().getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if(!lobbyguardConfig.getBoolean("prevent-explosions", true)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if(!lobbyguardConfig.getBoolean("prevent-explosions", true)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) return;
        if(!lobbyguardConfig.getBoolean("prevent-damage", true)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityAttackEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player damager)) return;
        if(!lobbyguardConfig.getBoolean("prevent-attack", true)) return;
        if(this.buildModeRepository.isInBuilderMode(damager.getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        this.buildModeRepository.setBuilderMode(event.getPlayer().getUniqueId(), false);
    }
}
