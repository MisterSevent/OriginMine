package net.mistersevent.originmine;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import java.util.Base64;

import net.mistersevent.core.other.dabatase.DatabaseCredentials;
import net.mistersevent.core.other.dabatase.mysql.MySQL;
import net.mistersevent.core.other.dabatase.mysql.impl.MySQLImpl;
import net.mistersevent.core.other.dabatase.redis.Redis;
import net.mistersevent.core.other.dabatase.redis.impl.RedisImpl;
import net.mistersevent.core.other.dabatase.redis.packet.RedisPacket;
import net.mistersevent.core.other.util.ClassGetter;
import net.mistersevent.core.spigot.Services;
import net.mistersevent.core.spigot.api.configuration.ConfigAPI;
import net.mistersevent.core.spigot.plugin.MisterSeventPlugin;
import net.mistersevent.originmine.account.Account;
import net.mistersevent.originmine.command.MineCommands;
import net.mistersevent.originmine.listener.player.PlayerListener;
import net.mistersevent.originmine.listener.server.MineListeners;
import net.mistersevent.originmine.menus.ReceiverMinaInventory;
import net.mistersevent.originmine.menus.SenderMinaInventory;
import net.mistersevent.originmine.rpacket.AccountStoragePacket;
import net.mistersevent.originmine.runnable.RunnableBlock;
import net.mistersevent.originmine.service.StorageService;
import net.mistersevent.originmine.service.impl.StorageServiceImpl;
import net.mistersevent.originmine.util.BungeeChannelApi;
import net.mistersevent.pick.enchantment.attributes.EnchantmentAttributes;
import org.bukkit.Bukkit;

public final class OriginMine extends MisterSeventPlugin {

    private static OriginMine instance;
    public static boolean RECEIVER;

    private BungeeChannelApi bungeeChannelApi;


    @Override
    public void load() {

        instance = this;
        RECEIVER = getConfig().getBoolean("Server.use");
        saveDefaultConfig();
        registerListeners();
        Services.create(this);
        Services.add(Redis.class, new RedisImpl("origin-mine", new DatabaseCredentials(this.getConfig().getString("redis.host"), (String) null, (String) null, this.getConfig().getString("redis.pass"), this.getConfig().getInt("redis.port"))));
        Services.add(MySQL.class, new MySQLImpl("origin-mine", new DatabaseCredentials(this.getConfig().getString("mysql.host"), this.getConfig().getString("mysql.db"), this.getConfig().getString("mysql.user"), this.getConfig().getString("mysql.pass"), this.getConfig().getInt("mysql.port"))));
        Services.add(StorageService.class, new StorageServiceImpl());

    }

    public void enable() {
        this.bungeeChannelApi = BungeeChannelApi.of(this);
        ((StorageService) Services.get(StorageService.class)).load();
        ((Redis) Services.get(Redis.class)).connect(new RedisPubSubAdapter<String, String>() {
            public void message(String channel, String message) {
                java.lang.String subscribed;
                if (OriginMine.RECEIVER) {
                    subscribed = "mines.receiver";
                }else {
                    subscribed = "mines.sender";
                }

                if (channel.equals(subscribed)) {
                    byte[] raw = Base64.getDecoder().decode(java.lang.String.valueOf(message));
                    ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(raw);
                    java.lang.String id = byteArrayDataInput.readUTF();
                    ClassGetter.getClassesForPackage(this, "net.mistersevent.originmine.rpacket").stream().filter((clazz) -> {
                        return clazz.getSimpleName().equalsIgnoreCase(id);
                    }).findFirst().ifPresent((clazz) -> {
                        try {
                            RedisPacket packet = (RedisPacket) clazz.newInstance();
                            packet.read(byteArrayDataInput);
                            packet.process();
                        }catch (IllegalAccessException | InstantiationException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }, "mines.receiver", "mines.sender");

        new MineCommands();
        this.registerListeners(new PlayerListener(), new MineListeners(), new ReceiverMinaInventory(), new SenderMinaInventory());
        if (RECEIVER) {
            new RunnableBlock().runTaskTimer(this, 20L, OriginMine.getInstance().getConfig().getInt("regeneration-delay") * 20L);
        }
    }

    public void disable() {
        if (RECEIVER) {
            Bukkit.getOnlinePlayers().forEach((player) -> {
                ((Redis)Services.get(Redis.class)).sendPacket(new AccountStoragePacket(new Account(player)), "mines.sender");
            });
        }

        ((Redis) Services.get(Redis.class)).shutdown();

    }

    public static OriginMine getInstance() {
        return instance;
    }

    public BungeeChannelApi getBungeeChannelApi() {
        return bungeeChannelApi;
    }

}

