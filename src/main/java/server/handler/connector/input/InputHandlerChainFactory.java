package server.handler.connector.input;

import server.core.Connector;
import server.core.ServerContext;
import server.handler.connector.HandlerChain;
import server.handler.connector.MessageHandler;
import service.handler.HttpHandler;

import java.util.LinkedList;

public class InputHandlerChainFactory
{
    //反射方法根据配置生成输入处理器链
    public static HandlerChain handlerChainFromContext1(Connector connector) throws IllegalAccessException, InstantiationException
    {
        HandlerChain handlerChain = new HandlerChain(connector);
        LinkedList<Class<? extends MessageHandler>> messageHandlers = ServerContext.get().getConfiguration().getInputMessageHandlers();
        //没有配置一个处理器，直接返回
        if(messageHandlers.size() == 0)
        {
            return handlerChain;
        }
        //最后一个处理器,不需要动态代理
        MessageHandler nextMessageHandler = messageHandlers.getLast().newInstance();
        nextMessageHandler.setConnector(connector);
        handlerChain.addHandlerFromTail(nextMessageHandler);

        //从后向前依次代理
        for(int i = messageHandlers.size()-2; i >= 0 ; i--)
        {

            InputMessageHandlerProxy messageHandlerProxy = new InputMessageHandlerProxy(nextMessageHandler);

            nextMessageHandler = messageHandlerProxy.getProxy(messageHandlers.get(i));
            nextMessageHandler.setConnector(connector);
            handlerChain.addHandlerFromTail(nextMessageHandler);
        }
        return handlerChain;
    }


    //直接写死Handler，性能更强，比赛专用
    public static HandlerChain handlerChainFromContext(Connector connector) throws IllegalAccessException, InstantiationException
    {
        HandlerChain handlerChain = new HandlerChain(connector);

        //最后一个处理器,不需要动态代理
        MessageHandler nextMessageHandler = new HttpHandler();
        nextMessageHandler.setConnector(connector);
        handlerChain.addHandlerFromTail(nextMessageHandler);

        return handlerChain;
    }
}


