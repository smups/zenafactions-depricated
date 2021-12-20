package ZenaCraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import net.md_5.bungee.api.ChatColor;

public class ChangeFactionName implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){        
        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) return App.invalidSyntax(player);

        String name = args[0];

        Faction faction = (Faction) App.factionIOstuff.getPlayerFaction(player);

        if (faction.getMembers().get(player.getUniqueId()) != 0){
            player.sendMessage(App.zenfac + ChatColor.RED + "You don't have the appropriate rank to do this!" +
                " You have to be at least: " + ChatColor.GREEN + faction.getRanks()[0]);
            return true;
        }
        
        faction.setName(name);
        player.sendMessage(App.zenfac + "Changed faction colour!");
        return true;
    }
}