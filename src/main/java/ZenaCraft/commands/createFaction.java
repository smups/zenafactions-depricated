package ZenaCraft.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import net.md_5.bungee.api.ChatColor;

public class createFaction implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        Plugin plugin = App.getPlugin(App.class);
        
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        String name = args[0];

        if (App.factionHashMap.containsKey(name)){
            player.sendMessage(App.zenfac + ChatColor.DARK_RED + "Faction Already exists!");
            return true;
        }
        HashMap<UUID, Integer> founder = new HashMap<UUID, Integer>();
        founder.put(player.getUniqueId(), 0);
        String[] defaultRanks = {"Founder", "Bigshot", "Member"};
        Faction newFaction = new Faction(name, defaultRanks, 0.0, founder, name);

        App.factionHashMap.put(name, newFaction);
        App.playerHashMap.replace(player.getUniqueId(), name);

        player.setMetadata("faction", new FixedMetadataValue(plugin, name));
        player.sendMessage(App.zenfac + ChatColor.DARK_RED + "Created faction: " + name);
        
        return true;
    }
}