package ARmoney.main;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.economy.Economy;

public class ARmain extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");
	private static ARmain instance;
	private static Economy econ = null;
	
	public void onEnable() {
		saveDefaultConfig();
		if (!setupEconomy() ) {
			log.severe(String.format("§cПлагин на экономику недоступен!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		instance = this;
		Bukkit.getPluginManager().registerEvents(new ARcommands(), this);
		//new ARcommands();
		log.info("§7[§5ARmoney§7] Enabled!");
	}
	
	public void onDisable() {
		log.info("§7[§5ARmoney§7] Disabled!");
	}
	
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) return false;

		econ = rsp.getProvider();
		return econ != null;
	}
	public static ARmain getInstance() {
		return instance;
	}
	
	public static Economy getEconomy() {
		return econ;
	}
}
