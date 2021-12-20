package ZenaCraft.objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import ZenaCraft.App;

public class FactionQChunk implements Serializable{
    static final long serialVersionUID = 100L;

    //persistent fields
    final String name;
    final double[] pos;
    final UUID uuid;
    private byte[][] chunkData = new byte[100][100];
    private String qString;

    //transient fields
    transient HashMap<UUID,Integer> onlinePlayers = new HashMap<UUID,Integer>();   

    public FactionQChunk(String newName, Player player, double[] newPos){
        name = newName;
        onlinePlayers.put(player.getUniqueId(), 0); //player : rank
        pos = new double[] {newPos[0], newPos[1]};

        calcQString(); //this *has* to happen before getFQChunkdata!!!!
        getFQChunkData(newName, newPos);

        uuid = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FactionQChunk factionQChunk = (FactionQChunk) o;
        if (name != factionQChunk.name) return false;
        return true;
    }

    @Override
    public int hashCode(){
        return uuid.hashCode();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        //do the deserilasation
        in.defaultReadObject();

        //handle stupid cases
        if (qString == null) calcQString();
    }

    private void calcQString(){
        if (pos[0] > 0 && pos[1] > 0) qString = "Q1";
        if (pos[0] < 0 && pos[1] > 0) qString = "Q2";
        if (pos[0] < 0 && pos[1] < 0) qString = "Q3";
        if (pos[0] > 0 && pos[1] < 0) qString = "Q4";
    }

    private synchronized void getFQChunkData(String name, double[] pos){
        String fileLoc = new String(App.FQChunk_db);

        fileLoc = qString + "/" + name + ".ser";

        File fQFile = new File(fileLoc);

        if (!fQFile.exists()){
            try{
                for (byte[] value : this.chunkData){
                    Arrays.fill(value, (byte) -1);
                }
                FileOutputStream file = new FileOutputStream(fQFile);
                ObjectOutputStream out = new ObjectOutputStream(file);
                out.writeObject(this);
                file.close();
            }
            catch (IOException i){
                i.printStackTrace();
            }
        }
        else{
            try{
                FileInputStream file = new FileInputStream(fQFile);
                ObjectInputStream in = new ObjectInputStream(file);
                FactionQChunk data = (FactionQChunk) in.readObject();
                this.chunkData = data.chunkData;
                in.close();
                file.close();
            }
            catch (IOException i){
                i.printStackTrace();
            }
            catch (ClassNotFoundException c){
                c.printStackTrace();
            }
        }
    }

    public synchronized void saveFQChunkData(){
        String fileLoc = new String(App.FQChunk_db);
        if (pos[0] > 0 && pos[1] > 0) fileLoc += "Q1/";
        if (pos[0] < 0 && pos[1] > 0) fileLoc += "Q2/";
        if (pos[0] < 0 && pos[1] < 0) fileLoc += "Q3/";
        if (pos[0] > 0 && pos[1] < 0) fileLoc += "Q4/";

        fileLoc += name;
        fileLoc += ".ser";

        File fQFile = new File(fileLoc);

        try{
            FileOutputStream file = new FileOutputStream(fQFile);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(this);
            file.close();
        }
        catch (IOException i){
            i.printStackTrace();
        }
    }

    public String getName(){
        return name;
    }
    public synchronized HashMap<UUID,Integer> getOnlinePlayers(){
        return onlinePlayers;
    }
    public synchronized void setOnlinePlayers(HashMap<UUID,Integer> newOnlinePlayers){
        this.onlinePlayers = newOnlinePlayers;
    }
    public synchronized void addOnlinePlayer(Player player){
        this.onlinePlayers.put(player.getUniqueId(), 1);
    }
    public synchronized void removeOnlinePlayer(Player player){
        this.onlinePlayers.remove(player.getUniqueId());
    }
    public double[] getPos(){
        return pos;
    }
    public synchronized byte[][] getChunkData(){
        return chunkData;
    }
    public synchronized void setChunkData(byte[][] newChunkData){
        this.chunkData = newChunkData;
    }
    public byte[] getSign(){
        if (qString.equals("Q1")) return new byte[] {1,1};
        if (qString.equals("Q2")) return new byte[] {-1,1};
        if (qString.equals("Q3")) return new byte[] {-1,-1};
        if (qString.equals("Q4")) return new byte[] {1,-1};
        return null;
    }
}