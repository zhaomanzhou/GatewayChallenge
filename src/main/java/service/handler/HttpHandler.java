package service.handler;

import lombok.extern.slf4j.Slf4j;
import server.core.ServerContext;
import server.handler.connector.AbstractMessageHandler;
import util.ChannelIOUtil;


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author zhaomanzhou
 * @date 2020/3/27 10:36 下午
 */

@Slf4j
public class HttpHandler extends AbstractMessageHandler {

    static String statCmd = "stat";

    final byte[] headArray = "HTTP/1.1 200\nContent-Type: text/plain;charset=UTF-8\n\n".getBytes();


    static AtomicBoolean isUsed = new AtomicBoolean(false);
    @Override
    public void onMessageArrived(Object o) {
        String s = (String)o;
        //处理HTTP请求， 获取路径
        s = s.substring(s.indexOf(' '), s.indexOf('\n'));
        //log.info("request path: {}", s);

        s = s.substring(s.indexOf("/")+1);
        //log.info(s);
        s = s.substring(0, s.indexOf(" "));
        //log.info("request path: {}", s);
        ByteBuffer content = null;
        SocketChannel socketChannel = connector.getSocketChannel();
        try {
            content = handleMessage(s);

            //并发  乐观锁
            while (!isUsed.compareAndSet(false, true)){log.info("dd");};
            socketChannel.write(new ByteBuffer[]{ByteBuffer.wrap(headArray), content});
            isUsed.set(false);
        } catch (IOException e) {
            isUsed.set(false);
            log.warn("Write Exception {}", e.getMessage());
        }finally {

            if(content != null)
            {
                ChannelIOUtil.pool.releasePool(content);
            }
            connector.close();
        }


    }


    public ByteBuffer handleMessage(String s)
    {
        String fileName = "/tmp/tmp" + s + ".txt";
        File f = new File(fileName);
        if(!f.exists())
        {
            try {
                f.createNewFile();
            } catch (IOException e) {
                ByteBuffer buffer = ChannelIOUtil.pool.getPool();
                log.warn("Failed to create file", e);
                buffer.put("Failed to create file".getBytes());
                buffer.flip();
                return buffer;
            }
        }
        try {
            String[] cmd = new String[]{statCmd, fileName};
            ByteBuffer buffer = ChannelIOUtil.pool.getPool();
            Process exec = Runtime.getRuntime().exec(cmd);
            Channels.newChannel(exec.getInputStream()).read(buffer);
            buffer.flip();
            return buffer;

        } catch (IOException e) {
            ByteBuffer buffer = ChannelIOUtil.pool.getPool();
            log.warn("can't get file information", e);
            buffer.put("can't get file information".getBytes());
            buffer.flip();
            return buffer;
        }

    }
}
