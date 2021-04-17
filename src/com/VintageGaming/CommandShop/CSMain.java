package com.VintageGaming.CommandShop;

import java.io.File;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.economy.Economy;

public class CSMain extends JavaPlugin {
	//Regular CommandShop
	static File config;
	static Plugin instance;
	static Economy econ;
	static Permission perms;
	
	@Override
	public void onEnable() {
		if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		setupPermissions();
		CreateConfig.create(this);
		instance = this;
		CreateConfig.registerPerms(this);
		getServer().getPluginManager().registerEvents(new CSInvEvent(), this);
		getCommand("cshop").setExecutor(new CSCommand());
		getLogger().info("CommandShop Enabled!");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("CommandShop Disabled!");
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	private boolean setupPermissions() {
		if (getServer().getPluginManager().isPluginEnabled("Vault")) {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
		}
		return false;
    }
	
}
