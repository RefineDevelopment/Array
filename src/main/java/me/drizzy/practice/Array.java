package me.drizzy.practice;

import lombok.Setter;
import me.allen.ziggurat.Ziggurat;
import me.drizzy.practice.event.types.brackets.BracketsManager;
import me.drizzy.practice.event.types.gulag.GulagManager;
import me.drizzy.practice.event.types.lms.LMSManager;
import me.drizzy.practice.event.types.oitc.OITCManager;
import me.drizzy.practice.event.types.parkour.ParkourManager;
import me.drizzy.practice.knockback.KnockbackManager;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.hcf.HCFManager;
import me.drizzy.practice.hcf.bard.EffectRestorer;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.placeholders.Placeholders;
import me.drizzy.practice.profile.rank.Rank;
import me.drizzy.practice.profile.rank.RankType;
import me.drizzy.practice.profile.rank.apis.DefaultProvider;
import me.drizzy.practice.queue.QueueThread;
import me.drizzy.practice.register.RegisterCommands;
import me.drizzy.practice.register.RegisterListeners;
import me.drizzy.practice.util.*;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.ItemBuilder;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.yaml.snakeyaml.error.YAMLException;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.scoreboard.Aether;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import me.drizzy.practice.util.command.Honcho;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.arena.ArenaTypeAdapter;
import me.drizzy.practice.arena.ArenaTypeTypeAdapter;
import me.drizzy.practice.event.types.spleef.SpleefManager;
import me.drizzy.practice.event.types.sumo.SumoManager;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.kit.KitTypeAdapter;
import me.drizzy.practice.hologram.HologramPlaceholders;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.scoreboard.Scoreboard;
import me.drizzy.practice.tablist.Tab;
import me.drizzy.practice.array.essentials.Essentials;
import me.drizzy.practice.util.external.duration.Duration;
import me.drizzy.practice.util.external.duration.DurationTypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import lombok.Getter;

import java.util.Random;

@Getter
public class Array extends JavaPlugin {

    private static Array Array;

    private BasicConfigurationFile mainConfig;

    private BasicConfigurationFile arenasConfig;

    private BasicConfigurationFile kitsConfig;

    private BasicConfigurationFile eventsConfig;

    private BasicConfigurationFile messagesConfig;

    public static Random random;

    @Setter private RankType rankManager;
    
    @Setter private Rank rankSystem;

    private Kit hcfKit;

    private MongoDatabase mongoDatabase;

    private Aether scoreboard;

    private Ziggurat tab;

    private SumoManager sumoManager;

    private BracketsManager bracketsManager;

    private LMSManager LMSManager;

    private ParkourManager parkourManager;

    private SpleefManager spleefManager;

    private OITCManager OITCManager;

    private GulagManager gulagManager;

    private KnockbackManager knockbackManager;

    private HCFManager HCFManager;

    private EffectRestorer effectRestorer;

    private Essentials essentials;

    @Getter
    private static Honcho honcho;

    private boolean disabling = false;

    private EntityHider entityHider;

    public static Array getInstance() {
        return Array;
    }

