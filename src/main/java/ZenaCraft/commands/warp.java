package ZenaCraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Warp;

public class warp implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){        
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;

        if (args.length != 1) return App.invalidSyntax(player);

        Faction f = App.factionIOstuff.getPlayerFaction(player);
        
        for(Warp w : f.getWarpList()){
            if(w.getName().equals(args[0])){
                f.getWarp(args[0]).warpPlayer(player);
                break;
            }
        }

        return true;
    }
}
