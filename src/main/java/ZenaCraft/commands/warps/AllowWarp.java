package ZenaCraft.commands.warps;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Rank;
import ZenaCraft.objects.Warp;
import net.md_5.bungee.api.ChatColor;

public class AllowWarp extends TemplateCommand{
    
    public AllowWarp() {super();}

    @Override
    protected boolean run() {
        Faction faction = App.factionIOstuff.getPlayerFaction(player);

        Warp change = null;

        for(Warp w : faction.getWarpList())
            if (w.getName().equals(args[0])) change = w;
        
        if(change == null){
            player.sendMessage(App.zenfac + ChatColor.RED +
                "That warp doesn't exist!");
                return true;
        }

        if (!faction.getPlayerRank(player).hasPerm(change.getPerm())) return invalidRank(player);

        for(Rank r : faction.getRanks()){
            if(!r.getName().equals(args[1])) continue;
            r.addPerm(change.getPerm());
            player.sendMessage(App.zenfac + ChatColor.GREEN + "Added warp permission to rank " + r.getName());
            return true;
        }

        player.sendMessage(App.zenfac + ChatColor.RED + "That rank doesn't exist!");
        return true;
        
    }
}