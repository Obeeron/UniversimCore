package com.obeeron.universim.commands;

import com.google.common.base.Preconditions;
import com.obeeron.universim.UVSCore;
import com.obeeron.universim.Universim;
import com.obeeron.universim.common.UnivItemManager;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UniversimCommand implements TabExecutor {
    private final List<String> universimIds = new ArrayList<>();

    public UniversimCommand(){
        universimIds.addAll(UnivItemManager.getInstance().getAllUnivIds());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String[] args) {
        universimCommand(sender, command, args);
        return true;
    }

    private void universimCommand(CommandSender sender, org.bukkit.command.Command command, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED+"Usage: "+command.getUsage());
            return;
        }

        String subCommand = args[0];
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        switch (subCommand) {
            case "reload" -> univReloadCommand(sender, subArgs);
            case "give" -> univGiveCommand(sender, subArgs);
            case "id" -> idCommand(sender, subArgs);
            case "showmeta" -> metaCommand(sender, subArgs);
            default -> sender.sendMessage(ChatColor.RED+"Usage: "+command.getUsage());
        }
    }

    private void univReloadCommand(CommandSender sender, String[] args) {
        boolean isPlayer = sender instanceof org.bukkit.entity.Player;
        String usage = ChatColor.RED +"Usage: /universim reload";

        if (isPlayer && !sender.hasPermission(Universim.PERM_RELOAD)) {
            sender.sendMessage(ChatColor.RED+"Permission denied. This incident will be reported.");
            return;
        }
        if (args.length != 0) {
            sender.sendMessage(usage);
            return;
        }

        Universim.getInstance().reload();
        if (isPlayer)
            sender.sendMessage(ChatColor.GREEN+"Universim reloaded!");
    }

    private void univGiveCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player)){
            sender.sendMessage(ChatColor.RED+"This command can only be used by players.");
            return;
        }
        String usage = ChatColor.RED +"Usage: /universim give <univ_id> [amount]";

        if (!sender.hasPermission(Universim.PERM_GIVE)) {
            sender.sendMessage(ChatColor.RED + "Permission denied. This incident will be reported.");
            return;
        }

        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(usage);
            return;
        }

        String univId = args[0];
        ItemStack item = UnivItemManager.getInstance().getUnivItem(UVSCore.univNSK(univId));
        if (item == null) {
            sender.sendMessage(ChatColor.RED+"Invalid univ_id\n"+usage);
            return;
        }
        item = item.clone();

        if (args.length == 2) {
            try {
                String amountStr = args[1];
                int amount = Integer.parseInt(amountStr);
                Preconditions.checkArgument(amount>0);
                item.setAmount(amount);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED+"Invalid amount\n"+usage);
                return;
            }
        }

        if (!((Player) sender).getInventory().addItem(item).isEmpty()) {
            sender.sendMessage(ChatColor.RED+"Inventory full!");
        }
    }

    private void idCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED+"This command can only be used by players.");
            return;
        }

        String usage = ChatColor.RED +"Usage: /universim id <get|remove|set>";

        if (!player.hasPermission(Universim.PERM_MANAGE_UNIVID)) {
            sender.sendMessage("§cPermission denied. This incident will be reported.");
            return;
        }

        if (args.length == 0) {
            player.sendMessage(usage);
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(ChatColor.RED+"You must be holding an item to use this command.");
            return;
        }

        String subCommand = args[0];
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        switch (subCommand) {
            case "get" -> getUnivId(player, item, subArgs);
            case "remove" -> removeUnivId(player, item, subArgs);
            case "set" -> setUnivId(player, item, subArgs);
            default -> player.sendMessage(usage);
        }
    }

    private void getUnivId(Player player, ItemStack item, String[] args) {
        if (args.length != 0) {
            player.sendMessage(ChatColor.RED + "Usage: /universim id get");
            return;
        }
        NamespacedKey nsk = UVSCore.getUnivId(item);
        if (nsk == null)
            player.sendMessage("This item doesn't have an Universim ID.");
        else
            player.sendMessage(ChatColor.GREEN+"Universim ID: "+ChatColor.ITALIC+nsk.getKey());
    }

    private void removeUnivId(Player player, ItemStack item, String[] args) {
        if (args.length != 0) {
            player.sendMessage(ChatColor.RED + "Usage: /universim id remove");
            return;
        }
        NamespacedKey nsk = UVSCore.getUnivId(item);
        if (nsk == null){
            player.sendMessage("This item doesn't have an Universim ID.");
            return;
        }
        UVSCore.removeUnivId(item);
        player.sendMessage(ChatColor.GREEN + "Universim ID " + ChatColor.ITALIC + nsk.getKey() + ChatColor.GREEN + " removed.");
    }

    private void setUnivId(Player player, ItemStack item, String[] args) {
        if (args.length != 1 || args[0] == null) {
            player.sendMessage(ChatColor.RED + "Usage: /universim id set <univ_id>");
            return;
        }
        String univId = args[0];
        UVSCore.setUnivId(item, univId);
        player.sendMessage(ChatColor.GREEN + "Universim ID set to " + ChatColor.ITALIC + args[0] + ChatColor.GREEN + " .");
    }

    private void metaCommand(CommandSender sender, String[] subArgs) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED+"This command can only be used by players.");
            return;
        }

        String usage = ChatColor.RED +"Usage: /universim showmeta";

        if (!player.hasPermission(Universim.PERM_SHOWMETA)) {
            sender.sendMessage(ChatColor.RED+"Permission denied. This incident will be reported.");
            return;
        }

        if (subArgs.length != 0) {
            player.sendMessage(usage);
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(ChatColor.RED+"You must be holding an item to use this command.");
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            player.sendMessage(ChatColor.RED+"This item doesn't have any meta.");
            return;
        }

        player.sendMessage(ChatColor.GREEN+"Item meta:\n"+meta.serialize());
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = onTabComplete2(sender, command, alias, args);
        if (completions == null)
            return null;
        return keepStartsWith(completions, args[args.length-1]);
    }

    public List<String> onTabComplete2(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equals("universim")) {
            if (args.length == 1) {
                return Arrays.asList("reload", "give", "id", "showmeta");
            } else if (args.length == 2) {
                if (args[0].equals("give")) {
                    return universimIds;
                } else if (args[0].equals("id")) {
                    return Arrays.asList("get", "remove", "set");
                }
            } else if (args.length == 3) {
                if (args[0].equals("id") && args[1].equals("set")) {
                    return universimIds;
                }
            }
        }
        return Collections.emptyList();
    }
    private List<String> keepStartsWith(List<String> list, String startsWith) {
        return list.stream().filter(s -> s.startsWith(startsWith)).collect(Collectors.toList());
    }
}
