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

public class SetWarpTax implements CommandExecutor{

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

        double factiontax = -1;

        try{
            factiontax = Double.parseDouble(args[1]);
        }
        catch (Exception e){
            App.invalidSyntax(player);
            return true;
        }

        change.setFactionTax(factiontax/100);

        DecimalFormat df = new DecimalFormat("00");

        player.sendMessage(App.zenfac + "Changed factiontax for warp " + 
            change.getName() + " to: " + ChatColor.BOLD + "" + ChatColor.GREEN +
            df.format(change.getFactionTax()*100) + "%");

        return true;
    }
}