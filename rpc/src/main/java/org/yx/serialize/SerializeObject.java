package org.yx.serialize;

/**
 * Created by 杨欣 on 2018/6/10.
 * 按照其他rpc框架对象解码优化接口
 *java自带的serializable本地化持久对象没问题，
 *如果通过nio实现就是巨坑，参考netty的java原始
 *序列化实现，netty实现了自己的objectoutputstream
 *和objectinputstream，这部分google说明比较少。
 *大多数都认为使用io socket就行了。综合考虑后，
 *决定采取protobuff实现对象传输序列化
 */
public interface SerializeObject {
    <T> byte[] serialize(T o);
    <T> T deserialize(byte[] bytes, Class<T> cls);
}
