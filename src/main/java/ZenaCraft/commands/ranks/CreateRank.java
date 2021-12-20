package ZenaCraft.commands.ranks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.events.PlayerCreateRankEvent;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Rank;

public class CreateRank extends TemplateCommand{

    public CreateRank() {super(2, true, 0);}

    Faction f;

    @Override
    protected boolean checkArgSize(Player player, String[] args) {
        if (args.length < 2){
            invalidSyntax(player);
            return false;
        }
        if (args[0].equals("new")){
            if (args.length != 2){
                invalidSyntax(player);
                return false;
            }
            return true;
        }
        else if (args[0].equals("copy")){
            if (args.length > 3){
                invalidSyntax(player);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean run() {
        f = App.factionIOstuff.getPlayerFaction(player);
        if (args[0].equals("new")) return newRank();
        return copyRank();
    }

    private boolean newRank(){
        Rank r = new Rank(args[1]);
        if (f.getRanks().contains(r)){
            player.sendMessage(App.zenfac + ChatColor.RED +
                "There already exists a rank with that name!");
            return true;
        }

        f.addRank(r);
        player.sendMessage(App.zenfac + "Added rank: " + r.getName());
        
        //call event
        PlayerCreateRankEvent event = new PlayerCreateRankEvent(f, player, r);
        event.callEvent();
        
        return true;
    }

    private boolean copyRank(){
        Rank r1 = new Rank(args[1]);
        if (!f.getRanks().contains(r1)){
            player.sendMessage(App.zenfac + ChatColor.RED +
                "Can't copy a rank that doens't exist!");
            return true;
        }

        r1 = f.getRanks().get(f.getRanks().indexOf(r1));

        if (args.length == 2) f.addRank(new Rank(r1));
        else f.addRank(new Rank(r1, args[2]));
        return true;
    }
    
}