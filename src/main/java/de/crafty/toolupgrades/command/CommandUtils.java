package de.crafty.toolupgrades.command;

import de.crafty.toolupgrades.recipe.RecipeManager;
import de.crafty.toolupgrades.upgrade.ToolUpgrade;
import de.crafty.toolupgrades.upgrade.UpgradeItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CommandUtils {


    protected static Player getTarget(String[] args, int expectedPlayerArg, Player defaultPlayer) {

        if (args.length <= expectedPlayerArg)
            return defaultPlayer;


        for (Player player : Bukkit.getOnlinePlayers()) {
            if (args[expectedPlayerArg].equals(player.getName()))
                return player;
        }

        return null;
    }

    protected static void fetchPlayerNames(String arg, List<String> completionList) {
        Bukkit.getOnlinePlayers().stream().filter(player -> player.getName().toUpperCase().startsWith(arg.toUpperCase())).forEach(player -> completionList.add(player.getName()));
    }

    protected static void fetchUpgrades(String arg, List<String> completionList) {
        Arrays.stream(ToolUpgrade.values()).filter(upgrade -> upgrade.name().startsWith(arg.toUpperCase())).forEach(upgrade -> completionList.add(upgrade.name().toLowerCase()));
    }

    protected static void fetchUpgradeItems(String arg, List<String> completionList) {
        UpgradeItem.list().stream().filter(upgradeItem -> upgradeItem.getId().toUpperCase().startsWith(arg.toUpperCase())).forEach(upgradeItem -> completionList.add(upgradeItem.getId()));
    }

    protected static void fetchUpgradeRecipes(String arg, List<String> completionList) {
        RecipeManager.recipes().stream().filter(recipe -> recipe.getId().toUpperCase().startsWith(arg.toUpperCase())).forEach(recipe -> completionList.add(recipe.getId()));
    }

}
