package ZenaCraft.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Warp;

public class SetWarpRequirement implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 2) return App.invalidSyntax(player);

        Faction faction = App.factionIOstuff.getPlayerFaction(player);

        List<Warp> wList = faction.getWarpList();

        if (wList.size() == 0){
            player.sendMessage(App.zenfac + ChatColor.RED + "your faction doesn't have any warps!");
            return true;
        }

        Warp change = null;

        for(Warp w : wList){
            if (w.getName().equals(args[0])) change = w;
        }
        if (change == null){
            player.sendMessage(App.zenfac + ChatColor.RED + "That warp doesn't exist!");
            return true;
        }

        int rankreq = 1;
        if (change.getRankReq() < rankreq) rankreq = change.getRankReq();
        
        if (faction.getPlayerRank(player) > rankreq){
            player.sendMessage(App.zenfac + ChatColor.RED + "You don't have " + 
            "the appropriate rank for this! You have to be at least: " + ChatColor.BOLD +
            faction.getRanks()[rankreq]);
            return true;
        }

        for(int i = 0; i + 1 < faction.getRanks().length; i++){
            if(faction.getRanks()[i].equals(args[1])){
                change.setRankReq(i);
                player.sendMessage(App.zenfac + "Changed rank requirement for warp" +
                    change.getName() + " to: " + ChatColor.BOLD + faction.getRanks()[i]);
                return true;
            }
        }

        player.sendMessage(App.zenfac + ChatColor.RED + "That rank doesn't exist!");
        return true;
    }
}
