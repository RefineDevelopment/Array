package xyz.refinedev.practice;

import com.google.gson.Gson;
import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.refinedev.practice.adapters.ScoreboardAdapter;
import xyz.refinedev.practice.adapters.TablistAdapter;
import xyz.refinedev.practice.api.API;
import xyz.refinedev.practice.api.ArrayAPI;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaProvider;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.arena.ArenaTypeProvider;
import xyz.refinedev.practice.arena.meta.RatingType;
import xyz.refinedev.practice.arena.meta.RatingTypeProvider;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.config.ConfigHandler;
import xyz.refinedev.practice.events.EventManager;
import xyz.refinedev.practice.events.EventProvider;
import xyz.refinedev.practice.events.EventType;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitProvider;
import xyz.refinedev.practice.leaderboards.external.LeaderboardPlaceholders;
import xyz.refinedev.practice.managers.*;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileProvider;
import xyz.refinedev.practice.profile.divisions.Divisions;
import xyz.refinedev.practice.profile.rank.Rank;
import xyz.refinedev.practice.pvpclasses.bard.EffectRestorer;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.CommandService;
import xyz.refinedev.practice.util.command.Drink;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;
import xyz.refinedev.practice.util.nametags.NameTagHandler;
import xyz.refinedev.practice.util.other.Description;
import xyz.refinedev.practice.util.other.EntityHider;
import xyz.refinedev.practice.util.scoreboard.ScoreboardHandler;
import xyz.refinedev.practice.util.scoreboard.AssembleStyle;
import xyz.refinedev.tablist.TablistHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 2/13/2021
 * Project: Array
 */

@Getter @Setter
public class Array extends JavaPlugin {

    @Getter private static CommandService drink;
    @Getter private static Array instance;
    @Getter private static API api;

    public static Gson GSON = new Gson();

    /*
     * All ours Configs
     */
    private BasicConfigurationFile mainConfig, arenasConfig, kitsConfig, eventsConfig, menuConfig, killeffectsConfig,
            messagesConfig, scoreboardConfig, tablistConfig, divisionsConfig, hotbarConfig, rateConfig;//, brawlConfig;

    /*
     * Mongo
     */
    private Executor mongoThread;
    private MongoDatabase mongoDatabase;

    /*
     * Handlers
     */
    private ScoreboardHandler scoreboardHandler;
    private ConfigHandler configHandler;
    private TablistHandler tablistHandler;
    private NameTagHandler nameTagHandler;

    /*
     * All Managers
     */
    private EventManager eventManager;
    private HotbarManager hotbarManager;
    private ListenersManager listenersManager;
    private CommandsManager commandsManager;
    private KnockbackManager knockbackManager;
    private KillEffectManager killEffectManager;
    private LeaderboardsManager leaderboardsManager;
    private Divisions divisionsManager;
    private RatingsManager ratingsManager;
    private ClassManager classManager;
    private EffectRestorer effectRestorer;

    /*
     * Essential Utilities
     */
    public static Random random;
    private EntityHider entityHider;
    private boolean disabling = false;

    @Override
    public void onLoad() {
        instance = this;

        mainConfig = new BasicConfigurationFile(this, "config", false);
        arenasConfig = new BasicConfigurationFile(this, "arenas", false);
        kitsConfig = new BasicConfigurationFile(this, "kits", false);
        eventsConfig = new BasicConfigurationFile(this, "events", false);
        hotbarConfig = new BasicConfigurationFile(this, "hotbar", false);
        messagesConfig = new BasicConfigurationFile(this, "lang", false);
        tablistConfig = new BasicConfigurationFile(this, "tablist", false);
        scoreboardConfig = new BasicConfigurationFile(this, "scoreboard", false);
        divisionsConfig = new BasicConfigurationFile(this, "divisions", false);
        menuConfig = new BasicConfigurationFile(this, "menus", false);
        rateConfig = new BasicConfigurationFile(this, "ratings", false);
        killeffectsConfig = new BasicConfigurationFile(this, "killeffects", false);
        //brawlConfig = new BasicConfigurationFile(this, "brawl");
    }

