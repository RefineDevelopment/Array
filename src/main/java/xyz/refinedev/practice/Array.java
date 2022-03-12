package xyz.refinedev.practice;

import com.google.gson.Gson;
import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.refinedev.practice.adapters.NameTagAdapter;
import xyz.refinedev.practice.adapters.ScoreboardAdapter;
import xyz.refinedev.practice.adapters.TablistAdapter;
import xyz.refinedev.practice.api.API;
import xyz.refinedev.practice.api.ArrayAPI;
import xyz.refinedev.practice.config.ConfigHandler;
import xyz.refinedev.practice.hook.core.CoreHandler;
import xyz.refinedev.practice.hook.hologram.HologramHandler;
import xyz.refinedev.practice.hook.placeholderapi.LeaderboardPlaceholders;
import xyz.refinedev.practice.hook.placeholderapi.PracticePlaceholders;
import xyz.refinedev.practice.hook.spigot.SpigotHandler;
import xyz.refinedev.practice.managers.*;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.pvpclasses.bard.EffectRestorer;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.Drink;
import xyz.refinedev.practice.util.command.command.CommandService;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.menu.MenuHandler;
import xyz.refinedev.practice.util.nametags.NameTagHandler;
import xyz.refinedev.practice.util.other.ClassUtil;
import xyz.refinedev.practice.util.other.Description;
import xyz.refinedev.practice.util.other.EntityHider;
import xyz.refinedev.practice.util.scoreboard.AssembleStyle;
import xyz.refinedev.practice.util.scoreboard.ScoreboardHandler;
import xyz.refinedev.practice.util.serialize.GsonFactory;
import xyz.refinedev.practice.util.timer.TimerHandler;
import xyz.refinedev.practice.util.timer.impl.BridgeArrowTimer;
import xyz.refinedev.practice.util.timer.impl.EnderpearlTimer;
import xyz.refinedev.tablist.TablistHandler;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

//import xyz.refinedev.practice.event.EventType;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 2/13/2021
 * Project: Array
 */

@Getter
public class Array extends JavaPlugin {

    @Getter private static Array instance;
    @Getter private static API api;

    public static Gson GSON;
    public static Random RANDOM;

    private BasicConfigurationFile mainConfig, arenasConfig, kitsConfig, eventsConfig, tablistConfig, menusConfig,
                                   messagesConfig, scoreboardConfig,  hotbarConfig, hologramsConfig;

    private MenuHandler menuHandler;
    private CoreHandler coreHandler;
    private TimerHandler timerHandler;
    private ConfigHandler configHandler;
    private SpigotHandler spigotHandler;
    private TablistHandler tablistHandler;
    private NameTagHandler nameTagHandler;
    private HologramHandler hologramHandler;
    private ScoreboardHandler scoreboardHandler;

    private KitManager kitManager;
    private ClanManager clanManager;
    //private EventManager eventManager;
    private MatchManager matchManager;
    private ArenaManager arenaManager;
    private PartyManager partyManager;
    private QueueManager queueManager;
    private MongoManager mongoManager;
    private HotbarManager hotbarManager;
    private EffectRestorer effectRestorer;
    private ProfileManager profileManager;
    private PvPClassManager pvpClassManager;
    private DivisionsManager divisionsManager;
    private TournamentManager tournamentManager;
    private LeaderboardsManager leaderboardsManager;

    private CommandService drink;
    private EntityHider entityHider;
    private boolean disabling, placeholderAPI;

    @Override
    public void onLoad() {
        instance = this;
        this.saveDefaultConfig();

        mainConfig = new BasicConfigurationFile(this, "config", false);
        menusConfig = new BasicConfigurationFile(this, "menus", false);
        kitsConfig = new BasicConfigurationFile(this, "kits", false);
        eventsConfig = new BasicConfigurationFile(this, "events", false);
        hotbarConfig = new BasicConfigurationFile(this, "hotbar", false);
        arenasConfig = new BasicConfigurationFile(this, "arenas", false);
        tablistConfig = new BasicConfigurationFile(this, "tablist", false);
        messagesConfig = new BasicConfigurationFile(this, "lang", false);
        hologramsConfig = new BasicConfigurationFile(this, "holograms", false);
        scoreboardConfig = new BasicConfigurationFile(this, "scoreboard", false);
    }

