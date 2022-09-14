package me.justacat.luxurycombat;

import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public final class LuxuryCombat extends JavaPlugin {

    public static LuxuryCombat instance;

    @Override
    public void onEnable() {

        instance = this;

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new Events(), this);

        Events.combat = CacheBuilder.newBuilder().expireAfterWrite(getConfig().getLong("combatTime"), TimeUnit.SECONDS).build();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static String colorMessage(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
