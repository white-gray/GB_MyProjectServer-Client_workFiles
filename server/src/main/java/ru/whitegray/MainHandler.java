package ru.whitegray;

import static ru.whitegray.GeneralCommands.*;

import io.netty.channel.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class MainHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = Logger.getLogger(MainHandler.class);
    private static final List<Channel> channels = new ArrayList<>();    // список подключившихся клиентов
    private static int newClientIndex = 1;                              // количество подключившихся клиентов
    private String clientName;                                          // имя текущего клиента
    private String clientChannel;                                       // идентифмкатор (id) текущего клиента
    private File clientPath;                                            // имя папки текущего клиента
    private File path;                                                  // имя папки где находится текущий клиент


/**
 *  активация канала с Клиентом
 */
    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {     // действия при подключении клиента
        log.info("Клиент подключается: " + channelHandlerContext);
        channels.add(channelHandlerContext.channel());                            // добавление подключившегося клиента в список подключившихся клиентов
        clientName = "Клиент_" + newClientIndex;
        newClientIndex++;
        clientChannel = channelHandlerContext.channel().id().asShortText();
        log.info("ID канала: "+ channelHandlerContext.channel().id() + "   " + clientChannel);
        newClient();
        broadcastMessage("SERVER", "Подключился новый клиент: " + clientName);  // сообщение от сервера, что клиент подключился
    }


    /**
     *      новый Клиент
     */
    public void newClient() {
        clientPath = makeClientFolder(".\\project\\server\\serverFiles\\"+clientName);
        path = clientPath;
    }


/**
 *  действия, когда клиент прислал сообщение, - рассматривается и выполняется присланная команда
 */
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object s) throws Exception {
        log.info("Получено сообщение: " + s.toString());
            if (s.toString().equalsIgnoreCase("_ls")) {
                messageList(GeneralCommands.ls(path, "На сервере"));
            }
            else if (s.toString().equalsIgnoreCase("_dir")) {
                messageList(GeneralCommands.dir(path, "На сервере"));
            }
            else if (s.toString().equalsIgnoreCase("_cd ..")) {
                cdUp();
            }
            else if (s.toString().startsWith("_cd")) {
                cdDir(s.toString());
            }
             else if (s.toString().startsWith("_get")){
                get(s.toString());
            }
              else if (s.toString().startsWith("_rm")){
                 rm(s.toString());
            }
            else if (s.toString().startsWith("_chName")){
                chName(s.toString());
            }
              else  if (s.toString().startsWith("*")) {
                sendBroadcastMessage(s.toString());
            }
            else if (s.toString().equalsIgnoreCase("help") || s.toString().equalsIgnoreCase("?")) {
                help();
            }
            else if (s.toString().startsWith("weSendFile")) {    // если первео слово в сообщении - weSendFile , то отправляем чтение сообщения
System.out.println("\n\tit's weSendFile!");
                receiveFile(s.toString());
            }
    }



    /**
     * Загружает файл на сервер
     */
    protected void receiveFile(String mes) throws IOException {
System.out.println("\n\tWe at receiveFile");
         String fileName = mes;                    // определение имени файла
         fileName = fileName.replaceFirst("weSendFile", "");
         fileName = fileName.strip();
System.out.println("we have fileName = " + fileName);
System.out.println("we have path = " + path);
        new SendFileTo(path+"\\"+fileName);
    }



    /**
     * Отправляет сообщение всем Клиентам
     */
    private void sendBroadcastMessage(String s) {
        String s2 = s.substring(1);         //удаляем из сообщенния первый символ *
        broadcastMessage(clientName, s2);
    }


