package cn.linkey.rule;

import java.util.HashMap;

public interface LinkeyRule {
    public String run(HashMap<String, Object> params) throws Exception;// 执行规则的接口
}
