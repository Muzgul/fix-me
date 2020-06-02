package router;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetSocketAddress;
/**
 * Hello world!
 *
 */
public class Router 
{
    final static ExecutorService executor = Executors.newCachedThreadPool();

    static AsynchronousServerSocketChannel markets;

    static AsynchronousServerSocketChannel brokers;

    static Handler getHandlerChain(){
        Handler chain       = new MarketHandler();
                chain.next  = new BrokerHandler();
        return chain;
    }
    
    public static void main( String[] args )
    {
        System.out.println( "Router: Hello World!" );
        try {
            markets = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5001));
            brokers = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5000));
            
            // ExecutorService executor = 

            executor.submit(new MarketServer());
            
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
