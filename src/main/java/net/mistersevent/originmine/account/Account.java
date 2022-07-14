package net.mistersevent.originmine.account;

import net.mistersevent.originmine.json.SerializeInventory;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Account {

    private final String name;
    private String[] playerInventory;
    private boolean clearInventory;
    private double exp;
    private double level;

    public Account(Player p) {
        this(p.getName(), false);
        this.exp = (double) p.getExp();
        this.level = (double) p.getLevel();
        this.playerInventory = SerializeInventory.playerInventoryToBase64(p.getInventory());
    }

    public Account(String name, boolean clearInventory) {
        this.name = name;
        this.clearInventory = clearInventory;
    }

    public JSONObject serialize() {
        org.json.simple.JSONObject object = new JSONObject();
        object.put("name", this.name);
        object.put("clearInventory", String.valueOf(this.clearInventory));
        object.put("playerInventory", String.join(",", this.playerInventory));
        object.put("exp", this.exp);
        object.put("level", this.level);
        return object;
    }

    public static Account deserialize (String json) {
        try {

            JSONObject object = (JSONObject) (new JSONParser()).parse(json);
            Account account = new Account(object.get("name").toString(), Boolean.parseBoolean(object.get("clearInventory").toString()));
            account.setExp((Double) object.get("exp"));
            account.setLevel((Double) object.get("level"));
            account.setPlayerInventory(((String) object.get("playerInventory")).split(","));
            return account;
        }catch (Exception var1) {
            var1.printStackTrace();
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public double getExp() {
        return exp;
    }

    public double getLevel() {
        return level;
    }

    public String[] getPlayerInventory() {
        return playerInventory;
    }

    public boolean isClearInventory() {
        return clearInventory;
    }

    public void setClearInventory(boolean clearInventory) {
        this.clearInventory = clearInventory;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public void setPlayerInventory(String[] playerInventory) {
        this.playerInventory = playerInventory;
    }
}
