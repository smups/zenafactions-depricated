package ZenaCraft.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;

public class PlayerDamage implements Listener{

    @EventHandler
    void onPlayerDamage(EntityDamageByEntityEvent e){
        Player attacker;
        Player defender;
        Plugin plugin = App.getPlugin(App.class);

        if (e.getEntity() instanceof Player) defender = (Player) e.getEntity();
        else return;

        if (e.getDamager() instanceof Player) attacker = (Player) e.getDamager();
        else{
            if(!defender.hasMetadata("lastAttacker")) defender.setMetadata("lastAttacker", new FixedMetadataValue(plugin, ""));
            return;
        }
    
        if(!defender.hasMetadata("lastAttacker")) defender.setMetadata("lastAttacker", new FixedMetadataValue(plugin, attacker.getName()));
        else if(defender.getMetadata("lastAttacker").get(0).asString().equals(attacker.getName())) return;

        defender.playSound(defender.getLocation(), Sound.MUSIC_DISC_PIGSTEP, 500f, 1f);
    }   
}