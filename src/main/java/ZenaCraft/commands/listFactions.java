package ZenaCraft.commands;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class listFactions implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        String response = App.zenfac + "Current factions: ";

        for (Map.Entry mapElement : App.factionIOstuff.getFactionList().entrySet()){
            Faction value = (Faction) mapElement.getValue();
            int number = value.getMembers().size();
            response += (value.getPrefix() + " (" + String.valueOf(number) + "), ");
        }
        if (!(sender instanceof Player)){
            Bukkit.getLogger().info(response);
            return true;
        }
        Player player = (Player) sender;
        player.sendMessage(response);
        return true;
    }
}
