package com.birdow.armoney;

import com.birdow.armoney.commands.ARmoneyCommand;
import com.birdow.armoney.utils.Message;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class ARmoney extends JavaPlugin {

    private static Economy econ = null;
    private static ARmoney instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - §cПлагин на экономику недоступен!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Message.load(getConfig());
        Bukkit.getPluginManager().registerEvents(new ARmoneyCommand(), this);
        getLogger().info("ARmoney by BIRDOW включён!");
    }

    @Override
    public void onDisable() {}

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return true;
    }

    public static ARmoney getInstance() { return instance; }
    public static Economy getEconomy() { return econ; }
}
