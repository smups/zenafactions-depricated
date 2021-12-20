package ZenaCraft.commands.ranks;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class RemoveRank extends TemplateCommand{

    public RemoveRank() {super(1, true, 0);}

    @Override
    protected boolean run() {
        Faction f = App.factionIOstuff.getPlayerFaction(player);

        if(f.getRanks().size() == 3){
            player.sendMessage(App.zenfac + ChatColor.RED + "Can't have fewer than 3 factions!");
            return true;
        }

        f.getRanks().forEach((rank) -> {
            if(rank.getName().equals(args[0])){
                if (!rank.equals(f.getDefaultRank())){
                    f.removeRank(rank);
                    player.sendMessage(App.zenfac + ChatColor.GREEN + "deleted rank " + args[0] + "!");

                    //move rankless players to default rank
                    f.getMembers().forEach((id, r) -> {if(r.equals(rank)) r = f.getDefaultRank();});
                }
                else player.sendMessage(App.zenfac + ChatColor.RED + "Can't delete default rank!");
            }
        });

        return true;
    }
    
}