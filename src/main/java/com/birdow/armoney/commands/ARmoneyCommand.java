package com.birdow.armoney.commands;

import com.birdow.armoney.ARmoney;
import com.birdow.armoney.utils.AbstractCommand;
import com.birdow.armoney.utils.Message;
import com.google.common.collect.Lists;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import static com.birdow.armoney.utils.Utils.color;


public class ARmoneyCommand extends AbstractCommand implements Listener {

    public boolean isAutoSell = false;
    public boolean isPickupSell = false;

    public ARmoneyCommand() { super("armoney"); }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            Message.unknown_command.send(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("ARmoney.reload")){
                Message.dont_have_permission.send(sender);
                return;
            }

            ARmoney.getInstance().reloadConfig();
            Message.config_reloaded.send(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("help")) {
            String pluginVersion = ARmoney.getInstance().getDescription().getVersion();
            sender.sendMessage(color("&8[&5ARmoney&8] &7- &aversion: ") + pluginVersion + color(" &7- &cby BIRDOW\n"));
            sender.sendMessage(color("&5/ar sell &7[кол.во] - &fПоложить руду на счёт."));
            sender.sendMessage(color("&5/ar get &7[TAB] [кол.во] - &fСнять руду со счёта."));
            sender.sendMessage(color("&5/ar autosell - &aВкл&f/&cВыкл&f автопродажу добываемой руды."));
            sender.sendMessage(color("&5/ar pickupsell - &aВкл&f/&cВыкл&f автопродажу подбираемой руды."));
            sender.sendMessage(color("&5/balance &7- &fУзнать количество &aАР &fна счёту."));
            sender.sendMessage(color("&5/pay &7[ник] [кол.во] - &fОтправить игроку &aАР&f."));
            if (sender.hasPermission("ARmoney.reload"))
                sender.sendMessage(color("&5/ar reload &7- &fВыполнить перезагрузку плагина."));
            return;
        }

        if (!(sender instanceof Player)) {
            Message.only_player.send(sender);
            return;
        }
        Player p = (Player) sender;

        if (args[0].equalsIgnoreCase("autosell")) {
            isAutoSell = !isAutoSell;
            if (isAutoSell) Message.autosell_on.send(p);
            else Message.autosell_off.send(p);
            return;
        }

        if (args[0].equalsIgnoreCase("pickupsell")) {
            isPickupSell = !isPickupSell;
            if (isPickupSell) Message.pickupsell_on.send(p);
            else Message.pickupsell_off.send(p);
            return;
        }

