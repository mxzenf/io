package org.yx.serialize;

/**
 * Created by 杨欣 on 2018/6/10.
 */
public interface SerializeObject {
    byte[] serialize(Object o);
    Object deserialize(byte[] bytes);
}
