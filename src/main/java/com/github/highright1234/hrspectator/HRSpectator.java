package com.github.highright1234.hrspectator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class HRSpectator extends JavaPlugin implements CommandExecutor {

    private static HRSpectator instance;
    private final Set<Player> spectators = new HashSet<>();
    private final Map<Player, GameMode> gameMode = new HashMap<>();

    public static HRSpectator getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        getCommand("hrspectator").setExecutor(new Command());
        instance = this;
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!spectators.contains(event.getPlayer())) return;
                if (event.getPlayer().getGameMode() != GameMode.SPECTATOR) {
                    event.getPlayer().setGameMode(GameMode.SPECTATOR);
                }
                PacketContainer packet = event.getPacket();
                EnumWrappers.PlayerInfoAction action = packet.getPlayerInfoAction().read(0);

                if (action == EnumWrappers.PlayerInfoAction.ADD_PLAYER ||
                        action == EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE) {

                    List<PlayerInfoData> dataList = packet.getPlayerInfoDataLists().read(0);

                    for (int i = 0; i < dataList.size(); i++) {
                        PlayerInfoData data = dataList.get(0);

                        dataList.set(i,
                                new PlayerInfoData(
                                        data.getProfile(),
                                        data.getLatency(),
                                        EnumWrappers.NativeGameMode.ADVENTURE,
                                        data.getDisplayName()));
                    }

                    packet.getPlayerInfoDataLists().write(0, dataList);
                }
            }
        });
    }

    @Override
    public void onDisable() {

    }

    public Set<Player> getSpectators() {
        return spectators;
    }

    public void removeAllSpectators() {
        for (Player players : spectators) {
            removeSpectator(players);
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player);
        gameMode.put(player, player.getGameMode());
        player.setGameMode(GameMode.SPECTATOR);
        PacketContainer spectatePacket = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        spectatePacket.getIntegers().write(0, 3);
        spectatePacket.getFloat().write(0, 2.0F);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, spectatePacket);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        player.setAllowFlight(true);
    }

    public void removeSpectator(Player player) {
        spectators.remove(player);
        player.setGameMode(gameMode.get(player));
        gameMode.remove(player);
    }
}
