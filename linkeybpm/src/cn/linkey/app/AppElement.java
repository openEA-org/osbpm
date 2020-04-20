package cn.linkey.app;

public interface AppElement {
    public void run(String wf_num) throws Exception;// 设计运行调用接口

    public String getElementBody(String wf_num, boolean readOnly) throws Exception;//获得设计的Body内容方法

    public String getElementHtml(String wf_num) throws Exception;//获得设计生成的html代码
}
