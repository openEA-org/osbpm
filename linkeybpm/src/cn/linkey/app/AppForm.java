package cn.linkey.app;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.HtmlParser;
import cn.linkey.form.ModForm;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.Tools;

/**
 * 本类主要负打开应用表单并输出html
 * 
 * @author Administrator 本类为单实例类
 */
public class AppForm implements AppElement {

    @Override
    public void run(String wf_num) throws Exception {
        //String method = (String) BeanCtx.getRequest().getMethod();
        String beanId = getBeanId(wf_num);
        AppElement insAppElement = (AppElement) BeanCtx.getBean(beanId);
        insAppElement.run(wf_num);//根据UI类型运行设计解析引擎
    }
    

    /**
     * 
   	* @Description: 获取IOC配置的id
   	* @param:设计元素类型
   	* @return：设计元素IOC配置的id
   	* @author: Alibao
    * @date: 2018年1月30日 下午3:32:02
     */
	private String getBeanId(String wf_num) {
   		Document gridDoc = AppUtil.getDocByid("bpm_formlist", "FormNumber", wf_num, true);
           if (gridDoc.isNull()) {
               return "Error:The view does not exist!";
           }
           //UI类型映射
//           Map<String,String> uiTypesMap = new HashMap<>();
//           uiTypesMap.put("1", "layui_form");
//           uiTypesMap.put("3", "easyui_form");
//           String uiType = "3";
//           if(Tools.isNotBlank(gridDoc.g("UIType"))){
//           	uiType = gridDoc.g("UIType");
//           }
//           String beanId = uiTypesMap.get(uiType);
           
           //20180130  添加对UI类型的判断
           String uiType = gridDoc.g("UIType");
           
           String sql = "select FormIOC from BPM_UIList where UIType='"+uiType+"'";
           String beanId = Rdb.getValueBySql(sql);

           //System.out.println("beanID:"+beanId);
   		return beanId;
   	}


	@Override
	public String getElementBody(String wf_num, boolean readOnly) throws Exception {
		return null;
	}


	@Override
	public String getElementHtml(String wf_num) throws Exception {
		return null;
	}

}
