package me.drizzy.practice;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import me.allen.ziggurat.Ziggurat;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.ArenaTypeAdapter;
import me.drizzy.practice.arena.ArenaTypeTypeAdapter;
import me.drizzy.practice.essentials.Essentials;
import me.drizzy.practice.divisions.Divisions;
import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.events.types.brackets.BracketsManager;
import me.drizzy.practice.events.types.gulag.GulagManager;
import me.drizzy.practice.events.types.lms.LMSManager;
import me.drizzy.practice.events.types.oitc.OITCManager;
import me.drizzy.practice.events.types.parkour.ParkourManager;
import me.drizzy.practice.events.types.spleef.SpleefManager;
import me.drizzy.practice.events.types.sumo.SumoManager;
import me.drizzy.practice.hcf.HCFManager;
import me.drizzy.practice.hcf.bard.EffectRestorer;
import me.drizzy.practice.leaderboards.external.LeaderboardPlaceholders;
import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.kit.KitTypeAdapter;
import me.drizzy.practice.nametags.NametagHandler;
import me.drizzy.practice.nametags.provider.NametagEngine;
import me.drizzy.practice.nms.NMSManager;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.rank.Rank;
import me.drizzy.practice.profile.rank.RankType;
import me.drizzy.practice.profile.rank.apis.DefaultProvider;
import me.drizzy.practice.queue.QueueThread;
import me.drizzy.practice.register.RegisterCommands;
import me.drizzy.practice.register.RegisterListeners;
import me.drizzy.practice.scoreboard.Scoreboard;
import me.drizzy.practice.tablist.Tab;
import me.drizzy.practice.tablist.TabManager;
import me.drizzy.practice.util.other.Description;
import me.drizzy.practice.util.other.EntityHider;
import me.drizzy.practice.util.inventory.InventoryUtil;
import me.drizzy.practice.util.other.TaskUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.Honcho;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.duration.Duration;
import me.drizzy.practice.util.duration.DurationTypeAdapter;
import me.drizzy.practice.util.scoreboard.Aether;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
public class Array extends JavaPlugin {

    private static Array instance;

    /*
     * All ours Configs
     */
    private BasicConfigurationFile mainConfig, arenasConfig, kitsConfig, eventsConfig,
            messagesConfig, scoreboardConfig, tablistConfig, divisionsConfig, hotbarConfig;

    /*
     * All ours Async Threads
     */
    public Executor mainThread;
    public Executor taskThread;
    public Executor mongoThread;

    /*
     * Manager for ranks from APIs
     */
    @Setter private RankType rankManager;
    private Kit hcfKit;
    private Hotbar hotbar;

    /*
     * Mongo Database
     */
    private MongoDatabase mongoDatabase;

    /*
     * Tab and Scoreboard Adapters
     */
    private Aether scoreboard;
    private Ziggurat tab;
    private NametagHandler nametag;

    /*
     * All Event Managers
     */
    private SumoManager sumoManager;
    private BracketsManager bracketsManager;
    private LMSManager LMSManager;
    private ParkourManager parkourManager;
    private SpleefManager spleefManager;
    private OITCManager OITCManager;
    private GulagManager gulagManager;

    /*
     * Custom Divisions Handler
     */
    private Divisions divisionsManager;

    /*
     * Miscellaneous Managers
     */
    private NMSManager NMSManager;
    private TabManager tabManager;
    private HCFManager HCFManager;
    private EffectRestorer effectRestorer;

    /*
     * Essential Utilities
     */
    private Essentials essentials;
    @Getter private static Honcho honcho;
    public static Random random;
    private EntityHider entityHider;
    private boolean disabling = false;

    public static Array getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        random = new Random();
        honcho = new Honcho(this);

        /*
         * Async Executor Threads
         */
        this.mainThread = Executors.newFixedThreadPool(1);
        this.mongoThread = Executors.newFixedThreadPool(1);
        this.taskThread = Executors.newFixedThreadPool(1);

        /*
         * Main Configs
         */
        mainConfig = new BasicConfigurationFile(this, "config");
        arenasConfig = new BasicConfigurationFile(this, "arenas");
        kitsConfig = new BasicConfigurationFile(this, "kits");
        eventsConfig = new BasicConfigurationFile(this, "events");
        hotbarConfig = new BasicConfigurationFile(this, "hotbar");
        messagesConfig = new BasicConfigurationFile(this, "lang");
        tablistConfig = new BasicConfigurationFile(this, "tablist");
        scoreboardConfig = new BasicConfigurationFile(this, "scoreboard");
        divisionsConfig = new BasicConfigurationFile(this, "divisions");

