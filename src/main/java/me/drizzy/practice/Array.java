package me.drizzy.practice;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.adapters.NameTagAdapter;
import me.drizzy.practice.adapters.ScoreboardAdapter;
import me.drizzy.practice.adapters.TablistAdapter;
import me.drizzy.practice.api.API;
import me.drizzy.practice.api.ArrayAPI;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.ArenaProvider;
import me.drizzy.practice.arena.ArenaTypeProvider;
import me.drizzy.practice.clan.Clan;
import me.drizzy.practice.essentials.Essentials;
import me.drizzy.practice.profile.divisions.Divisions;
import me.drizzy.practice.arena.ArenaType;
import me.drizzy.practice.events.types.brackets.BracketsManager;
import me.drizzy.practice.events.types.gulag.GulagManager;
import me.drizzy.practice.events.types.lms.LMSManager;
import me.drizzy.practice.events.types.parkour.ParkourManager;
import me.drizzy.practice.events.types.spleef.SpleefManager;
import me.drizzy.practice.events.types.sumo.SumoManager;
import me.drizzy.practice.kit.KitProvider;
import me.drizzy.practice.managers.ClassManager;
import me.drizzy.practice.pvpclasses.bard.EffectRestorer;
import me.drizzy.practice.leaderboards.external.LeaderboardPlaceholders;
import me.drizzy.practice.profile.hotbar.Hotbar;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.managers.CMDManager;
import me.drizzy.practice.managers.ListenersManager;
import me.drizzy.practice.hook.SpigotHook;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileProvider;
import me.drizzy.practice.profile.rank.Rank;
import me.drizzy.practice.profile.rank.RankType;
import me.drizzy.practice.profile.rank.apis.DefaultProvider;
import me.drizzy.practice.queue.Queue;

import me.drizzy.practice.managers.TabManager;
import me.drizzy.practice.util.command.CommandService;
import me.drizzy.practice.util.command.Drink;
import me.drizzy.practice.util.nametags.NameTagHandler;
import me.drizzy.practice.util.other.Description;
import me.drizzy.practice.util.other.EntityHider;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.scoreboard.Assemble;
import me.drizzy.practice.util.scoreboard.AssembleStyle;
import me.drizzy.practice.util.tablist.TablistHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 2/13/2021
 * Project: Array
 */

@Getter @Setter
public class Array extends JavaPlugin {

    @Getter public static CommandService drink;
    @Getter public static Array instance;
    @Getter public static API api;

    /*
     * All ours Configs
     */
    private BasicConfigurationFile mainConfig, arenasConfig, kitsConfig, eventsConfig, menuConfig,
            messagesConfig, scoreboardConfig, tablistConfig, divisionsConfig, hotbarConfig, rateConfig;//, brawlConfig;

    /*
     * All ours Async Threads
     */
    public Executor mainThread;
    public Executor taskThread;
    public Executor mongoThread;

    /*
     * Manager for ranks from APIs
     */
    private RankType rankManager;
    private Hotbar hotbar;

    /*
     * Mongo Database
     */
    private MongoDatabase mongoDatabase;

    /*
     * Tab and Scoreboard Adapters
     */
    private Assemble scoreboardHandler;
    private TablistHandler tablistHandler;
    private NameTagHandler nameTagHandler;

    /*
     * All Event Managers
     */
    private SumoManager sumoManager;
    private BracketsManager bracketsManager;
    private LMSManager LMSManager;
    private ParkourManager parkourManager;
    private SpleefManager spleefManager;
    private GulagManager gulagManager;

    /*
     * Custom Divisions Handler
     */
    private Divisions divisionsManager;

    /*
     * Miscellaneous Managers
     */
    private TabManager tabManager;
    private ClassManager ClassManager;
    private EffectRestorer effectRestorer;

    /*
     * Essential Utilities
     */
    public static Random random;
    private Essentials essentials;
    private EntityHider entityHider;
    private boolean disabling = false;

