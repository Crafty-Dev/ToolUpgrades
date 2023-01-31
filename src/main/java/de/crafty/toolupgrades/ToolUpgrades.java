package de.crafty.toolupgrades;

import de.crafty.toolupgrades.command.CMD_applyUpgrade;
import de.crafty.toolupgrades.command.CMD_removeUpgrade;
import de.crafty.toolupgrades.command.CMD_upgrade;
import de.crafty.toolupgrades.command.CMD_upgradeRecipe;
import de.crafty.toolupgrades.event.PlayerApplyUpgradeListener;
import de.crafty.toolupgrades.event.UpgradeAbusePreventionListener;
import de.crafty.toolupgrades.event.UpgradeRecipeViewListener;
import de.crafty.toolupgrades.recipe.RecipeManager;
import de.crafty.toolupgrades.upgradehandler.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ToolUpgrades extends JavaPlugin {

    public static final String PREFIX = "\u00a77[\u00a75ToolUpgrades\u00a77] ";


    private static ToolUpgrades instance;

    @Override
    public void onEnable() {

        instance = this;

        RecipeManager.init();

        this.saveDefaultConfig();


        Bukkit.getConsoleSender().sendMessage(PREFIX + "Registering Listeners...");
        this.getCommand("applyUpgrade").setExecutor(new CMD_applyUpgrade());
        this.getCommand("removeUpgrade").setExecutor(new CMD_removeUpgrade());
        this.getCommand("upgrade").setExecutor(new CMD_upgrade());
        this.getCommand("upgradeRecipe").setExecutor(new CMD_upgradeRecipe());

        //General Listener
        Bukkit.getPluginManager().registerEvents(new PlayerApplyUpgradeListener(), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeAbusePreventionListener(), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeRecipeViewListener(), this);

        //Upgrade Handler
        Bukkit.getPluginManager().registerEvents(new MagnetismHandler(), this);
        Bukkit.getPluginManager().registerEvents(new AutoSmeltingHandler(), this);
        Bukkit.getPluginManager().registerEvents(new MultiMinerHandler(), this);
        Bukkit.getPluginManager().registerEvents(new TeleportationHandler(), this);
        Bukkit.getPluginManager().registerEvents(new FadelessHandler(), this);
        Bukkit.getPluginManager().registerEvents(new SoftFallHandler(), this);
        Bukkit.getPluginManager().registerEvents(new EnderMaskHandler(), this);
        Bukkit.getPluginManager().registerEvents(new MobCaptureHandler(), this);
        Bukkit.getPluginManager().registerEvents(new SilkyHandler(), this);
        Bukkit.getPluginManager().registerEvents(new LifeBonusHandler(), this);


        Bukkit.getConsoleSender().sendMessage(PREFIX + "\u00a7aPlugin enabled");
    }


    @Override
    public void onDisable() {

        RecipeManager.saveRecipes();

        Bukkit.getConsoleSender().sendMessage(PREFIX + "\u00a7cPlugin disabled");
    }


    public static ToolUpgrades getInstance() {
        return instance;
    }


    public int multiMinerMaxBlocks() {
        return this.getConfig().getInt("multiMinerMaxBlocks");
    }

    public int teleportationRange() {
        return this.getConfig().getInt("teleportationRange");
    }
}
