package cn.linkey.app;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

/**
 * 
* Copyright © 2018 A Little Bao. All rights reserved.
* 
* @ClassName: AppPage.java
* @Description:  * 本类主要跳转到 打开应用页面并输出html
 * 
* @version: v1.0.0
* @author: alibao
* @date: 2018年3月9日 下午3:43:03 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年3月9日     alibao           v1.0.0               修改原因
 */
public class AppPage implements AppElement {
    @Override
    public void run(String pageNum) throws Exception {
        getElementHtml(pageNum); //输出页面的html代码
    }

    /**
     * 获得页面的body内容，不输出js header
     */
    public String getElementBody(String pageNum, boolean readOnly) throws Exception {
    	//获取设计元素ID
        String beanId = getBeanId(pageNum);

        AppElement insAppElement = (AppElement) BeanCtx.getBean(beanId);
        
        return insAppElement.getElementBody(pageNum,readOnly);//根据UI类型运行设计解析引擎 //20181023 alibao
    }

    /**
     * 获得页面的html代码
     */
    public String getElementHtml(String pageNum) throws Exception {
    	//获取设计元素ID
        String beanId = getBeanId(pageNum);
        
        //System.out.println(beanId);
        AppElement insAppElement = (AppElement) BeanCtx.getBean(beanId);
        insAppElement.run(pageNum);////根据UI类型运行设计解析引擎
        return "";
    }
    
    /**
     * 
    * @Description: 获取BeanId
    *
    * @param:页面编号
    * @return：beanId
    * @author: alibao
    * @date: 2018年3月9日 下午3:55:07
     */
    private String getBeanId(String pageNum) {
		Document pageDoc = AppUtil.getDocByid("BPM_PageList", "PageNum", pageNum, true);
        if (pageDoc.isNull()) {
            return "Error:The view does not exist!";
        }
        
        String uiType = "3";
        if(Tools.isNotBlank(pageDoc.g("UIType"))){
        	uiType = pageDoc.g("UIType");
        }

        String sql = "select PageIOC from BPM_UIList where UIType='"+uiType+"'";
        String beanId = Rdb.getValueBySql(sql);
		return beanId;
	}
}