/**
 *    помощь
 */
    private void help() {                                   // метод , - переход на директорию вверх
System.out.println("\n\tIt's _help!!");
System.out.println("path = " + path);
System.out.println("clientPath = " + clientPath);
        try {
            log.info(fileText(".\\help.txt").toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            messageString(fileText(".\\project\\server\\src\\main\\resources\\help.txt").toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


/**
 *  переход на директорию вверх
 */
    private void cdUp() {                                   // метод , - переход на директорию вверх
System.out.println("\n\tIt's _cd .. !!!");
System.out.println("path = " + path);
System.out.println("clientPath = " + clientPath);
        if (path.equals(clientPath)) {                       // если мы находимся в крайней директории Клиент, запретит подниматься выше
System.out.println("Попал!!!");
            messageString("Выше не получится!");
            return;
    }
        path = path.getParentFile();
System.out.println("Now  path = " + path);
        messageList(GeneralCommands.ls(path, "На Сервере"));
    }


    /**
     *  переход в указанную директорию
     */
    private void cdDir(String s) {
System.out.println("\n\tI's cd !!!");
System.out.println("path = " + path);
System.out.println("s = " + s);
        String dir = s;                                     // создание пути из прописанного имени файла
        dir = dir.replaceFirst("_cd", "");
        dir = dir.strip();
System.out.println("Выбрана path = " + path +dir);
System.out.println("dir.startsWith()  = " + dir.startsWith("\\") );
        while (!dir.startsWith("\\")){
            dir=dir.replace(dir,"\\"+dir);
System.out.println("now dir = " + dir);
        }
System.out.println("Получили path = " + path+dir);
System.out.println("File(dir).isDirectory()) = " + new File(path+dir).isDirectory());
        if (new File(path.toString()+dir).isDirectory()){    // если указанное в надписи является дитекторией
System.out.println("We entered");
            path = new File(path.toString()+dir);          // переменной path присваивается имя новой директории
System.out.println("path = "+path);
            messageList(GeneralCommands.ls(path, "На Сервере"));   // dir новой директории
        }
        else{
            messageString(("Ошиблись с именем директории!"));
            return;
        }
System.out.println("Now  path = " + path);
    }


/**
 *      копирование файла в сервера на Клиента
 */
    private void get(String s) throws IOException {
System.out.println("\n\tI's _get !!!  path = " + path);
        String fileName = s;
        fileName = fileName.replaceFirst("_get", "");
        String fullFileName = path.toString()+"\\"+fileName.strip();
System.out.println("Выбран файл = " + fullFileName);
        if (!Files.exists(Path.of(fullFileName))){      // проверка есть ли этот файл
            messageString("Указанного файла " + fileName + " не найдено");
            return;
        }
System.out.println("Отправляем на Клиент предупреждение об отправке файла" + fileName);
        messageString("weSendFile "+fileName);                    // отправка сообщения на сервер
System.out.println("Файл с именем fileName = "+ fullFileName +" отправляется на SendFileFrom");
        new SendFileFrom(fullFileName);   // Отправка файла от Клиента на Сервер
        messageString("\nФайл " + fileName + " скопирован");
    }




/**
 *  удаленние файла
 */
    private void rm(String s) throws IOException {
System.out.println("\n\tI's rm !!!  path = " + path);
        String fileName = s;
        fileName = fileName.replaceFirst("_rm", "");
        String fullFileName = path.toString()+"\\"+fileName.strip();
System.out.println("Выбран файл = " + fullFileName);
        if (checkFileExists(fileName, fullFileName)) return;
System.out.println("Удаляем файл" + fileName);
        Files.delete(Path.of(fullFileName));
        messageString("\nФайл " + fileName + " удалён");
    }


/**
 *    проверка есть ли указанный файл
 */
    private boolean checkFileExists(String fileName, String fullFileName) {
        if (!Files.exists(Path.of(fullFileName))) {
            messageString("Указанного файла " + fileName + " не найдено");
            return true;
        }
        return false;
    }



/**
 * переименовать файл
 */
    private void chName(String s) {
System.out.println("\n\tI's chName !!!  path = " + path);
        String filesNames = s;                          // определение имени файла из прописанной команды
        filesNames = filesNames.replaceFirst("_chName", "").strip();
        String [] filesNamesGet = filesNames.split(" ");
        File fullFileNameOld = new File (path.toString()+"\\"+filesNamesGet[0].strip());
        File fullFileNameNew = new File (path.toString()+"\\"+filesNamesGet[1].strip());
        System.out.println("Для переименования выбран файл = " + fullFileNameOld);
        if(fullFileNameOld.renameTo(fullFileNameNew)) {
            messageString("\tфайл " + shortPath(fullFileNameOld.toString()) + " переименован в " + shortPath(fullFileNameNew.toString()));
            log.info ("\tфайл " + shortPath(fullFileNameOld.toString()) + " переименован в " + shortPath(fullFileNameNew.toString()));
        }
        else {
            messageString ("\tпереименование не удалось! Возможно неверно указано имя файла");
            log.info("\tпереименование не удалось!");
        }
    }


/**
 *  отправка сообщения в окно Клиента в формате String
 */
    public void messageString(String message) {           //  сообщение в виде String, отправляется текущему клиенту
        for (Channel c : channels) {
            if (c.id().asShortText().equals(clientChannel)) {
                    c.writeAndFlush(message+"\n");
    }}}


/**
 *  отправка сообщения в окно Клиента в формате List
 */
    public void messageList(List message) {           //  сообщение в виде List, отправляется текущему клиенту
        for (Channel c : channels) {
            if (c.id().asShortText().equals(clientChannel)) {
                for(Object string : message){
                    c.writeAndFlush(string+"\n");
                }
                c.writeAndFlush("\n");
                break;
            }
        }
    }


/**
 *  отправка сообщения в окна всем активныв Клиентам
 */
    public void broadcastMessage(String clientName, String message) {           //  сообщение отправляется всем
        String out = String.format("[%s]: %s\n", clientName, message);
        for (Channel c : channels) {
            if (c.id().asShortText().equals(clientChannel)) {
                String outMe = String.format("Отправлено всем: %s\n", message);
                c.writeAndFlush(outMe);
                continue;
            }
            c.writeAndFlush(out);
        }
    }


/**
 *  отключение Клиента
 */
    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) {   // выход или отключение клиента
        log.info("Клиент " + clientName + " вышел из сети");
        channels.remove(channelHandlerContext.channel());
        broadcastMessage("SERVER", "Клиент " + clientName + " вышел из сети");
        channelHandlerContext.close();
    }


/**
 *  если Клиент вышел, но не сам
 */
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {  // действия при исключениях (ошибках)
        log.info("Клиент " + clientName + " отвалился");
        channels.remove(channelHandlerContext.channel());
        broadcastMessage("SERVER", "Клиент " + clientName + " вышел из сети");
        channelHandlerContext.close();
    }
}
