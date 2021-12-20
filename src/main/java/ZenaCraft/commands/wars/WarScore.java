package ZenaCraft.commands.wars;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.War;

public class WarScore extends TemplateCommand{

    public WarScore() {super(0);}

    @Override
    protected boolean run() {
        Faction faction = App.factionIOstuff.getPlayerFaction(player);
        War war = App.warThread.getWarFromFaction(faction);

        if (war == null){
            player.sendMessage(App.zenfac + ChatColor.RED + "your faction is not at war!");
            return true;
        }

        String losing = ChatColor.RED + "" + ChatColor.BOLD + "[Losing]" + ChatColor.RESET;
        String winning = ChatColor.GREEN + "" + ChatColor.BOLD + "[Winning]" + ChatColor.RESET;
        String statusquo = "in a " + ChatColor.YELLOW + "" + ChatColor.BOLD + "[Status Quo]" + ChatColor.RESET;
        String reply = App.zenfac + ChatColor.WHITE + "You are ";

        if(war.getAttackers().equals(faction)){
            reply += ChatColor.BOLD + String.valueOf(war.getWarScore()*100) + "%" + ChatColor.RESET + " towards ";
            if (war.getWarScore() > 0.5) reply += winning;
            else if (war.getWarScore() < 0.5) reply += losing;
            else reply += statusquo;

            reply += " your war with " + war.getDefenders().getPrefix();
        }
        else{
            reply += ChatColor.BOLD + String.valueOf(war.getWarScore()*100) + "%" + ChatColor.RESET + " towards ";
            if (war.getWarScore() > 0.5) reply += winning;
            else if (war.getWarScore() < 0.5) reply += losing;
            else reply += statusquo;

            reply += " your war with " + war.getDefenders().getPrefix();
        }
        
        player.sendMessage(reply);

        return true;
    }
}
