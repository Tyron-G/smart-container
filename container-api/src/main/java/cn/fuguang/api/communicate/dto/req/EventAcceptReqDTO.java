package cn.fuguang.api.communicate.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class EventAcceptReqDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 事件
     */
    private String event;

    /**
     * 事件对应参数
     */
    private Map<String, Object> params;
}
