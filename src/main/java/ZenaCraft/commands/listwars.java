package ZenaCraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.War;

public class listwars implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){        
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;

        if (args.length != 0) return App.invalidSyntax(player);

        String reply = App.zenfac + "Ongoing wars: ";
        for (War war : App.warThread.getWars()){
            reply += (war.getAttackers().getPrefix() + ChatColor.RESET + " VS " + war.getDefenders().getPrefix() + ", ");
        }

        player.sendMessage(reply);
        return true;       
    }
}
