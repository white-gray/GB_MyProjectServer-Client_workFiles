package ru.whitegray;

import org.apache.log4j.Logger;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;


/**
 *  получение файла из канала
 */
public class SendFileTo {
    private static final Logger log = Logger.getLogger(SendFileTo.class);
      SendFileTo(String fullFileName) throws IOException {
        ServerSocket serverSocket = null;
        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;

          System.out.println("\n\tin SendFileTo. fullFileName = "+fullFileName);
        try {
            serverSocket = new ServerSocket(3333);
        } catch (IOException ex) {
            log.info("Can't setup server on this port number. ");
        }
log.info("setup server on this port number. ");

        try {
            assert serverSocket != null;
            socket = serverSocket.accept();
        } catch (IOException ex) {
            log.info("Can't accept client connection. ");
        }
log.info("accept client connection. socket = " +socket);

        try {
            assert socket != null;
            in = socket.getInputStream();
        } catch (IOException ex) {
            log.info("Can't get socket input stream. ");
        }
log.info("get socket input stream. in = "+ in);

        try {
            out = new FileOutputStream(fullFileName);
        } catch (FileNotFoundException ex) {
            log.info("File not found. ");
        }
log.info("File found. out = "+out);

        byte[] bytes = new byte[16*1024];

        int count;
        while (true) {
            assert in != null;
            if (!((count = in.read(bytes)) > 0)) break;
            assert out != null;
            out.write(bytes, 0, count);
System.out.println("bytes = " + Arrays.toString(bytes));
System.out.println("count = "+count);
        }

          assert out != null;
          out.close();
        in.close();
        socket.close();
        serverSocket.close();
System.out.println("\tout, in, socket, serverSocket closed");
      }
}
