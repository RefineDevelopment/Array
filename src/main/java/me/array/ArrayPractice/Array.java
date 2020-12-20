package me.array.ArrayPractice;

import cc.outlast.tablist.OutlastTab;
import com.bizarrealex.aether.Aether;
import com.bizarrealex.aether.AetherOptions;
import me.array.ArrayPractice.arena.command.*;
import me.array.ArrayPractice.event.impl.brackets.command.*;
import me.array.ArrayPractice.event.impl.lms.command.*;
import me.array.ArrayPractice.event.impl.spleef.command.*;
import me.array.ArrayPractice.event.impl.sumo.command.*;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.kit.KitEditorListener;
import me.array.ArrayPractice.kit.command.*;
import me.array.ArrayPractice.movement.PlayerMovementListener;
import me.array.ArrayPractice.profile.command.*;
import me.array.ArrayPractice.profile.runnables.SaveRunnable;
import me.array.ArrayPractice.tournament.command.*;
import org.bukkit.plugin.RegisteredServiceProvider;
import me.array.ArrayPractice.queue.QueueThread;
import me.array.ArrayPractice.scoreboard.ScoreboardAdapter;
import me.array.ArrayPractice.tab.Tab;
import org.bukkit.plugin.Plugin;
import me.array.ArrayPractice.util.events.ArmorListener;
import me.array.ArrayPractice.queue.QueueListener;
import me.array.ArrayPractice.util.events.WorldListener;
import me.array.ArrayPractice.match.MatchListener;
import me.array.ArrayPractice.profile.hotbar.HotbarListener;
import me.array.ArrayPractice.party.PartyListener;
import me.array.ArrayPractice.event.impl.spleef.SpleefListener;
import me.array.ArrayPractice.event.impl.parkour.ParkourListener;
import me.array.ArrayPractice.event.impl.lms.FFAListener;
import me.array.ArrayPractice.event.impl.brackets.BracketsListener;
import me.array.ArrayPractice.event.impl.sumo.SumoListener;
import me.array.ArrayPractice.util.essentials.listener.EssentialsListener;
import me.array.ArrayPractice.util.external.menu.MenuListener;
import me.array.ArrayPractice.profile.ProfileListener;
import org.bukkit.event.Listener;
import org.bukkit.Difficulty;
import me.array.ArrayPractice.util.InventoryUtil;
import org.bukkit.Material;
import me.array.ArrayPractice.util.external.duration.DurationTypeAdapter;
import me.array.ArrayPractice.util.external.duration.Duration;

import java.io.File;
import java.util.Arrays;