        this.loadMessages();

        essentials = new Essentials();
        essentials.load();

        tabManager = new TabManager();
        tabManager.load();

        //To Prevent Stealing and Renaming (Skidding)
        if (!Description.getAuthor().contains("Drizzy")) {
            logger(CC.CHAT_BAR);
            logger("&cYou edited the plugin.yml, please don't do that");
            logger("&cPlease check your plugin.yml and try again.");
            logger("            &cDisabling Array");
            logger(CC.CHAT_BAR);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //To Prevent Stealing and Renaming (Skidding)
        if (!Description.getName().contains("Array")) {
            logger(CC.CHAT_BAR);
            logger("&cYou edited the plugin.yml, please don't do that");
            logger(" &cPlease check your plugin.yml and try again.");
            logger("            &cDisabling Array");
            logger(CC.CHAT_BAR);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.mainThread.execute(() -> {
            registerAll();
            Array.logger("&7Registering Commands...");
            TaskUtil.runLater(RegisterCommands::register, 5L);

            divisionsManager = new Divisions();

            sumoManager = new SumoManager();
            bracketsManager = new BracketsManager();
            LMSManager = new LMSManager();
            parkourManager = new ParkourManager();
            spleefManager = new SpleefManager();
            gulagManager = new GulagManager();
            OITCManager = new OITCManager();

            if (essentials.getMeta().isCoreHookEnabled()) {
                new Rank();
            } else {
                setRankManager(new DefaultProvider());
            }

            this.entityHider = EntityHider.enable();
            this.effectRestorer = new EffectRestorer(this);
            this.HCFManager = new HCFManager(this);

            if (essentials.getMeta().isHCFEnabled()) {
                //Create HCF's Duel Kit
                this.hcfKit = new Kit("HCFTeamFight");
                this.hcfKit.setDisplayIcon(new ItemBuilder(Material.BEACON).clearEnchantments().clearFlags().build());
                this.hcfKit.save();
            }

            Arrays.asList(Material.WORKBENCH,
                    Material.STICK,
                    Material.WOOD_PLATE,
                    Material.WOOD_BUTTON,
                    Material.SNOW_BLOCK
            ).forEach(InventoryUtil::removeCrafting);

            for ( World world : this.getServer().getWorlds() ) {
                world.setDifficulty(Difficulty.EASY);
            }
            //Register Essentials and Listeners
            RegisterListeners.register();
            this.registerEssentials();

        });
    }

    @Override
    public void onDisable() {
        mainThread.execute(() -> {
            //Stop all matches and Remove the placed Block
            Match.cleanup();
            //Save Everything before disabling to prevent data loss
            Kit.getKits().forEach(Kit::save);
            Arena.getArenas().forEach(Arena::save);
            Profile.getProfiles().values().forEach(Profile::save);
            //Save our Values to Config
            getTabManager().save();
            getEssentials().save();
            getHotbar().save();
            //Save our Event Setup
            getBracketsManager().save();
            getLMSManager().save();
            getSumoManager().save();
            getParkourManager().save();
            getGulagManager().save();
            //Clear out the PlayerList for Vanilla Tab
            Profile.getPlayerList().clear();
        });
        disabling = true;
    }

    private void registerAll() {
        try {
            preLoadMongo();
        } catch (Exception e) {
            logger(CC.CHAT_BAR);
            logger("            &4&lMongo Internal Error");
            logger("        &cMongo is not setup correctly!");
            logger(     "&cPlease check your mongo and try again.");
            logger("              &4&lDisabling Array");
            logger(CC.CHAT_BAR);
            this.shutDown();
            return;
        }

        logger("&7Loading Profiles!");
        Profile.preload();
        logger("&aLoaded Profiles!");

        try {
            logger("&7Loading Kits!");
            Kit.preload();
            logger("&aLoaded Kits!");
        } catch (YAMLException e) {
            logger(CC.CHAT_BAR);
            logger("       &cError Loading Kits: &cYAML Error");
            logger("    &cThis means your configuration was wrong.");
            logger("  &cPlease check your Kits config and try again!");
            logger("               &4&lDisabling Array");
            logger(CC.CHAT_BAR);
            this.shutDown();
            return;
        }
        try {
            logger("&7Loading Arenas!");
            Arena.preload();
        } catch (YAMLException e) {
            logger(CC.CHAT_BAR);
            logger("      &cError Loading Kits: &cYAML Error");
            logger("   &cThis means your configuration was wrong.");
            logger(" &cPlease check your Arenas config and try again!");
            logger("              &4&lDisabling Array");
            logger(CC.CHAT_BAR);
            this.shutDown();
            return;
        }

        hotbar = new Hotbar();
        Hotbar.preload();
        Match.preload();
        Party.preload();
        TaskUtil.runAsync(() -> NMSManager = new NMSManager());

        honcho.registerTypeAdapter(Arena.class, new ArenaTypeAdapter());
        honcho.registerTypeAdapter(ArenaType.class, new ArenaTypeTypeAdapter());
        honcho.registerTypeAdapter(Kit.class, new KitTypeAdapter());
        honcho.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
    }

    private void registerEssentials() {

        this.scoreboard = new Aether(this, new Scoreboard());
        this.scoreboard.getOptions().hook(true);
        logger("&7Setting up Scoreboard");

        if (essentials.getMeta().isTabEnabled()) {
            logger("&7Setting up Tablist");
            this.tab = new Ziggurat(this, new Tab());
        }

        this.nametag = new NametagHandler();
        this.nametag.preLoad();
        this.nametag.setEngine(new NametagEngine());
        logger("&7Setting up Nametags");

        new QueueThread().start();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new LeaderboardPlaceholders().register();
            logger("&7Found PlaceholderAPI, Registering Expansions....");
        } else {
            logger("&cPlaceholderAPI was NOT found, Holograms will NOT work!");
        }

        Profile.loadGlobalLeaderboards();
    }

