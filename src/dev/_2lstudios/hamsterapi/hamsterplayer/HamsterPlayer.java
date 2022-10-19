package dev._2lstudios.hamsterapi.hamsterplayer;

import dev._2lstudios.hamsterapi.Debug;
import dev._2lstudios.hamsterapi.HamsterAPI;
import dev._2lstudios.hamsterapi.enums.HamsterHandler;
import dev._2lstudios.hamsterapi.handlers.HamsterChannelHandler;
import dev._2lstudios.hamsterapi.handlers.HamsterDecoderHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.nio.channels.ClosedChannelException;

public class HamsterPlayer {
    private final Player player;
    private final HamsterAPI hamsterAPI;
    private Channel channel;
    private boolean setup = false;
    private boolean injected = false;

    HamsterPlayer(final Player player) {
        this.player = player;
        this.hamsterAPI = HamsterAPI.getInstance();
    }

    public Player getPlayer() {
        return this.player;
    }

    // Sends the HamsterPlayer to another Bungee server
    public void sendServer(final String serverName) {
        hamsterAPI.getBungeeMessenger().sendPluginMessage("ConnectOther", player.getName(), serverName);
    }

    // Forcibly closes the player connection
    public void closeChannel() {
        if (channel != null && channel.isActive()) {
            channel.close();
        }
    }

    public void sendPacket(final Packet packet) {
        try {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        } catch (final Exception e) {
            hamsterAPI.getLogger().info("Failed to send packet to player " + player.getName() + "!");
        }
    }

    public Channel getChannel() {
        return channel;
    }

    // Removes handlers from the player pipeline
    public void uninject() {
        if (injected && channel != null && channel.isActive()) {
            final ChannelPipeline pipeline = channel.pipeline();

            if (pipeline.get(HamsterHandler.HAMSTER_DECODER) != null) {
                pipeline.remove(HamsterHandler.HAMSTER_DECODER);
            }

            if (pipeline.get(HamsterHandler.HAMSTER_CHANNEL) != null) {
                pipeline.remove(HamsterHandler.HAMSTER_CHANNEL);
            }
        }
    }

    // Sets variables to simplify packet handling and inject
    public void setup()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        if (!setup) {
            EntityPlayer handle = ((CraftPlayer) player).getHandle();

            PlayerConnection playerConnection = handle.playerConnection;

            NetworkManager networkManager = playerConnection.networkManager;

            this.channel = networkManager.channel;
            Debug.info("Getting Channel from networkManager field (" + this.player.getName() + ")");

            this.setup = true;
        }
    }

    // Injects handlers to the player pipeline with NMS
    public void inject() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            NoSuchFieldException, ClosedChannelException {
        if (!injected) {
            setup();

            if (!channel.isActive()) {
                Debug.warn("Trying to inject a player with NIO channel closed (" + this.player.getName() + ")");
                throw new ClosedChannelException();
            }

            final ChannelPipeline pipeline = channel.pipeline();
            final ByteToMessageDecoder hamsterDecoderHandler = new HamsterDecoderHandler(this);
            final ChannelDuplexHandler hamsterChannelHandler = new HamsterChannelHandler(this);

            if (pipeline.get("decompress") != null) {
                pipeline.addAfter("decompress", HamsterHandler.HAMSTER_DECODER, hamsterDecoderHandler);
                Debug.info("Added HAMSTER_DECODER in pipeline after decompress (" + this.player.getName() + ")");
            } else if (pipeline.get("splitter") != null) {
                pipeline.addAfter("splitter", HamsterHandler.HAMSTER_DECODER, hamsterDecoderHandler);
                Debug.info("Added HAMSTER_DECODER in pipeline after spliter (" + this.player.getName() + ")");
            } else {
                Debug.crit("No ChannelHandler was found on the pipeline to inject HAMSTER_DECODER (" + this.player.getName() + ")");
                throw new IllegalAccessException(
                        "No ChannelHandler was found on the pipeline to inject " + HamsterHandler.HAMSTER_DECODER);
            }

            if (pipeline.get("decoder") != null) {
                pipeline.addAfter("decoder", HamsterHandler.HAMSTER_CHANNEL, hamsterChannelHandler);
                Debug.info("Added HAMSTER_CHANNEL in pipeline after decoder (" + this.player.getName() + ")");
            } else {
                Debug.crit("No ChannelHandler was found on the pipeline to inject HAMSTER_CHANNEL (" + this.player.getName() + ")");
                throw new IllegalAccessException(
                        "No ChannelHandler was found on the pipeline to inject " + hamsterChannelHandler);
            }

            this.injected = true;
        }
    }

    // Injects but instead of returning an exception returns sucess (Boolean)
    public boolean tryInject() {
        try {
            setup();
            inject();
        } catch (final IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException
                       | ClosedChannelException e) {
            if (Debug.isEnabled()) {
                Debug.crit("Exception throwed while injecting:");
                e.printStackTrace();
            }
            return false;
        }

        return true;
    }
}
