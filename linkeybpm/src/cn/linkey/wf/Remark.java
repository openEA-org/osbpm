package cn.linkey.wf;

public interface Remark {

    /**
     * 流程表单中获得流转记录，用来显示到流程表单中
     * 
     * @param docUnid 文档unid
     * @param remarkType 意见类型
     * @param remarkTable 流转记录所在数据库表BPM_InsRemarkList,BPM_ArchivedRemarkList,BPM_AllRemarkList,默认从视图中找
     * @param isReadFlag 0表示办理意见，1表示阅读意见
     * @return
     */
    public abstract String getRemarkList(String docUnid, String remarkType, String remarkTable, String isReadFlag);

    /**
     * 添加阅读记录
     * 
     * @return 返回1表示添加成功，负数表示失败
     */
    public abstract int AddReadRemark(String nodeid, String nodeName, String startTime);

    /**
     * 添加普通办理的流转记录
     * 
     * @param actionid 执行本次提交的动作actionid
     * @param remark 流转记录内容
     * @return
     */
    public abstract int AddRemark(String actionid, String remark);

    /**
     * 添加流转记录
     * 
     * @param actionid 执行本次提交的动作actionid
     * @param remark 流转记录内容
     * @param remarkLevel 日记级别0表示系统日记只有系统管理员才可看到，1表示用户级日记所有人都可以看到
     * @return
     */
    public abstract int AddRemark(String actionid, String remark, String remarkLevel);

}