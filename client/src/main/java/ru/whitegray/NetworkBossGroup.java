package ru.whitegray;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.log4j.Logger;

public class NetworkBossGroup {
    private static final Logger log = Logger.getLogger(NetworkBossGroup.class);
    private SocketChannel channel;                          // канал

    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    public NetworkBossGroup(Callback onMessageReceivedCallback) {
        Thread t = new Thread(() -> {
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();        // создаётся пул-менеджер потоков для подключение клиента
            try {
                Bootstrap b = new Bootstrap();                              // настройки программного сервера
                b.group(workerGroup)                                        // пулы-менеджеры с которыми будет работать сервер
                        .channel(NioSocketChannel.class)                    // какие каналы использовать
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {   // настройка процесса общения с клиентом. В <SocketChannel> есть информация о клиенте (IP, порт, ...)
                                channel = socketChannel;
                                socketChannel.pipeline()
                                        .addLast(new StringDecoder(), new StringEncoder(), new ClientHandler(onMessageReceivedCallback));   // здесь прописано, что отправленные в поток данные будут кодироваться в байты; а пришедшие в потоке данные будут декодироваться в символы; и запустится ClientHandler()
                            }
                        });
                log.info("channel for send messages between Klients = " + channel);
                ChannelFuture future = b.connect(HOST, PORT).sync();        // запустить клиента
                future.channel().closeFuture().sync();                      // клиент работает и ждёт, - до тех пор, пока его остановят
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();                           // остановить пул-менеджер потоков клиента
            }
        });
        t.start();
    }

    public void sendMessage(String str) {                                   // отправление сообщения на Сервер
System.out.println("channel = " + channel);
System.out.println("go to channel.writeAndFlush\n\n");
        channel.writeAndFlush(str);
    }


    public void close() {
        channel.close();
    }

}
