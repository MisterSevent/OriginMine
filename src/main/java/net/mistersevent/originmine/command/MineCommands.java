package net.mistersevent.originmine.command;

import net.mistersevent.core.other.dabatase.redis.Redis;
import net.mistersevent.core.spigot.Services;
import net.mistersevent.core.spigot.command.CommandCreator;
import net.mistersevent.core.spigot.command.builder.impl.CommandBuilderImpl;
import net.mistersevent.core.spigot.command.helper.CommandHelper;
import net.mistersevent.originmine.OriginMine;
import net.mistersevent.originmine.account.Account;
import net.mistersevent.originmine.menus.ReceiverMinaInventory;
import net.mistersevent.originmine.menus.SenderMinaInventory;
import net.mistersevent.originmine.rpacket.AccountStoragePacket;
import net.mistersevent.originmine.service.StorageService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class MineCommands {


    public MineCommands() {
        this.setup();
    }

    public void setup() {
        CommandCreator.create(new CommandBuilderImpl() {
            @Override
            public void handler(CommandSender commandSender, CommandHelper commandHelper, String... strings) throws Exception {
                if (OriginMine.RECEIVER) {
                    Player p = commandHelper.getPlayer(commandSender);
                    new ReceiverMinaInventory().showInventory(p);
                }else {
                    Player p = commandHelper.getPlayer(commandSender);
                    new SenderMinaInventory().showInventory(p);
                }
            }

        }).player().plugin(OriginMine.getInstance()).register("mina", "mineracao");
    }
}
