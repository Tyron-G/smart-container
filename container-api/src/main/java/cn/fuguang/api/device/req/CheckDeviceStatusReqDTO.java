package cn.fuguang.api.device.req;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckDeviceStatusReqDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 仓门id
     */
    private String gateId;

    /**
     * 设备id
     */
    private String deviceId;
}
