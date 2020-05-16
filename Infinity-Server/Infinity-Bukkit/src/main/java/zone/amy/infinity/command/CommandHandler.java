package zone.amy.infinity.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class CommandHandler implements Listener {
    private static int PAGE_SIZE = 7;

    public static List<String> paginate(String title, List<String> in, int page) {
        int max_pages = (in.size()/(PAGE_SIZE+1) + 1);
        if (page > max_pages - 1) page = max_pages - 1;
        else if (page < 0) page = 0;
        List<String> out = new ArrayList<>();
        out.add(ChatColor.GREEN + "====== " + ChatColor.DARK_GREEN + title + ChatColor.GREEN + " ======");
        out.addAll(in.subList(PAGE_SIZE * page, Math.min(in.size(), PAGE_SIZE * (page+1))));
        out.add(ChatColor.GREEN + "====== Page " + (page + 1) + " of " + max_pages + " ======");
        return out;
    }

    @Getter(AccessLevel.PUBLIC) List<Command> registeredCommands = new ArrayList<>();

    public void registerCommand(Class<? extends Command> command) {
        try {
            registeredCommands.add(command.getConstructor(CommandHandler.class).newInstance(this));
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Unable to construct command \"" + command.getName() + "\"", e);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to resolve command tree on command \"" + command.getName() + "\"", e);
        }
    }

    public void unregisterCommand(Class<? extends Command> command) {
        Set<Command> commands = registeredCommands.stream().filter(command1 -> command1.getClass() == command).collect(Collectors.toSet());
        if (commands.size() > 0) {
            commands.forEach(registeredCommands::remove);
            return;
        }
        throw new IllegalStateException("Tried to unregister command that was not registered.");
    }

    @EventHandler
    public void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/")) return;

        Player sender = event.getPlayer();

        String[] components = event.getMessage().substring(1, event.getMessage().length()).split(" ");

        Command command = matchCommand(components[0], registeredCommands);
        if (command == null) return;
        boolean helpMode = components.length > 1 && (components[components.length - 2].equalsIgnoreCase("help") || components[components.length - 1].equalsIgnoreCase("help"));
        int page = 0;
        if (helpMode && !components[components.length - 1].equalsIgnoreCase("help")) {
            try {
                page = Integer.parseInt(components[components.length - 1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Page must be a number.");
                return;
            }
            components = Arrays.asList(components).subList(0, components.length - 2).toArray(new String[components.length - 2]);
        } else if (helpMode) {
            components = Arrays.asList(components).subList(0, components.length - 1).toArray(new String[components.length - 1]);
        }

        List<ScheduledCommand> scheduledCommands = new ArrayList<>();
        List<Command> chain = new ArrayList<>();

        boolean syntaxWrong = false;

        try {
            Map<String, Object> data = new HashMap<>();
            int cursor = 0;
            int position = 0;
            while (cursor < components.length) {
                if (!sender.hasPermission(command.getMeta().permission())) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use the " + ChatColor.DARK_RED + command.getMeta().names()[0] + ChatColor.RED + " command");
                    syntaxWrong = true;
                    break;
                }
                if (command.getSubCommands().size() == 0 && components.length > cursor + command.getMeta().argumentCount() + 1) {
                    sender.sendMessage(ChatColor.RED + "Too many arguments! Proper usage: " + ChatColor.DARK_RED + "/..." + command.getUsage());
                    syntaxWrong = true;
                    break;
                }
                chain.add(command);
                scheduledCommands.add(new ScheduledCommand(sender, command, Arrays.copyOfRange(components, cursor += 1, Math.min(components.length, cursor += command.getMeta().argumentCount())), position++));

                if (cursor < components.length) {
                    Command lastCommand = command;
                    command = matchCommand(components[cursor], command.getSubCommands());
                    if (command == null) {
                        sender.sendMessage(ChatColor.RED + "Invalid subcommand - for a list of subcommands use " + ChatColor.DARK_RED + "/..." + lastCommand.getUsage() + " help [page]");
                        syntaxWrong = true;
                        break;
                    }
                }
            }

            if (!syntaxWrong) {
                Command[] chainArray = chain.toArray(new Command[chain.size()]);

                for (ScheduledCommand scheduledCommand : scheduledCommands) {
                    if (helpMode) {
                        if (scheduledCommand.getPosition() == scheduledCommands.size() - 1) {
                            StringBuilder humanTrail = new StringBuilder();
                            humanTrail.append(ChatColor.GRAY).append("/");
                            for (Command previousCommand : chain.subList(0, scheduledCommand.getPosition() + 1)) {
                                humanTrail.append(previousCommand.getUsage()).append(" ");
                            }

                            List<String> out = new ArrayList<>();
                            for (Command subCommand : scheduledCommand.getCommand().getSubCommands()) {
                                if (sender.hasPermission(subCommand.getMeta().permission())) {
                                    out.add(humanTrail.toString() + ChatColor.GREEN + subCommand.getUsage() + ChatColor.WHITE + " - " + ChatColor.GREEN + ChatColor.ITALIC + subCommand.getMeta().description());
                                }
                            }

                            if (out.size() == 0) {
                                String syntax = scheduledCommand.getCommand().getMeta().syntax();
                                if (syntax.equals("")) {
                                    sender.sendMessage(ChatColor.RED + "There is no further help available for this command.");
                                } else {
                                    sender.sendMessage(ChatColor.GREEN + "Command Syntax: " + humanTrail.toString());
                                }
                            } else {
                                out = paginate("Command Help", out, page-1);
                                sender.sendMessage(out.toArray(new String[out.size()]));
                            }
                        }
                    } else if (!scheduledCommand.run(data, chainArray)) {
                        break;
                    }
                }

                if (!helpMode) {
                    List<Command> reversedChain = new ArrayList<>(chain);
                    Collections.reverse(reversedChain);
                    for (Command command1 : reversedChain) {
                        List<Command> subTrail = chain.subList(chain.indexOf(command1), chain.size());
                        command1.onTreeFinished(sender, subTrail.toArray(new Command[subTrail.size()]), data);
                    }
                }
            }

            event.setCancelled(true);
        } catch (Exception e) {
            event.getPlayer().sendMessage(ChatColor.RED + "An internal error occurred while running this command.");
            e.printStackTrace();
        }
    }

    private Command matchCommand(String keyword, Collection<Command> commands) {
        for (Command command : commands) {
            if (Arrays.asList(command.getMeta().names()).contains(keyword)) return command;
        }
        return null;
    }

    @Getter
    @AllArgsConstructor
    private static class ScheduledCommand {
        private Player sender;
        private Command command;
        private String[] arguments;
        private int position;

        private boolean run(Map<String, Object> data, Command[] chain) {
            return command.run(sender, arguments, chain, data, position == chain.length - 1);
        }
    }
}
