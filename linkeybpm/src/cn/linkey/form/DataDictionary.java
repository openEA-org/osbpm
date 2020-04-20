package cn.linkey.form;

import cn.linkey.dao.Rdb;

/**
 * 数据字典操作类
 * 
 * @author Administrator
 *
 */
public class DataDictionary {

    /**
     * 根据数据字典的id获得数据字典的如果值有多个会用逗号分隔
     * 
     * @param dataid
     * @return 返回数据字典的配置值
     */
    public static String getValueByDataid(String dataid) {
        String sql = "select FieldValue from BPM_DataDicValueList where Dataid='" + dataid + "'";
        String fdValue = Rdb.getValueBySql(sql);
        return fdValue;
    }

}
