package org.yx.serialize;

import java.io.*;
import java.lang.reflect.Field;

/**
 * Created by 杨欣 on 2018/6/10.
 */
public class DefaultSerializeObject implements SerializeObject {
    @Override
    public <T> byte[] serialize(T o) {
        if (!(o instanceof Serializable)){
            throw new RuntimeException("必须实现Serializable接口");
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
//            oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.flush();
            System.out.println("写入字节数:"+bos.size());
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
    public <T> T deserialize(byte[] bytes, Class<T> cls) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        System.out.print("读取字节数"+bis.available());
        try {
            ois = new ObjectInputStream(bis);
            return (T)ois.readObject();
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
