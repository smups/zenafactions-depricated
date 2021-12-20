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
        if (!player.isOp()) return true;

        for (Map.Entry mEntry : App.factionIOstuff.getFactionList().entrySet()){
            Faction faction = (Faction) mEntry.getValue();
            if (faction.getName().equals(args[0])){
                faction.setInfluence(Double.parseDouble(args[1]));
                App.factionIOstuff.reloadScoreBoard(null);
                return true;
            }
        }
        player.sendMessage(App.zenfac + ChatColor.RED + "Faction " + args[0] + " not found!");
        return true;
    }
}