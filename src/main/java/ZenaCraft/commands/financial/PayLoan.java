package ZenaCraft.commands.financial;

import java.util.Map.Entry;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class PayLoan extends TemplateCommand{

    public PayLoan() {super(2);}

    @Override
    protected boolean run() {
        Faction faction = null;

        for(Entry mEntry : App.factionIOstuff.getFactionList().entrySet()){
            Faction f = (Faction) mEntry.getValue();
            if (f.getName().equals(args[0])) faction = f;
        }

        if (faction == null) return factionNoExist(player);

        try{
            faction.getRunningLoans().get(Integer.parseInt(args[1]) - 1).payLoan();
        }
        catch (ArrayIndexOutOfBoundsException outE){
            player.sendMessage(App.zenfac + faction.getPrefix() + ChatColor.RED +
                "doesn't have that many loans!");
            return true;
        }
        catch (Exception e){
            return invalidSyntax(player);
        }

        return true;
    }
}