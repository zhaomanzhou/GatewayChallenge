package util;

import server.core.ServerContext;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ChannelIOUtil
{


    public static ByteBufferPool pool = new ByteBufferPool(ServerContext.get().getConfiguration().getBufferPoolSize());

    public static String readFromChannel(SocketChannel c) throws IOException
    {
        ByteBuffer buffer =  pool.getPool();
        String result;
        try {
            int readed = c.read(buffer);
            if(readed == -1)
                throw new IOException("连接关闭");
            result =  new String(buffer.array(), 0, readed, StandardCharsets.UTF_8);
        } finally {
            pool.releasePool(buffer);
        }

        return result;

    }

    public static void writeToChannel(String msg, SocketChannel c) throws IOException {
        ByteBuffer buffer =  pool.getPool();
        try {
            buffer = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
            c.write(buffer);
        } catch (IOException e) {
            throw e;
        } finally {
            pool.releasePool(buffer);
        }

    }
}
