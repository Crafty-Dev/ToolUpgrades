package de.crafty.toolupgrades.command;

import de.crafty.toolupgrades.util.UpgradeOverview;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMD_upgradeOverview implements CommandExecutor {




    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


        if(cmd.getName().equalsIgnoreCase("upgradeOverview") && args.length == 0){
            if(sender instanceof Player player)
                UpgradeOverview.open(player);
            else
                UpgradeOverview.printAll();

            return true;
        }

        return false;
    }
}
