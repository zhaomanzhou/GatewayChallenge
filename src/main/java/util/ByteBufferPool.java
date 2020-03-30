package util;

import lombok.extern.slf4j.Slf4j;
import server.core.ServerContext;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ByteBuffer池子，用来读取请求中的数据
 * @author zhaomanzhou
 * @date 2020/3/27 11:59 下午
 */
@Slf4j
public class ByteBufferPool {
    private LinkedBlockingQueue<ByteBuffer> pools;



    public ByteBufferPool(int poolSize)
    {
        pools = new LinkedBlockingQueue<>();
        for (int i = 0; i < poolSize; i++) {
            pools.add(ByteBuffer.allocate(ServerContext.get().getConfiguration().getReadBufferSize()));
        }
    }


    public ByteBuffer getPool()
    {
        ByteBuffer poll = null;
        try {
            poll = pools.take();
        } catch (InterruptedException e) {
            log.warn(e.getStackTrace().toString());
        }
        return poll;
    }

    public synchronized void releasePool(ByteBuffer byteBuffer)
    {
        byteBuffer.clear();
        try {
            pools.put(byteBuffer);
        } catch (InterruptedException e) {
            log.warn(e.getStackTrace().toString());
        }
    }
}
