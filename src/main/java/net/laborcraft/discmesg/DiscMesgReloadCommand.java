package net.laborcraft.discmesg;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DiscMesgReloadCommand extends Command {

    private final DiscMesg plugin;

    public DiscMesgReloadCommand(DiscMesg plugin) {
        super("discmesgreload");
        super.setUsage("Uso: /discmesgreload");
        super.setPermission("discmesg.reload");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {

        if (strings.length != 0) {
            return false;
        }

        plugin.onDisable();
        plugin.onEnable();
        commandSender.sendMessage(Component.text("Plugin ricaricato.", NamedTextColor.GREEN));

        return true;
    }
}
