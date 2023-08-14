package me.indian.bds;

import me.indian.bds.config.Config;
import me.indian.bds.file.ServerProperties;
import me.indian.bds.logger.Logger;
import me.indian.bds.util.ScannerUtil;
import me.indian.bds.util.SystemOs;
import me.indian.bds.util.ThreadUtil;

import java.util.Arrays;
import java.util.Scanner;

public class Settings {

    private final BDSAutoEnable bdsAutoEnable;
    private final ServerProperties serverProperties;
    private final Logger logger;
    private final Config config;
    private final String enter;

    public Settings(final BDSAutoEnable bdsAutoEnable) {
        this.bdsAutoEnable = bdsAutoEnable;
        this.serverProperties = this.bdsAutoEnable.getServerProperties();
        this.logger = this.bdsAutoEnable.getLogger();
        this.config = this.bdsAutoEnable.getConfig();
        this.enter = "[Enter = Domyślnie]";
    }

    public void loadSettings(final Scanner scanner) {
        final ScannerUtil scannerUtil = new ScannerUtil(scanner);
        if (!this.config.isFirstRun()) {
            scannerUtil.addQuestion((defaultValue) -> this.logger.info("Zastosować wcześnejsze ustawienia? (true/false) (Enter = true) "), true, (settings) -> {
                if (Boolean.parseBoolean(settings)) {
                    this.serverProperties.loadProperties();
                    this.currentSettings(scanner);
                } else {
                    this.logger.info("Zaczynamy od nowa");
                    this.init(scannerUtil);
                }
            });
        } else {
            this.init(scannerUtil);
        }
    }

    private void init(final ScannerUtil scannerUtil) {
        final long startTime = System.currentTimeMillis();
        SystemOs system;
        try {
            system = SystemOs.valueOf(scannerUtil.addQuestion((defaultValue) -> {
                        this.logger.info("&lPodaj system&r (Wykryty system: " + defaultValue + "): " + this.enter);
                        this.logger.info("Obsługiwane systemy: " + Arrays.toString(SystemOs.values()));
                    }, Defaults.getSystem(),
                    (input) -> this.logger.info("System ustawiony na:&1 " + input.toUpperCase())
            ).toUpperCase());
        } catch (final IllegalArgumentException exception) {
            this.logger.error("Podano nie znany system , ustawiono domyślnie na: LINUX");
            system = SystemOs.LINUX;
        }
        this.config.setSystemOs(system);
        this.config.setFileName(scannerUtil.addQuestion((defaultValue) -> this.logger.info("&lPodaj nazwę pliku&r (Domyślnie: " + defaultValue + "): " + this.enter), Defaults.getDefaultFileName(), (input) -> {
            this.logger.info("Nazwa pliku ustawiona na:&1 " + input);
            if (this.config.getSystemOs() == SystemOs.LINUX) {
                if (input.contains(".exe")) {
                    this.logger.alert("&lW tym wypadku będzie potrzebne&r&n&bWINE ");
                    this.config.setWine(true);
                } else {
                    this.config.setWine(false);
                }
            }
        }));

        this.config.setFilesPath(scannerUtil.addQuestion((defaultValue) -> this.logger.info("&lPodaj ścieżkę do plików servera&r  (Domyślnie: " + defaultValue + "): " + this.enter), Defaults.getJarPath(), (input) -> this.logger.info("Ścieżke do plików servera ustawiona na: " + input)));
        this.config.save();

        if (this.config.isLoaded())
            scannerUtil.addQuestion((defaultValue) -> this.logger.info("&lZaładować jakąś inną versie&r (Domyślnie: " + defaultValue + "): " + this.enter),
                    false,
                    (input) -> {
                        if (Boolean.parseBoolean(input)) {
                            this.config.setLoaded(false);
                            this.config.save();
                        }
                    });

        if (!this.config.isLoaded()) this.versionQuestion(scannerUtil);
        this.serverProperties.loadProperties();

        final boolean backup = scannerUtil.addQuestion((defaultValue) -> this.logger.info("&lWłączyć Backupy&r (Domyślnie: " + defaultValue + ")? " + this.enter), true, (input) -> this.logger.info("Backupy ustawione na:&1 " + input));
        this.config.setBackup(backup);
        if (backup) {
            final int backupFrequency = scannerUtil.addQuestion((defaultValue) -> this.logger.info("&lCo ile minut robic backup?&r (Domyślnie: " + defaultValue + ")? " + this.enter), 60, (input) -> this.logger.info("Backup bedzie robiony co:&1 " + (Integer.parseInt(input) == 0 ? 60 : Integer.parseInt(input) + "&a minut")));
            this.config.setBackupFrequency(backupFrequency == 0 ? 60 : backupFrequency);
        }

        this.logger.info("&aKonfiguracija servera&r");

        this.serverProperties.setServerPort(scannerUtil.addQuestion((defaultValue) -> {
            this.logger.info("&lUstaw port v4?&r (Aktualny z &bserver.properties&r to: " + defaultValue + ")" + this.enter);
            this.logger.info("#cPamiętaj że twoja siec musi miec dostepny ten port");
        }, this.serverProperties.getServerPort(), (input) -> this.logger.info("Port v4 ustawiony na:&1 " + input)));

        this.serverProperties.setServerPortV6(scannerUtil.addQuestion((defaultValue) -> {
            this.logger.info("&lUstaw port v6?&r (Aktualny z &bserver.properties&r to: " + defaultValue + ")" + this.enter);
            this.logger.info("#cJeśli twoja maszyna obsługuje ipv6 ustaw go na dostepny z puli portów");
        }, this.serverProperties.getServerPortV6(), (input) -> this.logger.info("Port v6 ustawiony na:&1 " + input)));

        final int threads = scannerUtil.addQuestion((defaultValue) -> {
            this.logger.info("&lLiczba wątków używana przez server&r (Dostępna liczba wątków to około " + ThreadUtil.getThreadsCount() + ") ");
            this.logger.info("Maksymalna liczba wątków, jakie serwer będzie próbował wykorzystać, Jeśli ustawione na&b 0&r wtedy będzie używać najwięcej jak to możliwe.");
        }, 0, (input) -> this.logger.info("Liczba wątków ustawiona na:&1 " + (Integer.parseInt(input) <= -1 ? 0 : input)));

        this.serverProperties.setMaxThreads(threads <= -1 ? 0 : threads);
        this.serverProperties.setClientSideChunkGeneration(scannerUtil.addQuestion(
                (defaultValue) -> {
                    this.logger.info("&lClient Side Chunks&r (Domyślnie: " + defaultValue + ")? " + this.enter);
                    this.logger.info("Jeśli jest &1true&r serwer poinformuje klientów, " + "że mają możliwość generowania chunków poziomu wizualnego poza odległościami interakcji graczy. ");
                },
                true, (input) -> this.logger.info("Ustawiono Client Side Chunks na:&1 " + input)
        ));

        this.logger.info("Ukończono odpowiedzi w&a " + ((System.currentTimeMillis() - startTime) / 1000.0) + "&r sekund");
        this.config.save();
        this.currentSettings(scannerUtil.getScanner());
    }

