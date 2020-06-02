package broker;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.Future;
import java.io.IOException;
import java.net.InetSocketAddress;
// import java.util.concurrent.ExecutionException;
// import java.util.concurrent.Future;
/**
 * Hello world!
 *
 */
public class Broker 
{
    public static void main( String[] args )
    {
        System.out.println( "Broker: Hello World!" );
        // String market, String item, String quantity, String price
        if (args.length != 5){
            Utilities.println("Please enter the correct arguments: MARKET ITEM QUANTITY PRICE");
            return ;
        }
        try {
            AsynchronousSocketChannel broker = AsynchronousSocketChannel.open();
            Future <Void> result = broker.connect(new InetSocketAddress("localhost", 5000));
            result.get();
            // String str= buyMessage("01", args[0], args[1], args[2], args[3]);
            
            String str= buyMessage("01", args[0], args[1], args[2], args[3]);
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // write into buffer
            Future<Integer> writeval = broker.write(
                ByteBuffer.wrap(str.getBytes()));
            System.out.println("Writing to server: "+str);
            writeval.get();

            // clear and read
            buffer.clear();
            Future<Integer> readval = broker.read(buffer);
            readval.get();
            System.out.println("Received from market: "
                + new String(buffer.array()).trim());

            // clear and close
            buffer.clear();
            broker.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String buyMessage(String id, String market, String item, String quantity, String price){
        String presum = String.format("0=%s|1=%s|2=%s|3=%s|4=%s|6=%s", id, item, quantity, market, price, "BUY");
        String message = String.format("%s|10=%d", presum, Utilities.calculateChecksum(presum));
        return message;
    }
}
