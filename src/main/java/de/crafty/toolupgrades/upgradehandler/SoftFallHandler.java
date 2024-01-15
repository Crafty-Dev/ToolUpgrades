package de.crafty.toolupgrades.upgradehandler;

import de.crafty.toolupgrades.ToolUpgrades;
import de.crafty.toolupgrades.upgrade.ToolUpgrade;
import de.crafty.toolupgrades.util.ToolManager;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class SoftFallHandler implements Listener {

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {

        if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;

        if (!(event.getEntity() instanceof Player player))
            return;

        ItemStack bootsStack = player.getInventory().getArmorContents()[0];

        if (!ToolManager.hasUpgrade(bootsStack, ToolUpgrade.SOFT_FALL))
            return;

        World world = player.getWorld();

        player.playSound(player.getLocation(), Sound.BLOCK_POWDER_SNOW_PLACE, 1.0F, 1.0F);
        event.setCancelled(true);

        int steps = (int) Math.min(event.getDamage() / 5, 4);
        this.playFallAnimation(player, steps, 10, 0.5D);

        if (ToolUpgrades.getInstance().softFallBlockReplacement())
            this.performBlockReplacement(player, event.getDamage());


    }

    private void playFallAnimation(Player player, int steps, int baseParticles, double distance) {

        World world = player.getWorld();
        Location playerLocation = player.getLocation();

        for (int step = 0; step < steps; step++) {
            int finalStep = step;
            Bukkit.getScheduler().scheduleSyncDelayedTask(ToolUpgrades.getInstance(), () -> {
                int totalParticles = baseParticles + finalStep * 8;
                for (int i = 0; i < totalParticles; i++) {
                    Location particleLoc = playerLocation.clone();

                    Vector rotationVec = new Vector(1, 1, 1).normalize();
                    rotationVec.rotateAroundY(i * (360.0D / totalParticles));

                    particleLoc.add(new Vector(distance + finalStep * 0.25D, 0, distance + finalStep * 0.25D).multiply(rotationVec));

                    world.spawnParticle(Particle.REDSTONE, particleLoc, 100, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(255 - (steps * 50) + (finalStep * 50), 255 - (steps * 50) + (finalStep * 50), 255 - (steps * 50) + (finalStep * 50)), 0.3F));
                }

            }, 2L * step);
        }
    }


    private void performBlockReplacement(Player player, double damage) {

        if (damage < 10.0D)
            return;

        World world = player.getWorld();
        Random rand = new Random();

        int r = damage < 32.0D ? 1 : 2;

        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {

                if ((x == r && z == r) || (x == r && z == -r) || (x == -r && z == r) || (x == -r && z == -r))
                    continue;

                Location loc = player.getLocation().add(x, -1, z);
                Block block = loc.getBlock();

                if (block.getType() == Material.AIR) {
                    Block below = block.getLocation().add(0, -1, 0).getBlock();
                    if (below.getType() != Material.AIR)
                        block = below;
                }

                float f = rand.nextFloat();

                if (block.getType() == Material.GRASS_BLOCK) {
                    if (f < 0.25F)
                        block.setType(Material.DIRT);
                    else if (f < 0.4F)
                        block.setType(Material.COARSE_DIRT);
                    else if (f < 0.55F)
                        block.setType(Material.PODZOL);

                    world.playSound(loc, Sound.BLOCK_GRASS_BREAK, 1.0F, 1.0F);
                    continue;
                }

                if (block.getType() == Material.STONE) {
                    if (f < 0.3F)
                        block.setType(Material.COBBLESTONE);
                    else if (f < 0.55F)
                        block.setType(Material.GRAVEL);

                    world.playSound(loc, Sound.BLOCK_STONE_BREAK, 1.0F, 1.0F);
                }
            }

        }

    }

}
