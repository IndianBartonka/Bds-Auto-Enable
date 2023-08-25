package me.indian.bds.config.sub.log;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Log extends OkaeriConfig {

    @Comment({""})
    @Comment({"Nie zapisuje tych informacj które zawierają:"})
    @Comment({"W pliku"})
    private List<String> noFile = Arrays.asList("[Json]", "[Blocks]", "[Components]", "[Molang]",
            "[Item]", "[Recipes]", "[FeatureRegistry]", "[Actor]",
            " ERROR]", " WARN]",
            "\"component_groups\"");
    @Comment({""})
    @Comment("W konsoli")
    private List<String> noConsole = Arrays.asList("[Json]", "[Blocks]", "[Components]", "[Molang]",
            "[Item]", "[Recipes]", "[FeatureRegistry]", "[Actor]",
            "\"component_groups\"");

    @Comment({""})
    @Comment("W konsoli discord")
    @Comment("UWAGA!!! Jeśli używasz 1gb ram dla aplikacij (co jest w miare optymalne) nie usuwaj nic z tąd albo daj wiecej ram!!!!")
    private List<String> noDiscordConsole = new ArrayList<>();

    public Log() {
        this.noDiscordConsole.addAll(this.noConsole);
    }

    public List<String> getNoFile() {
        return this.noFile;
    }

    public List<String> getNoConsole() {
        return this.noConsole;
    }

    public List<String> getNoDiscordConsole() {
        return this.noDiscordConsole;
    }
}