    @Override
    public void onEnable() {
        instance = this;
        random = new Random();
        api = new ArrayAPI();
        drink = Drink.get(this);

        //Experimenting
        System.setProperty("file.encoding", "UTF-8");

        /*
         * Async Executor Threads
         */
        this.mainThread = Executors.newSingleThreadExecutor();
        this.mongoThread = Executors.newSingleThreadExecutor();
        this.taskThread = Executors.newSingleThreadExecutor();

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
        menuConfig = new BasicConfigurationFile(this, "menus");
        rateConfig = new BasicConfigurationFile(this, "ratings");
        //brawlConfig = new BasicConfigurationFile(this, "brawl");

        this.loadMessages();

        mainConfig.getConfiguration().options().header(
                "######################################################################\n" +
                "                                                                     #\n" +
                "          Array Practice Core - Developed By Drizzy#0278             #\n" +
                "     Bought at Purge Development - https://discord.gg/VXzUMfBefZ     #\n" +
                "                                                                     #\n" +
                "######################################################################");
        mainConfig.save();

        essentials = new Essentials();
        tabManager = new TabManager();

        //To Prevent Stealing and Renaming (Skidding)
        if (!Description.getAuthor().contains("Drizzy") || !Description.getName().contains("Array")) {
            logger(CC.CHAT_BAR);
            logger("&cYou edited the plugin.yml, haha get caught in 4k");
            logger("&cPlease check your plugin.yml and try again.");
            logger("            &cDisabling Array");
            logger(CC.CHAT_BAR);
            shutDown();
            return;
        }

        this.preload();

        divisionsManager = new Divisions();
        sumoManager = new SumoManager();
        bracketsManager = new BracketsManager();
        LMSManager = new LMSManager();
        parkourManager = new ParkourManager();
        spleefManager = new SpleefManager();
        gulagManager = new GulagManager();

        logger("&7Registering Commands...");
        CMDManager CMDManager = new CMDManager();
        CMDManager.registerCommands();

        logger("&7Registering Listeners....");
        ListenersManager listenersManager = new ListenersManager();
        listenersManager.registerListeners();

        if (essentials.getMeta().isCoreHookEnabled()) {
            Rank.preLoad();
        } else {
            rankManager = new DefaultProvider();
        }

        this.entityHider = EntityHider.enable();
        this.effectRestorer = new EffectRestorer(this);
        this.ClassManager= new ClassManager(this);
        this.preloadAdapters();
    }

    @Override
    public void onDisable() {
        //Stop all matches and Remove the placed Block
        Match.cleanup();
        //Save Everything before disabling to prevent data loss
        Kit.getKits().forEach(Kit::save);
        Arena.getArenas().forEach(Arena::save);
        Profile.getProfiles().values().forEach(Profile::save);
        Clan.getClans().forEach(Clan::save);
        getClassManager().onDisable();
        //Save our Values to Config
        getTabManager().save();
        getEssentials().save();
        //Save our Event Setup
        getBracketsManager().save();
        getLMSManager().save();
        getSumoManager().save();
        getParkourManager().save();
        getGulagManager().save();
        //Clear out the PlayerList for Vanilla Tab
        Profile.getPlayerList().clear();
        disabling=true;
    }

    private void preload() {
        try {
            preLoadMongo();
        } catch (Exception e) {
            logger("&cAn Error occured while loading Mongo, please check your mongo configuration and try again.");
            this.shutDown();
            return;
        }

        logger("&7Loading Profiles!");
        Profile.preload();
        logger("&aLoaded Profiles!");

        logger("&7Loading Clans!");
        Clan.preload();
        logger("&aLoaded Clans!");

        try {
            logger("&7Loading Kits!");
            Kit.preload();
            logger("&aLoaded Kits!");
        } catch (YAMLException e) {
            logger("&cAn Error occured while loading Kits, please check kits.yml and try again.");
            this.shutDown();
            return;
        }

        try {
            logger("&7Loading Arenas!");
            Arena.preload();
        } catch (YAMLException e) {
            logger("&cAn Error occured while loading Arenas, please check arenas.yml and try again.");
            this.shutDown();
            return;
        }

        Hotbar.preload();
        Match.preload();
        Party.preload();
        Queue.preLoad();
        SpigotHook.preload();

        drink.bind(Arena.class).toProvider(new ArenaProvider());
        drink.bind(ArenaType.class).toProvider(new ArenaTypeProvider());
        drink.bind(Kit.class).toProvider(new KitProvider());
        drink.bind(Profile.class).toProvider(new ProfileProvider());
    }

    private void preloadAdapters() {

        logger("&7Setting up Scoreboard");
        this.scoreboardHandler = new Assemble(this, new ScoreboardAdapter());
        this.scoreboardHandler.setAssembleStyle(AssembleStyle.MODERN);
        this.scoreboardHandler.setTicks(2);

        logger("&7Setting up NameTags");
        this.nameTagHandler = new NameTagHandler();
        this.nameTagHandler.hook();
        this.nameTagHandler.registerProvider(new NameTagAdapter());

        if (Essentials.getMeta().isTabEnabled()) {
            logger("&7Setting up TablistAdapter");
            this.tablistHandler = new TablistHandler(new TablistAdapter(), this, 20);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new LeaderboardPlaceholders().register();
            logger("&7Found PlaceholderAPI, Registering Expansions....");
        } else {
            logger("&cPlaceholderAPI was NOT found, Holograms will NOT work!");
        }
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
        messagesConfig.save();
    }

    public void shutDown() {
        logger("Shutting down Array!");
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public static void logger(String message) {
        String msg = CC.translate("&8[&cArray&8] &r" + message);
        Bukkit.getConsoleSender().sendMessage(msg);
    }
}
