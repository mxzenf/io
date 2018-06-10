package org.yx.bean;

import java.io.Serializable;

/**
 * Created by 杨欣 on 2018/6/7.
 */
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = 1707147347457999089L;
    private String id;
    private Object result;
    private Exception exception;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}

