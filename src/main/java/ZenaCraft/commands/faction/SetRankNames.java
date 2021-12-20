package ZenaCraft.commands.faction;

import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class SetRankNames extends TemplateCommand{

    public SetRankNames() {super(3);}

    @Override
    protected boolean checkArgSize(Player player, String[] args) {
        if(args.length > 0 && args.length < 4) return true;
        invalidSyntax(player);
        return false;
    }

    @Override
    protected boolean run() {
        Faction faction = App.factionIOstuff.getPlayerFaction(player);

        if (faction.getMembers().get(player.getUniqueId()) != 0) return invalidRank(player, 0);

        String[] newRanks = faction.getRanks();

        if (argSize > 0) newRanks[0] = args[0];
        if (argSize > 1) newRanks[1] = args[1];
        if (argSize > 2) newRanks[2] = args[2];

        faction.setRanks(newRanks);
        player.sendMessage(App.zenfac + "Changed rank names!");
        return true; 
    }
}
