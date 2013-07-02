
package com.zhji.location.app.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DatabaseUtils {

    /**
     * This method convert object to bytes
     * 
     * @param object
     * @return bytes
     * @throws IOException
     */
    public static byte[] convertObjectToByte(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        baos.close();
        oos.close();
        return baos.toByteArray();
    }

    /**
     * This method convert bytes to objects
     * 
     * @param data
     * @return object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object convertByteToObject(byte[] data) throws IOException,
            ClassNotFoundException {
        if (data == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object object = ois.readObject();
        bais.close();
        ois.close();
        return object;
    }
}
