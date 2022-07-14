package net.mistersevent.originmine.rpacket;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.mistersevent.core.other.dabatase.redis.packet.RedisPacket;
import net.mistersevent.core.spigot.Services;
import net.mistersevent.originmine.account.Account;
import net.mistersevent.originmine.service.StorageService;

public class AccountStoragePacket extends RedisPacket {

    private final StorageService storageService = (StorageService) Services.get(StorageService.class);
    private Account account;

    public void read(ByteArrayDataInput byteArrayDataInput) {
        this.account = Account.deserialize(byteArrayDataInput.readUTF());
    }

    public void write(ByteArrayDataOutput byteArrayDataOutput) {
        byteArrayDataOutput.writeUTF(this.account.serialize().toJSONString());
    }

    public void process() {
        this.storageService.create(this.account);
    }

    public AccountStoragePacket(Account account) {
        this.account = account;
    }

    public AccountStoragePacket() {

    }

}
