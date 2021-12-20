package ZenaCraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class createWarp implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){        
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;

        if (args.length != 1) return App.invalidSyntax(player);

        Faction faction = App.factionIOstuff.getPlayerFaction(player);

        if (faction.getPlayerRank(player) > 1){
            player.sendMessage(App.zenfac + ChatColor.RED + "You don't have " + 
            "the appropriate rank for this! You have to be at least: " + ChatColor.BOLD +
            faction.getRanks()[1]);
            return true;
        }

        double createCost = App.getPlugin(App.class).getConfig().getDouble("warpCreationCost");
        double warpScale = App.getPlugin(App.class).getConfig().getDouble("warpScale");
        createCost = createCost*Math.pow(faction.getWarpList().size(), warpScale);

        if ((faction.getBalance() < createCost) && (faction.getID() != 0)){
            player.sendMessage(App.zenfac + ChatColor.RED + "Your faction doesn't have enough money to create a warp!");
            return true;
        }

        if (faction.hasWarp(args[0])){
            player.sendMessage(App.zenfac + ChatColor.RED + "This warp already exists!");
            return true;
        }

        if (!player.hasMetadata("createWarp") || !player.getMetadata("createWarp").get(0).asBoolean()){
            player.sendMessage(App.zenfac + ChatColor.WHITE + "are you sure you want to create a new warp?" +
            " This costs " + ChatColor.GOLD + "Ƒ" + String.valueOf((double) Math.round(createCost*100.0)/100.0) + ChatColor.RESET +
            ". Retype this command to confirm!");
            player.setMetadata("createWarp", new FixedMetadataValue(App.getPlugin(App.class), true));
            return true;
        }

        if (faction.getID() !=0 ) faction.removeBalance(createCost);
        faction.addWarp(player.getLocation(), args[0]);
        player.sendMessage(App.zenfac + ChatColor.GREEN + "Warp created!");
        player.setMetadata("createWarp", new FixedMetadataValue(App.getPlugin(App.class), false));
        return true;
    }
}
