package net.laborcraft.discmesg;

import me.clip.placeholderapi.PlaceholderAPI;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DiscMesgCommand extends Command {

    private final DiscMesg plugin;

    public DiscMesgCommand(DiscMesg plugin) {
        super("discmesg");
        super.setUsage("Uso: /discmesg <messaggio>");
        super.setPermission("discmesg.use");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {

        if (strings.length == 0) {
            return false;
        }

        String message = String.join(" ", strings);

        String formatted;
        if(commandSender instanceof Player player){
            formatted = PlaceholderAPI.setPlaceholders(player, message);
        } else {
            formatted = PlaceholderAPI.setPlaceholders(null, message);
        }

        Bukkit.getAsyncScheduler().runNow(plugin, t -> {
            TextChannel channel = plugin.getTargetChannel();
            if (channel != null) {
                plugin.getLogger().info("[" + commandSender.getName() + " -> " + channel.getName() + "] " + formatted);
                channel.sendMessage(formatted).queue(
                        success -> {},
                        error -> plugin.getLogger().warning("Message failed: " + error.getMessage())
                );
            }
        });

        return true;
    }
}