package org.yx.server;

import org.yx.bean.RpcRequest;
import org.yx.bean.RpcResponse;

/**
 * Created by 杨欣 on 2018/6/11.
 */
public interface RpcHandler {
    public void handle(RpcRequest request, RpcResponse response);
}
