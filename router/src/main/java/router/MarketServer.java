package router;

public class MarketServer implements Runnable {
    public void run(){
        Router.markets.accept(null, Router.getHandlerChain());
    }
}