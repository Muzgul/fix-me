package router;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public abstract class Handler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousSocketChannel>{

    public Handler next;

    public abstract void handle(AsynchronousSocketChannel result, AsynchronousSocketChannel attachment);
}