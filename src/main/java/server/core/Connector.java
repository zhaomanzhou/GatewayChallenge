package server.core;

import server.handler.connector.HandlerChain;
import server.handler.connector.input.InputHandlerChainFactory;
import server.handler.connector.output.OutputHandlerChainFactory;
import util.ChannelIOUtil;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 对SocketChannel进行封装，
 */
public class Connector
{
    private SocketChannel channel;

    /**
     * 读入消息处理器
     */
    private HandlerChain inputHandlerChain;
    /**
     * 写入消息处理器
     */
    private HandlerChain outputHandlerChain;
    private String id;
    static AtomicInteger m = new AtomicInteger(1);




    private Object attach;

    public Connector(SocketChannel channel)
    {
        this.channel = channel;
        this.id =  "" + Connector.m.getAndIncrement();
        try
        {
            inputHandlerChain = InputHandlerChainFactory.handlerChainFromContext(this);
            outputHandlerChain = OutputHandlerChainFactory.handlerChainFromContext(this);
        } catch (IllegalAccessException | InstantiationException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Handler chain 创建失败");
        }

        ServerContext.get().getConfiguration().getConnectorStatusChangedListener().onChannelConnected(this);

    }

    public void close()
    {
        ServerContext.get().getConfiguration().getConnectorStatusChangedListener().onChannelClosed(this);
        ServerContext.get().getConfiguration().getRegistry().unRegisterInput(this);
        try
        {
            channel.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public SocketChannel getSocketChannel()
    {
        return this.channel;
    }


    public void onChannelActive(String msg)
    {
        inputHandlerChain.doChainHandler(msg);
    }

    /**
     * 把数据王handler里写
     * @param s
     */
    public void write(String s)
    {
        try
        {
            outputHandlerChain.doChainHandler(s);
        } catch (Exception e)
        {
            ServerContext.get().getConfiguration().getConnectorStatusChangedListener()
                    .onException(this, e);
        }
    }


    /**
     * 真正把数据包送出
     */
    public void sendOut(String s)
    {
        try
        {
            ChannelIOUtil.writeToChannel(s, this.channel);
        } catch (IOException e)
        {
            ServerContext.get().getConfiguration().getConnectorStatusChangedListener()
                    .onException(this, e);
        }
    }


    public String getId()
    {
        return id;
    }

    public Object getAttach()
    {
        return attach;
    }

    public void setAttach(Object attach)
    {
        this.attach = attach;
    }
}
