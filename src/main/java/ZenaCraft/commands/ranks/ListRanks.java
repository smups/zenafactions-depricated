package ZenaCraft.commands.ranks;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Rank;

public class ListRanks extends TemplateCommand{

    @Override
    protected boolean run() {
        Faction f = App.factionIOstuff.getPlayerFaction(player);

        String response = App.zenfac + "Your Faction has the following ranks: \n";

        for (Rank r : f.getRanks()){
            response += ChatColor.RESET + "[" + f.getPrefix() + " | " + r.getName() +
                ChatColor.RESET + "] Number of perms: " + ChatColor.BOLD +
                String.valueOf(r.getLevel()) + ChatColor.RESET + ", \n"; 
        }

        player.sendMessage(response);
        
        return true;
    }
    
}