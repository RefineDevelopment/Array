package me.drizzy.practice.util.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.command.command.CommandOption;
import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;
import me.drizzy.practice.util.command.map.CommandData;
import me.drizzy.practice.util.command.map.MethodData;
import me.drizzy.practice.util.command.map.ParameterData;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class HonchoExecutor
{
    private final Honcho honcho;
    private final CommandSender sender;
    private final String label;
    private final CommandData commandData;
    private String[] args;

    public HonchoExecutor(final Honcho honcho, final CommandSender sender, final String label, final CommandData commandData, final String[] args) {
        this.honcho = honcho;
        this.sender = sender;
        this.label = label;
        this.commandData = commandData;
        this.args = args;
    }

    public void execute() {
        if (!this.commandData.getMeta().permission().equalsIgnoreCase("") && !this.sender.hasPermission(this.commandData.getMeta().permission())) {
            this.sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
            return;
        }
        for (final MethodData methodData : this.commandData.getMethodData()) {
            if (methodData.getMethod().getDeclaringClass().equals(this.commandData.getInstance().getClass())) {
                if (methodData.getParameterData().length - 1 > this.args.length) {
                    boolean doContinue=true;
                    for ( final ParameterData parameterData : methodData.getParameterData() ) {
                        if (parameterData.getType().equals(CommandOption.class) && methodData.getParameterData().length - 2 <= this.args.length) {
                            doContinue=false;
                            break;
                        }
                    }
                    if (doContinue) {
                        break;
                    }
                }
                for ( final MethodData otherMethodData : this.commandData.getMethodData() ) {
                    if (!otherMethodData.equals(methodData)) {
                        if (methodData.getParameterData().length == otherMethodData.getParameterData().length && methodData.getParameterData()[0].getType().equals(CommandSender.class) && otherMethodData.getParameterData()[0].getType().equals(Player.class) && this.sender instanceof Player) {
                            break;
                        }
                        if (this.args.length != methodData.getParameterData().length - 1 && this.args.length - methodData.getParameterData().length > this.args.length - otherMethodData.getParameterData().length) {
                            break;
                        }
                    }
                }
                if (methodData.getParameterData().length > 0 && (methodData.getParameterData()[0].getType().equals(CommandSender.class) || methodData.getParameterData()[0].getType().equals(Player.class))) {
                    List<Object> arguments=new ArrayList<>();
                    ParameterData[] parameters=methodData.getParameterData();

                    arguments.add(sender);

                    if (methodData.getParameterData()[0].getType().equals(Player.class) && !(sender instanceof Player)) {
                        continue;
                    }

                    for ( int i=1; i < parameters.length; i++ ) {
                        ParameterData parameter=parameters[i];
                        CommandTypeAdapter adapter=honcho.getTypeAdapter(parameter.getType());

                        if (adapter == null) {
                            arguments.add(null);
                            continue;
                        }

                        Object object;
                        if (i == (parameters.length - 1)) {
                            object=adapter.convert(StringUtils.join(args, " ", i - 1, args.length), parameter.getType());
                        } else {
                            object=adapter.convert(args[i - 1], parameter.getType());
                        }

                        if (parameter.getType().equals(CommandOption.class) && object == null) {
                            List<String> replacement=new ArrayList<>(Arrays.asList(args));
                            replacement.add(i - 1, null);
                            args=replacement.toArray(new String[0]);
                        }

                        if (object instanceof CommandOption) {
                            CommandOption option=(CommandOption) object;
                            if (!(Arrays.asList(this.commandData.getMeta().options())).contains(option.getTag().toLowerCase())) {
                                sender.sendMessage(ChatColor.RED + "Unrecognized command option \"-" + option.getTag().toLowerCase() + "\"!");
                                break;
                            }
                        }

                        arguments.add(object);
                    }

                    if (arguments.size() == parameters.length) {
                        try {
                            methodData.getMethod().invoke(this.commandData.getInstance(), arguments.toArray());
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        return;
                    }
                }

            }
        }
    this.sender.sendMessage(this.getUsage());
    }


    private String getUsage() {
        final StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.RED).append("Usage: /").append(this.label);
        if (this.commandData.getMeta().options().length > 0) {
            final List<String> options = new ArrayList<>();
            for (final String option : this.commandData.getMeta().options()) {
                options.add("-" + option.toLowerCase());
            }
            builder.append(" [");
            builder.append(StringUtils.join(options, ","));
            builder.append("]");
        }
        final Map<Integer, List<String>> arguments = new HashMap<>();
        for (final MethodData methodData : this.commandData.getMethodData()) {
            final ParameterData[] parameters = methodData.getParameterData();
            for (int i = 1; i < parameters.length; ++i) {
                final List<String> argument = arguments.getOrDefault(i - 1, new ArrayList<>());
                final ParameterData parameterData = parameters[i];
                if (parameterData.getType().equals(CommandOption.class)) {
                    arguments.put(i - 1, null);
                }
                else {
                    if (parameterData.getCpl() != null) {
                        argument.add(parameterData.getCpl().value().toLowerCase());
                    }
                    else {
                        final String name = parameterData.getName();
                        if (!argument.contains(name)) {
                            argument.add(name);
                        }
                    }
                    arguments.put(i - 1, argument);
                }
            }
        }
        for (int j = 0; j < arguments.size(); ++j) {
            final List<String> argument2 = arguments.get(j);
            if (argument2 != null) {
                builder.append(" <").append(StringUtils.join(argument2, "/")).append(">");
            }
        }
        return builder.toString();
    }
}
