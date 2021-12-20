package ZenaCraft.commands.faction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;

public class ListMembers extends TemplateCommand{

    public ListMembers() {super(0);}

    @Override
    protected boolean run() {
        HashMap<UUID, Integer> members = App.factionIOstuff.getPlayerFaction(player).getMembers();

        String response = App.zenfac + ChatColor.WHITE + "Current members (" + String.valueOf(members.size()) + "): ";
        
        for (Map.Entry mapElement : members.entrySet()){
            UUID uuid = (UUID) mapElement.getKey();
            response += (Bukkit.getOfflinePlayer(uuid).getName() + ", ");
        }
        
        player.sendMessage(response);
        return true;
    }
}