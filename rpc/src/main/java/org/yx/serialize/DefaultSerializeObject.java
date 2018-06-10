package org.yx.serialize;

import java.io.*;
import java.lang.reflect.Field;

/**
 * Created by 杨欣 on 2018/6/10.
 */
public class DefaultSerializeObject implements SerializeObject {
    @Override
    public byte[] serialize(Object o) {
        if (!(o instanceof Serializable)){
            throw new RuntimeException("必须实现Serializable接口");
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("序列化失败"+e.getMessage());
        } finally{
            try {
                if (oos != null){
                    oos.close();
                }
                bos.close();
            } catch (Exception e){

            }

        }

    }

    @Override
    public Object deserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("反序列化失败"+e.getMessage());
        } finally {
            try {
                if (null != ois){
                    ois.close();
                }
                bis.close();
            } catch (Exception e){

            }
        }

    }
}
