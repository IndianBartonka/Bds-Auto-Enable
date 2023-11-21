package me.indian.bds.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import java.io.File;
import me.indian.bds.Defaults;
import me.indian.bds.config.sub.AutoMessagesConfig;
import me.indian.bds.config.sub.discord.DiscordConfig;
import me.indian.bds.config.sub.log.LogConfig;
import me.indian.bds.config.sub.rest.RestApiConfig;
import me.indian.bds.config.sub.version.VersionManagerConfig;
import me.indian.bds.config.sub.watchdog.WatchDogConfig;

public class AppConfigManager {

    private final AppConfig appConfig;
    private final DiscordConfig discordConfig;
    private final LogConfig logConfig;
    private final RestApiConfig restApiConfig;
    private final VersionManagerConfig versionManagerConfig;
    private final WatchDogConfig watchDogConfig;
    private final AutoMessagesConfig autoMessagesConfig;

    public AppConfigManager() {
        final String appDir = Defaults.getAppDir() + File.separator + "config" + File.separator;

        this.appConfig = ConfigManager.create(AppConfig.class, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(appDir + "config.yml");
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        this.discordConfig = ConfigManager.create(DiscordConfig.class, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(appDir + "Discord.yml");
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        this.logConfig = ConfigManager.create(LogConfig.class, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(appDir + "Log.yml");
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        this.restApiConfig = ConfigManager.create(RestApiConfig.class, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(appDir + "RestApi.yml");
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        this.versionManagerConfig = ConfigManager.create(VersionManagerConfig.class, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(appDir + "VersionManager.yml");
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        this.watchDogConfig = ConfigManager.create(WatchDogConfig.class, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(appDir + "WatchDog.yml");
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        this.autoMessagesConfig = ConfigManager.create(AutoMessagesConfig.class, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(appDir + "AutoMessages.yml");
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }

    public void load() {
        this.appConfig.load();
        this.discordConfig.load();
        this.logConfig.load();
        this.restApiConfig.load();
        this.versionManagerConfig.load();
        this.watchDogConfig.load();
        this.autoMessagesConfig.load();
    }

    public void save() {
        this.appConfig.save();
        this.discordConfig.save();
        this.logConfig.save();
        this.restApiConfig.save();
        this.versionManagerConfig.save();
        this.watchDogConfig.save();
        this.autoMessagesConfig.save();
    }

    public AppConfig getConfig() {
        return this.appConfig;
    }

    public DiscordConfig getDiscordConfig() {
        return this.discordConfig;
    }

    public LogConfig getLogConfig() {
        return this.logConfig;
    }

    public RestApiConfig getRestApiConfig() {
        return this.restApiConfig;
    }

    public VersionManagerConfig getVersionManagerConfig() {
        return this.versionManagerConfig;
    }

    public WatchDogConfig getWatchDogConfig() {
        return this.watchDogConfig;
    }

    public AutoMessagesConfig getAutoMessagesConfig() {
        return this.autoMessagesConfig;
    }
}