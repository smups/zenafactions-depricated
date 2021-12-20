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

public class promote implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){        
        if (!(sender instanceof Player)){
            return true;
        }
        Player p = (Player) sender;

        if (args.length != 1) return App.invalidSyntax(p);

        String promotedName = args[0];
        Player promotedPlayer = Bukkit.getPlayer(promotedName);

        if (promotedPlayer == null){
            p.sendMessage(App.zenfac + ChatColor.RED + "That player doesn't exist!");
            return true;
        }

        Faction faction = App.factionIOstuff.getPlayerFaction(promotedPlayer);

        if (!faction.getMembers().containsKey(promotedPlayer.getUniqueId())){
            p.sendMessage(App.zenfac + ChatColor.RED + "that player isn't in your faction!");
            return true;
        }
        
        if (faction.getMembers().get(p.getUniqueId()) != 0){
            p.sendMessage(App.zenfac + ChatColor.RED + "you don't have the appropriate permission for this! You need to be at least: " + faction.getRanks()[0]);
            return true;
        }

        Integer rank = faction.getMembers().get(promotedPlayer.getUniqueId());

        if (rank == 0){
            p.sendMessage(App.zenfac + ChatColor.RED + "Can't promote someone who is " + faction.getRanks()[0] + " rank!");
            return true;
        }

        faction.getMembers().replace(promotedPlayer.getUniqueId(), rank - 1);
        p.sendMessage(App.zenfac + "Promoted " + ChatColor.WHITE + args[0] + ChatColor.AQUA + " to the rank of " + ChatColor.BOLD + faction.getRanks()[rank - 1]);
        promotedPlayer.sendMessage(App.zenfac + "You were promoted to the rank of: " + ChatColor.BOLD + ChatColor.GREEN + faction.getRanks()[rank - 1]);
        promotedPlayer.playSound(promotedPlayer.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f);
        return true;
    }

}
