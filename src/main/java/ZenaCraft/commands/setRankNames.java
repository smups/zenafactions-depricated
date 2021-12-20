package ZenaCraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class setRankNames implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;
        Faction faction = App.factionIOstuff.getPlayerFaction(player);

        if (faction.getMembers().get(player.getUniqueId()) != 0){
            player.sendMessage(App.zenfac + ChatColor.RED + "you don't have the appropriate permission for this! You need to be at least: " + faction.getRanks()[0]);
            return true;
        }
        String[] newRanks = faction.getRanks();

        if (args.length > 0) newRanks[0] = args[0];
        else if (args.length > 1) newRanks[1] = args[1];
        else if (args.length > 2) newRanks[2] = args[2];
        else return App.invalidSyntax(player);

        faction.setRanks(newRanks);
        player.sendMessage(App.zenfac + "Changed rank names!");
        return true; 
    }
}
