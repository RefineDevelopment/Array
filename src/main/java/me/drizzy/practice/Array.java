package me.drizzy.practice;

import me.drizzy.practice.event.types.brackets.BracketsManager;
import me.drizzy.practice.event.types.gulag.GulagManager;
import me.drizzy.practice.event.types.lms.LMSManager;
import me.drizzy.practice.event.types.parkour.ParkourManager;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.kits.utils.ArmorClassManager;
import me.drizzy.practice.match.kits.utils.bard.EffectRestorer;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.queue.QueueThread;
import me.drizzy.practice.register.RegisterCommands;
import me.drizzy.practice.register.RegisterListeners;
import me.drizzy.practice.util.*;
import org.bukkit.World;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.yaml.snakeyaml.error.YAMLException;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.scoreboard.Aether;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import me.drizzy.practice.util.command.Honcho;
import me.allen.ziggurat.Ziggurat;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.ArenaType;
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

    private MongoDatabase mongoDatabase;

    private Aether scoreboard;

    private Ziggurat tab;

    private SumoManager sumoManager;

    private BracketsManager bracketsManager;

    private LMSManager LMSManager;

    private ParkourManager parkourManager;

    private SpleefManager spleefManager;

    private GulagManager gulagManager;

    private ArmorClassManager armorClassManager;

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
        Array= this;

        random= new Random();

        honcho = new Honcho(this);

        mainConfig = new BasicConfigurationFile(this, "config");
        arenasConfig = new BasicConfigurationFile(this, "arenas");
        kitsConfig = new BasicConfigurationFile(this, "kits");
        eventsConfig = new BasicConfigurationFile(this, "events");
        messagesConfig = new BasicConfigurationFile(this, "messages");

        if (!Description.getAuthor().contains("Drizzy")) {
            this.logger("------------------------------------------------");
            this.logger("&cYou edited the plugin.yml, please don't do that");
            this.logger( "&cPlease check your plugin.yml and try again.");
            this.logger("            &cDisabling Array");
            this.logger("------------------------------------------------");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!Description.getName().contains("Array")) {
            this.logger("------------------------------------------------");
            this.logger("&cYou edited the plugin.yml, please don't do that");
            this.logger(" &cPlease check your plugin.yml and try again.");
            this.logger("            &cDisabling Array");
            this.logger("------------------------------------------------");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (this.mainConfig.getBoolean("Performance-mode")) {
            TaskUtil.runAsync(() -> {
            this.registerAll();
            RegisterCommands.register();
            });
        } else {
            this.registerAll();
            RegisterCommands.register();
        }

        honcho.registerTypeAdapter(Duration.class, new DurationTypeAdapter());

        sumoManager = new SumoManager();
        bracketsManager = new BracketsManager();
        LMSManager = new LMSManager();
        parkourManager = new ParkourManager();
        spleefManager = new SpleefManager();
        gulagManager = new GulagManager();

        this.entityHider = EntityHider.enable();
        this.effectRestorer = new EffectRestorer(this);
        this.armorClassManager = new ArmorClassManager(this);

        new Kit("HCFTeamFight");

        Arrays.asList(
                Material.WORKBENCH,
                Material.STICK,
                Material.WOOD_PLATE,
                Material.WOOD_BUTTON,
                Material.SNOW_BLOCK
        ).forEach(InventoryUtil::removeCrafting);

        for ( World world : this.getServer().getWorlds() ) {
            world.setDifficulty(Difficulty.EASY);
         }

        if (this.mainConfig.getBoolean("Performance-mode")) {
            TaskUtil.runAsync(() -> {
                RegisterListeners.register();
                this.registerEssentials();
            });
        } else {
            RegisterListeners.register();
            this.registerEssentials();
        }
    }

    @Override
    public void onDisable() {
        disabling = true;
        Match.cleanup();
        for (Profile profile : Profile.getProfiles().values()) {
            profile.save();
        }
        Kit.getKits().forEach(Kit::save);
        Arena.getArenas().forEach(Arena::save);
        Profile.getProfiles().values().forEach(Profile::save);
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
        } catch (NullPointerException | MongoInterruptedException | MongoInternalException | MongoCommandException | MongoClientException e) {
            this.logger("------------------------------------------------");
            this.logger("            &4&lMongo Internal Error");
            this.logger("        &cMongo is not setup correctly!");
            this.logger(     "&cPlease check your mongo and try again.");
            this.logger("              &4&lDisabling Array");
            this.logger("------------------------------------------------");
            Bukkit.getPluginManager().disablePlugin(this);
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
            this.logger("------------------------------------------------");
            this.logger("       &cError Loading Kits: &cYAML Error");
            this.logger("    &cThis means your configuration was wrong.");
            this.logger("  &cPlease check your Kits config and try again!");
            this.logger("              &4&lDisabling Array");
            this.logger("------------------------------------------------");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        try {
            this.logger("&bLoading Arenas!");
            Arena.preload();
        } catch (YAMLException e) {
            this.logger("------------------------------------------------");
            this.logger("      &cError Loading Kits: &cYAML Error");
            this.logger("   &cThis means your configuration was wrong.");
            this.logger(" &cPlease check your Arenas config and try again!");
            this.logger("              &4&lDisabling Array");
            this.logger("------------------------------------------------");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        new Hotbar();
        Match.preload();
        Party.preload();

        essentials = new Essentials();

        honcho.registerTypeAdapter(Arena.class, new ArenaTypeAdapter());
        honcho.registerTypeAdapter(ArenaType.class, new ArenaTypeTypeAdapter());
        honcho.registerTypeAdapter(Kit.class, new KitTypeAdapter());
    }

    private void registerEssentials() {
        this.scoreboard = new Aether(this, new Scoreboard());
        this.scoreboard.getOptions().hook(true);
        if (mainConfig.getBoolean("Tab.Enabled")) {
            this.tab=new Ziggurat(this, new Tab());
        }
        new QueueThread().start();
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new HologramPlaceholders().register();
        } else {
            this.logger("&cPlaceholderAPI was NOT found, Holograms will NOT work!");
        }
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
}
