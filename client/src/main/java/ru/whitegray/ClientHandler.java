package ru.whitegray;

import static ru.whitegray.GeneralCommands.*;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = Logger.getLogger(ClientHandler.class);

    private final Callback onMessageReceivedCallback;
    public static String clientName;                                     // имя клиента, которое определил Сервер
    private static File clientPath;                                      // имя папки текущего клиента
    private static File path;                                            // имя текущей папки текущего клиента

    public static File getClientPath() {
        return clientPath;
    }
    public static File getPath() {
        return path;
    }

    public static void setPath(File path) {
        ClientHandler.path = path;
    }



    public ClientHandler(Callback onMessageReceivedCallback) {
        this.onMessageReceivedCallback = onMessageReceivedCallback;
System.out.println("onMessageReceivedCallback = " + onMessageReceivedCallback);
    }


/**
 *  что приходит по каналу. Рассматривается, обрабатывается, и передаётся в окно Клиента
 */
    @Override
    public void channelRead (ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
System.out.println("\n\twe are in ClientHandler.channelRead !!!");
System.out.println("type msg " + msg.getClass().getName());
        if (onMessageReceivedCallback != null) {
            onMessageReceivedCallback.callback(msg);
System.out.println("s = " + msg +" + " + msg.toString());
// определение какое имя сервер дал клиенту при подключении Клиента
        if (msg.toString().startsWith("Отправлено всем: Подключился новый клиент: ")){
            clientName = msg.toString().replace("Отправлено всем: Подключился новый клиент: ", "");
            clientName = clientName.replace("\n", "");
System.out.println("with enter SendFileFrom clientName = " + clientName);
            clientPath = makeClientFolder(".\\project\\client\\clientFiles\\"+clientName);    // определение имени папки нового Клиентf
            path = clientPath;
        }
// если пришло сообщение, что Сервер хочет передать файл с именем этого файла
        if (msg.toString().startsWith("weSendFile")) {
System.out.println("\n\tit's weSendFile!");     // если первое слово в сообщении - weSendFile , то переходим на приём файла
            receiveFile(msg.toString());
        }
        }
    }



/**
 * Загружает файла на Клиента
 */
    private void receiveFile(String mes) throws IOException {
System.out.println("\n\tWe at receiveFile");
        String fileName = mes;                    // определение имени файла
        fileName = fileName.replaceFirst("weSendFile", "");
        fileName = fileName.strip();
System.out.println("we have fileName = " + fileName);
System.out.println("we have path = " + path);
        new SendFileTo(path+"\\"+fileName);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ChannelHandlerContext, Throwable cause) {
        log.error("Channel " + ChannelHandlerContext+ " is closing with "+cause);
        ChannelHandlerContext.close();
        cause.printStackTrace();
    }
}