    @Override
    public void onEnable() {
        api = new ArrayAPI(this);
        RANDOM = new Random();
        GSON = GsonFactory.getPrettyGson();
        drink = Drink.get(this);

        this.consoleLog("&c------------------------------------------------");

        this.configHandler = new ConfigHandler(this);
        this.configHandler.init();

        this.menuHandler = new MenuHandler(this, menusConfig);
        this.menuHandler.init();

        Locale.init(this);

        if (!Description.getAuthor().contains("RefineDevelopment") || !Description.getName().contains("Array")
           || !Description.getAuthor().contains("Nick_0251") || !Description.getWebsite().equalsIgnoreCase("https://dsc.gg/refine")) {
            this.logger(CC.CHAT_BAR);
            this.logger("&cYou edited the plugin.yml, haha get caught in 4k");
            this.logger("&cPlease check your plugin.yml and try again.");
            this.logger("&cDisabling Array");
            this.logger(CC.CHAT_BAR);
            Bukkit.shutdown();
            return;
        }

        this.mongoManager = new MongoManager(this, mainConfig);
        this.mongoManager.init();

        this.divisionsManager = new DivisionsManager(this, mainConfig);
        this.divisionsManager.init();

        this.clanManager = new ClanManager(this, mongoManager.getClans());
        this.clanManager.init();

        this.coreHandler = new CoreHandler(this);
        this.coreHandler.init();

        this.queueManager = new QueueManager(this);
        this.queueManager.init();

        this.kitManager = new KitManager(this, kitsConfig);
        this.kitManager.init();

        this.profileManager = new ProfileManager(this, mongoManager.getProfiles());
        this.profileManager.init();

        this.arenaManager = new ArenaManager(this, arenasConfig);
        this.arenaManager.init();

        this.matchManager = new MatchManager(this);
        this.matchManager.init();

        this.partyManager = new PartyManager(this);
        this.partyManager.init();

        /*this.eventManager = new EventManager(this, eventsConfig);
        this.eventManager.init();*/

        this.tournamentManager = new TournamentManager(this);

        this.hotbarManager = new HotbarManager(this, hotbarConfig);
        this.hotbarManager.init();

        this.leaderboardsManager = new LeaderboardsManager(this);
        this.leaderboardsManager.init();

        this.pvpClassManager = new PvPClassManager(this);
        this.pvpClassManager.init();

        this.effectRestorer = new EffectRestorer(this);
        this.effectRestorer.init();

        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);
        this.entityHider.init();

        this.initExpansions();

        ClassUtil.registerCommands("xyz.refinedev.practice.cmds");
        ClassUtil.registerListeners("xyz.refinedev.practice.listeners");

