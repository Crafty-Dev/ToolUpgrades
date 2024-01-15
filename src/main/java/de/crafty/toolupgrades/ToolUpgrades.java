package de.crafty.toolupgrades;

import de.crafty.toolupgrades.command.*;
import de.crafty.toolupgrades.event.PlayerApplyUpgradeListener;
import de.crafty.toolupgrades.event.UpgradeAbusePreventionListener;
import de.crafty.toolupgrades.event.UpgradeRecipeViewListener;
import de.crafty.toolupgrades.recipe.RecipeManager;
import de.crafty.toolupgrades.upgradehandler.*;
import de.crafty.toolupgrades.util.UpgradeOverview;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ToolUpgrades extends JavaPlugin {

    public static final String PREFIX = "\u00a77[\u00a75ToolUpgrades\u00a77] ";


    //TODO Make Celestial consume xp
    //TODO Enable falldamage for celestial

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
        this.getCommand("upgradeOverview").setExecutor(new CMD_upgradeOverview());

        //General Listener
        Bukkit.getPluginManager().registerEvents(new PlayerApplyUpgradeListener(), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeAbusePreventionListener(), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeRecipeViewListener(), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeOverview(), this);

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
        Bukkit.getPluginManager().registerEvents(new EnchantmentRelocationHandler(), this);
        Bukkit.getPluginManager().registerEvents(new CelestialHandler(), this);

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


    public static void runPostEventTask(Runnable runnable){
        Bukkit.getScheduler().scheduleSyncDelayedTask(ToolUpgrades.getInstance(), runnable);
    }

    public int multiMinerMaxBlocks() {
        return this.getConfig().getInt("multiMinerMaxBlocks");
    }

    public int teleportationRange() {
        return this.getConfig().getInt("teleportationRange");
    }

    public boolean softFallBlockReplacement(){
        return this.getConfig().getBoolean("softFallBlockReplacement");
    }

    public boolean celestialDrainXP(){
        return this.getConfig().getBoolean("celestialDrainXP");
    }

    public double celestialXPToDrain(){
        return this.getConfig().getDouble("celestialXPToDrain");
    }

    public boolean celestialFallDamage(){
        return this.getConfig().getBoolean("celestialFallDamage");
    }
}
