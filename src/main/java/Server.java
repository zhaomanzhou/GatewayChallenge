import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import server.bootstrap.ServerBootstrap;
import server.core.ServerConfiguration;
import server.core.ServerContext;
import server.handler.connector.input.impl.SimplePrintHandler;
import server.schedule.impl.ConnectorRegistrySelectImp;
import service.handler.HttpHandler;
import service.handler.ServerStatusHandler;

import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @author zhaomanzhou
 * @date 2020/3/27 10:19 下午
 */
@Slf4j
public class Server {

    static {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("root").setLevel(Level.valueOf("error"));
    }
    public static void main(String[] args) {

        int threadSize = Runtime.getRuntime().availableProcessors()*2 + 2;
        int poolSize = 500;

        if(args.length > 0)
        {
            threadSize = Integer.parseInt(args[0]);
        }

        if(args.length > 1)
        {
            poolSize = Integer.parseInt(args[1]);
        }

        if(args.length > 2)
        {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.getLogger("root").setLevel(Level.valueOf("info"));
        }




        ServerConfiguration configuration = new ServerConfiguration.ServerConfigurationBuilder()
                .port(8888)
                .connectorRegistry(new ConnectorRegistrySelectImp())
                .connectorStatusChangedListener(new ServerStatusHandler())
                //.addInputMessageHandler(FixedLengthDecoder.class)
                .addInputMessageHandler(HttpHandler.class)
                .readBufferSize(1024)
                .scheduleThreadSize(threadSize)
                .bufferPoolSize(poolSize)
                .inStringCharset(Charset.forName("UTF-8"))
                .build();
        ServerContext.init(configuration);
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.start();
        System.out.println("server started on port: " + configuration.getPort());
//        Scanner sc = new Scanner(System.in);
//        while (true)
//        {
//            String next = sc.next();
//            if(next.equals("quit"))
//            {
//                bootstrap.close();
//                break;
//            }
//        }
//        System.out.println("Server stopped");
    }
}
