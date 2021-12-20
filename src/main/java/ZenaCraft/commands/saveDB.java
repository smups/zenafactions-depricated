package ZenaCraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import net.md_5.bungee.api.ChatColor;

public class saveDB implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (!player.isOp()){
                player.sendMessage(App.zenfac + ChatColor.DARK_RED + "You are not permitted to use this command");
                return true;
            }
        }
        sender.sendMessage(App.zenfac + "Saving databases");
        App.factionIOstuff.saveDB();
        return true;
    }
}