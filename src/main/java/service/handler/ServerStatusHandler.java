package service.handler;

import lombok.extern.slf4j.Slf4j;
import server.core.Connector;
import server.handler.global.ConnectorStatusChangedListener;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ServerStatusHandler implements ConnectorStatusChangedListener
{
    private AtomicInteger activeConnect = new AtomicInteger(0);

    @Override
    public void onChannelConnected(Connector c)
    {

        //activeConnect.getAndIncrement();


        //log.info("------------" + c.getId() + " -----------connected");
    }

    @Override
    public void onChannelClosed(Connector c)
    {
        //activeConnect.getAndDecrement();
        //log.info("activeConnect------------" + activeConnect.get());

        //log.info("------------" + c.getId() + " -----------closed");

    }

    @Override
    public void onException(Connector c, Exception e)
    {
        log.warn("Exception {}", e.toString());
        c.close();
    }
}
