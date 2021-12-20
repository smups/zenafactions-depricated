package ZenaCraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Colour;
import ZenaCraft.objects.Faction;

public class ChangeColour implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){        
        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) return App.invalidSyntax(player);

        String color = args[0];

        Faction faction = (Faction) App.factionIOstuff.getPlayerFaction(player);

        if (faction.getMembers().get(player.getUniqueId()) != 0){
            player.sendMessage(App.zenfac + ChatColor.RED + "You don't have the appropriate rank to do this!" +
                " You have to be at least: " + ChatColor.GREEN + faction.getRanks()[0]);
            return true;
        }

        if (color.equals(new String("black"))){
            Colour c = new Colour(0x000000, ChatColor.BLACK);
            faction.setColour(c);
        }
        if (color.equals(new String("dark_blue"))){
            Colour c = new Colour(0x0000AA, ChatColor.DARK_BLUE);
            faction.setColour(c);
        }
        if (color.equals(new String("dark_green"))){
            Colour c = new Colour(0x00AA00, ChatColor.DARK_GREEN);
            faction.setColour(c);
        }
        if (color.equals(new String("dark_aqua"))){
            Colour c = new Colour(0x00AAAA, ChatColor.DARK_AQUA);
            faction.setColour(c);
        }
        if (color.equals(new String("dark_red"))){
            Colour c = new Colour(0xAA0000, ChatColor.DARK_RED);
            faction.setColour(c);
        }
        if (color.equals(new String("dark_purple"))){
            Colour c = new Colour(0xAA00AA, ChatColor.DARK_PURPLE);
            faction.setColour(c);
        }
        if (color.equals(new String("gold"))){
            Colour c = new Colour(0xFFAA00, ChatColor.GOLD);
            faction.setColour(c);
        }
        if (color.equals(new String("gray"))){
            Colour c = new Colour(0xAAAAAA, ChatColor.GRAY);
            faction.setColour(c);
        }
        if (color.equals(new String("dark_grey"))){
            Colour c = new Colour(0x555555, ChatColor.DARK_GRAY);
            faction.setColour(c);
        }
        if (color.equals(new String("blue"))){
            Colour c = new Colour(0x5555FF, ChatColor.BLUE);
            faction.setColour(c);
        }
        if (color.equals(new String("green"))){
            Colour c = new Colour(0x55FF55, ChatColor.GREEN);
            faction.setColour(c);
        }
        if (color.equals(new String("red"))){
            Colour c = new Colour(0xFF5555, ChatColor.RED);
            faction.setColour(c);
        }
        if (color.equals(new String("light_purple"))){
            Colour c = new Colour(0xFF55FF, ChatColor.LIGHT_PURPLE);
            faction.setColour(c);
        }
        if (color.equals(new String("yellow"))){
            Colour c = new Colour(0xFFFF55, ChatColor.YELLOW);
            faction.setColour(c);
        }
        if (color.equals(new String("white"))){
            Colour c = new Colour(0xFFFFFF, ChatColor.WHITE);
            faction.setColour(c);
        }
        if (color.equals(new String("aqua"))){
            Colour c = new Colour(0x55FFFF, ChatColor.AQUA);
            faction.setColour(c);
        }

        App.factionIOstuff.reloadScoreBoard(null);
        player.sendMessage(App.zenfac + "Changed colour! New prefix: " + faction.getPrefix());
        return true;
    }    
}