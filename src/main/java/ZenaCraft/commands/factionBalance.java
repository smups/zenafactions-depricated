package ZenaCraft.commands;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class factionBalance implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 0) return App.invalidSyntax(player);

        Faction faction = App.factionIOstuff.getPlayerFaction(player);
        DecimalFormat df = new DecimalFormat("0.00");
        String balance = df.format(faction.getBalance());
        player.sendMessage(App.zenfac + faction.getPrefix() +  ChatColor.WHITE + " has:" + ChatColor.GREEN + "Ƒ" + balance);
        return true;
    }
}