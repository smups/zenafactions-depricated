package ZenaCraft.commands;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;

public class listFactions implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        String response = App.zenfac + "Current factions: ";

        for (Map.Entry mapElement : App.factionHashMap.entrySet()){
            String key = (String) mapElement.getKey();
            response += (key + ", ");
        }
        player.sendMessage(response);
        return true;
    }
}
