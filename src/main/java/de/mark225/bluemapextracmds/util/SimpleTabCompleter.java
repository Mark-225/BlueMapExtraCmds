package de.mark225.bluemapextracmds.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public interface SimpleTabCompleter extends TabCompleter {


    public default List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        onTabComplete(sender, command, alias, args, completions);
        if(args.length >= 1){
            completions.removeIf(string -> !string.startsWith(args[args.length-1]));
        }
        return completions;
    }

    public void onTabComplete(CommandSender sender, Command command, String alias, String[] args, List<String> completions);
}
