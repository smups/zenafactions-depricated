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

        if (args.length != 2) return App.invalidSyntax(player);

        String name = args[0];
        String color = (String) args[1];

        int factionID = player.getMetadata("factionID").get(0).asInt();
        Faction faction = (Faction) App.factionIOstuff.getFaction(factionID);

        if (faction.getMembers().get(player.getUniqueId()) != 0){
            player.sendMessage(App.zenfac + ChatColor.DARK_RED + "You're not the owner of this faction!");
            return true;
        }

        String newPrefix = "";

        if (color.equals(new String("red"))){
            newPrefix += ChatColor.DARK_RED;
            faction.setColor(0xFF5555);
        }
        if (color.equals(new String("green"))){
            newPrefix += ChatColor.GREEN;
            faction.setColor(0x55FF55);
        }
        if (color.equals(new String("blue"))){
            newPrefix += ChatColor.BLUE;
            faction.setColor(0x5555FF);
        }
        if (color.equals(new String("dark red"))){
            newPrefix += ChatColor.DARK_RED;
            faction.setColor(0xAA0000);
        }
        if (color.equals(new String("dark green"))){
            newPrefix += ChatColor.DARK_GREEN;
            faction.setColor(0x00AA00);
        }
        if (color.equals(new String("dark blue"))){
            newPrefix += ChatColor.DARK_BLUE;
            faction.setColor(0x0000AA);
        }
        if (color.equals(new String("purple"))){
            newPrefix += ChatColor.LIGHT_PURPLE;
            faction.setColor(0xFF55FF);
        }
        if (color.equals(new String("dark purple"))){
            newPrefix += ChatColor.DARK_PURPLE;
            faction.setColor(0xAA00AA);
        }
        if (color.equals(new String("orange"))){
            newPrefix += ChatColor.GOLD;
            faction.setColor(0xFFAA00);
        }
        newPrefix += name;

        faction.setPrefix(newPrefix);
        player.sendMessage(App.zenfac + "new prefix set as: " + newPrefix + " [" + color + "]");
        App.factionIOstuff.reloadScoreBoard(null);
        return true;
    }
    
}