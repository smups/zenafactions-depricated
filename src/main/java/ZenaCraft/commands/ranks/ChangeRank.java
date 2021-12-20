package ZenaCraft.commands.ranks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.events.PlayerChangeRankEvent;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Rank;

public class ChangeRank extends TemplateCommand{

    public ChangeRank() {super(2, true, 0);}

    @Override
    protected boolean run() {
        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) return playerNoExist(player, args[0]);

        if(!hasPerm(player)) return invalidRank(player);

        Faction f = App.factionIOstuff.getPlayerFaction(player);

        if(!f.isMember(target)){
            player.sendMessage(App.zenfac + ChatColor.RED + "That player isn't in your faction!");
            return true;
        }

        Rank pRank = f.getPlayerRank(player);
        Rank tRank = f.getPlayerRank(target);

        if(pRank.getLevel() < tRank.getLevel()){
            player.sendMessage(App.zenfac + ChatColor.RED + "Can't change the rank of a player with more permissions than you!");
            return true;
        }

        Rank newRank = null;
        for(Rank r : f.getRanks()) if(r.getName().equals(args[1])) newRank = r;

        if(newRank == null){
            player.sendMessage(App.zenfac + ChatColor.RED + "No such rank exits!");
            return true;
        }

        //create event
        PlayerChangeRankEvent event = new PlayerChangeRankEvent(f, player, target, tRank, newRank);
        event.callEvent();

        f.changeRank(target.getUniqueId(), newRank);
        
        player.sendMessage(App.zenfac + ChatColor.GREEN + "Changed rank of " + target.getName() +
            " from " + tRank.getName() + " to: " + newRank.getName());
        return true;
    }    
}