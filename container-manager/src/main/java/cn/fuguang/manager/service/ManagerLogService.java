package cn.fuguang.manager.service;

public interface ManagerLogService {

    void record(String module, String action, String targetId, String operator, String content);
}
