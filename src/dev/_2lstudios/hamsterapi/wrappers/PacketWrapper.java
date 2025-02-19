package dev._2lstudios.hamsterapi.wrappers;

import dev._2lstudios.hamsterapi.enums.PacketType;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PacketWrapper {
    private final Object packet;
    private final String name;

    private final Map<String, String> strings = new HashMap<>();
    private final Map<String, Double> doubles = new HashMap<>();
    private final Map<String, Float> floats = new HashMap<>();
    private final Map<String, Integer> integers = new HashMap<>();
    private final Map<String, Boolean> booleans = new HashMap<>();
    private final Map<String, ItemStack> items = new HashMap<>();
    private final Map<String, Object> objects = new HashMap<>();

    public PacketWrapper(final Object packet) {

        this.packet = packet;
        this.name = packet.getClass().getSimpleName();

        final Class<?> packetClass = packet.getClass();

        for (final Field field : packetClass.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                final String fieldName = field.getName();
                final Object value = field.get(packet);

                if (value instanceof String) {
                    this.strings.put(fieldName, (String) value);
                } else if (value instanceof Integer) {
                    this.integers.put(fieldName, (Integer) value);
                } else if (value instanceof Float) {
                    this.floats.put(fieldName, (Float) value);
                } else if (value instanceof Double) {
                    this.doubles.put(fieldName, (Double) value);
                } else if (value instanceof Boolean) {
                    this.booleans.put(fieldName, (Boolean) value);
                }

                if (value instanceof net.minecraft.server.v1_8_R3.ItemStack) {
                    final ItemStack itemStack = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_8_R3.ItemStack) value);

                    this.items.put(fieldName, itemStack);
                    this.objects.put(fieldName, itemStack);
                } else {
                    this.objects.put(fieldName, value);
                }

                field.setAccessible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPacketType(final String packetName) {
        return this.name.equals(packetName);
    }

    public boolean isPacketType(final PacketType packetType) {
        return this.name.contains(packetType.toString());
    }

    public PacketType getType() {
        for (final PacketType packetType : PacketType.values()) {
            if (packetType.name().equals(this.name)) {
                return packetType;
            }
        }

        return null;
    }

    public void write(final String key, final Object value) {
        try {
            final Field field = this.packet.getClass().getDeclaredField(key);

            field.setAccessible(true);
            field.set(packet, value);
            field.setAccessible(false);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void write(final String key, final ItemStack itemStack) {
        try {
            final Field field = this.packet.getClass().getDeclaredField(key);
            final net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);

            field.setAccessible(true);
            field.set(packet, nmsItemStack);
            field.setAccessible(false);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public String getString(String key) {
        return this.strings.get(key);
    }

    public int getInteger(String key) {
        return this.integers.get(key);
    }

    public boolean getBoolean(String key) {
        return this.booleans.get(key);
    }

    public double getDouble(String key) {
        return this.doubles.get(key);
    }

    public float getFloat(String key) {
        return this.floats.get(key);
    }

    public ItemStack getItem(String key) {
        return this.items.get(key);
    }

    public Map<String, String> getStrings() {
        return this.strings;
    }

    public Map<String, Integer> getIntegers() {
        return this.integers;
    }

    public Map<String, Boolean> getBooleans() {
        return this.booleans;
    }

    public Map<String, Double> getDouble() {
        return this.doubles;
    }

    public Map<String, Float> getFloats() {
        return this.floats;
    }

    public Map<String, ItemStack> getItems() {
        return this.items;
    }

    public Map<String, Object> getObjects() {
        return this.objects;
    }

    public Object getPacket() {
        return this.packet;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.packet.toString();
    }
}
