package cn.linkey.app;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;

/**
 * 本类负责生成editor grid视图
 * 
 * @author Administrator 本类为单例类
 */
public class AppEditorGrid implements AppElement {

    @Override
    public void run(String wf_num) throws Exception {
    	   String beanId = getBeanId(wf_num);
           AppElement insAppElement = (AppElement) BeanCtx.getBean(beanId);
           insAppElement.run(wf_num);//根据UI类型运行设计解析引擎
    }

    
    private String getBeanId(String wf_num) {
   		Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", wf_num, true);
           if (gridDoc.isNull()) {
               return "Error:The view does not exist!";
           }
           //UI类型映射
//           Map<String,String> uiTypesMap = new HashMap<>();
//           uiTypesMap.put("1", "layui_form");
//           uiTypesMap.put("3", "easyui_form");
           
           //20180130  添加对UI类型的判断
           String uiType = gridDoc.g("UIType");
           
           String sql = "select EditGridIOC from BPM_UIList where UIType='"+uiType+"'";
           String beanId = Rdb.getValueBySql(sql);
           //String beanId = uiTypesMap.get(uiType);
   		return beanId;
   	}


	@Override
	public String getElementBody(String wf_num, boolean readOnly) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getElementHtml(String wf_num) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
 
}
