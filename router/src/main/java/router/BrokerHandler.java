package router;

import java.util.concurrent.Future;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import router.Handler;

public class BrokerHandler extends Handler{
    @Override
    public void handle(AsynchronousSocketChannel broker, AsynchronousSocketChannel market){
        try {

            Integer id = Router.id_track++;
            Router.routerTable.put(id, broker);
            String str = Integer.toString(id);
            Future<Integer> writeval = broker.write(
                ByteBuffer.wrap(str.getBytes()));
            System.out.println("Assigning Broker ID: "+str);
            writeval.get();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            broker.read( buffer ).get();
            String message = new String(buffer.array()).trim();
            Integer marketId = Integer.parseInt(message.substring(message.indexOf("3=") + 2, message.indexOf("|", message.indexOf("3="))));
            Utilities.println("Got from broker and sending to market with ID: " + marketId);

            if (Router.routerTable.get(marketId) != null){
                Utilities.println(new String(buffer.array()).trim());
                
                buffer.flip();
                Router.routerTable.get(marketId).write( buffer ).get();
                buffer.clear();
    
                Router.routerTable.get(marketId).read(buffer).get();
                Utilities.println("Got from market & sending to broker: ");
                Utilities.println(new String(buffer.array()).trim());
                
                buffer.flip();
                broker.write(buffer).get();
                buffer.clear();
            }

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