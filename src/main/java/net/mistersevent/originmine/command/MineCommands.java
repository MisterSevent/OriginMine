package net.mistersevent.originmine.command;

import net.mistersevent.core.other.dabatase.redis.Redis;
import net.mistersevent.core.spigot.Services;
import net.mistersevent.core.spigot.command.CommandCreator;
import net.mistersevent.core.spigot.command.builder.impl.CommandBuilderImpl;
import net.mistersevent.core.spigot.command.helper.CommandHelper;
import net.mistersevent.originmine.OriginMine;
import net.mistersevent.originmine.account.Account;
import net.mistersevent.originmine.rpacket.AccountStoragePacket;
import net.mistersevent.originmine.service.StorageService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class MineCommands {

    private final Redis redis = (Redis) (Redis) Services.get(Redis.class);
    private final StorageService storageService = (StorageService) Services.get(StorageService.class);

    public MineCommands() {
        this.setup();
    }

    public void setup() {
        CommandCreator.create(new CommandBuilderImpl() {
            @Override
            public void handler(CommandSender commandSender, CommandHelper commandHelper, String... strings) throws Exception {
                if (OriginMine.RECEIVER) {
                    Player p = commandHelper.getPlayer(commandSender);
                    p.sendMessage("§aConectando ao servidor padrão...");
                    p.setMetadata("spawnCommand", new FixedMetadataValue(OriginMine.getInstance(), true));
                    MineCommands.this.redis.sendPacket(new AccountStoragePacket(new Account(p)), "mines.sender");
                    Bukkit.getScheduler().scheduleSyncDelayedTask(OriginMine.getInstance(), () -> {
                        OriginMine.getInstance().getBungeeChannelApi().connect(p, OriginMine.getInstance().getConfig().getString("Server.server-name-default"));
                    }, 60L);
                }else {
                    Player p = commandHelper.getPlayer(commandSender);
                    MineCommands.this.storageService.remove(p.getName());
                    MineCommands.this.redis.sendPacket(new AccountStoragePacket(new Account(p)), "mines.receiver");
                    p.sendMessage("§aConectando ao servidor de mineração...");
                    p.setMetadata("mineCommand", new FixedMetadataValue(OriginMine.getInstance(), true));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(OriginMine.getInstance(), () -> {
                        OriginMine.getInstance().getBungeeChannelApi().connect(p, OriginMine.getInstance().getConfig().getString("Server.server-name-bungeecord"));
                    }, 60L);
                }
            }
        }).player().plugin(OriginMine.getInstance()).register("mina", "mundomina");
    }
}
