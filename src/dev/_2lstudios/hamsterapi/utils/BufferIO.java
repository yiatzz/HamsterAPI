package dev._2lstudios.hamsterapi.utils;

import dev._2lstudios.hamsterapi.wrappers.PacketWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.util.AttributeKey;
import net.minecraft.server.v1_8_R3.*;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class BufferIO {
    private final Inflater inflater;
    private final int compressionThreshold;

    public BufferIO(final int compressionThreshold) {
        this.inflater = new Inflater();
        this.compressionThreshold = compressionThreshold;
    }

    public ByteBuf split(final ByteBuf bytebuf) throws DecoderException {

        bytebuf.markReaderIndex();

        byte[] abyte = new byte[3];

        for (int i = 0; i < abyte.length; ++i) {
            if (!bytebuf.isReadable()) {
                bytebuf.resetReaderIndex();
                throw new DecoderException("Unreadable byte");
            }

            abyte[i] = bytebuf.readByte();
            if (abyte[i] >= 0) {
                final PacketDataSerializer packetDataSerializer = new PacketDataSerializer(Unpooled.wrappedBuffer(abyte));

                try {
                    final int bytes = packetDataSerializer.e();

                    if (bytebuf.readableBytes() >= bytes) {
                        return bytebuf.readBytes(bytes);
                    }

                    bytebuf.resetReaderIndex();
                } finally {
                    packetDataSerializer.release();
                }

                throw new DecoderException("Too much unreadeable bytes");
            }
        }

        return null;
    }

    public ByteBuf decompress(final ByteBuf byteBuf) throws DecoderException,
            DataFormatException {
        if (byteBuf.readableBytes() != 0 && compressionThreshold > -1) {

            final PacketDataSerializer packetData = new PacketDataSerializer(byteBuf);
            final int bytes = packetData.e(); // packetData.e()

            if (bytes == 0) {
                return packetData.readBytes(packetData.readableBytes());
            }

            if (bytes < compressionThreshold) {
                throw new DecoderException("[BufferIO] Badly compressed packet - size of " + bytes
                        + " is below server threshold of " + compressionThreshold);
            } else if (bytes > 2097152) {
                throw new DecoderException("[BufferIO] Badly compressed packet - size of " + bytes
                        + " is larger than protocol maximum of " + 2097152);
            }

            final byte[] abyte = new byte[(int) packetData.readableBytes()];

            packetData.readBytes(abyte);
            inflater.setInput(abyte);

            final byte[] bbyte = new byte[bytes];

            inflater.inflate(bbyte);
            inflater.reset();

            return Unpooled.wrappedBuffer(bbyte);
        } else {
            return byteBuf;
        }
    }

    public PacketWrapper decode(final ChannelHandlerContext chx, final ByteBuf byteBuf, final int maxCapacity)
            throws DecoderException, IOException, IllegalAccessException,
            InstantiationException {

        if (byteBuf.readableBytes() != 0) {
            final int capacity = byteBuf.capacity();

            if (capacity > maxCapacity) {
                if (byteBuf.refCnt() > 0) {
                    byteBuf.clear();
                }

                throw new DecoderException("[BufferIO] Max decoder capacity exceeded. capacity: " + capacity);
            }

            final Channel channel = chx.channel();
            final PacketDataSerializer packetDataSerializer = new PacketDataSerializer(byteBuf);

            final int id = packetDataSerializer.e();

            final AttributeKey<EnumProtocol> attributeKey = NetworkManager.c;

            final EnumProtocol attribute = channel.attr(attributeKey).get();

            Packet<?> packet = attribute.a(EnumProtocolDirection.SERVERBOUND, id);

            if (packet == null) {
                throw new IOException("[BufferIO] Bad packet received. id: " + id);
            }

            packet.a(packetDataSerializer);

            return new PacketWrapper(packet);
        } else {
            return null;
        }
    }
}