package ZenaCraft.commands;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;

public class ListLoadedFQCs implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){        
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        String response = App.zenfac + "Loaded FQC's: ";

        for (Map.Entry mapElement : App.factionIOstuff.getLoadedFQChunks().entrySet()){
            String key = (String) mapElement.getKey();
            response += (key + ", ");
        }
        player.sendMessage(response);
        return true;
    }
}
