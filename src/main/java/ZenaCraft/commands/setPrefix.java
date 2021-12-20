package ZenaCraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import net.md_5.bungee.api.ChatColor;

public class setPrefix implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){        
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        String name = args[0];
        String color = (String) args[1];

        int factionID = player.getMetadata("factionID").get(0).asInt();
        Faction faction = (Faction) App.factionIOstuff.getFaction(factionID);

        if (faction.getMembers().get(player.getUniqueId()) != 0){
            player.sendMessage(App.zenfac + ChatColor.DARK_RED + "You're not the owner of this faction!");
            return true;
        }

        String newPrefix = "";

        if (color.equals(new String("red"))) newPrefix += ChatColor.DARK_RED;
        if (color.equals(new String("green"))) newPrefix += ChatColor.GREEN;
        if (color.equals(new String("blue"))) newPrefix += ChatColor.BLUE;
        if (color.equals(new String("purple"))) newPrefix += ChatColor.DARK_PURPLE;
        if (color.equals(new String("orange"))) newPrefix += ChatColor.GOLD;
        newPrefix += name;

        faction.setPrefix(newPrefix);
        player.sendMessage(App.zenfac + "new prefix set as: " + newPrefix + "[" + color + "]");
        return true;
    }
    
}