import me.array.ArrayPractice.event.impl.parkour.command.ParkourTpCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourSetSpawnCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourLeaveCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourJoinCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourHostCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourCooldownCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourCancelCommand;
import me.array.ArrayPractice.party.command.PartyBanCommand;
import me.array.ArrayPractice.party.command.PartyUnbanCommand;
import me.array.ArrayPractice.party.command.PartyLeaderCommand;
import me.array.ArrayPractice.party.command.PartyOpenCommand;
import me.array.ArrayPractice.party.command.PartyLeaveCommand;
import me.array.ArrayPractice.party.command.PartyKickCommand;
import me.array.ArrayPractice.party.command.PartyJoinCommand;
import me.array.ArrayPractice.party.command.PartyInviteCommand;
import me.array.ArrayPractice.party.command.PartyInfoCommand;
import me.array.ArrayPractice.party.command.PartyHelpCommand;
import me.array.ArrayPractice.party.command.PartyDisbandCommand;
import me.array.ArrayPractice.party.command.PartyCreateCommand;
import me.array.ArrayPractice.party.command.PartyCloseCommand;
import me.array.ArrayPractice.match.command.StopSpectatingCommand;
import me.array.ArrayPractice.match.command.SpectateCommand;
import me.array.ArrayPractice.match.command.ViewInventoryCommand;
import me.array.ArrayPractice.duel.command.RematchCommand;
import me.array.ArrayPractice.duel.command.DuelAcceptCommand;
import me.array.ArrayPractice.duel.command.DuelCommand;
import me.array.ArrayPractice.profile.command.donator.FlyCommand;
import me.array.ArrayPractice.profile.command.chat.BroadcastCommand;
import me.array.ArrayPractice.event.EventCommand;
import me.array.ArrayPractice.profile.stats.command.LeaderboardsCommand;
import me.array.ArrayPractice.profile.stats.command.StatsCommand;
import me.array.ArrayPractice.profile.command.staff.RemoveProfileCommand;
import me.array.ArrayPractice.profile.command.staff.SetSpawnCommand;
import me.array.ArrayPractice.kit.KitTypeAdapter;
import me.array.ArrayPractice.arena.ArenaTypeTypeAdapter;
import me.array.ArrayPractice.arena.ArenaType;
import me.array.ArrayPractice.arena.ArenaTypeAdapter;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.profile.hotbar.Hotbar;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.EntityHider;
import com.qrakn.honcho.Honcho;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import net.milkbowl.vault.chat.Chat;
import me.array.ArrayPractice.util.essentials.Essentials;
import me.array.ArrayPractice.event.impl.spleef.SpleefManager;
import me.array.ArrayPractice.event.impl.parkour.ParkourManager;
import me.array.ArrayPractice.event.impl.lms.FFAManager;
import me.array.ArrayPractice.event.impl.brackets.BracketsManager;
import me.array.ArrayPractice.event.impl.sumo.SumoManager;
import com.qrakn.phoenix.lang.file.type.BasicConfigurationFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Array extends JavaPlugin
{
    private static Array Array;
    private BasicConfigurationFile mainConfig;
    private BasicConfigurationFile arenasConfig;
    private BasicConfigurationFile kitsConfig;
    private BasicConfigurationFile eventsConfig;
    private MongoDatabase database;
    private SumoManager sumoManager;
    private BracketsManager bracketsManager;
    private FFAManager ffaManager;
    private ParkourManager parkourManager;
    private SpleefManager spleefManager;
    private Essentials essentials;
    private Chat chat;
    private Honcho honcho;
    private EntityHider entityHider;

    public void onEnable() {
        Array=this;
        this.honcho=new Honcho(this);
        this.mainConfig=new BasicConfigurationFile(this, "config");
        this.arenasConfig=new BasicConfigurationFile(this, "arenas");
        this.kitsConfig=new BasicConfigurationFile(this, "kits");
        this.eventsConfig=new BasicConfigurationFile(this, "events");
        this.loadMongo();
        Profile.init();
        Kit.init();
        Arena.init();
        Hotbar.init();
        Match.init();
        Party.init();
        this.essentials=new Essentials(this);
        this.honcho.registerTypeAdapter(Arena.class, new ArenaTypeAdapter());
        this.honcho.registerTypeAdapter(ArenaType.class, new ArenaTypeTypeAdapter());
        this.honcho.registerTypeAdapter(Kit.class, new KitTypeAdapter());
        for ( final Object command : Arrays.asList(new TournamentCommand(), new SpawnCommand(), new BracketsKnockbackCommand(), new FFAKnockbackCommand(), new SpleefKnockbackCommand(), new SumoKnockbackCommand(), new KitCommand(), new KitSaveCommand(), new KitRemoveCommand(), new SetSpawnCommand(), new ArrayCommand(), new StatsCommand(), new PracticeCommand(), new PingCommand(), new RemoveProfileCommand(), new TsbCommand(), new TduelCommand(), new TpmCommand(), new ArenaGenerateCommand(), new ArenaCommand(), new ArenaSaveCommand(), new LeaderboardsCommand(), new OptionsCommand(), new EventCommand(), new BroadcastCommand(), new FlyCommand(), new ArenaAddKitCommand(), new ArenaRemoveKitCommand(), new ArenaSetSpawnCommand(), new ArenaSetPointCommand(), new ArenaCreateCommand(), new ArenaRemoveCommand(), new ArenasCommand(), new ArenaTpCommand(), new DuelCommand(), new DuelAcceptCommand(), new RematchCommand(), new ViewInventoryCommand(), new SpectateCommand(), new StopSpectatingCommand(), new PartyCloseCommand(), new PartyCreateCommand(), new PartyDisbandCommand(), new PartyHelpCommand(), new PartyInfoCommand(), new PartyInviteCommand(), new PartyJoinCommand(), new PartyKickCommand(), new PartyLeaveCommand(), new PartyOpenCommand(), new PartyLeaderCommand(), new PartyUnbanCommand(), new PartyBanCommand(), new KitCreateCommand(), new KitGetLoadoutCommand(), new KitSetLoadoutCommand(), new KitListCommand(), new BracketsLeaveCommand(), new BracketsCancelCommand(), new BracketsCooldownCommand(), new BracketsJoinCommand(), new BracketsSetSpawnCommand(), new BracketsHostCommand(), new BracketsTpCommand(), new SumoCancelCommand(), new SumoCooldownCommand(), new SumoHostCommand(), new SumoJoinCommand(), new SumoLeaveCommand(), new SumoSetSpawnCommand(), new SumoTpCommand(), new FFACancelCommand(), new FFACooldownCommand(), new FFAHostCommand(), new FFAJoinCommand(), new FFALeaveCommand(), new FFASetSpawnCommand(), new FFATpCommand(), new ParkourCancelCommand(), new ParkourCooldownCommand(), new ParkourHostCommand(), new ParkourJoinCommand(), new ParkourLeaveCommand(), new ParkourSetSpawnCommand(), new ParkourTpCommand(), new SpleefCancelCommand(), new SpleefCooldownCommand(), new SpleefHostCommand(), new SpleefJoinCommand(), new SpleefLeaveCommand(), new SpleefSetSpawnCommand(), new SpleefTpCommand(), new TournamentListCommand(), new TournamentLeaveCommand(), new TournamentJoinCommand(), new TournamentHostCommand(), new TournamentCancelCommand()) ) {
            this.honcho.registerCommand(command);
        }
        this.honcho.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        this.sumoManager=new SumoManager();
        this.bracketsManager=new BracketsManager();
        this.ffaManager=new FFAManager();
        this.parkourManager=new ParkourManager();
        this.spleefManager=new SpleefManager();
        this.entityHider=EntityHider.enable();
        Arrays.asList(Material.WORKBENCH, Material.STICK, Material.WOOD_PLATE, Material.WOOD_BUTTON, Material.SNOW_BLOCK).forEach(InventoryUtil::removeCrafting);
        this.getServer().getWorlds().forEach(world -> {
            world.setDifficulty(Difficulty.HARD);
            this.essentials.clearEntities(world);
        });
        Arrays.asList(new PlayerMovementListener(), new ProfileListener(), new MenuListener(this), new EssentialsListener(this), (Listener) new SumoListener(), (Listener) new BracketsListener(), (Listener) new FFAListener(), (Listener) new ParkourListener(), (Listener) new SpleefListener(), (Listener) new KitEditorListener(), (Listener) new PartyListener(), (Listener) new HotbarListener(), (Listener) new MatchListener(), (Listener) new WorldListener(), (Listener) new QueueListener(), (Listener) new ArmorListener()).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, (Plugin) this));
        new OutlastTab(this, new Tab());
        new Aether(this, new ScoreboardAdapter(), new AetherOptions().hook(true));
        new QueueThread().start();
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveRunnable(), 10L * 40L * 5L, 10L * 40L * 5L);
    }

    public void onDisable() {
        Match.cleanup();
        Kit.getKits().forEach(Kit::save);
        Profile.getProfiles().values().forEach(Profile::save);
    }

    private void loadMongo() {
        MongoClient client=new MongoClient(new MongoClientURI(mainConfig.getString("Mongo.URL")));
        this.database = client.getDatabase(mainConfig.getString("Mongo.Database"));
    }

    public static Array get() {
        return Array;
    }

    public boolean setupChat() {
        final RegisteredServiceProvider<Chat> rsp = (RegisteredServiceProvider<Chat>)this.getServer().getServicesManager().getRegistration((Class)Chat.class);
        this.chat = rsp.getProvider();
        return this.chat != null;
    }

    public BasicConfigurationFile getMainConfig() {
        return this.mainConfig;
    }

    public BasicConfigurationFile getArenasConfig() {
        return this.arenasConfig;
    }

    public BasicConfigurationFile getKitsConfig() {
        return this.kitsConfig;
    }

    public BasicConfigurationFile getEventsConfig() {
        return this.eventsConfig;
    }

    public MongoDatabase getMongoDatabase() {
        return this.database;
    }

    public SumoManager getSumoManager() {
        return this.sumoManager;
    }

    public BracketsManager getBracketsManager() {
        return this.bracketsManager;
    }

    public FFAManager getFfaManager() {
        return this.ffaManager;
    }

    public ParkourManager getParkourManager() {
        return this.parkourManager;
    }

    public SpleefManager getSpleefManager() {
        return this.spleefManager;
    }

    public Essentials getEssentials() {
        return this.essentials;
    }

    public Chat getChat() {
        return this.chat;
    }

    public Honcho getHoncho() {
        return this.honcho;
    }

    public EntityHider getEntityHider() {
        return this.entityHider;
    }
}
