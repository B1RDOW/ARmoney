package com.birdow.armoney.utils;

import com.google.common.collect.Lists;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Message {
    inventory_is_full, lack_of_ore, sell_success, lack_of_ar, get_success, invalid_price, auto_add, autosell_on,
    autosell_off, pickupsell_on, pickupsell_off, only_player, unknown_command, dont_have_permission, config_reloaded;


    private List<String> msg;

    @SuppressWarnings("unchecked")
    public static void load(FileConfiguration c) {
        for (Message message : Message.values()) {
            Object obj = c.get("messages." + message.name());
            if (obj instanceof List) {
                message.msg = (((List<String>) obj)).stream().map(Utils::color).collect(Collectors.toList());
            } else {
                message.msg = Lists.newArrayList(obj == null ? "§8[§5ARmoney§8]§с Сообщение не найдено!" : Utils.color(obj.toString()));
            }
        }
    }

    public Sender replace(String from, String to) {
        Sender sender = new Sender();
        return sender.replace(from, to);
    }

    public void send(CommandSender player) {
        new Sender().send(player);
    }

    public class Sender {
        private final Map<String, String> placeholders = new HashMap<>();

        public void send(CommandSender player) {
            for (String message : Message.this.msg) {
                sendMessage(player, replacePlaceholders(message));
            }
        }

        public Sender replace(String from, String to) {
            placeholders.put(from, to);
            return this;
        }

        private void sendMessage(CommandSender player, String message) { player.sendMessage(message); }

        private String replacePlaceholders(String message) {
            if (!message.contains("{")) return message;
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
            return message;
        }
    }
}