package ru.whitegray;

import  static ru.whitegray.GeneralCommands.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

public class Controller implements Initializable {
    private static final Logger log = Logger.getLogger(Controller.class);
    private NetworkBossGroup networkBossGroup;
    private File path;




    @FXML
    TextField msgField;

    @FXML
    TextArea mainArea;




    @Override
    public void initialize(URL location, ResourceBundle resources) {   // инициализация канала подключения к серверу
        networkBossGroup = new NetworkBossGroup((args) -> {             // предачи сообщений
            mainArea.appendText((String)args[0]);
        });
        sendMessageAtMainArea_String ("\tСписок команд: ? (или help)");
System.out.println("mainArea = " +mainArea);
    }



/**
 *  вывод сообщения в формати String в окне Клиента
 */
    private void sendMessageAtMainArea_String(String message) {                 // выводит сообщение (String)  в окно Клиента
        mainArea.appendText(message+"\n");
    }



/**
 *  вывод сообщения в формати List в окне Клиента
 */
    private void sendMessageAtMainArea_List(List message) {                 // выводит сообщение (List) в окно Клиента
        for (Object mesString : message)  {
            mainArea.appendText(mesString+"\n");
        }
        mainArea.appendText("\n");
    }


/**
 *    проверка есть ли указанный файл
 */
    private boolean checkFileExists(String fileName, String fullFileName) {
        if (!Files.exists(Path.of(fullFileName))) {
            sendMessageAtMainArea_String("Указанного файла " + fileName + " не найдено");
            return true;
        }
        return false;
    }



/**
 *  рассмотр строкиКоманд, и исполнение написанного
 */
    public void sendMsgAction() throws IOException {
System.out.println("\n\tmsgField.toString() = " + msgField.toString());
        path = ClientHandler.getPath();
        if (msgField.getText().equalsIgnoreCase("ls")){
            ls();
        }
        else if (msgField.getText().equalsIgnoreCase("dir")){
            dir();
        }
        else if (msgField.getText().equalsIgnoreCase("cd ..")){
            cdUp();
        }
        else  if (msgField.getText().startsWith("cd")){
            cdDir();
        }
        if (msgField.getText().startsWith("put")){
            put();
        }
        else if (msgField.getText().startsWith("rm")){
            rm();
        }
        else if (msgField.getText().startsWith("chName")){
            chName();
        }
        else if (msgField.getText().startsWith("exit")) {               // если ввели exit, - закрыть Клиента
            exitAction();
        }
System.out.println("msgField.getText() = " + msgField.getText());
System.out.println("Go to NetworkBossGroup.sendMessage");
        networkBossGroup.sendMessage(msgField.getText());                    // отправка сообщения на сервер
        clearCommandArea();
    }



/**
 *    отобразить список файлов list
 */
    private void ls() {
System.out.println("\n\tIt's ls!!!  path = " + path);
        sendMessageAtMainArea_List(GeneralCommands.ls(path, "У Вас"));
        clearCommandArea();
    }


/**
 *   отобразить список файлов dir
 */
    private void dir() {
System.out.println("\n\tIt's dir!!!  path = " + path);
        sendMessageAtMainArea_List(GeneralCommands.dir(path, "У Вас"));
        clearCommandArea();
    }



/**
 *   перейти в даректорию выше
 */
    private void cdUp() {
System.out.println("\n\tIt's cd .. !!!  path = " + path);
System.out.println("ClientHandler.getClientPath() = " + ClientHandler.getClientPath());
        if (path.equals(ClientHandler.getClientPath())) {           // если мы находимся в крайней директории Клиент, запретит подниматься выше
System.out.println("Попал!!!");
            sendMessageAtMainArea_List(List.of("Выше не получится!"));
            return;
        }
        path = path.getParentFile();
        ClientHandler.setPath(path);
System.out.println("Now  path = " + path);
        sendMessageAtMainArea_List(GeneralCommands.ls(path, "У Вас"));
        clearCommandArea();
    }


/**
 *   перейти в указанную директорию
 */
protected void cdDir() {
System.out.println("\n\tI's cd !!!  path = " + path);
        String dir = msgField.getText();                          // создание пути из прописанного имени файла
        dir = dir.replaceFirst("cd", "");
        dir = dir.strip();
System.out.println("Выбрана path = " + path.toString()+dir);
System.out.println("dir.startsWith()  = " + dir.startsWith("\\") );
        while (!dir.startsWith("\\")){              // добаудение слэша, если не указали
            dir=dir.replace(dir,"\\"+dir);
System.out.println("now dir = " + dir);
        }
System.out.println("Получили path = " + path.toString()+dir);
System.out.println("File(dir).isDirectory()) = " + new File(path.toString()+dir).isDirectory());
        if (new File(path.toString()+dir).isDirectory()){                           // если указанное в надписи является дитекторией
System.out.println("We entered");
            path = new File(path.toString()+dir);          // переменной path присваивается имя новой директории
System.out.println("path = "+path);
            ClientHandler.setPath(path);                            // запись основной переменной path
            System.out.println("ClientHandler.setPath(path)= " + ClientHandler.getPath());
            sendMessageAtMainArea_List(GeneralCommands.ls(path, "У Вас"));   // dir новой директории
        }
        else{
            sendMessageAtMainArea_List(List.of("Ошиблись с именем директории!"));
            return;
        }
System.out.println("Now  path = " + path);
        clearCommandArea();
    }


/**
 *      скопировать файл от Клиента на Сервер
 */
    private void put() throws IOException {
System.out.println("\n\tI's put !!!  path = " + path);
        String fileName = msgField.getText();                          // определение имени файла из прописанной команды
        fileName = fileName.replaceFirst("put", "");
        String fullFileName = path.toString()+"\\"+fileName.strip();
System.out.println("Выбран файл = " + fullFileName);
        if (checkFileExists(fileName, fullFileName)) return;
        System.out.println("Отправляем на Сервер предупреждение об отправке файла" + fileName);
        networkBossGroup.sendMessage("weSendFile "+fileName);                    // отправка сообщения на сервер
        clearCommandArea();
System.out.println("Файл с именем fileName = "+ fullFileName +" отправляется на SendFileFrom");
        new SendFileFrom(fullFileName);   // Отправка файла от Клиента на Сервер
        sendMessageAtMainArea_String("\nФайл " + fileName + " скопирован");
    }



/**
 *  удаленние файла
 */
    private void rm() throws IOException {
System.out.println("\n\tI's rm !!!  path = " + path);
        String fileName = msgField.getText();                          // определение имени файла из прописанной команды
        fileName = fileName.replaceFirst("rm", "");
        fileName = fileName.strip();
        String fullFileName = path.toString()+"\\"+fileName.strip();
System.out.println("Выбран файл = " + fullFileName);
        if (checkFileExists(fileName, fullFileName)) return;
System.out.println("Удаляем файл" + fileName);
        Files.delete(Path.of(fullFileName));
        sendMessageAtMainArea_String("\nФайл " + fileName + " удалён");
        clearCommandArea();
    }


/**
 * переименовать файл
  */
    private void chName() {
System.out.println("\n\tI's chName !!!  path = " + path);
        String filesNames = msgField.getText();                          // определение имени файла из прописанной команды
        filesNames = filesNames.replaceFirst("chName", "").strip();
        String [] filesNamesGet = filesNames.split(" ");
        File fullFileNameOld = new File (path.toString()+"\\"+filesNamesGet[0].strip());
        File fullFileNameNew = new File (path.toString()+"\\"+filesNamesGet[1].strip());
System.out.println("Для переименования выбран файл = " + fullFileNameOld);
        if(fullFileNameOld.renameTo(fullFileNameNew)) {
            sendMessageAtMainArea_String("\tфайл " + shortPath(fullFileNameOld.toString()) + " переименован в " + shortPath(fullFileNameNew.toString()));
            log.info ("\tфайл " + shortPath(fullFileNameOld.toString()) + " переименован в " + shortPath(fullFileNameNew.toString()));
            clearCommandArea();
        }
        else {
            sendMessageAtMainArea_String ("\tпереименование не удалось! Возможно неверно указано имя файла");
            log.info("\tпереименование не удалось!");
        }
    }



/**
 *  очистка поля ввода команд
 */
    private void clearCommandArea() {
        msgField.clear();                       // очищает строку ввода окна Клиента
        msgField.requestFocus();                // переводит курсор на строку ввода окна Клиента
    }


 /**
  *  закрытие окно Клиента
  */
    public void exitAction() {
        networkBossGroup.close();
        Platform.exit();
    }
}