    public static void logger(String message) {
        String msg = CC.translate("&8[&cArray&8] &r" + message);
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    private void preLoadMongo() {
        if (mainConfig.getBoolean("MONGO.URI-MODE")) {
            MongoClient client = new MongoClient(new MongoClientURI(mainConfig.getString("MONGO.URI.CONNECTION_STRING")));
            this.mongoDatabase = client.getDatabase(mainConfig.getString("MONGO.URI.DATABASE"));
        } else {
            MongoClient client;
            if (mainConfig.getBoolean("MONGO.NORMAL.AUTHENTICATION.ENABLED")) {
                MongoCredential credential = MongoCredential.createCredential(
                        mainConfig.getString("MONGO.NORMAL.AUTHENTICATION.USERNAME"),
                        mainConfig.getString("MONGO.NORMAL.DATABASE"),
                        mainConfig.getString("MONGO.NORMAL.AUTHENTICATION.PASSWORD").toCharArray()
                );

                client = new MongoClient(new ServerAddress(mainConfig.getString("MONGO.NORMAL.HOST"),
                        mainConfig.getInteger("MONGO.NORMAL.PORT")), Collections.singletonList(credential));
            } else {
                client = new MongoClient(mainConfig.getString("MONGO.NORMAL.HOST"),
                        mainConfig.getInteger("MONGO.NORMAL.PORT"));
            }
            this.mongoDatabase = client.getDatabase(mainConfig.getString("MONGO.NORMAL.DATABASE"));
        }
    }

    public void shutDown() {
        this.onDisable();
        logger("Shutting down Array!");
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public void loadMessages() {

        if (this.messagesConfig == null) {
            return;
        }

        Arrays.stream(Locale.values()).forEach(language -> {
            if (this.messagesConfig.getConfiguration().getString(language.getPath()) == null || this.messagesConfig.getConfiguration().getStringList(language.getPath()) == null) {
                if (language.getListValue() != null) {
                    this.messagesConfig.getConfiguration().set(language.getPath(), language.getListValue());
                }
                if (language.getValue() != null) {
                    this.messagesConfig.getConfiguration().set(language.getPath(), language.getValue());
                }
            }
        });

        try {
            this.messagesConfig.getConfiguration().save(messagesConfig.getFile());
        } catch (Exception ignored) {}
    }
}
