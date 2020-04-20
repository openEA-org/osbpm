package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ProcessEngine;

/**
 * 强制归档文件,只需要运行archiveDocument()函数即可
 * 
 * 参数说明:无需传入参数
 * 
 * @author Administrator
 */
public class R_S003_B057 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();

        BeanCtx.getLinkeywf().setEndNodeid("GoToArchived"); //设置结束环节的节点id，这样就会linkeywf.run()方法会自动运行归档程序进行归档

        linkeywf.setRunStatus(true);//表示运行成功
        return "流程已成功归档!";
    }

}
