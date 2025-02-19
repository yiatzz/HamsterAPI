package dev._2lstudios.hamsterapi;

import dev._2lstudios.hamsterapi.hamsterplayer.HamsterPlayer;
import dev._2lstudios.hamsterapi.hamsterplayer.HamsterPlayerManager;
import dev._2lstudios.hamsterapi.listeners.PlayerJoinListener;
import dev._2lstudios.hamsterapi.listeners.PlayerQuitListener;
import dev._2lstudios.hamsterapi.messengers.BungeeMessenger;
import dev._2lstudios.hamsterapi.utils.BufferIO;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HamsterAPI extends JavaPlugin {
    private static HamsterAPI instance;
    //private Reflection reflection;
    private BufferIO bufferIO;
    private BungeeMessenger bungeeMessenger;
    private HamsterPlayerManager hamsterPlayerManager;

    private static synchronized void setInstance(final HamsterAPI hamsterAPI) {
        HamsterAPI.instance = hamsterAPI;
    }

    public static synchronized HamsterAPI getInstance() {
        return instance;
    }

    public static String getVersion(Server server) {
        final String packageName = server.getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    private void initialize() {
        //final Server server = getServer();

        int compressionThreshold = MinecraftServer.getServer().getPropertyManager().getInt("network-compression-threshold", 256);

        setInstance(this);

        //  this.reflection = new Reflection(server.getClass().getPackage().getName().split("\\.")[3]);
        this.bufferIO = new BufferIO(compressionThreshold);
        this.hamsterPlayerManager = new HamsterPlayerManager();
        this.bungeeMessenger = new BungeeMessenger(this);
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        if (this.getConfig().getBoolean("debug")) {
            Debug.init(this);

            Debug.info("Debug mode is enabled in HamsterAPI (" + Version.getCurrentVersion().toString() + ")");
            Debug.warn("It is recommended not to use this mode in production.");
            Debug.crit("Debug mode can affect server performance while it is active.");
        }

        final Server server = getServer();
        final PluginManager pluginManager = server.getPluginManager();

        initialize();

        server.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerQuitListener(hamsterPlayerManager), this);

        for (final Player player : server.getOnlinePlayers()) {
            final HamsterPlayer hamsterPlayer = this.hamsterPlayerManager.add(player);

            hamsterPlayer.tryInject();
        }
    }

    @Override
    public void onDisable() {
        final Server server = getServer();

        for (final Player player : server.getOnlinePlayers()) {
            final HamsterPlayer hamsterPlayer = this.hamsterPlayerManager.get(player);

            if (hamsterPlayer != null) {
                hamsterPlayer.uninject();
            }

            this.hamsterPlayerManager.remove(player);
        }
    }

    public BufferIO getBufferIO() {
        return this.bufferIO;
    }

    public BungeeMessenger getBungeeMessenger() {
        return this.bungeeMessenger;
    }

    public HamsterPlayerManager getHamsterPlayerManager() {
        return this.hamsterPlayerManager;
    }

    /*
    public Reflection getReflection() {
        return this.reflection;
    }
     */
}
