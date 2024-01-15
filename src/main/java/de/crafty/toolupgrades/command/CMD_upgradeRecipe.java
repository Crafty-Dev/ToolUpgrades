package de.crafty.toolupgrades.command;

import de.crafty.toolupgrades.ToolUpgrades;
import de.crafty.toolupgrades.recipe.RecipeManager;
import de.crafty.toolupgrades.recipe.UpgradeRecipe;
import de.crafty.toolupgrades.upgrade.UpgradeItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CMD_upgradeRecipe implements TabExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!"upgradeRecipe".equals(cmd.getName()) || !(sender instanceof Player player))
            return false;


        if (args.length == 1 && args[0].equalsIgnoreCase("reset")) {

            if(!player.isOp()){
                player.sendMessage(ToolUpgrades.PREFIX + "\u00a7cOnly Admins are authorized to reset the Upgrade Recipes");
                return true;
            }

            player.sendMessage(ToolUpgrades.PREFIX + "Type /upgraderecipe reset \u00a7bconfirm \u00a77to reset all recipes");
            return true;
        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("reset") && args[1].equals("confirm")) {

                if(!player.isOp()){
                    player.sendMessage(ToolUpgrades.PREFIX + "\u00a7cOnly Admins are authorized to reset the Upgrade Recipes");
                    return true;
                }

                RecipeManager.reset();
                player.sendMessage(ToolUpgrades.PREFIX + "Upgrade Recipes have been resetted");
                return true;
            }

            if (args[0].equalsIgnoreCase("show")) {
                for (UpgradeRecipe recipe : RecipeManager.recipes()) {
                    if (!recipe.getId().equalsIgnoreCase(args[1]))
                        continue;

                    RecipeManager.openRecipeView(player, recipe);
                    return true;
                }
                player.sendMessage(ToolUpgrades.PREFIX + "Could not find recipe for \u00a7c" + args[1]);
                return true;
            }

            if (args[0].equalsIgnoreCase("delete")) {

                if (!player.isOp()) {
                    player.sendMessage(ToolUpgrades.PREFIX + "\u00a7cOnly Admins are authorized to delete Upgrade Recipes");
                    return true;
                }

                for (UpgradeRecipe recipe : RecipeManager.recipes()) {
                    if (!recipe.getId().equalsIgnoreCase(args[1]))
                        continue;

                    RecipeManager.deleteRecipe(recipe);
                    player.sendMessage(ToolUpgrades.PREFIX + "Recipe: \u00a7c" + recipe.getId() + " \u00a77has been deleted");
                    return true;
                }
                player.sendMessage(ToolUpgrades.PREFIX + "Could not find recipe for \u00a7c" + args[1]);
                return true;
            }

        }

        if (args.length == 4) {

            if (!args[0].equalsIgnoreCase("create"))
                return false;

            if(!player.isOp()){
                player.sendMessage(ToolUpgrades.PREFIX + "\u00a7cOnly Admins are authorized to create new Upgrade Recipes");
                return true;
            }

            for (UpgradeRecipe recipe : RecipeManager.recipes()) {
                if (recipe.getId().equalsIgnoreCase(args[3])) {
                    player.sendMessage(ToolUpgrades.PREFIX + "Recipe Id \u00a7c" + recipe.getId() + " \u00a77is already used");
                    return true;
                }
            }

            UpgradeItem item = null;
            for (UpgradeItem upgradeItem : UpgradeItem.list()) {
                if (upgradeItem.getId().equalsIgnoreCase(args[1]))
                    item = upgradeItem;
            }

            if (item == null) {
                player.sendMessage(ToolUpgrades.PREFIX + "Could not find Upgrade Item: \u00a7c" + args[1]);
                return true;
            }

            try {
                UpgradeRecipe.Type type = UpgradeRecipe.Type.valueOf(args[2].toUpperCase());

                switch (type) {

                    case SHAPED, SHAPELESS -> {
                        Inventory inv = Bukkit.createInventory(null, InventoryType.WORKBENCH, "\u00a77Creator: " + item.getUpgrade().getDisplayName());
                        RecipeManager.CREATORS.put(inv, new UpgradeRecipe.CreationData(args[3], type, item));

                        inv.setItem(0, item.getStack());
                        player.openInventory(inv);
                    }

                    case SMELTING, BLASTING, SMOKING -> {
                        Inventory inv = Bukkit.createInventory(null, InventoryType.FURNACE, "\u00a77Creator: " + item.getUpgrade().getDisplayName());
                        RecipeManager.CREATORS.put(inv, new UpgradeRecipe.CreationData(args[3], type, item));

                        inv.setItem(1, new ItemStack(Material.COAL, 64));
                        inv.setItem(2, item.getStack());
                        player.openInventory(inv);
                    }

                }

            } catch (Exception e) {
                player.sendMessage(ToolUpgrades.PREFIX + "Invalid Recipe Type: \u00a7c" + args[2]);
                return true;
            }
        }


        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        List<String> list = new ArrayList<>();

        if (!"upgradeRecipe".equals(cmd.getName()))
            return list;

        if (args.length == 1)
            list.addAll(List.of("reset", "show", "create", "delete").stream().filter(s -> s.toUpperCase().startsWith(args[0].toUpperCase())).toList())
                    ;
        if (args.length == 2 && (args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("delete")))
            CommandUtils.fetchUpgradeRecipes(args[1], list);

        if (args.length == 2 && args[0].equalsIgnoreCase("create"))
            CommandUtils.fetchUpgradeItems(args[1], list);

        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            List<String> types = new ArrayList<>();
            for (UpgradeRecipe.Type value : UpgradeRecipe.Type.values()) {
                types.add(value.name());
            }
            list.addAll(types.stream().filter(type -> type.toUpperCase().startsWith(args[2])).toList());
        }

        return list;
    }
}
