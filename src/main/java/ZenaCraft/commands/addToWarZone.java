package ZenaCraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import ZenaCraft.objects.War;

public class addToWarZone implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        War war = App.warThread.getWarFromFaction(App.factionIOstuff.getPlayerFaction(player));

        if (war == null){
            player.sendMessage(App.zenfac + ChatColor.RED + "your faction is not at war!");
            return true;
        }

        Plugin plugin = App.getPlugin(App.class);
        double warcost = plugin.getConfig().getDouble("warCost");

        if(!player.hasMetadata("addToWarZone") || !player.getMetadata("addToWarZone").get(0).asBoolean()){
            String influenceString = ChatColor.BOLD + String.valueOf(warcost) + ChatColor.RESET + "" + ChatColor.RED + " influence! Type this command again to confirm.";
            player.sendMessage(App.zenfac + "are you sure you want to add this chunk to your war demands? This costs " + influenceString);
            player.setMetadata("addToWarZone", new FixedMetadataValue(plugin, true));
            return true;
        }
        player.setMetadata("addToWarZone", new FixedMetadataValue(plugin, false));
        App.warThread.addWarzone(war, null, null, player);
        return true;
    }
    
}