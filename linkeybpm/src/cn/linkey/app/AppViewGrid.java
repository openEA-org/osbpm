package cn.linkey.app;

import java.util.HashMap;
import java.util.Map;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

/**
 * 本类负责生成view grid视图
 * 
 * @author Administrator 本类为单例类
 */
public class AppViewGrid implements AppElement {

    @Override
    public void run(String wf_num) throws Exception {
    	getElementHtml(wf_num); // 输出视图的html代码
    }

    /**
     * 打开视图运行并输出view 的 html代码
     */
	public String getElementHtml(String wf_num) throws Exception {
		
		//获取设计元素ID
        String beanId = getBeanId(wf_num);

        AppElement insAppElement = (AppElement) BeanCtx.getBean(beanId);
        insAppElement.run(wf_num);////根据UI类型运行设计解析引擎
        
		return "";
	}

	/**
     * 生成插入表单中的视图的HTML代码
     * 
     * @return
     */
	public String getElementBody(String wf_num, boolean readOnly) throws Exception {
		
		//获取设计元素ID
        String beanId = getBeanId(wf_num);

        AppElement insAppElement = (AppElement) BeanCtx.getBean(beanId);
        insAppElement.getElementBody(wf_num,readOnly);////根据UI类型运行设计解析引擎
        
		return "";
	}

	/**
	 * 
	* @Description: 获取IOC配置的id
	*
	* @param:设计元素类型
	* @return：设计元素IOC配置的id
	* @author: Alibao
	* @date: 2018年1月16日 上午10:32:46
	 */
	private String getBeanId(String wf_num) {
		//System.out.println("wf_num:" + wf_num);
		Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", wf_num, true);
        if (gridDoc.isNull()) {
            return "Error:The view does not exist!";
        }
        
        //UI类型映射
//        Map<String,String> uiTypesMap = new HashMap<>();
//        uiTypesMap.put("1", "layui_view");
//        uiTypesMap.put("3", "easyui_view");
//        
//        String uiType = "3";
//        if(Tools.isNotBlank(gridDoc.g("UIType"))){
//        	uiType = gridDoc.g("UIType");
//        }
//        String beanId = uiTypesMap.get(uiType);
        
//        //20180115  添加对UI类型的判断  20180207UI类型的GridIOC从数据库查
        String uiType = "3";
        if(Tools.isNotBlank(gridDoc.g("UIType"))){
        	uiType = gridDoc.g("UIType");
        }

        String sql = "select GridIOC from BPM_UIList where UIType='"+uiType+"'";
        String beanId = Rdb.getValueBySql(sql);
        
		return beanId;
	}

}