    @Override
    public void onEnable() {
        Array = this;
        random = new Random();
        honcho = new Honcho(this);

        //Seteup All the Configs
        mainConfig = new BasicConfigurationFile(this, "config");
        arenasConfig = new BasicConfigurationFile(this, "arenas");
        kitsConfig = new BasicConfigurationFile(this, "kits");
        eventsConfig = new BasicConfigurationFile(this, "events");
        messagesConfig = new BasicConfigurationFile(this, "messages");

        //To Prevent Stealing and Renaming (Skidding)
        if (!Description.getAuthor().contains("Drizzy")) {
            this.logger(CC.CHAT_BAR);
            this.logger("&cYou edited the plugin.yml, please don't do that");
            this.logger( "&cPlease check your plugin.yml and try again.");
            this.logger("            &cDisabling Array");
            this.logger(CC.CHAT_BAR);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //To Prevent Stealing and Renaming (Skidding)
        if (!Description.getName().contains("Array")) {
            this.logger(CC.CHAT_BAR);
            this.logger("&cYou edited the plugin.yml, please don't do that");
            this.logger(" &cPlease check your plugin.yml and try again.");
            this.logger("            &cDisabling Array");
            this.logger(CC.CHAT_BAR);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        //Register Main Aspects and Commands
        registerAll();
        RegisterCommands.register();

        sumoManager = new SumoManager();
        bracketsManager = new BracketsManager();
        LMSManager = new LMSManager();
        parkourManager = new ParkourManager();
        spleefManager = new SpleefManager();
        gulagManager= new GulagManager();
        OITCManager = new OITCManager();

        if (mainConfig.getBoolean("Array.Core-Hook")) {
            //Core API Support
            rankSystem = new Rank();
        } else {
            setRankManager(new DefaultProvider());
        }
        this.entityHider = EntityHider.enable();
        this.effectRestorer = new EffectRestorer(this);
        this.HCFManager= new HCFManager(this);

        if (mainConfig.getBoolean("Array.HCF-Enabled")) {
            //Create HCF's Duel Kit
            this.hcfKit = new Kit("HCFTeamFight");
            Kit.getByName("HCFTeamFight").setDisplayIcon(new ItemBuilder(Material.BEACON).clearEnchantments().clearFlags().build());
            Kit.getByName("HCFTeamFight").save();
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

    }

    @Override
    public void onDisable() {
        disabling = true;
        //Stop all matches and Remove the placed Block
        Match.cleanup();
        //Save Everything before disabling to prevent data loss
        Kit.getKits().forEach(Kit::save);
        Arena.getArenas().forEach(Arena::save);
        Profile.getProfiles().values().forEach(Profile::save);
        //Clear out the PlayerList for Vanilla Tab
        Profile.getPlayerList().clear();
    }

    public MetadataValue getMetadata(Metadatable m, String tag) {
        for (MetadataValue mv : m.getMetadata(tag))
            if (mv != null && mv.getOwningPlugin() != null && mv.getOwningPlugin() == this) {
                return mv;
            }
        return null;
    }

    private void registerAll() {
        try {
            preLoadMongo();
        } catch (Exception e) {
            this.logger(CC.CHAT_BAR);
            this.logger("            &4&lMongo Internal Error");
            this.logger("        &cMongo is not setup correctly!");
            this.logger(     "&cPlease check your mongo and try again.");
            this.logger("              &4&lDisabling Array");
            this.logger(CC.CHAT_BAR);
            this.shutDown();
            return;
        }

        this.logger("&bLoading Profiles!");
        Profile.preload();
        this.logger("&aLoaded Profiles!");

        try {
            this.logger("&bLoading Kits!");
            Kit.preload();
            this.logger("&aLoaded Kits!");
        } catch (YAMLException e) {
            this.logger(CC.CHAT_BAR);
            this.logger("       &cError Loading Kits: &cYAML Error");
            this.logger("    &cThis means your configuration was wrong.");
            this.logger("  &cPlease check your Kits config and try again!");
            this.logger("               &4&lDisabling Array");
            this.logger(CC.CHAT_BAR);
            this.shutDown();
            return;
        }
        try {
            this.logger("&bLoading Arenas!");
            Arena.preload();
        } catch (YAMLException e) {
            this.logger(CC.CHAT_BAR);
            this.logger("      &cError Loading Kits: &cYAML Error");
            this.logger("   &cThis means your configuration was wrong.");
            this.logger(" &cPlease check your Arenas config and try again!");
            this.logger("              &4&lDisabling Array");
            this.logger(CC.CHAT_BAR);
            this.shutDown();
            return;
        }
        new Hotbar();
        Match.preload();
        Party.preload();
        TaskUtil.runAsync(() -> {
            knockbackManager=new KnockbackManager();
        });
        essentials = new Essentials();

        honcho.registerTypeAdapter(Arena.class, new ArenaTypeAdapter());
        honcho.registerTypeAdapter(ArenaType.class, new ArenaTypeTypeAdapter());
        honcho.registerTypeAdapter(Kit.class, new KitTypeAdapter());
        honcho.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
    }

    private void registerEssentials() {
        //Setup Scoreboard & Tab (Spent 2 Days figuring out why scoreboard was not working,
        // turns out you gotta register it before tab)
        this.scoreboard=new Aether(this, new Scoreboard());
        this.scoreboard.getOptions().hook(true);
        //Setup Tab
        if (mainConfig.getBoolean("Tab.Enabled")) {
            this.tab=new Ziggurat(this, new Tab());
        }
        //Start the Queue Thread
        new QueueThread().start();

        //PlaceholderAPI Hook
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new HologramPlaceholders().register();
            new Placeholders().register();
            logger("&bFound PlaceholderAPI, Registering Expansions....");
        } else {
            logger("&cPlaceholderAPI was NOT found, Holograms will NOT work!");
        }

        //Load the Global Leaderboards (Also a bug fix for leaderboards being blank on start)
        Profile.loadGlobalLeaderboards();
    }

    public static void logger(String message) {
        String msg = CC.translate("&8[&bArray&8] &r" + message);
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    private void preLoadMongo() {
        MongoClient client = new MongoClient(new MongoClientURI(mainConfig.getString("Mongo.URL")));
        this.mongoDatabase = client.getDatabase(mainConfig.getString("Mongo.Database"));
    }

    public void shutDown() {
        this.onDisable();
        logger("Shutting down Array!");
        Bukkit.getPluginManager().disablePlugin(this);
    }
}
