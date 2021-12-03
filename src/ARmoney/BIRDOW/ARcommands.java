package ARmoney.BIRDOW;

import net.milkbowl.vault.economy.Economy;
import java.util.List;
import com.google.common.collect.Lists;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

public class ARcommands extends CommandKit implements Listener {
	
	public boolean autoSell = false;
	public boolean pickupSell = false;
	
	public ARcommands() {
		super("ar");
	}
	
	public String lang(String way) {
		return ARmain.getInstance().getConfig().getString("messages." + way).replace("&","§");
	}

	public void execute(CommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(lang("admin.unknown_command"));
			return;
		}
		if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("armoney.reload")) {
				sender.sendMessage(lang("admin.dont_have_permission"));
				return;
			}
			ARmain.getInstance().reloadConfig();
			sender.sendMessage(lang("admin.prefix") + lang("admin.config_reloaded"));
			return;
		}
		if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(lang("admin.prefix") + "- §aversion: " + ARmain.getInstance().getDescription().getVersion() + " §7- §cby BIRDOW");
			sender.sendMessage("");
			sender.sendMessage("§5/ar sell §7[TAB] [кол.во] - " + lang("help.sell"));
			sender.sendMessage("§5/ar get §7[TAB] [кол.во] - " + lang("help.get"));
			sender.sendMessage("§5/ar autosell §7[enable/disable] - " + lang("help.autosell"));
			sender.sendMessage("§5/ar pickupsell §7[enable/disable] - " + lang("help.pickupsell"));
			sender.sendMessage("§5/balance §7- " + lang("help.bal"));
			sender.sendMessage("§5/pay §7[ник] [кол.во] - " + lang("help.pay"));
			if (sender.hasPermission("armoney.reload")) sender.sendMessage("§5/ar reload §7- " + lang("help.reload"));
			return;
		}
		
		if (sender instanceof Player) {
			Economy economy = ARmain.getEconomy();
			Player p = (Player)sender;
			Material mat = null;
			
			if (args.length < 1) {
				sender.sendMessage(lang("admin.unknown_command"));
				return;
			} else {
				if (args[0].equalsIgnoreCase("autosell")) {
					if (!autoSell) {
						autoSell = true;
						p.sendMessage(lang("main.autosell_on"));
					} else if (autoSell) {
						autoSell = false;
						p.sendMessage(lang("main.autosell_off"));
					}
					return;
				}
				else if (args[0].equalsIgnoreCase("pickupsell")) {
					if (!pickupSell) {
						pickupSell = true;
						p.sendMessage(lang("main.pickupsell_on"));
					} else if (pickupSell) {
						pickupSell = false;
						p.sendMessage(lang("main.pickupsell_off"));
					}
					return;
				}
			}
			if (args.length < 2 || args.length < 1) {
				sender.sendMessage(lang("admin.unknown_command"));
				return;
			} else {
				if (args[1].equalsIgnoreCase("diamond_ore")) mat = Material.DIAMOND_ORE;
				else if (args[1].equalsIgnoreCase("deepslate_diamond_ore")) mat = Material.DEEPSLATE_DIAMOND_ORE;
				else {
					sender.sendMessage(lang("admin.unknown_command"));
					return;
				}
			}

			if (args.length == 3) {
				try {
					int requestedDiamonds = Integer.parseInt(args[2]);
					int requestedDiamondTradeIn = Integer.parseInt(args[2]);
					if (requestedDiamondTradeIn <= 0 || requestedDiamonds <= 0) {
						p.sendMessage(lang("main.ar_positive"));
						return;
					}
					if (args[0].equalsIgnoreCase("sell")) {
						int diamondOreInInventory = 0;
						ItemStack[] var8 = p.getInventory().getContents();
						int var9 = var8.length;

						for(int var10 = 0; var10 < var9; ++var10) {
							ItemStack item = var8[var10];
							if (item != null && item.getType() == mat) {
								diamondOreInInventory += item.getAmount();
							}
						}
					
						if (requestedDiamondTradeIn > diamondOreInInventory) {
							p.sendMessage(lang("main.lack_of_ore").replace("{count}", String.valueOf(requestedDiamondTradeIn - diamondOreInInventory)));
						} else {
							p.getInventory().removeItem(new ItemStack[]{new ItemStack(mat, requestedDiamondTradeIn)});
							economy.depositPlayer(p, requestedDiamondTradeIn);
							p.sendMessage(lang("main.sell_success").replace("{count}", String.valueOf(requestedDiamondTradeIn)));
						}
					} else if (args[0].equalsIgnoreCase("get")) {
						if (requestedDiamonds > economy.getBalance(p)) {
							p.sendMessage(lang("main.lack_of_ar").replace("{count}", String.valueOf(requestedDiamonds - economy.getBalance(p))));
						} else {
							economy.withdrawPlayer(p, requestedDiamonds);
							p.getInventory().addItem(new ItemStack[]{new ItemStack(mat, requestedDiamonds)});
							p.sendMessage(lang("main.get_success").replace("{count}", String.valueOf(requestedDiamonds)));
						}
					}
				} catch (Exception error) {
					sender.sendMessage(lang("main.be_a_number"));
				}
			}
		} else sender.sendMessage(lang("admin.prefix") + "§cЭта команда может быть исполниена только игроком!");
	}
	
	@EventHandler
	public void blockBreak (BlockBreakEvent e) {
		if (autoSell) {
			Player p = e.getPlayer();
			if (p.getGameMode() != GameMode.CREATIVE && p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
				int diamondOreCost = 1;
				Block b = e.getBlock();
				Material mat = b.getType();
				Economy economy = ARmain.getEconomy();
				if (mat.equals(Material.DIAMOND_ORE) || mat.equals(Material.DEEPSLATE_DIAMOND_ORE)) {
					e.setDropItems(false);
					economy.depositPlayer(p, diamondOreCost);
					p.sendMessage(lang("main.auto_add").replace("{count}", String.valueOf(diamondOreCost)));
				}
			}
		}
	}
	@EventHandler
	public void onPickup​(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player && pickupSell) {
			Player p = (Player) e.getEntity();
			if (p.getGameMode() != GameMode.CREATIVE && e.getItem().getItemStack().getType() == Material.DIAMOND_ORE || e.getItem().getItemStack().getType() == Material.DEEPSLATE_DIAMOND_ORE) {
				ItemStack itemToRemove = e.getItem().getItemStack();
				Economy economy = ARmain.getEconomy();
				e.setCancelled(true);
			    e.getItem().remove();
				economy.depositPlayer(p, itemToRemove.getAmount());
				p.sendMessage(lang("main.auto_add").replace("{count}", String.valueOf(itemToRemove.getAmount())));
			}
		}
	}
	
	public List<String> complete(CommandSender sender, String[] args) {
		if (args.length == 1) return Lists.newArrayList("help", "reload", "sell", "get", "autosell", "pickupsell");
		if (args[0].equalsIgnoreCase("sell") || args[0].equalsIgnoreCase("get")) {
			if (args.length == 2 && args.length != 1) return Lists.newArrayList("diamond_ore", "deepslate_diamond_ore");
		}
		return Lists.newArrayList();
	}
}