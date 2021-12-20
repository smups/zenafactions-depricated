package ZenaCraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class factionBalance implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;

        int factionID = player.getMetadata("factionID").get(0).asInt();
        if (!(App.factionIOstuff.getFactionList().containsKey(factionID))){
            player.sendMessage(App.zenfac + ChatColor.RED + "Error: no faction assigned to player");
            return true;
        }
        Faction faction = (Faction) App.factionIOstuff.getFaction(factionID);
        String balance = String.valueOf(faction.getBalance());
        player.sendMessage(App.zenfac + ChatColor.WHITE + faction.getName() + " has: $" + balance);
        return true;
    }
}