        if (args[0].equalsIgnoreCase("sell")) {
            if (args.length < 2) {
                Message.unknown_command.send(sender);
                return;
            }

            Economy economy = ARmoney.getEconomy();
            int diamondOreInInventory = 0;
            int deepslateDiamondOreInInventory = 0;

            for (ItemStack item : p.getInventory().getContents()) {
                if (item != null) {
                    if (item.getType() == Material.DIAMOND_ORE) diamondOreInInventory += item.getAmount();
                    if (item.getType() == Material.DEEPSLATE_DIAMOND_ORE)
                        deepslateDiamondOreInInventory += item.getAmount();
                }
            }

            int requestedDiamondTradeIn;
            try {
                if (args[1].equalsIgnoreCase("all"))
                    requestedDiamondTradeIn = diamondOreInInventory + deepslateDiamondOreInInventory;
                else requestedDiamondTradeIn = Integer.parseInt(args[1]);
                if (requestedDiamondTradeIn <= 0) throw new IllegalArgumentException();
            } catch (Exception e) {
                Message.invalid_price.send(p);
                return;
            }

            if (requestedDiamondTradeIn > diamondOreInInventory + deepslateDiamondOreInInventory) {
                Message.lack_of_ore.replace("{count}", String.valueOf(requestedDiamondTradeIn - (diamondOreInInventory + deepslateDiamondOreInInventory))).send(p);
                return;
            } else {
                if (diamondOreInInventory >= deepslateDiamondOreInInventory) {
                    if (diamondOreInInventory >= requestedDiamondTradeIn) {
                        p.getInventory().removeItem(new ItemStack(Material.DIAMOND_ORE, requestedDiamondTradeIn));
                    } else {
                        p.getInventory().removeItem(new ItemStack(Material.DEEPSLATE_DIAMOND_ORE, requestedDiamondTradeIn - diamondOreInInventory));
                        p.getInventory().removeItem(new ItemStack(Material.DIAMOND_ORE, requestedDiamondTradeIn));
                    }
                } else {
                    if (deepslateDiamondOreInInventory >= requestedDiamondTradeIn) {
                        p.getInventory().removeItem(new ItemStack(Material.DEEPSLATE_DIAMOND_ORE, requestedDiamondTradeIn));
                    } else {
                        p.getInventory().removeItem(new ItemStack(Material.DIAMOND_ORE, requestedDiamondTradeIn - deepslateDiamondOreInInventory));
                        p.getInventory().removeItem(new ItemStack(Material.DEEPSLATE_DIAMOND_ORE, requestedDiamondTradeIn));
                    }
                }
                economy.depositPlayer(p, requestedDiamondTradeIn);
                Message.sell_success.replace("{count}", String.valueOf(requestedDiamondTradeIn)).send(p);
            }
            return;
        }
        if (args[0].equalsIgnoreCase("get")) {
            Material material;
            if (args.length < 2) {
                Message.unknown_command.send(sender);
                return;
            } else {
                if (args[1].equalsIgnoreCase("diamond_ore")) material = Material.DIAMOND_ORE;
                else if (args[1].equalsIgnoreCase("deepslate_diamond_ore")) material = Material.DEEPSLATE_DIAMOND_ORE;
                else {
                    Message.unknown_command.send(sender);
                    return;
                }
            }

            Economy economy = ARmoney.getEconomy();

            int requestedDiamonds;
            try {
                if (args[2].equalsIgnoreCase("all")) requestedDiamonds = (int) economy.getBalance(p);
                else requestedDiamonds = Integer.parseInt(args[2]);
                if (requestedDiamonds <= 0) throw new IllegalArgumentException();
            } catch (Exception e) {
                Message.invalid_price.send(p);
                return;
            }
            if (requestedDiamonds > economy.getBalance(p)) {
                Message.lack_of_ar.replace("{count}", String.valueOf(requestedDiamonds - economy.getBalance(p))).send(p);
                return;
            } else {
                @NotNull HashMap<Integer, ItemStack> overflow = p.getInventory().addItem(new ItemStack(material, requestedDiamonds));
                if (!overflow.isEmpty()) {
                    for (ItemStack i : overflow.values()) {
                        economy.withdrawPlayer(p, requestedDiamonds - i.getAmount());
                        Message.get_success.replace("{count}", String.valueOf(requestedDiamonds - i.getAmount())).send(p);
                        Message.inventory_is_full.replace("{count}", String.valueOf(i.getAmount())).send(p);
                    } return;
                } else {
                    Message.get_success.replace("{count}", String.valueOf(requestedDiamonds)).send(p);
                }
            } return;
        }
        Message.unknown_command.send(sender);
    }

    @EventHandler
    public void blockBreak (BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE && e.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) && isAutoSell) {
            Player p = e.getPlayer();
            int diamondOreCost = 1;
            Material mat = e.getBlock().getType();
            Economy economy = ARmoney.getEconomy();
            if (mat.equals(Material.DIAMOND_ORE) || mat.equals(Material.DEEPSLATE_DIAMOND_ORE)) {
                e.setDropItems(false);
                economy.depositPlayer(p, diamondOreCost);
                Message.auto_add.replace("{count}", String.valueOf(diamondOreCost)).send(p);
            }
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player && isPickupSell) {
            Player p = (Player) e.getEntity();
            if (p.getGameMode() != GameMode.CREATIVE && (e.getItem().getItemStack().getType() == Material.DIAMOND_ORE || e.getItem().getItemStack().getType() == Material.DEEPSLATE_DIAMOND_ORE)) {
                ItemStack itemToRemove = e.getItem().getItemStack();
                Economy economy = ARmoney.getEconomy();
                e.setCancelled(true);
                e.getItem().remove();
                economy.depositPlayer(p, itemToRemove.getAmount());
                Message.auto_add.replace("{count}", String.valueOf(itemToRemove.getAmount())).send(p);
            }
        }
    }

    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) return Lists.newArrayList("help", "reload", "sell", "get", "autosell", "pickupsell");
        if ((args[0].equalsIgnoreCase("sell") && args.length == 2) || (args[0].equalsIgnoreCase("get") && args.length == 3)) return Lists.newArrayList("all", "<amount>");
        if (args[0].equalsIgnoreCase("get") && args.length == 2) return Lists.newArrayList("diamond_ore", "deepslate_diamond_ore");
        return Lists.newArrayList();
    }
}
