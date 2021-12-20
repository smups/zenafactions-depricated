package ZenaCraft.commands.warps;

import java.util.List;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Warp;

public class ListWarps extends TemplateCommand{

    public ListWarps() {super(0);}

    @Override
    protected boolean run() {
        Faction faction = App.factionIOstuff.getPlayerFaction(player);

        List<Warp> wList = faction.getWarpList();

        if (wList.size() == 0){
            player.sendMessage(App.zenfac + ChatColor.RED + "your faction doesn't have any warps!");
            return true;
        }

        int rank = faction.getPlayerRank(player);
        String response = App.zenfac + "Avaliable warps (" + String.valueOf(wList.size()) + "): ";


        response += ChatColor.RESET + "[" + faction.getPrefix() + ChatColor.RESET + "] ";
        for(Warp w : wList){
            if (w.getRankReq() < rank) continue;
            response += w.getName() + ChatColor.WHITE + " (";
            response += formatMoney(w.calcWarpCost(player)) + ChatColor.WHITE + "), ";
        }


        //now add the default warps anyone can access!
        Faction defaulFaction = App.factionIOstuff.getFaction(0);

        if (defaulFaction.equals(faction)){
            player.sendMessage(response);
            return true;
        }

        response += "[" + defaulFaction.getPrefix() + "] " + ChatColor.RESET;
        wList = defaulFaction.getWarpList();
        
        for(Warp w : wList){
            if (w.getRankReq() < rank) continue;
            response += w.getName() + ChatColor.WHITE + "(" ;
            response += formatMoney(w.calcWarpCost(player)) + ChatColor.WHITE + "), ";
        }

        player.sendMessage(response);
        return true;
    }   
}