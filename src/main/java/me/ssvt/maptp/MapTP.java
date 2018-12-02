package me.ssvt.maptp;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ssvt.maptp.commands.SpawnTeleport;
import me.ssvt.maptp.events.MapTeleport;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MapTP extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("tpspawn").setExecutor(new SpawnTeleport());
        getServer().getPluginManager().registerEvents(new MapTeleport(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
