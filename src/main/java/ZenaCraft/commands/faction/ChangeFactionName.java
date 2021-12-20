package ZenaCraft.commands.faction;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class ChangeFactionName extends TemplateCommand{

    public ChangeFactionName() {super(1, true, 0);}

    @Override
    protected boolean run() {
        String name = args[0];

        Faction faction = (Faction) App.factionIOstuff.getPlayerFaction(player);

        if(hasPerm(player)) return invalidRank(player);

        faction.setName(name);
        player.sendMessage(App.zenfac + "Changed faction name!");
        App.factionIOstuff.reloadScoreBoard(null);
        
        return true;
    }
}