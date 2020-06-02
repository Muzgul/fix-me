package router;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import router.Handler;

public class BrokerHandler extends Handler{
    @Override
    public void handle(AsynchronousSocketChannel broker, AsynchronousSocketChannel market){
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            broker.read( buffer ).get();
            Utilities.println("Got from broker and sending to market: ");
            Utilities.println(new String(buffer.array()).trim());
            
            buffer.flip();
            market.write( buffer ).get();
            buffer.clear();

            market.read(buffer).get();
            Utilities.println("Got from market & sending to broker: ");
            Utilities.println(new String(buffer.array()).trim());
            
            buffer.flip();
            broker.write(buffer).get();
            buffer.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Communication complete - re task broker service
        Router.executor.submit( new BrokerService(market) );
    }
    @Override
    public void completed(AsynchronousSocketChannel broker, AsynchronousSocketChannel market){
        Utilities.println("BROKER (ROUTER) CONNECTED TO MARKET");
        this.handle(broker, market);
    }

    @Override
    public void failed(Throwable e, AsynchronousSocketChannel market){
        Utilities.println("The BROKER(ROUTER) handler failed: ");
        e.printStackTrace();
        Thread.currentThread().interrupt();
    }
}