package ZenaCraft.commands;

import java.util.Map;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class SetInfluence extends TemplateCommand{

    public SetInfluence() {super(2);}

    @Override
    protected boolean run() {
        if (!player.isOp()){
            player.sendMessage(App.zenfac + ChatColor.RED + "Admin command only");
        }

        for (Faction faction : App.factionIOstuff.getFactionList()){

            if (faction.getName().equals(args[0])){

                Double influence = formatDouble(args[1]);
                if (influence == null) return invalidSyntax(player);

                faction.setInfluence(influence);

                App.factionIOstuff.reloadScoreBoard(null);
                return true;
            }
        }

        return factionNoExist(player);
    }
}