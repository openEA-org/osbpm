package cn.linkey.form;

import java.util.HashMap;

public interface ApprovalForm {

    /**
     * 获得流程处理单
     * 
     * @param nodeParams
     * @return
     * @throws Exception
     */
    public abstract String getEngineApprovalForm(HashMap<String, Object> nodeParams) throws Exception;

    /**
     * 获得服务输出的流程处理单
     * 
     * @param nodeParams
     * @return
     * @throws Exception
     */
    public abstract String getWsApprovalForm(HashMap<String, Object> nodeParams) throws Exception;

}