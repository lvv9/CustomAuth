package me.liuweiqiang.customauth.controller.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "请求结果响应体")
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 8992436576262574064L;

    @ApiModelProperty(value = "响应状态回执码")
    private Integer status;

    @ApiModelProperty(value = "响应回执消息")
    private String msg;

    @ApiModelProperty(value = "数据体")
    private T data;

    @ApiModelProperty(value = "响应时间戳")
    private final long timestamps = System.currentTimeMillis();

    public ResponseResult() {
    }

    public ResponseResult(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public long getTimestamps() {
        return timestamps;
    }

    public synchronized static <T> ResponseResult<T> e(ResponseCode statusEnum) {
        return e(statusEnum,null);
    }

    public synchronized static <T> ResponseResult<T> e(ResponseCode statusEnum, T data) {
        ResponseResult<T> res = new ResponseResult<>();
        res.setStatus(statusEnum.code);
        res.setMsg(statusEnum.msg);
        res.setData(data);
        return res;
    }



}
