package market;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.io.IOException;
import java.net.InetSocketAddress;
/**
 * Hello world!
 *
 */
public class Market 
{
    private static Integer id = null;

    private static Map<String, Integer> items = new HashMap<>(){
        {
            put("some", 2);
            put("thing", 2);
        }
    };

    public static void main( String[] args )
    {
        System.out.println( "Market: Hello World!" );
        try {
            AsynchronousSocketChannel market = AsynchronousSocketChannel.open();
            // socket.connect(new InetSocketAddress("localhost", 5000), "Test message", Handler.sendHandler());          // Attempt to connect to the 'router' - localhost:5000
            Future <Void> result = market.connect(new InetSocketAddress("localhost", 5001));                             // Attempt to connect to the 'router' - localhost:5000
            result.get();
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // read into buffer
            Future<Integer> readId = market.read(buffer);
            readId.get();
            String assignedID = new String(buffer.array()).trim();
            System.out.println("Assigned ID: "
                + assignedID);
            id = Integer.parseInt(assignedID);
            buffer.clear();
            // connected - forever listen for new brokers
            while(true){
                buffer = ByteBuffer.allocate(1024);

                // read into buffer
                Future<Integer> readval = market.read(buffer);
                readval.get();
                String routerMessage = new String(buffer.array()).trim();
                System.out.println("Received from broker: "
                    + routerMessage);

                // flip and write into buffer            
                buffer.flip();
                items.forEach((key, value) -> System.out.println(key + ":" + value));
                String str = returnMessage(routerMessage);
                Future<Integer> writeval = market.write(
                    ByteBuffer.wrap(str.getBytes()));
                System.out.println("Writing to server: "+str);
                items.forEach((key, value) -> System.out.println(key + ":" + value));
                writeval.get();

                // clear and close
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String returnMessage(String routerMessage){
        String result;
        String presum = routerMessage.substring(0, routerMessage.lastIndexOf('|'));
        String routerchecksummessage = routerMessage.substring(routerMessage.lastIndexOf('|') + 1);
        Integer routerchecksum = Integer.parseInt(routerchecksummessage.substring(routerchecksummessage.indexOf('=') + 1));
        Integer checksum = Utilities.calculateChecksum(presum);
        
        String[] parts = presum.split("\\|");

        Map<String, String> keyvalue = new HashMap<>();
        for (String part : parts) {
            // System.out.println(part);
            keyvalue.put(part.substring(0, part.indexOf("=")), part.substring(part.indexOf("=") + 1));
        }
        String item = keyvalue.get("1");
        String quantity = keyvalue.get("2");
        if (routerchecksum == checksum && // checksum check
            item != null && quantity != null && // items are present
            items.get(item) != null && // item exists in db
            items.get(item) >= Integer.parseInt(quantity)){ // has quanitity
            result = String.format("0=%s|1=%s|2=%s|3=%s|4=%s|6=%s", id, item, quantity, keyvalue.get("0"), keyvalue.get("4"), "EXECUTED");
            items.put(item, items.get(item) - Integer.parseInt(quantity));
        } else {
            result = String.format("0=%s|1=%s|2=%s|3=%s|4=%s|6=%s", id, item, quantity, keyvalue.get("0"), keyvalue.get("4"), "REJECTED");
        }

        return String.format("%s|10=%d", result, Utilities.calculateChecksum(result));
    }
}
