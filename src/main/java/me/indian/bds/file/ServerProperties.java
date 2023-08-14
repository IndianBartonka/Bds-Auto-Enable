package me.indian.bds.file;

import me.indian.bds.BDSAutoEnable;
import me.indian.bds.config.Config;
import me.indian.bds.logger.Logger;
import me.indian.bds.util.ConsoleColors;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ServerProperties {

    private final BDSAutoEnable bdsAutoEnable;
    private final Properties properties;
    private final Logger logger;
    private final Config config;

    public ServerProperties(final BDSAutoEnable bdsAutoEnable) {
        this.bdsAutoEnable = bdsAutoEnable;
        this.properties = new Properties();
        this.config = this.bdsAutoEnable.getConfig();
        this.logger = this.bdsAutoEnable.getLogger();
    }

    public void loadProperties() {
        try {
            final InputStream input = Files.newInputStream(Paths.get(this.config.getFilesPath() + "/server.properties"));
            this.properties.clear();
            this.properties.load(input);
        } catch (final IOException exception) {
            this.logger.critical(ConsoleColors.RED + "Wystąpił krytyczny błąd podczas ładowania " + ConsoleColors.GREEN + "server.properties" + ConsoleColors.RESET);
            throw new RuntimeException(exception);
        }
    }

    private void saveProperties() {
        try {
            this.properties.store(Files.newOutputStream(Paths.get(this.config.getFilesPath() + "/server.properties")), null);
        } catch (final IOException e) {
            this.logger.critical(ConsoleColors.RED + "Wystąpił krytyczny błąd podczas zapisywania " + ConsoleColors.GREEN + "server.properties" + ConsoleColors.RESET);
            this.bdsAutoEnable.getServerProcess().instantShutdown();
            throw new RuntimeException(e);
        }
    }

    public void reloadServerProperties() {
        this.saveProperties();
        this.loadProperties();
    }

    public String getWorldName() {
        return this.properties.getProperty("level-name");
    }

    public int getServerPort() {
        return Integer.parseInt(this.properties.getProperty("server-port"));
    }

    public int getServerPortV6() {
        return Integer.parseInt(this.properties.getProperty("server-portv6"));
    }

    public int getMaxThreads() {
        return Integer.parseInt(this.properties.getProperty("max-threads"));
    }

    public boolean isClientSideChunkGeneration() {
        return Boolean.parseBoolean(this.properties.getProperty("client-side-chunk-generation-enabled"));
    }

    public boolean isAllowList() {
        return Boolean.parseBoolean(this.properties.getProperty("allow-list"));
    }

    public void setServerPort(final int port) {
        this.properties.setProperty("server-port", String.valueOf(port));
        this.reloadServerProperties();
    }

    public void setServerPortV6(final int port) {
        this.properties.setProperty("server-portv6", String.valueOf(port));
        this.reloadServerProperties();
    }

    public void setMaxThreads(final int threads) {
        this.properties.setProperty("max-threads", String.valueOf(threads));
        this.reloadServerProperties();
    }

    public void setClientSideChunkGeneration(final boolean clientSide) {
        this.properties.setProperty("client-side-chunk-generation-enabled", String.valueOf(clientSide));
        this.reloadServerProperties();
    }

    public void setAllowList(final boolean allowList) {
        this.properties.setProperty("allow-list", String.valueOf(allowList));
        this.reloadServerProperties();
    }

    public Properties getProperties() {
        return this.properties;
    }
}