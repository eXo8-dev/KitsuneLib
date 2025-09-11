package me.eXo8_.kitsunelib.command;

import me.eXo8_.kitsunelib.KitsuneLib;
import me.eXo8_.kitsunelib.utils.ColorUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiConsumer;

public class Command implements CommandExecutor, TabCompleter
{
    private final String name;
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private BiConsumer<CommandSender, String[]> executor;
    private boolean playerOnly = false;
    private String permission;

    public Command(String name) {
        this.name = name.toLowerCase();
    }

    public Command setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public Command register()
    {
        var command = KitsuneLib.getPlugin().getCommand(name);
        if (command == null) throw new RuntimeException("Command not found in plugin.yml: " + name);
        command.setTabCompleter(this);
        command.setExecutor(this);
        return this;
    }

    public Command setPlayerOnly(boolean playerOnly)
    {
        this.playerOnly = playerOnly;
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
        if (playerOnly && !(sender instanceof Player))
        {
            sender.sendMessage(ColorUtil.parse("&cOnly players can execute this command!"));
            return true;
        }

        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission))
        {
            sender.sendMessage(ColorUtil.parse("&cYou do not have permission to execute this command!"));
            return true;
        }

        if (args.length == 0 && !subCommands.isEmpty())
        {
            if (executor != null) executor.accept(sender, args);
            return true;
        }

        SubCommand sub = subCommands.get(args[0].toLowerCase());

        if (sub != null)
        {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            sub.execute(sender, subArgs);
        }
        else sender.sendMessage(ColorUtil.parse("&cUnknown subcommand."));

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args)
    {
        if (args.length == 0) return Collections.emptyList();

        if (args.length == 1)
        {
            List<String> completions = new ArrayList<>();
            String input = args[0].toLowerCase();

            Set<SubCommand> seen = new HashSet<>();

            for (SubCommand sub : subCommands.values())
                if (seen.add(sub) && sub.getName().toLowerCase().startsWith(input))
                    completions.add(sub.getName());

            return completions;
        }

        SubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub != null) return sub.tabComplete(Arrays.copyOfRange(args, 1, args.length));

        return Collections.emptyList();
    }

    public static class Builder implements me.eXo8_.kitsunelib.builder.Builder<Command>
    {
        private final Command command;

        private Builder(String name) {
            this.command = new Command(name);
        }

            public static Builder of(String name) {
            return new Builder(name);
        }

            public Builder permission(String permission)
            {
                command.setPermission(permission);
                return this;
            }

            public Builder playerOnly()
            {
                command.setPlayerOnly(true);
                return this;
            }

            public Builder executor(BiConsumer<CommandSender, String[]> executor)
            {
                command.setExecutor(executor);
                return this;
            }

            public Builder subCommand(SubCommand sub)
            {
                command.addSubCommand(sub);
                return this;
            }

            public Builder playerOnly(boolean v)
            {
                command.setPlayerOnly(v);
                return this;
            }

            public Builder register()
            {
                command.register();
                return this;
            }

            @Override
            public Command build() {
            return command;
        }
    }
}