    private void currentSettings(final Scanner scanner) {
        System.out.println();
        this.logger.info("Aktualne informacije");
        this.logger.info("System:&1 " + this.config.getSystemOs());
        this.logger.info("Nazwa pliku:&1 " + this.config.getFileName());
        this.logger.info("Wine:&1 " + this.config.isWine());
        this.logger.info("Ścieżka plików: " + this.config.getFilesPath());
        this.logger.info("Wersija:&1 " + this.config.getVersion());

        boolean backup = this.config.isBackup();
        this.logger.info("Backup:&1 " + backup);

        if (backup) {
            this.logger.info("Częstotliwość robienia backup:&1 " + this.config.getBackupFrequency() + "&a minut");
            this.logger.info("Nazwa świata:&1 " + this.serverProperties.getWorldName());
        }

        this.logger.info("Port v4:&1 " + this.serverProperties.getServerPort());
        this.logger.info("Port v6:&1 " + this.serverProperties.getServerPortV6());
        this.logger.info("Liczba wątków używana przez server:&1 " + this.serverProperties.getMaxThreads());
        this.logger.info("Czy klient generuje chunki:&1 " + this.serverProperties.isClientSideChunkGeneration());

        this.logger.info("Kliknij enter aby kontunować");
        scanner.nextLine();

        if (this.config.isFirstRun()) {
            this.config.setFirstRun(false);
        }
        this.config.save();
    }

    private void versionQuestion(final ScannerUtil scannerUtil) {
        this.config.setVersion(scannerUtil.addQuestion(
                (defaultValue) -> {
                    this.logger.info("&lJaką versie załadować?&r (Domyślnie: " + defaultValue + "): " + this.enter);
                    if (this.bdsAutoEnable.getVersionManager().getAvailableVersions().isEmpty()) {
                        this.logger.info("Nie znaleziono żadnej wersij");
                    } else {
                        this.logger.info("Pobrane versije: " + this.bdsAutoEnable.getVersionManager().getAvailableVersions());
                    }
                    this.logger.info("Aby pobrać jakaś versie wpisz jej numer (niektóre mogą mieć .01 / .02 na końcu)");
                },
                this.config.getVersion(), (input) -> this.logger.info("Wersia do załadowania ustawiona na:&1 " + input)
        ));
        this.bdsAutoEnable.getVersionManager().loadVersion();
    }
}