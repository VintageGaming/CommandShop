package com.VintageGaming.CommandShop;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.milkbowl.vault.economy.Economy;

public class CSInvEvent implements Listener {
	//Regular CommandShop
	Economy eco;
	HashMap<Player, Double> prices = new HashMap<Player, Double>();
	HashMap<Player, String> permissions = new HashMap<Player, String>();

	@EventHandler
	public void shopClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (!e.getView().getTitle().equals("Command Shop")) return;
		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
		
		ItemStack purchased = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		ItemMeta pMeta = purchased.getItemMeta();
		ItemStack item = e.getCurrentItem();
		ItemMeta meta = item.getItemMeta();
		String name = meta.getDisplayName().replace("/", "");
		pMeta.setDisplayName("/" + name);
		Double price = CSMain.instance.getConfig().getDouble(name + ".price");
		
		if (!meta.getLore().get(0).equalsIgnoreCase("purchased") && !meta.getDisplayName().equalsIgnoreCase("unavailable")) {
			if (CSMain.instance.getConfig().get(name + ".permission") == null) {
				e.getWhoClicked().sendMessage(ChatColor.RED + "There is no permission set for this command!");
				e.setCancelled(true);
				return;
			}
			eco = CSMain.econ;
			//Take away Price and Add Permission
			if (CSMain.instance.getConfig().getBoolean("ConfirmPurchase")) {
				prices.put(p, price);
				permissions.put(p, CSMain.instance.getConfig().getString(name + ".permission"));
				confirmPurchase(p);
				return;
			}
			else if (eco.getBalance((OfflinePlayer) p) >= CSMain.instance.getConfig().getDouble(name + ".price")) {
				eco.withdrawPlayer(p, price);
				CSMain.perms.playerAdd(p, CSMain.instance.getConfig().getString(name + ".permission"));
				p.closeInventory();
				p.openInventory(CSCommand.craftInv(CSCommand.inv, p));
				p.sendMessage(ChatColor.GREEN + "Command Purchased!");
			}
			else {
				p.sendMessage(ChatColor.RED + "You don't have enough Money!");
				return;
			}
			e.setCancelled(true);
			return;
		}
		else if (meta.getDisplayName().equalsIgnoreCase("unavailable")) {
			e.getWhoClicked().sendMessage(ChatColor.RED + "You Don't have permission!");
			e.setCancelled(true);
			return;
		}
		else if (meta.getLore().get(0) == "purchased") {
			e.getWhoClicked().sendMessage(ChatColor.GREEN + "You Already have this Command!");
			e.setCancelled(true);
			return;
		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void confirmEvent(InventoryClickEvent e) {
		//Fix clicked item
		if (!e.getView().getTitle().equals("Confirm Purchase")) return;
		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
		Player p = (Player) e.getWhoClicked();
		if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Confirm")) {
			if (eco.getBalance((OfflinePlayer) p) >= prices.get(p)) {
				eco.withdrawPlayer(p, prices.get(p));
				CSMain.perms.playerAdd(p, permissions.get(p));
				removePlayer(p);
				e.setCancelled(true);
				p.closeInventory();
				p.openInventory(CSCommand.craftInv(CSCommand.inv, p));
				p.sendMessage(ChatColor.GREEN + "Command Purchased!");
				return;
			}
			else {
				e.setCancelled(true);
				removePlayer(p);
				p.closeInventory();
				p.sendMessage(ChatColor.RED + "You Don't have Enough Money!");
			}
		}
		else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Cancel")) {
			p.closeInventory();
			p.sendMessage(ChatColor.RED + "Transaction Canceled!");
			removePlayer(p);
			e.setCancelled(true);
			return;
		}
		else {
			e.setCancelled(true);
			return;
		}
	}
	
	public void confirmPurchase(Player p) {
		Inventory confirm = Bukkit.createInventory(null, 27, "Confirm Purchase");
		for (int i=0;i<9;i++) {
			ItemStack item;
			ItemMeta meta;
			//Move set item to after if statements
			if (i<4) {
				item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
				meta = item.getItemMeta();
				meta.setDisplayName("Confirm");
				item.setItemMeta(meta);
				//confirm.setItem(i, item);
				//confirm.setItem(i+9, item);
				//confirm.setItem(i+18, item);
			}
			else if (i==4) {
				item = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
				meta = item.getItemMeta();
				meta.setDisplayName("Divider");
				item.setItemMeta(meta);
				//confirm.setItem(i, item);
				//confirm.setItem(i+9, item);
				//confirm.setItem(i+18, item);
			}
			else {
				item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
				meta = item.getItemMeta();
				meta.setDisplayName("Cancel");
				item.setItemMeta(meta);
				//confirm.setItem(i, item);
				//confirm.setItem(i+9, item);
				//confirm.setItem(i+18, item);
			}
			confirm.setItem(i, item);
			confirm.setItem(i+9, item);
			confirm.setItem(i+18, item);
		}
		p.openInventory(confirm);
	}
	
	public void removePlayer(Player p) {
		prices.remove(p);
		permissions.remove(p);
	}
}
