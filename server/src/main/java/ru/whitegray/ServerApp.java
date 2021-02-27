package ru.whitegray;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.log4j.Logger;

public class ServerApp {
    private static final Logger log = Logger.getLogger(ServerApp.class);
   private static final int PORT = 9999;

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);  // создаётся пул-менеджер потоков для подключение клиентов (один поток)
        EventLoopGroup workerGroup = new NioEventLoopGroup();         // создаётся пул-менеджер потоков для остальной работы (не ограничено)
        try {
            ServerBootstrap b = new ServerBootstrap();          // настройки программного сервера
            b.group(bossGroup, workerGroup)                     //пулы-менеджеры с которыми будет работать сервер
                    .channel(NioServerSocketChannel.class)      // какие каналы использовать
                    .childHandler(new ChannelInitializer<SocketChannel>() {     // настройка процесса общения с клиентом. В <SocketChannel> есть информация о клиенте (IP, порт, ...)
                        @Override
                        public void initChannel(SocketChannel socketChannel)  {
                            socketChannel.pipeline()
                                    .addLast(new StringDecoder(), new StringEncoder(), new MainHandler()); // при каждом коннекте клиента запустить класс MainHandler; а также классы, выполняющие то, что отправленные в поток данные будут кодироваться в байты; а пришедшие в потоке данные будут декодироваться в символы. И запуск обработчика MainHandler()
                        }
                    });
            ChannelFuture future = b.bind(PORT).sync();     // запуск сервера
            log.info("SERVER: Запущен..."); 
           future.channel().closeFuture().sync();          // сервер работает и ждёт, - до тех пор, пока его остановят
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();             // закрытие всех потоков для завершения программы
            workerGroup.shutdownGracefully();
        }
    }
}

