package me.eXo8_.kitsunelib.command;

import me.eXo8_.kitsunelib.KitsuneLib;
import me.eXo8_.kitsunelib.utils.ColorUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;
import java.util.function.BiConsumer;

public class Command implements CommandExecutor, TabCompleter
{
    private final String name;
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private BiConsumer<CommandSender, String[]> executor;
    private String permission;

    public Command(String name)
    {
        this.name = name.toLowerCase();

        var command = KitsuneLib.getPlugin().getCommand(name);
        if (command == null) throw new RuntimeException("Command not found in plugin.yml: " + name);

        command.setTabCompleter(this);
        command.setExecutor(this);
    }

    public Command setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public Command setExecutor(BiConsumer<CommandSender, String[]> executor) {
        this.executor = executor;
        return this;
    }

    public Command addSubCommand(SubCommand sub)
    {
        subCommands.put(sub.getName().toLowerCase(), sub);
        for (String alias : sub.getAliases()) subCommands.put(alias.toLowerCase(), sub);
        return this;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
    {
        if (permission != null && !sender.hasPermission(permission))
        {
            sender.sendMessage(ColorUtil.parse("&cYou do not have permission to execute this command!"));
            return true;
        }

        if (args.length == 0)
        {
            if (executor != null) executor.accept(sender, args);
            return true;
        }

        SubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub != null) sub.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        else sender.sendMessage(ColorUtil.parse("&cUnknown subcommand."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            List<String> completions = new ArrayList<>();

            for (SubCommand sub : subCommands.values())
                if (sub.getName().toLowerCase().startsWith(args[0].toLowerCase())) completions.add(sub.getName());

            return completions;
        }
        return Collections.emptyList();
    }
}