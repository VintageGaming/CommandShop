package com.VintageGaming.CommandShop;

import java.io.File;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

public class CreateConfig {
	//Regular CommandShop
	public static void create(Plugin plugin) {
	try{
		if(!plugin.getDataFolder().exists()){
			plugin.getDataFolder().mkdirs();
		}
		CSMain.config = new File(plugin.getDataFolder(), "config.yml");
		if(!CSMain.config.exists()){
			plugin.getLogger().info("config.yml not found, creating....");
			plugin.saveDefaultConfig();
		}else{
			plugin.getLogger().info("config.yml found, loadinig.....");
		}
	} catch(Exception e){
		e.printStackTrace();
	}
	}
	
	public static void registerPerms(Plugin plugin) {
		for (String com : plugin.getConfig().getKeys(false)) {
			if (com.equalsIgnoreCase("ConfrimPurchase")) continue;
			plugin.getServer().getPluginManager().addPermission(new Permission("cshop.purchase." + com));
		}
	}
}
