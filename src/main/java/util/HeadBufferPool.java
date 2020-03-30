package util;

import lombok.extern.slf4j.Slf4j;
import server.core.ServerContext;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
@Slf4j
public class HeadBufferPool
{
    private LinkedBlockingQueue<ByteBuffer> pools;


    public HeadBufferPool(int poolSize)
    {
        pools = new LinkedBlockingQueue<>();
        for (int i = 0; i < poolSize; i++) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(100);
            buffer.put("HTTP/1.1 200\nContent-Type: text/plain;charset=UTF-8\n\n".getBytes());
            pools.add(buffer);
        }
    }


    public ByteBuffer getBuffer()
    {
        ByteBuffer poll = null;
        try {
            poll = pools.take();
        } catch (InterruptedException e) {
            log.warn(e.getStackTrace().toString());
        }
        return poll;
    }

    public synchronized void releaseBuffer(ByteBuffer byteBuffer)
    {
        byteBuffer.flip();
        try {
            pools.put(byteBuffer);
        } catch (InterruptedException e) {
            log.warn(e.getStackTrace().toString());
        }
    }
}
