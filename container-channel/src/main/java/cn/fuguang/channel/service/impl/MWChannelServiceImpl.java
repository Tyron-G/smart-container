package cn.fuguang.channel.service.impl;


import cn.fuguang.channel.service.MWChannelService;
import org.springframework.stereotype.Service;

@Service
public class MWChannelServiceImpl implements MWChannelService {

    @Override
    public cn.fuguang.channel.pojo.dto.MwSendSmsResDTO sendSingle() {
        return new cn.fuguang.channel.pojo.dto.MwSendSmsResDTO();
    }
}
