package me.indian.bds.discord.jda.manager;


import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import me.indian.bds.BDSAutoEnable;
import me.indian.bds.Defaults;
import me.indian.bds.config.AppConfigManager;
import me.indian.bds.discord.jda.DiscordJda;
import me.indian.bds.logger.Logger;
import me.indian.bds.util.GsonUtil;
import me.indian.bds.util.MathUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class LinkingManager {

    private final AppConfigManager appConfigManager;
    private final DiscordJda discordJda;
    private final Logger logger;
    private final File linkedAccountsJson;
    private final HashMap<String, Long> linkedAccounts;
    private final HashMap<String, String> accountsToLink;

    public LinkingManager(final BDSAutoEnable bdsAutoEnable, final DiscordJda discordJda) {
        this.appConfigManager = bdsAutoEnable.getAppConfigManager();
        this.discordJda = discordJda;
        this.logger = bdsAutoEnable.getLogger();
        this.linkedAccountsJson = new File(Defaults.getAppDir() + "linkedAccounts.json");
        this.createJson();
        this.linkedAccounts = this.loadLinkedAccounts();
        this.accountsToLink = new HashMap<>();

        this.startTasks();
    }

    public boolean isLinked(final String name) {
        return this.linkedAccounts.containsKey(name);
    }

    public boolean isLinked(final long id) {
        return this.linkedAccounts.containsValue(id);
    }

    public boolean linkAccount(final String code, final long id) {
        final String name = this.getNameToLinkByCode(code);
        if (name == null) return false;

        if (this.accountsToLink.get(name).equals(code)) {
            this.accountsToLink.remove(name);
            this.linkedAccounts.put(name, id);
            this.doForLinked();
            this.saveLinedAccounts();
            return true;
        }

        return false;
    }

    public void unLinkAccount(final String name) {
        if (this.isLinked(name)) {
            this.linkedAccounts.remove(name);
        }
    }

    public void unLinkAccount(final long id) {
        if (this.isLinked(id)) {
            this.linkedAccounts.remove(this.getNameByID(id), id);
        }
    }

    public void addAccountToLink(final String name, final String code) {
        if (this.isLinked(name)) return;
        this.accountsToLink.put(name, code);
    }

    public String getNameToLinkByCode(final String code) {
        for (final Map.Entry<String, String> map : this.accountsToLink.entrySet()) {
            if (Objects.equals(map.getValue(), code)) return map.getKey();
        }
        return null;
    }

    public String getNameByID(final long id) {
        for (final Map.Entry<String, Long> map : this.linkedAccounts.entrySet()) {
            if (Objects.equals(map.getValue(), id)) return map.getKey();
        }
        return null;
    }

    public long getIdByName(final String name) {
        return this.linkedAccounts.get(name);
    }

    private void startTasks() {
        final long saveTime = MathUtil.minutesTo(30, TimeUnit.MILLISECONDS);
        final long forLinkedTime = MathUtil.minutesTo(1, TimeUnit.MILLISECONDS);
        final Timer timer = new Timer("LinkedAccountsTimer", true);

        final TimerTask saveAccountsTimer = new TimerTask() {
            @Override
            public void run() {
                LinkingManager.this.saveLinedAccounts();
            }
        };

        final TimerTask doForLinkedTask = new TimerTask() {
            @Override
            public void run() {
                LinkingManager.this.doForLinked();
            }
        };

        timer.scheduleAtFixedRate(saveAccountsTimer, saveTime, saveTime);
        timer.scheduleAtFixedRate(doForLinkedTask, forLinkedTime, forLinkedTime);
    }

    public void saveLinedAccounts() {
        try (final FileWriter writer = new FileWriter(this.linkedAccountsJson)) {
            writer.write(GsonUtil.getGson().toJson(this.linkedAccounts));
            this.logger.info("Pomyślnie zapisano&b połączone konta z discord");
        } catch (final Exception exception) {
            this.logger.critical("Nie udało się zapisać&b połączonych kont z discord", exception);
        }
    }

    private HashMap<String, Long> loadLinkedAccounts() {
        try (final FileReader reader = new FileReader(this.linkedAccountsJson)) {
            final Type type = new TypeToken<HashMap<String, Long>>() {
            }.getType();
            final HashMap<String, Long> loadedMap = GsonUtil.getGson().fromJson(reader, type);
            return (loadedMap == null ? new HashMap<>() : loadedMap);
        } catch (final Exception exception) {
            this.logger.critical("Nie udało się załadować&b połączonych kont z discord", exception);
        }
        return new HashMap<>();
    }

    private void doForLinked() {
        for (final Map.Entry<String, Long> map : this.linkedAccounts.entrySet()) {
            final long id = map.getValue();
            final Guild guild = this.discordJda.getGuild();
            final Member member = guild.getMemberById(id);

            if (member == null) continue;
            if (guild.getSelfMember().canInteract(member)) member.modifyNickname(this.getNameByID(id)).queue();

            final Role role = guild.getRoleById(this.appConfigManager.getDiscordConfig()
                    .getDiscordBotConfig().getLinkedRoleID());

            if (role == null) continue;
            if (guild.getSelfMember().canInteract(role)) guild.addRoleToMember(member, role).queue();

        }
    }

    private void createJson() {
        if (!this.linkedAccountsJson.exists()) {
            try {
                if (!this.linkedAccountsJson.createNewFile()) {
                    this.logger.critical("Nie można utworzyć&b linkedAccounts.json");
                }
            } catch (final Exception exception) {
                this.logger.critical("Nie udało się utworzyć&b linkedAccounts.json", exception);
            }
        }
    }
}