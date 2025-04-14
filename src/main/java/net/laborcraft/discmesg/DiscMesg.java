package net.laborcraft.discmesg;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

public final class DiscMesg extends JavaPlugin {

    private JDA jda;
    private TextChannel targetChannel;

    @Override
    public void onEnable() {

        //load config
        saveDefaultConfig();
        String guildId = getConfig().getString("guild-id");
        String channelName = getConfig().getString("channel-name");
        String botToken = getConfig().getString("bot-token");
        if(guildId == null || channelName == null || botToken == null || guildId.isEmpty() || channelName.isEmpty() || botToken.isEmpty()) {
            getLogger().severe("Missing config values! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            jda = JDABuilder.createDefault(botToken)
                    .build();
            jda.awaitReady();

            Guild guild = jda.getGuildById(guildId);
            if (guild == null) {
                getLogger().severe("Bot not in specified guild! Disabling...");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            targetChannel = guild.getTextChannelsByName(channelName, true)
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (targetChannel == null) {
                getLogger().severe("Channel \""+ channelName +"\" not found! Disabling...");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            Bukkit.getCommandMap().register("discmesg", new DiscMesgCommand(this));
            Bukkit.getCommandMap().register("discmesg", new DiscMesgReloadCommand(this));

            getLogger().info("DiscMesg enabled.");

        } catch (InterruptedException e) {
            getLogger().severe("Initialization failed: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (jda != null) {
            // Force shutdown and wait for completion
            jda.shutdownNow(); // More aggressive shutdown
            try {
                // Wait max 3 seconds for shutdown
                if (!jda.awaitShutdown(Duration.ofSeconds(3))) {
                    getLogger().warning("JDA took too long to shut down!");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // Additional cleanup
        getServer().getAsyncScheduler().cancelTasks(this);
        getServer().getServicesManager().unregisterAll(this);
    }

    public TextChannel getTargetChannel() { return targetChannel; }
}
