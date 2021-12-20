package ZenaCraft.commands.ranks;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Rank;

public class AddRankPerm extends TemplateCommand{

    public AddRankPerm() {super(2, true, 0);}

    @Override
    protected boolean run() {
        Faction f = App.factionIOstuff.getPlayerFaction(player);

        Rank r = null;
        for (Rank rank : f.getRanks()) if(rank.getName().equals(args[0])) r = rank;
        if (r == null) return rankNoExist(player, args[0]);

        String perm = args[2];
        if(!App.permList.contains(perm)){
            player.sendMessage(App.zenfac + ChatColor.RED + "That permission doesn't exist!");
            return true;
        }

        if(!f.getPlayerRank(player).hasPerm(perm)) return invalidRank(player, perm);

        r.addPerm(perm);
        player.sendMessage(App.zenfac + ChatColor.GREEN + "Added permission " +
            perm + " to rank: " + r.getName());
            
        return true;
    }

}