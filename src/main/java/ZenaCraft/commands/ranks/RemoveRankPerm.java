package ZenaCraft.commands.ranks;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Rank;
import net.md_5.bungee.api.ChatColor;

public class RemoveRankPerm extends TemplateCommand{

    public RemoveRankPerm() {super(2, true, 0);}

    @Override
    protected boolean run() {
        Faction f = App.factionIOstuff.getPlayerFaction(player);

        Rank r = null;
        for (Rank rank : f.getRanks()) if(rank.getName().equals(args[0])) r = rank;
        if (r == null) return rankNoExist(player, args[0]);

        String perm = args[1];
        if(!App.permList.contains(perm)){
            player.sendMessage(App.zenfac + ChatColor.RED + "That permission doesn't exist!");
            return true;
        }

        if(!r.hasPerm(perm)){
            player.sendMessage(App.zenfac + ChatColor.RED + "Rank " + r.getName() +
                " doesn't have permission " + perm + " to remove!");
            return true;
        }

        if(!f.getPlayerRank(player).hasPerm(perm)) return invalidRank(player, perm);

        r.removePerm(perm);
        player.sendMessage(App.zenfac + ChatColor.GREEN + "Removed permission " +
            perm + " from rank: " + r.getName());

        return true;
    }
    
}