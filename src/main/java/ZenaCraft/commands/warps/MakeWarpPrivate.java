package ZenaCraft.commands.warps;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Warp;

public class MakeWarpPrivate extends TemplateCommand{

    public MakeWarpPrivate() {super(1);}

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

        String perm = change.getPerm();

        //remove warp from all ranks
        faction.getRanks().forEach((rank) -> {rank.removePerm(perm);});

        //add the perm to the player
        faction.getPlayerRank(player).addPerm(perm);
        //owner has access to everything
        faction.getRanks().get(0).addPerm(perm);

        player.sendMessage(App.zenfac + ChatColor.GREEN + "Made warp private!");
        return true;
    }
    
}