package de.tert0.lobbycore;

import de.tert0.lobbycore.commands.BuildCommand;
import de.tert0.lobbycore.listeners.LobbyGuardListeners;
import de.tert0.lobbycore.listeners.PlayerJoinListener;
import de.tert0.lobbycore.listeners.PlayerMoveListener;
import de.tert0.lobbycore.repositories.BuildModeRepository;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.Objects;
import java.util.logging.Logger;

public final class LobbyCore extends JavaPlugin {
    private static LobbyCore plugin;
    private Logger logger;
    private FileConfiguration config;
    JedisPool redisPool;
    BuildModeRepository buildModeRepository;

    public LobbyCore() {
        super();
        LobbyCore.plugin = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.logger = this.getLogger();
        this.config = this.getConfig();

        this.redisPool = this.connectRedis();
        if(this.redisPool == null) return;
        this.buildModeRepository = new BuildModeRepository(this.redisPool);


        BuildCommand buildCommand = new BuildCommand(buildModeRepository);
        Objects.requireNonNull(this.getCommand("build")).setExecutor(buildCommand);
        Objects.requireNonNull(this.getCommand("build")).setTabCompleter(buildCommand);

        getServer().getPluginManager().registerEvents(new LobbyGuardListeners(buildModeRepository, Objects.requireNonNull(config.getConfigurationSection("lobbyguard"))), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        String PORTALS_CHANNEL = "lobbycore:portal_send";
        getServer().getMessenger().registerOutgoingPluginChannel(this, PORTALS_CHANNEL);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(PORTALS_CHANNEL, config), this);
    }

    public JedisPool connectRedis() {
        ConfigurationSection redisConfig = this.config.getConfigurationSection("redis");
        if(redisConfig == null) {
            this.logger.warning("Could not load the redis config!");
            return null;
        }
        String host = redisConfig.getString("host");
        int port = redisConfig.getInt("port", Protocol.DEFAULT_PORT);
        String password = redisConfig.getString("password", "");
        if(password.isEmpty()) {
            password = null;
        }
        int database = redisConfig.getInt("database", Protocol.DEFAULT_DATABASE);
        return new JedisPool(
                new GenericObjectPoolConfig<>(),
                host, port, Protocol.DEFAULT_TIMEOUT, password, database
        );
    }

    public static LobbyCore getInstance() {
        return LobbyCore.plugin;
    }

    @Override
    public void onDisable() {
        if(this.redisPool != null) this.redisPool.close();
    }
}
