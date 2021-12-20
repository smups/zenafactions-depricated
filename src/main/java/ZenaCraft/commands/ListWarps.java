package ZenaCraft.commands;

import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Warp;

public class ListWarps implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;

        Faction faction = App.factionIOstuff.getPlayerFaction(player);

        List<Warp> wList = faction.getWarpList();

        if (wList.size() == 0){
            player.sendMessage(App.zenfac + ChatColor.RED + "your faction doesn't have any warps!");
            return true;
        }

        int rank = faction.getPlayerRank(player);
        String response = App.zenfac + "Avaliable warps (" + String.valueOf(wList.size()) + "): ";

        DecimalFormat df = new DecimalFormat("0.00");

        for(Warp w : wList){
            if (w.getRankReq() < rank) continue;
            response += w.getName() + ChatColor.WHITE + "(" + ChatColor.GOLD + "Ƒ";
            response += df.format(w.calcWarpCost(player)) + ChatColor.WHITE + "), " + ChatColor.AQUA;
        }

        player.sendMessage(response);
        return true;
    }    
}
