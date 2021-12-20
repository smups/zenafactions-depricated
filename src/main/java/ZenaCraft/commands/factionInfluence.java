package ZenaCraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class factionInfluence implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;

        String factionString = player.getMetadata("faction").get(0).asString();
        if (!(App.factionHashMap.containsKey(factionString))){
            player.sendMessage(App.zenfac + ChatColor.RED + "Error: no faction assigned to payer" + factionString);
            return true;
        }
        Faction faction = (Faction) App.factionHashMap.get(factionString);
        String influence = String.valueOf(faction.getInfluence());
        player.sendMessage(App.zenfac + ChatColor.WHITE + faction.getName() + " has: " + influence);
        return true;
    }
}