package ZenaCraft.events;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;

public class PlayerJoin implements Listener{
    //This file checks on join if the player has metadata corresponding to membership of a new faction
    //if not, it adds the player to the 'neutral' faction. Also adds metadata tag for faction to new players
    
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Plugin plugin = App.getPlugin(App.class);

        Hashing obj = new Hashing();
        obj.event = event;
        obj.player = player;
        obj.plugin = plugin;

        Thread tr = new Thread(obj);
        tr.start();
    }

    private class Hashing implements Runnable{
        Player player = null;
        Plugin plugin = null;
        PlayerJoinEvent event = null;
        
        public void run(){
            if (!player.hasMetadata("faction")){
                HashMap<UUID,String> player_hash = getPlayerHash(player);

                if (!player_hash.containsKey(player.getUniqueId())){
                    player_hash.put(player.getUniqueId(), "default");
                    if (!player.hasMetadata("faction")){
                        player.setMetadata("faction", new FixedMetadataValue(plugin, "default"));
                    }
                    updateHash(player_hash);            
                    event.getPlayer().sendMessage(App.zenfac + ChatColor.DARK_RED + "You've been added to the international faction!");
                }
            
                else{
                    String faction = player_hash.get(player.getUniqueId());
                    if (!player.hasMetadata("faction")){
                        player.setMetadata("faction", new FixedMetadataValue(plugin, faction));
                    }
                    event.setJoinMessage(App.zenfac + "(" + faction + ") " + ChatColor.WHITE + "Welcome back " + ChatColor.BOLD + player.getDisplayName());
                }
            }
            else{
                String faction = player.getMetadata("faction").get(0).asString();
                event.setJoinMessage(App.zenfac + "(" + faction + ") " + ChatColor.WHITE + "Welcome back " + ChatColor.BOLD + player.getDisplayName());
            }
        }

        private HashMap<UUID,String> getPlayerHash(Player player){
            HashMap<UUID, String> players = null;
            try{
                FileInputStream file = new FileInputStream(App.player_db);
                ObjectInputStream in = new ObjectInputStream(file);
                players = (HashMap<UUID, String>) in.readObject();
                in.close();
                file.close();
    
                return players;
            }
            catch (IOException i) {
                i.printStackTrace();
                return players;
            }
            catch(ClassNotFoundException c){
                c.printStackTrace();
                return players;
            }
        }
    
        private void updateHash(HashMap<UUID,String> map){
            try{
                FileOutputStream file = new FileOutputStream(App.player_db);
                ObjectOutputStream out = new ObjectOutputStream(file);
                out.writeObject(map);
                out.close();
                file.close();
            }
            catch (IOException i){
                i.printStackTrace();
            }
        }
    }
}