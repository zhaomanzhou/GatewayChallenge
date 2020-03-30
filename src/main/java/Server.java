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

/**
 * @author zhaomanzhou
 * @date 2020/3/27 10:19 下午
 */
@Slf4j
public class Server {

    static {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("root").setLevel(Level.valueOf("info"));
    }
    public static void main(String[] args) {

        int threadSize = Runtime.getRuntime().availableProcessors()*2 + 3;
        int poolSize = 500;

        if(args.length > 0)
        {
            threadSize = Integer.parseInt(args[0]);
        }
        if(args.length > 1)
        {
            poolSize = Integer.parseInt(args[1]);
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
        log.info("server started on port: " + configuration.getPort());
    }
}
