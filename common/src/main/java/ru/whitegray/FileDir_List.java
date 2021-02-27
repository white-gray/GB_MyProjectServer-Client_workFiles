package ru.whitegray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FileDir_List {



/**
 *  метод отоюражения списка файлов и директорий dir - основной
 */
    public static List dir(File path) {       // Отображение все файлов и папок выбранной, и вложенных  дирректории
System.out.println("I in FileDir_List.ls_all");
System.out.println("path = " + path);
       try {
            return Files.walk(Paths.get(path.toString()))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        List result = new LinkedList();
System.out.println("result = "+result);
        return result;
    }



/**
 *  метод отоюражения списка файлов и директорий ls - основной
 */
    public static List<String> ls(File path) {           // Отображение все файлов и папок выбранной  дирректории
//        File dir = new File(clientPath);
System.out.println("I inFileDir_List.ls");
System.out.println("path = " + path);
        List<String> result = new LinkedList<>();
        try {
            File[] files = path.listFiles();

            assert files != null;
            for (File file : files) {
                if (file.isDirectory()) {
                    result.add("\tdirectory: " + shortPath(file.getCanonicalPath()));
                } else {
                    long lastModified = file.lastModified();                                            // | Форматирование даты изменения файла с миллисекунд на адекватную
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");          // |

                    result.add("\tfile: " + shortPath(file.getCanonicalPath()) + ", размер файла: "
                            + file.length() + " byte, дата изменения: " + sdf.format(new Date(lastModified)));
                }
            }
System.out.println("result = "+result);
//            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
System.out.println("result = "+result);
System.out.println("path = "+ path);
        return result;
    }


/**
 *   преобразование полного пути к пути от папки программы
 */
    static String shortPath(String path){
System.out.println("\n\tshortPath");
        int start = path.indexOf("Клиент_");
System.out.println("start  = " + start);
        String delete = path.substring(0, start);
System.out.println("delete = " + delete);
        path = path.replace(delete, "");
System.out.println("now path = " + path);
        return path;
    }
}
