package ZenaCraft.commands;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class joinFaction implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        Plugin plugin = App.getPlugin(App.class);

        if(!player.hasMetadata("joinFaction") || !player.getMetadata("joinFaction").get(0).asBoolean()){
            player.sendMessage(App.zenfac + "are you sure you want to change faction? You'll leave your current faction. Type this command again to confirm.");
            player.setMetadata("joinFaction", new FixedMetadataValue(plugin, true));
            return true;
        }
        player.setMetadata("joinFaction", new FixedMetadataValue(plugin, false));

        //check syntax
        if (args.length != 1) return App.invalidSyntax(player);

        //check if they're trying to boop you by joining their own faction
        if (App.factionIOstuff.getFaction(player.getMetadata("factionID").get(0).asInt()).getName().equals(args[0])){
            player.sendMessage(App.zenfac + ChatColor.RED + "Can't join a faction you're already a member of!");
            return true;
        }

        for (Map.Entry mEntry : App.factionIOstuff.getFactionList().entrySet()){
            Faction faction = (Faction) mEntry.getValue();

            if (faction.getName().equals(args[0])){
                App.factionIOstuff.changePlayerFaction(faction, player, 2);
                player.sendMessage(App.zenfac + ChatColor.GREEN + "You've been added to the faction: " + ChatColor.BOLD + faction.getPrefix());
                return true;
            }
        }
        player.sendMessage(App.zenfac + ChatColor.RED + "Faction " + args[0] + " not found!");
        return true;
    }
}
