package de.tert0.lobbycore.commands;

import com.google.common.collect.ImmutableList;
import de.tert0.lobbycore.repositories.BuildModeRepository;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class BuildCommand implements TabExecutor {
    private final BuildModeRepository buildModeRepository;

    public BuildCommand(BuildModeRepository buildModeRepository) {
        this.buildModeRepository = buildModeRepository;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        Player target;
        if(!sender.hasPermission("lobbycore.build")) {
            sender.sendMessage(Component.text("You dont have the permissions to use this command!", NamedTextColor.RED));
            return false;
        }
        if(args.length == 0) {
            if(sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(Component.text("Specify a player!", NamedTextColor.RED));
                return false;
            }
        } else if(args.length == 1) {
            if(!sender.hasPermission("lobbycore.build.other")) {
                sender.sendMessage(Component.text("You dont have the permissions to use this command affecting other players!", NamedTextColor.RED));
                return false;
            }

            target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                sender.sendMessage(Component.text("Could not find player!", NamedTextColor.RED));
                return false;
            }
        } else {
            sender.sendMessage(Component.text("Wrong amount of arguments!", NamedTextColor.RED));
            return false;
        }

        boolean currentStatus = this.buildModeRepository.isInBuilderMode(target.getUniqueId());

        this.buildModeRepository.setBuilderMode(target.getUniqueId(), !currentStatus);
        if(currentStatus) {
            sender.sendMessage(Component.text("Disabled build mode!", NamedTextColor.RED));
        } else {
            sender.sendMessage(Component.text("Enabled build mode!", NamedTextColor.GREEN));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(username -> username.startsWith(args[0]))
                    .collect(Collectors.toList());
        }
        return ImmutableList.of();
    }
}
