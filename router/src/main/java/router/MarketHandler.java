package router;


import java.nio.channels.AsynchronousSocketChannel;

public class MarketHandler extends Handler {
    @Override
    public void handle(AsynchronousSocketChannel market, AsynchronousSocketChannel attachment){
        if (attachment != null)
            this.next.handle(market, attachment);
        else {
            try {
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