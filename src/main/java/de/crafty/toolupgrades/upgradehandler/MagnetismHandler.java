package de.crafty.toolupgrades.upgradehandler;

import de.crafty.toolupgrades.upgrade.ToolUpgrade;
import de.crafty.toolupgrades.util.ToolManager;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MagnetismHandler implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak$0(BlockDropItemEvent event) {

        Player player = event.getPlayer();
        ItemStack usedStack = player.getInventory().getItemInMainHand();

        if (!ToolManager.hasUpgrade(usedStack, ToolUpgrade.MAGNETISM) || player.getGameMode() == GameMode.CREATIVE)
            return;


        List<ItemStack> drops = new ArrayList<>();
        event.getItems().forEach(item -> drops.add(item.getItemStack()));

        this.manageDrops(drops, player);


        event.setCancelled(true);

    }


    @EventHandler
    public void onBlockBreak$1(BlockBreakEvent event) {

        Player player = event.getPlayer();
        ItemStack usedStack = player.getInventory().getItemInMainHand();

        if (!ToolManager.hasUpgrade(usedStack, ToolUpgrade.MAGNETISM) || player.getGameMode() == GameMode.CREATIVE)
            return;

        if (event.getExpToDrop() > 0)
            this.manageExpDrop(event.getExpToDrop(), player);
        event.setExpToDrop(0);
    }


    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {

        Player player = event.getEntity().getKiller();

        if (player == null || !ToolManager.hasUpgrade(player.getInventory().getItemInMainHand(), ToolUpgrade.MAGNETISM))
            return;

        this.manageDrops(event.getDrops(), player);

        if (event.getDroppedExp() > 0)
            this.manageExpDrop(event.getDroppedExp(), player);

        event.getDrops().clear();
        event.setDroppedExp(0);
    }


    public static void manageDrops(List<ItemStack> drops, Player player) {

        World world = player.getWorld();

        HashMap<Integer, ItemStack> rest = player.getInventory().addItem(drops.toArray(new ItemStack[0]));
        rest.values().forEach(drop -> {

            Item item = world.dropItem(player.getLocation(), drop);
            item.setVelocity(player.getVelocity());
        });


    }

    public static void manageExpDrop(int xp, Player player) {
        World world = player.getWorld();

        ExperienceOrb orb = (ExperienceOrb) world.spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
        orb.setExperience(xp);
        orb.setVelocity(player.getVelocity());
    }

}
