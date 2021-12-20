package ZenaCraft.commands;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class setInfluence implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;

        if (!player.isOp()){
            player.sendMessage(App.zenfac + ChatColor.RED + "Admin command only");
        }
        
        if (args.length != 2) return App.invalidSyntax(player);

        for (Map.Entry mEntry : App.factionIOstuff.getFactionList().entrySet()){
            Faction faction = (Faction) mEntry.getValue();
            if (faction.getName().equals(args[0])){
                try{
                    faction.setInfluence(Double.parseDouble(args[1]));
                }
                catch (Exception e){
                    return App.invalidSyntax(player);
                }
                App.factionIOstuff.reloadScoreBoard(null);
                return true;
            }
        }
        player.sendMessage(App.zenfac + ChatColor.RED + "Faction " + args[0] + " not found!");
        return true;
    }
}