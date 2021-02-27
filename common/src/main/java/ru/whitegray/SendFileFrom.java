package ru.whitegray;

import java.io.*;
import java.net.Socket;


/**
 *  отправка файла в канал
 */
public class SendFileFrom {
    SendFileFrom(String fullFileName) throws IOException {

        System.out.println("\n\tin SendFileFrom. fullFileName = "+ fullFileName);
        Socket socket = new Socket("127.0.0.1", 3333);

        File file = new File(fullFileName);
System.out.println("File = " + file);
//        long length = file.length();    // Get the size of the file
        byte[] bytes = new byte[16 * 1024];
System.out.println("bytes = " + bytes);
        InputStream in = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();
System.out.println("in = "+in +"     out = "+out);
        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
System.out.println("bytes = " + bytes);
System.out.println("count = "+count);
        }

        out.close();
        in.close();
        socket.close();
System.out.println("\tout, in, socket closed");
    }
}
