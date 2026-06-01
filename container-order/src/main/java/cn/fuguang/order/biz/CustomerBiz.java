package cn.fuguang.order.biz;

import cn.fuguang.order.pojo.vo.req.AliAuthReq;
import cn.fuguang.order.pojo.vo.req.WechatLoginReq;
import cn.fuguang.order.pojo.vo.res.AliAuthRes;
import cn.fuguang.order.pojo.vo.res.WechatLoginRes;

public interface CustomerBiz {
    AliAuthRes aliAuth(AliAuthReq req);

    WechatLoginRes wechatLogin(WechatLoginReq req);
}
