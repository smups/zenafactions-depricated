package ZenaCraft.commands.ranks;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import net.md_5.bungee.api.ChatColor;

public class ListPerms extends TemplateCommand{

    @Override
    protected boolean run() {
        String perms = App.zenfac + ChatColor.WHITE + "Possible permissions: \n";

        for(String perm : App.permList){
            perms += perm + ",\n";
        }

        player.sendMessage(perms);
        return true;
    }    
}