    @Override
    public void onEnable() {
        random = new Random();
        api = new ArrayAPI();
        drink = Drink.get(this);

        System.setProperty("file.encoding", "UTF-8");

        this.mongoThread = Executors.newSingleThreadExecutor();

        this.configHandler = new ConfigHandler(this);
        this.configHandler.init();

        this.loadMessages();
        this.preload();

        if (!Description.getAuthor().contains("RefineDevelopment") || !Description.getName().contains("Array")
                || !Description.getAuthor().contains("Nick_0251") || !Description.getWebsite().equalsIgnoreCase("https://dsc.gg/refine")) {
            logger(CC.CHAT_BAR);
            logger("  &cYou edited the plugin.yml, haha get caught in 4k");
            logger("  &cPlease check your plugin.yml and try again.");
            logger("                 &cDisabling Array");
            logger(CC.CHAT_BAR);
            Bukkit.shutdown();
            return;
        }

        this.divisionsManager = new Divisions();

        this.eventManager = new EventManager(this);
        this.eventManager.init();

        this.hotbarManager = new HotbarManager(this);
        this.hotbarManager.init();

        this.leaderboardsManager = new LeaderboardsManager(this);
        this.leaderboardsManager.init();

        this.ratingsManager = new RatingsManager(this);
        this.ratingsManager.init();

        this.killEffectManager = new KillEffectManager(this, killeffectsConfig);
        this.killEffectManager.init();

        this.knockbackManager = new KnockbackManager(this);
        this.knockbackManager.init();

        this.commandsManager= new CommandsManager(this, drink);
        this.commandsManager.init();

        this.listenersManager = new ListenersManager(this);
        this.listenersManager.init();

        this.classManager = new ClassManager(this);
        this.classManager.init();

        this.effectRestorer = new EffectRestorer(this);
        this.effectRestorer.init();

        this.entityHider = new EntityHider(this);
        this.preloadAdapters();
    }

    @Override
    public void onDisable() {
        //Stop all matches and remove the placed Blocks
        Match.cleanup();

        //Save everything before disabling to prevent data loss
        Kit.getKits().forEach(Kit::save);
        Arena.getArenas().forEach(Arena::save);
        Profile.getProfiles().values().forEach(Profile::save);
        Clan.getClans().forEach(Clan::save);

        //Save our Values to Config
        this.configHandler.save();
        this.eventManager.save();
        this.classManager.onDisable();

        //Clear out the PlayerList for Vanilla Tab
        Profile.getPlayerList().clear();
        this.disabling = true;
    }

    public void preload() {
        //Static Abuse be like, but i aint using managers rn
        //cuz praxi was always static af
        this.preLoadMongo();
        Kit.preload();
        Profile.preload();
        Clan.preload();
        Arena.preload();
        Match.preload();
        Party.preLoad();
        Queue.preLoad();
        Rank.preLoad();

        drink.bind(Arena.class).toProvider(new ArenaProvider());
        drink.bind(ArenaType.class).toProvider(new ArenaTypeProvider());
        drink.bind(Kit.class).toProvider(new KitProvider());
        drink.bind(Profile.class).toProvider(new ProfileProvider());
        drink.bind(EventType.class).toProvider(new EventProvider());
        drink.bind(RatingType.class).toProvider(new RatingTypeProvider());
    }

    public void preloadAdapters() {
        this.scoreboardHandler = new ScoreboardHandler(this, new ScoreboardAdapter());
        this.scoreboardHandler.setAssembleStyle(AssembleStyle.MODERN);
        this.scoreboardHandler.setTicks(2);

        this.nameTagHandler = new NameTagHandler(this);
        this.nameTagHandler.init();

        if (this.configHandler.isTAB_ENABLED()) {
            this.tablistHandler = new TablistHandler(new TablistAdapter(), this, 20);
        }

        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new LeaderboardPlaceholders().register();
            this.logger("&7Found PlaceholderAPI, Registering Expansions....");
        } else {
            this.logger("&cPlaceholderAPI was NOT found, Holograms will NOT work!");
        }

        if (this.getServer().getPluginManager().isPluginEnabled("LunarClient-API")) {
            this.logger("&7Found LunarClient-API, Registering Cooldowns....");
            LunarClientAPICooldown.registerCooldown(new LCCooldown("Enderpearl", this.configHandler.getENDERPEARL_COOLDOWN(), TimeUnit.SECONDS, Material.ENDER_PEARL));
            LunarClientAPICooldown.registerCooldown(new LCCooldown("Bow", this.configHandler.getBOW_COOLDOWN(), TimeUnit.SECONDS, Material.BOW));
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
        mainConfig.getConfiguration().options().header(
                "#####################################################################\n" +
                "                                                                     #\n" +
                "          Array Practice Core - Developed By Drizzy#0278             #\n" +
                "       Bought at Refine Development - https://dsc.gg/refine          #\n" +
                "                                                                     #\n" +
                "#####################################################################");
        mainConfig.save();

        if (this.messagesConfig == null) return;

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

    public void logger(String message) {
        String msg = CC.translate("&8[&cArray&8] &r" + message);
        Bukkit.getConsoleSender().sendMessage(msg);
    }
}
