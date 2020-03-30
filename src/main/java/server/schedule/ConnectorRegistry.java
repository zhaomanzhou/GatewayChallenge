package server.schedule;

import server.core.Connector;


/**
 * 调度器
 */
public interface ConnectorRegistry
{
    public void start();
    public boolean isStarting();

    public void registerInput(Connector connector);
    public void unRegisterInput(Connector connector);

    public void close();


}
