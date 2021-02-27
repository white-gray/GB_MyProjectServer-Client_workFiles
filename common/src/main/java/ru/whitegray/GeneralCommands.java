package ru.whitegray;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

import static io.netty.util.AttributeKey.exists;

public class GeneralCommands {
    private static final Logger log = Logger.getLogger(GeneralCommands.class);


/**
 *  метод отображения списка файлов и директорий ls - начальный
 */
    protected static List<String> ls(File path, String who){
System.out.println("\n\tI in ClientCommands.ls");
System.out.println("clientPath = " + path);
        List<String> pathList = FileDir_List.ls(path);
        pathList.add(0, who + " в папке ..\\"+shortPath(path.toString())+ " :");
        return pathList;
    }


/**
 *  метод отображения списка файлов и директорий dir - начальный
 */
    protected static List<Object> dir(File path, String who){
System.out.println("\n\tI in ClientCommands.ls");
System.out.println("clientPath = " + path);
        List  pathListLomg = FileDir_List.dir(path);
        List<Object> pathList = new LinkedList<>();
        for (Object pos :pathListLomg){
            pos = "\t"+shortPath(pos.toString());
            pathList.add(pos);
        }
        pathList.add(0, who + " в папке ..\\"+shortPath(path.toString())+ " и вложенных есть файлы:");
        return pathList;
    }



/**
 *      выводит на экран содержимое файла
 */
    protected static StringBuilder fileText(String fileName) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
System.out.println(exists(fileName));
        exists(fileName);
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8));
        try {
            String s;
            while ((s = in.readLine()) != null) {
                sb.append(s);
                sb.append("\n");
            }
        }
        catch(IOException e){
                throw new RuntimeException(e);
            }
        finally{
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb;
            }
    }



/**
 *   укорачивает путь к файлу от реального до пути в области данной проги
 */
            protected static String shortPath(String path){
System.out.println("\n\tshortPath");
        int start = path.indexOf("Клиент_");
System.out.println("start  = " + start);
        String delete = path.substring(0, start);
System.out.println("delete = " + delete);
        path = path.replace(delete, "");
System.out.println("now path = " + path);
        return path;
    }



/**
 *  создание папки для клиенты, если Клиент новый, и папки у него ещё нет
 */
protected static File makeClientFolder(String path) {
    File clientPath = new File(path);
    if (!clientPath.exists()){
        clientPath.mkdirs();
        log.info("dir maked for you Klient");
    }
    return clientPath;
}
}
