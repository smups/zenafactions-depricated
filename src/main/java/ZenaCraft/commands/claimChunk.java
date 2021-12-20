package ZenaCraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;

public class claimChunk  implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;

        int radius;

        if (args.length < 1){
            radius = 1;
        }
        else radius = Integer.parseInt(args[0]);

        App.factionIOstuff.claimChunks(player, null, radius, null);
        return true;
    }
}
