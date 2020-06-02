package broker;

// import broker.Utilities;
import java.nio.channels.CompletionHandler;
import java.lang.Throwable;
import java.lang.Thread;

/*
    Package Class: Broker
        Handle socket creation and connection to "router" (socket channel)
        Constructor
            Socket establish and connection
        SendMessage: "BUY"
            Provide and manage the data stream
            Call Utilities for FIX Message
        ReceiveMessage: "EXECUTED" / "REJECTED"
            Provide feedback on returned message

*/

class Handler {

    static CompletionHandler<Void, String> sendHandler(){
        return new CompletionHandler<Void, String>() {
            @Override
            public void completed(Void result, String message){
                Utilities.println("The SEND handler completed.");
                Thread.currentThread().interrupt();
            }

            @Override
            public void failed(Throwable e, String message){
                Utilities.println("The SEND handler failed:");
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        };
    }

    static CompletionHandler<Integer, String> receiveHandler(){
        return new CompletionHandler<Integer, String>() {
            @Override
            public void completed(Integer result, String message){
                Utilities.println("We made it here, prepare shit for come back.");
            }

            @Override
            public void failed(Throwable e, String message){
                Utilities.println("There was a mad problem!!!");
            }
        };
    }
}