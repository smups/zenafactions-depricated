package ZenaCraft.events;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;

import ZenaCraft.App;

public class PlayerMove implements Listener{

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        
        Location oldLocation = event.getFrom();
        Location newLocation = event.getTo();

        Chunk newChunk = newLocation.getChunk();
        Chunk oldChunk = oldLocation.getChunk();

        //Check of de player naar een nieuwe chunk is gelopen
        if (oldChunk.getX() != newChunk.getX() || oldChunk.getZ() != newChunk.getZ()){
            String oldFQCName = "X" + String.valueOf(oldChunk.getX()/100) + "Z" + String.valueOf(oldChunk.getZ()/100);
            String newFQCName = "X" + String.valueOf(newChunk.getX()/100) + "Z" + String.valueOf(newChunk.getZ()/100);

            //Check of de player naar een nieuwe FQC is gelopen
            if(!oldFQCName.equals(newFQCName)){
                player.sendMessage("Moved from: " + oldFQCName + " to: " + newFQCName);
                new MovedFQC(player, oldLocation, newLocation);
            }
        }
    }

    private class MovedFQC implements Runnable{
        private Thread t;
        private Player player;
        private Location oldLocation;
        private Location newLocation;

        public MovedFQC(Player player, Location oldLocation, Location newLocation){
            this.player = player;
            this.oldLocation = oldLocation;
            this.newLocation = newLocation;
            this.start();
        }

        public Thread getThread(){
            return this.t;
        }

        public void start(){
            if (t == null){
                t = new Thread(this);
                t.start();
            }
        }

        public void run(){
            player.sendMessage(App.zenfac + ChatColor.WHITE + "loading new FQC...");
            try{
                //Wacht totdat de **verwijder** thread klaar is
                App.factionIOstuff.unLoadFQC(player, oldLocation).join();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            //laad nu de nieuwe benodigde threads
            App.factionIOstuff.loadFQC(player, newLocation);
            /*
                Okay dit is nog steeds suboptimaal: je zou een rare bug
                kunnen krijgen als een speler te snel van chunk wisselt
                omdat we eerste de oude chunks verwijderen (incl de chunk
                met de player) voordat we ze weer toevoegen, wat zuigt.
                Ook zouden er eigenlijk niet meerdere van deze threads 
                tegelijkertijd moeten runnen -> dit veroorzaakt een memory
                leak!

                Te lui om dit te fixen tho.
            */
        }
    }
    
/*
    private boolean claimChunkMethod(Location location, Player player){
        Chunk chunk = location.getChunk();
        MarkerSet markerSet = App.getMarkerSet();
        String fQCName = "X" + String.valueOf(chunk.getX()/100) + "Z" + String.valueOf(chunk.getZ()/100);

        FactionQChunk fQC = App.loadedFQChunks.get(fQCName);
        byte[][] chunkData = fQC.getChunkData();
        int ownerID = chunkData[Math.abs(chunk.getX() % 100)][Math.abs(chunk.getZ() % 100)];

        if (ownerID != -1){
            player.sendMessage(App.zenfac + ChatColor.DARK_RED + "This chunk is already claimed!");
            return false;
        }
        chunkData[Math.abs(chunk.getX() % 100)][Math.abs(chunk.getZ() % 100)] = player.getMetadata("factionID").get(0).asByte();

        //do the dynmap stuff
        AreaMarker marker = markerSet.createAreaMarker(String.valueOf(chunk.getX()) + String.valueOf(chunk.getZ()), player.getMetadata("faction").get(0).asString(), true, player.getWorld().getName(), new double[] {chunk.getX()*16, chunk.getX()*16 + 16}, new double[] {chunk.getZ()*16, chunk.getZ()*16 + 16}, true);
        marker.setFillStyle(0.1, 0x42f4f1);
        marker.setLineStyle(1, 0.2, 0x42f4f1);
        player.sendMessage(App.zenfac + "Chunk Claimed!");

        return true;
    }
*/
}