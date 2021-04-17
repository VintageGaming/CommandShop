package com.VintageGaming.CommandShop;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CSCommand implements CommandExecutor {
	//Regular CommandShop
	static Inventory inv;

	public boolean onCommand(CommandSender sender, Command command, String flag, String[] args) {
		//Check the player
		if (!(sender instanceof Player)) return true;
		Player p = (Player) sender;
		if (!p.hasPermission("cshop.buy")) {
			p.sendMessage(ChatColor.RED + "You don't have permission!");
			return true;
		}
		
		int size = formatSize(CSMain.instance.getConfig().getKeys(false).size()-1);;
		inv = Bukkit.createInventory(null, size, "Command Shop");
		inv = craftInv(inv, p);
		p.openInventory(inv);
		return true;
	}
	
	public static Inventory craftInv(Inventory inv, Player p) {
		Inventory newInv = inv;
		for (int k=1;k<CSMain.instance.getConfig().getKeys(false).size();k++) {
			ArrayList<String> commands = new ArrayList<String>(CSMain.instance.getConfig().getKeys(false));
			commands.remove(0);
			ItemStack com = new ItemStack(Material.RED_STAINED_GLASS_PANE);
			ItemMeta comMeta = com.getItemMeta();
			String price = "Invalid Price!";
			ArrayList<String> lores = new ArrayList<String>();
			if (!p.hasPermission("cshop.purchase." + commands.get(k-1)) && !p.hasPermission("cshop.purchase.*")) {
				com = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);
				comMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Unavailable");
				lores.add(0, ChatColor.RED + "You don't have permission!");
				comMeta.setLore(lores);
			}
			else if (CSMain.instance.getConfig().getString(commands.get(k-1) + ".permission") != null && p.hasPermission(CSMain.instance.getConfig().getString(commands.get(k-1) + ".permission"))) {
				com = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
				comMeta.setDisplayName("/" + commands.get(k-1));
				lores.add(0, "Purchased");
				comMeta.setLore(lores);
			}
			else {
				if (CSMain.instance.getConfig().get(commands.get(k-1) + ".price") != null) {
					price = CSMain.instance.getConfig().getString(commands.get(k-1) + ".price");
				}
				else {
					com = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);
				}
				comMeta.setDisplayName("/" + commands.get(k-1));
				lores.add(0, "$" + price);
				comMeta.setLore(lores);
			}
			com.setItemMeta(comMeta);
			newInv.setItem(k-1, com);
		}
		return newInv;
	}
	
	public int formatSize(int size) {
		if (size <=9) return 9;
		if (size <=27) return 27;
		return 54;
	}
}
