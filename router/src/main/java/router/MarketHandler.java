package router;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import java.nio.ByteBuffer;

public class MarketHandler extends Handler {
    @Override
    public void handle(AsynchronousSocketChannel market, AsynchronousSocketChannel attachment){
        if (attachment != null)
            this.next.handle(market, attachment);
        else {
            try {
                Integer id = Router.id_track++;
                Router.routerTable.put(id, market);
                String str = Integer.toString(id);
                Future<Integer> writeval = market.write(
                    ByteBuffer.wrap(str.getBytes()));
                System.out.println("Assigning Market ID: "+str);
                writeval.get();
                // Market is connected and listening - Open thread for new Market
                Router.executor.submit(new MarketServer());
                // Successful market connection - Open thread for brokers
                Router.executor.submit(new BrokerService(market));
            } catch (Exception e){
                // Worst case - something failed
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Override
    public void completed(AsynchronousSocketChannel market, AsynchronousSocketChannel attachment){
        Utilities.println("MARKET(ROUTER) CONNECTED");
        this.handle(market, attachment);
    }

    @Override
    public void failed(Throwable e, AsynchronousSocketChannel server){
        Utilities.println("The MARKET(ROUTER) handler failed:");
        e.printStackTrace();
        Thread.currentThread().interrupt();
    }
}