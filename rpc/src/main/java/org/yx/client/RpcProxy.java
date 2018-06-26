package org.yx.client;

import org.yx.bean.RpcRequest;
import org.yx.service.ServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Created by 杨欣 on 2018/6/26.
 * 生成对应的服务接口实现代理
 */
public class RpcProxy {
    private ServiceDiscovery serviceDiscovery;
    public <T> T create(Class<?> interfaceClass){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String interfaceName = interfaceClass.getName();
                Class[] parameterTypes = method.getParameterTypes();
                String address = serviceDiscovery.discovery(interfaceName);
                if (null == address) {
                    return null;
                }
                String ip = address.split(":")[0];
                int port = Integer.valueOf(address.split(":")[1]);
                RpcRequest request = new RpcRequest();
                request.setArgs(args);
                request.setId(UUID.randomUUID().toString());
                request.setServiceVersion("1");
                request.setParameterTypes(parameterTypes);
                request.setInterfaceName(interfaceName);
                request.setMethodName(method.getName());
                return new RpcClient(ip,port).send(request).getResult();
            }
        });
    }
}
