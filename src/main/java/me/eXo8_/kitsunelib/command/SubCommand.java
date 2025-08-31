package me.eXo8_.kitsunelib.command;

import me.eXo8_.kitsunelib.utils.ColorUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class SubCommand
{
    private final String name;
    private final List<String> aliases = new ArrayList<>();
    private BiConsumer<CommandSender, String[]> executor;
    private String permission;

    public SubCommand(String name) {
        this.name = name.toLowerCase();
    }

    public SubCommand setAliases(String... aliases)
    {
        this.aliases.clear();
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    public SubCommand setPermission(String permission)
    {
        this.permission = permission;
        return this;
    }

    public SubCommand setExecutor(BiConsumer<CommandSender, String[]> executor)
    {
        this.executor = executor;
        return this;
    }

    public void execute(CommandSender sender, String[] args)
    {
        if (permission != null && !sender.hasPermission(permission))
        {
            sender.sendMessage(ColorUtil.parse("&cYou do not have permission to execute this command!"));
            return;
        }

        if (executor != null) executor.accept(sender, args);
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
