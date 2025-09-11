package me.eXo8_.kitsunelib.command;

import me.eXo8_.kitsunelib.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SubCommand
{
    private final String name;
    private final List<String> aliases = new ArrayList<>();
    private BiConsumer<CommandSender, Object[]> parsedExecutor;
    private BiConsumer<CommandSender, String[]> rawExecutor;
    private String permission;
    private Function<String[], List<String>> tabCompleter;
    private boolean playerOnly = false;
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final List<Argument> arguments = new ArrayList<>();

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

    public SubCommand setParsedExecutor(BiConsumer<CommandSender, Object[]> parsedExecutor)
    {
        this.parsedExecutor = parsedExecutor;
        return this;
    }

    public SubCommand setPlayerOnly(boolean playerOnly)
    {
        this.playerOnly = playerOnly;
        return this;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public SubCommand setRawExecutor(BiConsumer<CommandSender, String[]> rawExecutor)
    {
        this.rawExecutor = rawExecutor;
        return this;
    }

    public SubCommand setTabCompleter(java.util.function.Function<String[], List<String>> completer) {
        this.tabCompleter = completer;
        return this;
    }

    public SubCommand addSubCommand(SubCommand sub)
    {
        subCommands.put(sub.getName().toLowerCase(), sub);
        for (String alias : sub.getAliases()) subCommands.put(alias.toLowerCase(), sub);
        return this;
    }

    public SubCommand addArgument(Argument arg) {
        arguments.add(arg);
        return this;
    }

    public BiConsumer<CommandSender, String[]> getRawExecutor() {
        return rawExecutor;
    }

    public BiConsumer<CommandSender, Object[]> getParsedExecutor() {
        return parsedExecutor;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }

    public Object[] parseArguments(String[] inputs)
    {
        Object[] parsed = new Object[arguments.size()];

        for (int i = 0; i < arguments.size(); i++)
        {
            Argument arg = arguments.get(i);

            String input = i < inputs.length ? inputs[i] : null;

            try {parsed[i] = arg.parse(input);}
            catch (IllegalArgumentException e) {throw new IllegalArgumentException("Error parsing argument '" + arg.name + "': " + e.getMessage());}
        }

        return parsed;
    }

    public void execute(CommandSender sender, String[] args)
    {
        if (playerOnly && !(sender instanceof Player))
        {
            sender.sendMessage(ColorUtil.parse("&cOnly players can execute this command!"));
            return;
        }

        if (permission != null && !sender.hasPermission(permission))
        {
            sender.sendMessage(ColorUtil.parse("&cYou do not have permission to execute this command!"));
            return;
        }

        if (args.length > 0)
        {
            SubCommand sub = subCommands.get(args[0].toLowerCase());

            if (sub != null)
            {
                sub.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                return;
            }
        }

        if (parsedExecutor != null)
        {
            try
            {
                Object[] parsed = parseArguments(args);
                parsedExecutor.accept(sender, parsed);
            }
            catch (IllegalArgumentException e) {sender.sendMessage(ColorUtil.parse("&c" + e.getMessage()));}
        }
        if (rawExecutor != null) rawExecutor.accept(sender, args);
        if (parsedExecutor == null && rawExecutor == null && args.length == 0) sender.sendMessage(ColorUtil.parse("&cNo executor defined for this subcommand."));
    }

    public List<String> tabComplete(String[] args)
    {
        if (tabCompleter != null) return tabCompleter.apply(args);

        if (args.length == 0)
        {
            if (!arguments.isEmpty()) return completeArgument(arguments.get(0), "");
            return new ArrayList<>(subCommands.keySet());
        }

        if (args.length == 1)
        {
            String input = args[0].toLowerCase();

            if (!subCommands.isEmpty())
            {
                List<String> completions = new ArrayList<>();
                for (SubCommand sub : new HashSet<>(subCommands.values()))
                    if (sub.getName().toLowerCase().startsWith(input))
                        completions.add(sub.getName());
                if (!completions.isEmpty()) return completions;
            }

            if (!arguments.isEmpty()) return completeArgument(arguments.get(0), args[0]);
        }

        SubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub != null) return sub.tabComplete(Arrays.copyOfRange(args, 1, args.length));

        int index = args.length - 1;

        if (index < arguments.size())
        {
            Argument arg = arguments.get(index);
            return completeArgument(arg, args[index]);
        }

        return Collections.emptyList();
    }

    private List<String> completeArgument(Argument arg, String input)
    {
        List<String> completions = new ArrayList<>();

        switch (arg.type) {
            case PLAYER -> completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            case WORLD -> completions.addAll(Bukkit.getWorlds().stream().map(World::getName).toList());
            case BOOLEAN -> completions.addAll(Arrays.asList("true", "false"));
            case MATERIAL -> completions.addAll(Arrays.stream(Material.values()).map(Enum::name).toList());
            case ENUM -> {
                if (arg.enumClass != null) completions.addAll(Arrays.stream(arg.enumClass.getEnumConstants()).map(Enum::name).toList());
            }
            case OFFLINE_PLAYER -> completions.addAll(
                    Arrays.stream(Bukkit.getOfflinePlayers())
                            .map(OfflinePlayer::getName)
                            .filter(Objects::nonNull)
                            .toList()
            );
            default -> {}
        }

        String lower = input.toLowerCase();
        completions.removeIf(c -> !c.toLowerCase().startsWith(lower));

        return completions;
    }


    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }



    public static class Argument
    {
        public final String name;
        public final Type type;
        public final boolean required;

        public final @Nullable Class<? extends Enum<?>> enumClass;


        public Argument(String name, Class<? extends Enum<?>> enumClass, boolean required)
        {
            if (name == null || name.isEmpty()) throw new IllegalArgumentException("Argument name cannot be null or empty");
            if (enumClass == null) throw new IllegalArgumentException("Enum class cannot be null");

            this.name = name;
            this.type = Type.ENUM;
            this.required = required;
            this.enumClass = enumClass;
        }

        public Argument(String name, Type type, boolean required)
        {
            if (name == null || name.isEmpty()) throw new IllegalArgumentException("Argument name cannot be null or empty");
            if (type == null) throw new IllegalArgumentException("Argument type cannot be null");

            this.name = name;
            this.type = type;
            this.required = required;
            this.enumClass = null;
        }

        public Object parse(String input)
        {
            if ((input == null || input.isEmpty()) && !required) return null;
            if ((input == null || input.isEmpty()) && required) throw new IllegalArgumentException("Missing required argument: " + name);
            switch (type) {
                case STRING -> { return input; }
                case INTEGER -> { try { return Integer.parseInt(input); } catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid integer for argument '" + name + "': " + input); } }
                case DOUBLE -> { try { return Double.parseDouble(input); } catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid double for argument '" + name + "': " + input); } }
                case BOOLEAN -> { return Boolean.parseBoolean(input); }
                case PLAYER -> { Player player = Bukkit.getPlayer(input); if (player == null) throw new IllegalArgumentException("Player not found: " + input); return player; }
                case OFFLINE_PLAYER -> { return Bukkit.getOfflinePlayer(input); }
                case UUID -> { try { return UUID.fromString(input); } catch (IllegalArgumentException e) { throw new IllegalArgumentException("Invalid UUID for argument '" + name + "': " + input); } }
                case WORLD -> { World world = Bukkit.getWorld(input); if (world == null) throw new IllegalArgumentException("World not found: " + input); return world; }
                case MATERIAL -> { Material mat = Material.getMaterial(input.toUpperCase()); if (mat == null) throw new IllegalArgumentException("Material not found: " + input); return mat; }
                case ENUM -> { if (enumClass == null) throw new IllegalArgumentException("Enum class not set for argument: " + name); try { return Enum.valueOf((Class) enumClass, input.toUpperCase()); } catch (Exception e) { throw new IllegalArgumentException("Invalid value for enum " + enumClass.getSimpleName() + ": " + input); } }
                default -> throw new IllegalArgumentException("Unknown argument type: " + type);
            }
        }

        public enum Type {
            STRING, INTEGER, DOUBLE, BOOLEAN, PLAYER, OFFLINE_PLAYER, UUID, WORLD, MATERIAL, ENUM;
        }
    }

    public static class Builder implements me.eXo8_.kitsunelib.builder.Builder<SubCommand>
    {
        private final SubCommand subCommand;

        private Builder(String name) {
            this.subCommand = new SubCommand(name);
        }

        public static Builder of(String name) {
            return new Builder(name);
        }

        public Builder alias(String... aliases)
        {
            subCommand.setAliases(aliases);
            return this;
        }

        public Builder permission(String permission)
        {
            subCommand.setPermission(permission);
            return this;
        }

        public Builder playerOnly()
        {
            subCommand.setPlayerOnly(true);
            return this;
        }

        public Builder executor(BiConsumer<CommandSender, Object[]> parsedExecutor)
        {
            subCommand.setParsedExecutor(parsedExecutor);
            return this;
        }

        public Builder rawExecutor(BiConsumer<CommandSender, String[]> rawExecutor)
        {
            subCommand.setRawExecutor(rawExecutor);
            return this;
        }

        public Builder argument(String name, SubCommand.Argument.Type type, boolean required)
        {
            subCommand.addArgument(new SubCommand.Argument(name, type, required));
            return this;
        }

        public Builder argument(String name, Class<? extends Enum<?>> enumClass, boolean required)
        {
            subCommand.addArgument(new SubCommand.Argument(name, enumClass, required));
            return this;
        }

        public Builder subCommand(SubCommand sub)
        {
            subCommand.addSubCommand(sub);
            return this;
        }

        public Builder tabCompleter(Function<String[], List<String>> completer) {
            subCommand.setTabCompleter(completer);
            return this;
        }

        @Override
        public SubCommand build() {
            return subCommand;
        }
    }
}
