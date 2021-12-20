package ZenaCraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class demote implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){        
        if (!(sender instanceof Player)){
            return true;
        }
        Player p = (Player) sender;

        if (args.length != 1) return App.invalidSyntax(p);

        String demotedName = args[0];
        Player demotedPlayer = Bukkit.getPlayer(demotedName);

        if (demotedPlayer == null){
            p.sendMessage(App.zenfac + ChatColor.RED + "That player doesn't exist!");
            return true;
        }

        Faction faction = App.factionIOstuff.getPlayerFaction(demotedPlayer);

        if (faction.getMembers().get(p.getUniqueId()) != 0){
            p.sendMessage(App.zenfac + ChatColor.RED + "you don't have the appropriate permission for this! You need to be at least: " + faction.getRanks()[0]);
            return true;
        }

        if (!faction.getMembers().containsKey(demotedPlayer.getUniqueId())){
            p.sendMessage(App.zenfac + ChatColor.RED + "that player isn't in your faction!");
            return true;
        }

        Integer rank = faction.getMembers().get(demotedPlayer.getUniqueId());

        if (rank == 2){
            p.sendMessage(App.zenfac + ChatColor.RED + "Can't demote someone who is " + faction.getRanks()[2] + " rank!");
            return true;
        }

        faction.getMembers().replace(demotedPlayer.getUniqueId(), rank + 1);
        p.sendMessage(App.zenfac + "Demoted " + ChatColor.WHITE + args[0] + ChatColor.AQUA + " to the rank of " + ChatColor.BOLD + faction.getRanks()[rank + 1]);
        demotedPlayer.sendMessage(App.zenfac + "You were demoted to the rank of: " + ChatColor.BOLD + ChatColor.RED + faction.getRanks()[rank + 1]);
        demotedPlayer.playSound(demotedPlayer.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1f, 1f);
        return true;
    }
}
