package ZenaCraft.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;

public class listMembers implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        HashMap<UUID, Integer> members = App.factionIOstuff.getPlayerFaction(player).getMembers();

        String response = App.zenfac + ChatColor.WHITE + "Current members (" + String.valueOf(members.size()) + "): ";
        
        for (Map.Entry mapElement : members.entrySet()){
            UUID uuid = (UUID) mapElement.getKey();
            response += (Bukkit.getPlayer(uuid).getName() + " ");
        }
        player.sendMessage(response);
        return true;
    }
}
