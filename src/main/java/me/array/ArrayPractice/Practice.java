package me.array.ArrayPractice;

import cc.outlast.tablist.OutlastTab;
import com.bizarrealex.aether.Aether;
import com.bizarrealex.aether.AetherOptions;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.qrakn.honcho.Honcho;
import com.qrakn.phoenix.lang.file.language.LanguageConfigurationFile;
import com.qrakn.phoenix.lang.file.type.BasicConfigurationFile;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.arena.ArenaType;
import me.array.ArrayPractice.arena.ArenaTypeAdapter;
import me.array.ArrayPractice.arena.ArenaTypeTypeAdapter;
import me.array.ArrayPractice.arena.command.*;
import me.array.ArrayPractice.duel.command.DuelAcceptCommand;
import me.array.ArrayPractice.duel.command.DuelCommand;
import me.array.ArrayPractice.duel.command.RematchCommand;
import me.array.ArrayPractice.event.EventCommand;
import me.array.ArrayPractice.event.impl.brackets.BracketsListener;
import me.array.ArrayPractice.event.impl.brackets.BracketsManager;
import me.array.ArrayPractice.event.impl.lms.LMSListener;
import me.array.ArrayPractice.event.impl.lms.LMSManager;
import me.array.ArrayPractice.event.impl.lms.command.*;
import me.array.ArrayPractice.event.impl.parkour.ParkourListener;
import me.array.ArrayPractice.event.impl.parkour.ParkourManager;
import me.array.ArrayPractice.event.impl.parkour.command.*;
import me.array.ArrayPractice.event.impl.skywars.SkyWarsChest;
import me.array.ArrayPractice.event.impl.skywars.SkyWarsListener;
import me.array.ArrayPractice.event.impl.skywars.SkyWarsManager;
import me.array.ArrayPractice.event.impl.spleef.SpleefListener;
import me.array.ArrayPractice.event.impl.spleef.SpleefManager;
import me.array.ArrayPractice.event.impl.spleef.command.*;
import me.array.ArrayPractice.event.impl.sumo.SumoListener;
import me.array.ArrayPractice.event.impl.sumo.SumoManager;
import me.array.ArrayPractice.event.impl.sumo.command.*;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.kit.KitEditorListener;
import me.array.ArrayPractice.kit.KitTypeAdapter;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.MatchListener;
import me.array.ArrayPractice.match.command.SpectateCommand;
import me.array.ArrayPractice.match.command.StopSpectatingCommand;
import me.array.ArrayPractice.match.command.ViewInventoryCommand;
import me.array.ArrayPractice.match.kits.utils.ArmorClassManager;
import me.array.ArrayPractice.match.kits.utils.bard.EffectRestorer;
import me.array.ArrayPractice.movement.PlayerMovementListener;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.party.PartyListener;
import me.array.ArrayPractice.hologram.PlaceholderAPIExtension;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileListener;
import me.array.ArrayPractice.profile.command.donator.FlyCommand;
import me.array.ArrayPractice.profile.command.donator.ToggleVisibilityCommand;
import me.array.ArrayPractice.profile.hotbar.Hotbar;
import me.array.ArrayPractice.profile.hotbar.HotbarListener;
import me.array.ArrayPractice.profile.stats.command.LeaderboardsCommand;
import me.array.ArrayPractice.profile.stats.command.StatsCommand;
import me.array.ArrayPractice.queue.QueueListener;
import me.array.ArrayPractice.queue.QueueThread;
import me.array.ArrayPractice.util.EntityHider;
import me.array.ArrayPractice.util.InventoryUtil;
import me.array.ArrayPractice.util.AquaCoreHook;
import me.array.ArrayPractice.util.essentials.Essentials;
import me.array.ArrayPractice.util.essentials.listener.EssentialsListener;
import me.array.ArrayPractice.util.events.ArmorListener;
import me.array.ArrayPractice.util.events.WorldListener;
import me.array.ArrayPractice.util.external.duration.Duration;
import me.array.ArrayPractice.util.external.duration.DurationTypeAdapter;
import me.array.ArrayPractice.util.external.menu.MenuListener;
import lombok.Getter;
import me.array.ArrayPractice.scoreboard.Scoreboard;
import me.array.ArrayPractice.tab.Tab;
import me.array.ArrayPractice.event.impl.brackets.command.*;
import me.array.ArrayPractice.event.impl.skywars.command.*;
import me.array.ArrayPractice.kit.command.*;
import me.array.ArrayPractice.party.command.*;
import me.array.ArrayPractice.profile.command.*;
import me.array.ArrayPractice.profile.command.staff.*;
import me.array.ArrayPractice.tournament.command.*;
import org.apache.commons.codec.language.bm.Lang;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class Practice extends JavaPlugin {

    private static Practice Practice;

    @Getter
    private BasicConfigurationFile mainConfig;
    @Getter
    private BasicConfigurationFile arenasConfig;
    @Getter
    private BasicConfigurationFile kitsConfig;
    @Getter
    private BasicConfigurationFile eventsConfig;
    @Getter
    private BasicConfigurationFile chestsConfig;
    @Getter
    private LanguageConfigurationFile lanuageConfig;

    @Getter
    private MongoDatabase mongoDatabase;

    @Getter
    private SumoManager sumoManager;
    @Getter
    private BracketsManager bracketsManager;
    @Getter
    private LMSManager LMSManager;
    @Getter
    private ParkourManager parkourManager;
    @Getter
    private SkyWarsManager skyWarsManager;
    @Getter
    private SpleefManager spleefManager;

    @Getter
    private ArmorClassManager armorClassManager;

    @Getter
    private EffectRestorer effectRestorer;

    @Getter
    private Essentials essentials;

    @Getter
    private AquaCoreHook coreHook;

    @Getter
    private Honcho honcho;

    @Getter
    private boolean disabling = false;

    @Getter
    private EntityHider entityHider;

    public static Practice getInstance() {
        return Practice;
    }

    @Override
    public void onEnable() {
        Practice = this;

        honcho = new Honcho(this);

        mainConfig = new BasicConfigurationFile(this, "config");
        arenasConfig = new BasicConfigurationFile(this, "arenas");
        kitsConfig = new BasicConfigurationFile(this, "kits");
        eventsConfig = new BasicConfigurationFile(this, "events");
        chestsConfig = new BasicConfigurationFile(this, "chests");
        lanuageConfig = new LanguageConfigurationFile(this, "lang");

        loadMongo();

        Profile.init();
        Kit.init();
        Arena.init();
        Hotbar.init();
        Match.init();
        Party.init();

        essentials = new Essentials(this);
        coreHook= new AquaCoreHook();

        honcho.registerTypeAdapter(Arena.class, new ArenaTypeAdapter());
        honcho.registerTypeAdapter(ArenaType.class, new ArenaTypeTypeAdapter());
        honcho.registerTypeAdapter(Kit.class, new KitTypeAdapter());

        for (Object command : Arrays.asList(
                //Staff commands
                new SetSpawnCommand(),
                new RemoveProfileCommand(),
                new SilentCommand(),
                new FollowCommand(),
                new UnFollowCommand(),
                new GetLocationCommand(),
                new SaveCommand(),

                //Player commands
                new TsbCommand(),
                new TduelCommand(),
                new StatsCommand(),
                new LeaderboardsCommand(),
                new OptionsCommand(),
                new EventCommand(),
                new PracticeCommand(),
                new ArrayCommand(),
                new PingCommand(),
                new SpawnCommand(),

                //Donator commands
                new FlyCommand(),
                new ToggleVisibilityCommand(),


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
                new ArenaSaveCommand(),
                new ArenaSetIconCommand(),

                //Duel commands
                new DuelCommand(),
                new DuelAcceptCommand(),
                new RematchCommand(),
                new ViewInventoryCommand(),
                new SpectateCommand(),
                new StopSpectatingCommand(),

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
                new KitSetRankedCommand(),

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

                //LMS command
                new LMSCancelCommand(),
                new LMSCooldownCommand(),
                new LMSHostCommand(),
                new LMSJoinCommand(),
                new LMSLeaveCommand(),
                new LMSSetSpawnCommand(),
                new LMSTpCommand(),
                new LMSHelpCommand(),

                //Parkour command
                new ParkourCancelCommand(),
                new ParkourCooldownCommand(),
                new ParkourHostCommand(),
                new ParkourJoinCommand(),
                new ParkourLeaveCommand(),
                new ParkourSetSpawnCommand(),
                new ParkourTpCommand(),
                new ParkourHelpCommand(),

                //SkyWars command
                new SkyWarsCancelCommand(),
                new SkyWarsCooldownCommand(),
                new SkyWarsHostCommand(),
                new SkyWarsJoinCommand(),
                new SkyWarsLeaveCommand(),
                new SkyWarsSetSpawnCommand(),
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

        Arrays.asList(
                Material.WORKBENCH,
                Material.STICK,
                Material.WOOD_PLATE,
                Material.WOOD_BUTTON,
                Material.SNOW_BLOCK
        ).forEach(InventoryUtil::removeCrafting);

        getServer().getWorlds().forEach(world -> {
            world.setDifficulty(Difficulty.HARD);
            essentials.clearEntities(world);
        });

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
                new PartyListener(),
                new HotbarListener(),
                new MatchListener(),
                new WorldListener(),
                new QueueListener(),
                new PlayerMovementListener(),
                new ArmorListener()
        )) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    if (mainConfig.getBoolean("ARRAY.TAB_ENABLED") == true) {
        new OutlastTab(this, new Tab());
    }

        new Aether(this, new Scoreboard());
        new AetherOptions().hook(true);

        new QueueThread().start();

        new PlaceholderAPIExtension().register();

        SkyWarsChest.loadAll();
    }

    @Override
    public void onDisable() {
        disabling = true;
        Match.cleanup();
        for (Profile profile : Profile.getProfiles().values()) {
            profile.save();
        }
    }

    private void loadMongo() {
        MongoClient client = new MongoClient(new MongoClientURI(mainConfig.getString("Mongo.URL")));
        this.mongoDatabase = client.getDatabase(mainConfig.getString("Mongo.Database"));
    }
}
