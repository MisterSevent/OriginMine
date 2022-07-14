package net.mistersevent.originmine.rpacket;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.mistersevent.core.other.dabatase.redis.packet.RedisPacket;
import net.mistersevent.core.spigot.Services;
import net.mistersevent.originmine.account.Account;
import net.mistersevent.originmine.service.StorageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;

public class PlayerClearInventory extends RedisPacket {

    private final StorageService storageService = (StorageService) Services.get(StorageService.class);
    private String playerName;

    public void read(ByteArrayDataInput byteArrayDataInput) {
        this.playerName = byteArrayDataInput.readUTF();
    }

    public void write(ByteArrayDataOutput byteArrayDataOutput) {
        byteArrayDataOutput.writeUTF(this.playerName);
    }

    public void process() {
        Player p = Bukkit.getPlayer(this.playerName);
        Account account = (Account) this.storageService.get(this.playerName);
        if (p != null) {
            p.getInventory().clear();
            p.getInventory().setArmorContents((ItemStack[]) null);
        }

        if (account != null) {
            account.setClearInventory(true);
        }
    }

    public PlayerClearInventory(String playerName) {
        this.playerName = playerName;
    }

    public PlayerClearInventory() {

    }

}
