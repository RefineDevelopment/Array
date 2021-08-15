package me.drizzy.practice.util.command;

import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;
import me.drizzy.practice.util.command.map.ParameterData;
import me.drizzy.practice.util.command.map.MethodData;
import me.drizzy.practice.util.command.command.CommandOption;
import java.util.List;
import me.drizzy.practice.util.command.map.CommandData;
import org.bukkit.command.CommandSender;

public class HonchoTabCompleter
{
    private final Honcho honcho;
    private final CommandSender sender;
    private final CommandData commandData;
    private final String fullMessage;
    private final String[] args;
    
    public HonchoTabCompleter(final Honcho honcho, final CommandSender sender, final CommandData commandData, final String fullMessage, final String[] args) {
        this.honcho = honcho;
        this.sender = sender;
        this.commandData = commandData;
        this.fullMessage = fullMessage;
        this.args = args;
    }
    
    public List<String> execute() {
        if (!this.commandData.getMeta().permission().equalsIgnoreCase("") && !this.sender.hasPermission(this.commandData.getMeta().permission())) {
            return null;
        }
        for (final MethodData methodData : this.commandData.getMethodData()) {
            if (methodData.getParameterData().length != 1) {
                final int paramsLength = methodData.getParameterData().length - 1;
                if (this.args.length <= paramsLength) {
                    int offset = 1;
                    final String[] args = this.args;
                    if (args.length == 0) {
                        ++offset;
                    }
                    if (paramsLength >= 2 && methodData.getParameterData()[1].getType() == CommandOption.class && args.length != 0 && !args[0].startsWith("-")) {
                        ++offset;
                    }
                    if (args.length <= paramsLength) {
                        final String source = this.fullMessage.endsWith(" ") ? "" : args[args.length - 1];
                        final ParameterData parameterData = methodData.getParameterData()[args.length + offset - 1];
                        final CommandTypeAdapter adapter = this.honcho.getTypeAdapter(parameterData.getType());
                        if (adapter != null) {
                            return adapter.tabComplete(source, parameterData.getType());
                        }
                    }
                }
            }
        }
        return null;
    }
}
