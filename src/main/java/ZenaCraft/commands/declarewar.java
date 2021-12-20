package ZenaCraft.commands;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class declarewar implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if(args.length != 1) return App.invalidSyntax(p);
        
        Faction attackers = App.factionIOstuff.getPlayerFaction(p);

        if (attackers == null){
            p.sendMessage(App.zenfac + ChatColor.RED + "you're not a member of a faction, so you can't declare war!");
            return true;
        }

        if (attackers.getMembers().get(p.getUniqueId()) != 0){
            p.sendMessage(App.zenfac + ChatColor.RED + "you don't have the appropriate permission for this! You need to be at least: " + attackers.getRanks()[0]);
            return true;
        }

        //check if they're trying to boop you by warring their own faction
        if (App.factionIOstuff.getFaction(p.getMetadata("factionID").get(0).asInt()).getName().equals(args[0])){
            p.sendMessage(App.zenfac + ChatColor.RED + "Can't declare war on yourself!");
            return true;
        }

        if (App.warThread.getWarFromFaction(attackers) != null){
            p.sendMessage(App.zenfac + ChatColor.RED + "you are already at war with someone!");
            return true;
        }

        for (Map.Entry mEntry : App.factionIOstuff.getFactionList().entrySet()){
            Faction defenders = (Faction) mEntry.getValue();

            if (App.warThread.getWarFromFaction(defenders) != null){
                p.sendMessage(App.zenfac + ChatColor.RED + "sorry, this faction is already at war with someone else!");
                return true;
            }

            if (defenders.getName().equals(args[0])){
                App.warThread.startWar(defenders, attackers, p.getChunk());
                Plugin plugin = App.getPlugin(App.class);
                attackers.setInfluence(attackers.getInfluence() - plugin.getConfig().getDouble("warCost"));
                App.factionIOstuff.reloadScoreBoard(null);
                return true;
            }
        }
        p.sendMessage(App.zenfac + ChatColor.RED + "Faction " + args[0] + " not found!");
        return true;
    }    
}
