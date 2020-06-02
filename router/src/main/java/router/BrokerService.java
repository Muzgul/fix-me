package router;

import java.nio.channels.AsynchronousSocketChannel;

public class BrokerService implements Runnable {

    private AsynchronousSocketChannel market;

    public BrokerService(AsynchronousSocketChannel market){
        this.market = market;
    }

    public void run(){
        if (this.market != null)
            Router.brokers.accept(this.market, Router.getHandlerChain());
    }
}