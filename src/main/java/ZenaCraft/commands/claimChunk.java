package ZenaCraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
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

        int radius = 1;

        if (args.length < 1) radius = 1;
        else if (args.length > 1) return App.invalidSyntax(player);

        if (!player.getWorld().getEnvironment().equals(World.Environment.NORMAL)){
            player.sendMessage(App.zenfac + ChatColor.RED + "factions are disabled in the Nether and the End");
            return true;
        }
        
        try{
            radius = Integer.parseInt(args[0]);
        }
        catch (Exception e){
            return App.invalidSyntax(player);
        }

        App.factionIOstuff.claimChunks(player, null, null, radius, null);
        return true;
    }
}
