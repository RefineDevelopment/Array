package me.drizzy.practice.util.command;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.drizzy.practice.util.command.command.adapter.impl.*;
import me.drizzy.practice.util.command.map.CommandData;
import me.drizzy.practice.util.command.map.MethodData;
import me.drizzy.practice.util.command.map.ParameterData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.command.command.CommandOption;
import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class Honcho implements Listener
{
    private final JavaPlugin plugin;
    private final Map<Class, CommandTypeAdapter> adapters;
    private final Map<String, CommandData> commands;
    
    public Honcho(final JavaPlugin plugin) {
        this.adapters = new HashMap<>();
        this.commands = new HashMap<>();
        this.plugin = plugin;
        this.registerTypeAdapter(Player.class, new PlayerTypeAdapter());
        this.registerTypeAdapter(OfflinePlayer.class, new OfflinePlayerTypeAdapter());
        this.registerTypeAdapter(String.class, new StringTypeAdapter());
        this.registerTypeAdapter(Integer.class, new IntegerTypeAdapter());
        this.registerTypeAdapter(Integer.TYPE, new IntegerTypeAdapter());
        this.registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
        this.registerTypeAdapter(Boolean.TYPE, new BooleanTypeAdapter());
        this.registerTypeAdapter(World.class, new WorldTypeAdapter());
        this.registerTypeAdapter(GameMode.class, new GameModeTypeAdapter());
        this.registerTypeAdapter(CommandOption.class, new CommandOptionTypeAdapter());
        Bukkit.getPluginManager().registerEvents(this, plugin);
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.TAB_COMPLETE) {
            public void onPacketReceiving(final PacketEvent event) {
                final PacketContainer packet = event.getPacket();
                final String text = packet.getStrings().read(0);
                if (text.startsWith("/")) {
                    final List<String> completed = Honcho.this.handleTabCompletion(event.getPlayer(), text);
                    if (completed != null) {
                        event.setCancelled(true);
                        final PacketContainer response = new PacketContainer(PacketType.Play.Server.TAB_COMPLETE);
                        response.getStringArrays().write(0, completed.toArray(new String[0]));
                        try {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), response);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onServerCommandEvent(final ServerCommandEvent event) {
        if (event instanceof Cancellable) {
            try {
                final Method method = event.getClass().getDeclaredMethod("setCancelled", Boolean.TYPE);
                method.invoke(event, this.handleExecution(event.getSender(), "/" + event.getCommand()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            this.handleExecution(event.getSender(), "/" + event.getCommand());
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocessEvent(final PlayerCommandPreprocessEvent event) {
        event.setCancelled(this.handleExecution(event.getPlayer(), event.getMessage()));
    }
    
    private List<String> handleTabCompletion(final Player player, final String message) {
        final String[] messageSplit = message.substring(1).split(" ");
        CommandData commandData = null;
        String label = null;
        for (int remaining = messageSplit.length; remaining > 0; --remaining) {
            label = StringUtils.join(messageSplit, " ", 0, remaining);
            if (this.commands.get(label.toLowerCase()) != null) {
                final CommandData possibleCommand = this.commands.get(label.toLowerCase());
                if (label.split(" ").length != messageSplit.length + (message.endsWith(" ") ? 1 : 0)) {
                    commandData = possibleCommand;
                    break;
                }
            }
        }
        if (commandData != null) {
            final String[] labelSplit = label.split(" ");
            String[] args = new String[0];
            if (messageSplit.length != labelSplit.length) {
                final int numArgs = messageSplit.length - labelSplit.length;
                args = new String[numArgs];
                System.arraycopy(messageSplit, labelSplit.length, args, 0, numArgs);
            }
            return new HonchoTabCompleter(this, player, commandData, message, args).execute();
        }
        return null;
    }
    
    private boolean handleExecution(final CommandSender commandSender, final String message) {
        final String[] messageSplit = message.substring(1).split(" ");
        CommandData commandData = null;
        String label = null;
        for (int remaining = messageSplit.length; remaining > 0; --remaining) {
            label = StringUtils.join(messageSplit, " ", 0, remaining);
            if (this.commands.get(label.toLowerCase()) != null) {
                commandData = this.commands.get(label.toLowerCase());
                break;
            }
        }
        if (commandData != null) {
            final String[] labelSplit = label.split(" ");
            String[] args = new String[0];
            if (messageSplit.length != labelSplit.length) {
                final int numArgs = messageSplit.length - labelSplit.length;
                args = new String[numArgs];
                System.arraycopy(messageSplit, labelSplit.length, args, 0, numArgs);
            }
            final HonchoExecutor executor = new HonchoExecutor(this, commandSender, label.toLowerCase(), commandData, args);
            if (commandData.getMeta().async()) {
                new BukkitRunnable() {
                    public void run() {
                        executor.execute();
                    }
                }.runTaskAsynchronously(this.plugin);
            }
            else {
                executor.execute();
            }
            return true;
        }
        return false;
    }
    
    public void forceCommand(final Player player, String command) {
        if (!command.startsWith("/")) {
            command = "/" + command;
        }
        this.handleExecution(player, command);
    }
    
    public void registerTypeAdapter(final Class clazz, final CommandTypeAdapter adapter) {
        this.adapters.put(clazz, adapter);
    }
    
    public CommandTypeAdapter getTypeAdapter(final Class clazz) {
        return this.adapters.get(clazz);
    }
    
    public void registerCommand(final Object object) {
        final CommandMeta meta = object.getClass().getAnnotation(CommandMeta.class);
        if (meta == null) {
            throw new RuntimeException(new ClassNotFoundException(object.getClass().getName() + " is missing CommandMeta Annotation, Please Contact a Developer"));
        }
        final List<MethodData> methodDataList =new ArrayList<>();
        for (final Method method : object.getClass().getMethods()) {
            if (method.getParameterCount() != 0) {
                if (CommandSender.class.isAssignableFrom(method.getParameters()[0].getType())) {
                    final ParameterData[] parameterData = new ParameterData[method.getParameters().length];
                    for (int i = 0; i < method.getParameterCount(); ++i) {
                        final Parameter parameter = method.getParameters()[i];
                        parameterData[i] = new ParameterData(parameter.getName(), parameter.getType(), parameter.getAnnotation(CPL.class));
                    }
                    methodDataList.add(new MethodData(method, parameterData));
                }
            }
        }
        final CommandData commandData = new CommandData(object, meta, methodDataList.toArray(new MethodData[0]));
        for (final String label : this.getLabels(object.getClass(), new ArrayList<>())) {
            this.commands.put(label.toLowerCase(), commandData);
        }
        if (meta.autoAddSubCommands()) {
            for (final Class<?> clazz : object.getClass().getDeclaredClasses()) {
                if (clazz.getSuperclass().equals(object.getClass())) {
                    try {
                        this.registerCommand(clazz.getDeclaredConstructor(object.getClass()).newInstance(object));
                    }
                    catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    private List<String> getLabels(final Class clazz, List<String> list) {
        final List<String> toReturn = new ArrayList<>();
        final Class superClass = clazz.getSuperclass();
        if (superClass != null) {
            final CommandMeta meta = (CommandMeta)superClass.getAnnotation(CommandMeta.class);
            if (meta != null) {
                list = this.getLabels(superClass, list);
            }
        }
        final CommandMeta meta = (CommandMeta)clazz.getAnnotation(CommandMeta.class);
        if (meta == null) {
            return list;
        }
        if (list.isEmpty()) {
            toReturn.addAll(Arrays.asList(meta.label()));
        }
        else {
            for (final String prefix : list) {
                for (final String label : meta.label()) {
                    toReturn.add(prefix + " " + label);
                }
            }
        }
        return toReturn;
    }
}
