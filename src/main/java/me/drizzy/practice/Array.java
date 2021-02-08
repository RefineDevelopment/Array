package me.drizzy.practice;

import me.drizzy.practice.arena.command.*;
import me.drizzy.practice.array.commands.*;
import me.drizzy.practice.array.commands.donator.FlyCommand;
import me.drizzy.practice.array.commands.staff.FollowCommand;
import me.drizzy.practice.array.commands.staff.GetUUIDCommand;
import me.drizzy.practice.array.commands.staff.SilentCommand;
import me.drizzy.practice.array.commands.staff.UnFollowCommand;
import me.drizzy.practice.array.listener.GoldenHeads;
import me.drizzy.practice.array.listener.MOTDListener;
import me.drizzy.practice.array.listener.ToggleSprintFix;
import me.drizzy.practice.event.EventCommand;
import me.drizzy.practice.event.types.brackets.BracketsListener;
import me.drizzy.practice.event.types.brackets.BracketsManager;
import me.drizzy.practice.event.types.brackets.command.*;
import me.drizzy.practice.event.types.lms.LMSListener;
import me.drizzy.practice.event.types.lms.LMSManager;
import me.drizzy.practice.event.types.lms.command.*;
import me.drizzy.practice.event.types.parkour.ParkourListener;
import me.drizzy.practice.event.types.parkour.ParkourManager;
import me.drizzy.practice.event.types.parkour.command.*;
import me.drizzy.practice.event.types.skywars.SkyWarsListener;
import me.drizzy.practice.event.types.skywars.SkyWarsManager;
import me.drizzy.practice.event.types.skywars.SkywarsChests;
import me.drizzy.practice.event.types.skywars.command.*;
import me.drizzy.practice.event.types.spleef.command.*;
import me.drizzy.practice.event.types.sumo.command.*;
import me.drizzy.practice.kit.command.*;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.MatchListener;
import me.drizzy.practice.match.command.MatchStatusCommand;
import me.drizzy.practice.match.command.SpectateCommand;
import me.drizzy.practice.match.command.StopSpectatingCommand;
import me.drizzy.practice.match.command.ViewInventoryCommand;
import me.drizzy.practice.match.kits.utils.ArmorClassManager;
import me.drizzy.practice.match.kits.utils.bard.EffectRestorer;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.party.PartyListener;
import me.drizzy.practice.party.command.*;
import me.drizzy.practice.profile.hotbar.Hotbar;
import me.drizzy.practice.profile.hotbar.HotbarListener;
import me.drizzy.practice.queue.QueueListener;
import me.drizzy.practice.queue.QueueThread;
import me.drizzy.practice.statistics.command.LeaderboardsCommand;
import me.drizzy.practice.statistics.command.StatsCommand;
import me.drizzy.practice.tournament.command.*;
import org.bukkit.World;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.yaml.snakeyaml.error.YAMLException;
import me.drizzy.practice.nms.NMSProvider;
import me.drizzy.practice.util.Description;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.scoreboard.Aether;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import me.drizzy.practice.util.command.Honcho;
import me.allen.ziggurat.Ziggurat;
import me.drizzy.practice.array.VerCommand;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.ArenaType;
import me.drizzy.practice.arena.ArenaTypeAdapter;
import me.drizzy.practice.arena.ArenaTypeTypeAdapter;
import me.drizzy.practice.array.ArrayCommand;
import me.drizzy.practice.duel.command.DuelAcceptCommand;
import me.drizzy.practice.duel.command.DuelCommand;
import me.drizzy.practice.duel.command.RematchCommand;
import me.drizzy.practice.event.types.spleef.SpleefListener;
import me.drizzy.practice.event.types.spleef.SpleefManager;
import me.drizzy.practice.event.types.sumo.SumoListener;
import me.drizzy.practice.event.types.sumo.SumoManager;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.kit.KitEditorListener;
import me.drizzy.practice.kit.KitTypeAdapter;
import me.drizzy.practice.hologram.PlaceholderAPIExtension;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileListener;
import me.drizzy.practice.scoreboard.Scoreboard;
import me.drizzy.practice.tablist.Tab;
import me.drizzy.practice.util.EntityHider;
import me.drizzy.practice.util.InventoryUtil;
import me.drizzy.practice.util.essentials.Essentials;
import me.drizzy.practice.util.essentials.listener.EssentialsListener;
import me.drizzy.practice.util.events.ArmorListener;
import me.drizzy.practice.util.events.WorldListener;
import me.drizzy.practice.util.external.duration.Duration;
import me.drizzy.practice.util.external.duration.DurationTypeAdapter;
import me.drizzy.practice.util.external.menu.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.event.Listener;
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

    private BasicConfigurationFile chestsConfig;

    private BasicConfigurationFile messagesConfig;

    public static Random random;

    private MongoDatabase mongoDatabase;

    private Aether scoreboard;

    private Ziggurat tab;

    private NMSProvider nms;

    private SumoManager sumoManager;

    private BracketsManager bracketsManager;

    private me.drizzy.practice.event.types.lms.LMSManager LMSManager;

    private ParkourManager parkourManager;

    private SkyWarsManager skyWarsManager;

    private SpleefManager spleefManager;

    private ArmorClassManager armorClassManager;

    private EffectRestorer effectRestorer;

    private Essentials essentials;

    private Honcho honcho;

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
        chestsConfig = new BasicConfigurationFile(this, "chests");

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
            this.logger("       &bError Loading Kits: &fYAML Error");
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
            this.logger("      &bError Loading Kits: &fYAML Error");
            this.logger("   &cThis means your configuration was wrong.");
            this.logger(" &cPlease check your Arenas config and try again!");
            this.logger("              &4&lDisabling Array");
            this.logger("------------------------------------------------");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Hotbar.preload();
        Match.preload();
        Party.preload();

        this.nms = new NMSProvider();
        nms.setup();

        essentials = new Essentials(this);


        honcho.registerTypeAdapter(Arena.class, new ArenaTypeAdapter());
        honcho.registerTypeAdapter(ArenaType.class, new ArenaTypeTypeAdapter());
        honcho.registerTypeAdapter(Kit.class, new KitTypeAdapter());

        for (Object command : Arrays.asList(
                //Staff commands
                new SilentCommand(),
                new FollowCommand(),
                new UnFollowCommand(),

                //Array Commands
                new ArrayCommand(),
                new ArraySetLobbyCommand(),
                new ArrayGoldenHeadCommand(),
                new ArrayReloadCommand(),
                new ArrayRenameCommand(),
                new ArrayResetStatsCommand(),
                new ArraySaveArenasCommand(),
                new ArraySaveCommand(),
                new ArraySaveDataCommand(),
                new ArraySaveKitsCommand(),
                new ArrayPracticeCommand(),
                new ArrayHCFCommand(),
                new ArraySpawnCommand(),
                new ArrayToggleScoreboardCommand(),
                new ArrayToggleDuelCommand(),
                new ArrayVerCommand(),
                new ArrayManageCommand(),
                new TestCommand(),
                new VerCommand(),

                //Player commands
                new StatsCommand(),
                new LeaderboardsCommand(),
                new ArraySettingsCommand(),
                new EventCommand(),
                new GetUUIDCommand(),
                new FlyCommand(),

                //Arena commands
                new ArenaAddKitCommand(),
                new ArenaRemoveKitCommand(),
                new ArenaSetSpawnCommand(),
                new ArenaSetPointCommand(),
                new ArenaCreateCommand(),
                new ArenaRemoveCommand(),
                new ArenasCommand(),
                new ArenaTpCommand(),
                new ArenaCommand(),
                new ArenasCommand(),
                new ArenaKitListCommand(),
                new ArenaSaveCommand(),
                new ArenaReloadCommand(),
                new ArenaSetIconCommand(),

                //Duel commands
                new DuelCommand(),
                new DuelAcceptCommand(),
                new RematchCommand(),
                new ViewInventoryCommand(),
                new SpectateCommand(),
                new StopSpectatingCommand(),
                new MatchStatusCommand(),

                //Party command
                new PartyCloseCommand(),
                new PartyCreateCommand(),
                new PartyDisbandCommand(),
                new PartyHelpCommand(),
                new PartyInfoCommand(),
                new PartyInviteCommand(),
                new PartyJoinCommand(),
                new PartyKickCommand(),
                new PartyLeaveCommand(),
                new PartyOpenCommand(),
                new PartyLeaderCommand(),
                new PartyUnbanCommand(),
                new PartyBanCommand(),

                //Kit command
                new KitCreateCommand(),
                new KitGetLoadoutCommand(),
                new KitSetLoadoutCommand(),
                new KitSetKnockbackProfileCommand(),
                new KitListCommand(),
                new KitCommand(),
                new KitSaveCommand(),
                new KitRemoveCommand(),
                new KitSetIconCommand(),
                new KitSetHitDelayCommand(),
                new KitSetRankedCommand(),
                new KitSumoCommand(),
                new KitBuildCommand(),
                new KitBoxUHCCommand(),
                new KitSetAntiFoodLossCommand(),
                new KitSetBowHPCommand(),
                new KitSetHealthRegenenerationCommand(),
                new KitSetInfiniteSpeedCommand(),
                new KitSetInfiniteStrengthCommand(),
                new KitComboCommand(),
                new KitSpleefCommand(),
                new KitTimedCommand(),
                new KitParkourCommand(),
                new KitPartyFFACommand(),
                new KitPartySplitCommand(),
                new KitDisableCommand(),
                new KitLavaKillCommand(),
                new KitWaterKillCommand(),
                new KitSetNoItemsCommand(),
                new KitShowHealthCommand(),
                new KitDisableCommand(),
                new KitEnableCommand(),
                new KitVoidSpawnCommand(),
                new KitStickSpawnCommand(),
                new KitNetherUHCCommand(),

                //Brackets command
                new BracketsLeaveCommand(),
                new BracketsCancelCommand(),
                new BracketsCooldownCommand(),
                new BracketsJoinCommand(),
                new BracketsSetSpawnCommand(),
                new BracketsHostCommand(),
                new BracketsTpCommand(),
                new BracketsHelpCommand(),
                new BracketsKnockbackCommand(),
                new BracketsForceStartCommand(),

                //Sumo command
                new SumoCancelCommand(),
                new SumoCooldownCommand(),
                new SumoHostCommand(),
                new SumoJoinCommand(),
                new SumoLeaveCommand(),
                new SumoSetSpawnCommand(),
                new SumoTpCommand(),
                new SumoHelpCommand(),
                new SumoKnockbackCommand(),
                new SumoForceStartCommand(),

                //LMS command
                new LMSCancelCommand(),
                new LMSCooldownCommand(),
                new LMSHostCommand(),
                new LMSJoinCommand(),
                new LMSLeaveCommand(),
                new LMSSetSpawnCommand(),
                new LMSTpCommand(),
                new LMSHelpCommand(),
                new LMSForceStartCommand(),

                //Parkour command
                new ParkourCancelCommand(),
                new ParkourCooldownCommand(),
                new ParkourHostCommand(),
                new ParkourJoinCommand(),
                new ParkourLeaveCommand(),
                new ParkourSetSpawnCommand(),
                new ParkourTpCommand(),
                new ParkourHelpCommand(),
                new ParkourForceStartComman(),

                //SkyWars command
                new SkyWarsCancelCommand(),
                new SkyWarsCooldownCommand(),
                new SkyWarsHostCommand(),
                new SkyWarsJoinCommand(),
                new SkyWarsLeaveCommand(),
                new SkyWarsSetSpawnCommand(),
                new SkywarsClearChestsCommand(),
                new SkyWarsTpCommand(),
                new SkyWarsSetChestCommand(),
                new SkyWarsHelpCommand(),

                //Spleef command
                new SpleefCancelCommand(),
                new SpleefCooldownCommand(),
                new SpleefHostCommand(),
                new SpleefJoinCommand(),
                new SpleefLeaveCommand(),
                new SpleefSetSpawnCommand(),
                new SpleefTpCommand(),
                new SpleefHelpCommand(),
                new SpleefForceStartCommand(),

                //Tournament command
                new TournamentCommand(),
                new TournamentLeaveCommand(),
                new TournamentJoinCommand(),
                new TournamentHostCommand(),
                new TournamentCancelCommand(),
                new TournamentListCommand()
        ))
            honcho.registerCommand(command);

        honcho.registerTypeAdapter(Duration.class, new DurationTypeAdapter());

        sumoManager = new SumoManager();
        bracketsManager = new BracketsManager();
        LMSManager = new LMSManager();
        parkourManager = new ParkourManager();
        skyWarsManager = new SkyWarsManager();
        spleefManager = new SpleefManager();

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
            world.setDifficulty(Difficulty.HARD);
            essentials.clearEntities(world);
        }

        for (Listener listener : Arrays.asList(
                new ProfileListener(),
                new MenuListener(this),
                new EssentialsListener(this),
                new SumoListener(),
                new BracketsListener(),
                new LMSListener(),
                new ParkourListener(),
                new SkyWarsListener(),
                new SpleefListener(),
                new KitEditorListener(),
                new MOTDListener(),
                new PartyListener(),
                new HotbarListener(),
                new MatchListener(),
                new WorldListener(),
                new GoldenHeads(),
                new ToggleSprintFix(),
                new QueueListener(),
                new ArmorListener()
        )) {
        this.getServer().getPluginManager().registerEvents(listener, this);
        }

        this.scoreboard = new Aether(this, new Scoreboard());
        this.scoreboard.getOptions().hook(true);
        if (mainConfig.getBoolean("Tab.Enabled")) {
        this.tab=new Ziggurat(this, new Tab());
        }
        new QueueThread().start();
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIExtension().register();
        } else {
            this.logger("&cPlaceholderAPI was NOT found, Holograms will NOT work!");
        }
        SkywarsChests.preload();
        Profile.loadGlobalLeaderboards();
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
    
    public static void logger(String message) {
        String msg = CC.translate("&8[&bArray&8] &r" + message);
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    private void preLoadMongo() {
        MongoClient client = new MongoClient(new MongoClientURI(mainConfig.getString("Mongo.URL")));
        this.mongoDatabase = client.getDatabase(mainConfig.getString("Mongo.Database"));
    }
}