        this.consoleLog("");
        this.consoleLog("&7Initialized &cArray &7Successfully!");
        this.consoleLog("&c------------------------------------------------");
    }

    @Override
    public void onDisable() {
        this.matchManager.getMatches().forEach(matchManager::cleanup);
        this.arenaManager.getArenas().forEach(arenaManager::save);
        this.kitManager.getKits().forEach(kitManager::save);
        this.clanManager.getClans().values().forEach(clanManager::save);
        this.profileManager.getProfiles().values().forEach(profileManager::save);
        this.hologramHandler.getHolograms().forEach(hologramHandler::save);
        this.arenaManager.clearGroundItems();

        this.configHandler.save();
        this.pvpClassManager.shutdown();
        //this.eventManager.shutdown();
        this.leaderboardsManager.shutdown();
        this.queueManager.shutdown();

        this.disabling = true;

        this.mongoManager.shutdown();
    }

    /**
     * This method initializes and hooks
     * into the APIs used by this plugin
     *
     * A very important method to initialize this
     * whole plugin.
     */
    public void initExpansions() {
        this.spigotHandler = new SpigotHandler(this);
        this.spigotHandler.init();

        TablistAdapter tablistAdapter = new TablistAdapter(this, tablistConfig);
        ScoreboardAdapter scoreboardAdapter = new ScoreboardAdapter(this, scoreboardConfig);
        NameTagAdapter nameTagAdapter = new NameTagAdapter(this);

        this.scoreboardHandler = new ScoreboardHandler(this, scoreboardAdapter);
        this.scoreboardHandler.setAssembleStyle(AssembleStyle.KOHI);
        this.scoreboardHandler.setTicks(2);

        this.nameTagHandler = new NameTagHandler(this);
        this.nameTagHandler.registerAdapter(nameTagAdapter);

        if (this.configHandler.isTAB_ENABLED()) {
           long tickTime = tablistConfig.getInteger("TABLIST.UPDATE_TICKS") * 20L;
           this.tablistHandler = new TablistHandler(tablistAdapter, this, tickTime);
        }

        this.timerHandler = new TimerHandler(this);
        this.timerHandler.registerTimer(new EnderpearlTimer(this));
        this.timerHandler.registerTimer(new BridgeArrowTimer(this));

        PluginManager pluginManager = this.getServer().getPluginManager();

        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            this.placeholderAPI = true;
            this.logger("&7Found &cPlaceholderAPI&7, Registering Expansions....");
            PlaceholderAPI.registerExpansion(new LeaderboardPlaceholders(this));
            PlaceholderAPI.registerExpansion(new PracticePlaceholders(this));
        }

        if (pluginManager.isPluginEnabled("LunarClient-API")) {
            this.logger("&7Found &cLunarClient-API&7, Registering Cool-downs....");
            LunarClientAPICooldown.registerCooldown(new LCCooldown("Enderpearl", this.configHandler.getENDERPEARL_COOLDOWN(), TimeUnit.SECONDS, Material.ENDER_PEARL));
            LunarClientAPICooldown.registerCooldown(new LCCooldown("Bow", this.configHandler.getBOW_COOLDOWN(), TimeUnit.SECONDS, Material.BOW));
        }

        if (pluginManager.isPluginEnabled("HolographicDisplays")) {
            this.logger("&7Found &cHolographicDisplays&7, Hooking holograms....");
            this.hologramHandler = new HologramHandler(this, hologramsConfig);
            this.hologramHandler.init();
        }

        this.logger("&7Registering permissions");
        this.registerPermissions();
        this.logger("&7Registered &cPermissions &7successfully!");
    }

    /**
     * Register Array's permissions manually
     */
    public void registerPermissions() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.addPermission(new Permission("array.profile.ranked", PermissionDefault.OP));
        pluginManager.addPermission(new Permission("array.duel.arena", PermissionDefault.OP));
        pluginManager.addPermission(new Permission("array.party.privacy", PermissionDefault.OP));
        pluginManager.addPermission(new Permission("array.party.ban", PermissionDefault.OP));
        pluginManager.addPermission(new Permission("array.party.limit", PermissionDefault.OP));
        drink.registerPermissions();
    }

    /**
     * Runs the given runnable asynchronously
     * This method is usually used in mongo and
     * other non-bukkit related tasks
     *
     * @param runnable {@link Runnable} the runnable
     */
    public void submitToThread(Runnable runnable) {
        ForkJoinPool.commonPool().execute(runnable);
    }

    public void logger(String message) {
        this.getServer().getConsoleSender().sendMessage(CC.translate("&c• " + message));
    }

    public void consoleLog(String string) {
        this.getServer().getConsoleSender().sendMessage(CC.translate(string));
    }
}
