package cn.linkey.sap;

import java.util.Properties;

import cn.linkey.factory.BeanCtx;

import com.sap.mw.jco.*;

/**
 * SAP系统链接池
 * 
 * <pre>
 * 本类为多实例类，需要为每一次操作创建一个实例对像，因为本类中要缓存client对像，
 * 此对像在使用完后要关闭，放回链接池中 如果是单实例则不能保持对client的引用
 *   示例： SAPConn sapconn=(SAPConn)BeanCtx.getBean("SAPConn"); //获得一个sap对像 JCO.Function
 *         function=sapconn.getRFCFunction("ZRFC_MM_SEARCH_LINFR"); ....进行输入输出的操作 
 *         sapconn.close();
 * </pre>
 * 
 * @author Administrator
 */
public class SAPConn {
    public static final String POOL_NAME = "BPMPOOL"; //链接池名称
    private JCO.Client client; //链接对像
    private JCO.Function function; //rfc函数对像

    public SAPConn() {
        JCO.Pool pool = JCO.getClientPoolManager().getPool(POOL_NAME);
        if (pool == null) {
            Properties logonProperties = new Properties();
            logonProperties.put("jco.client.ashost", BeanCtx.getSystemConfig("SAP.IP")); //ip地址
            logonProperties.put("jco.client.client", BeanCtx.getSystemConfig("SAP.Client")); //客户端口号
            logonProperties.put("jco.client.passwd", BeanCtx.getSystemConfig("SAP.Pwd")); //密码
            logonProperties.put("jco.client.sysnr", BeanCtx.getSystemConfig("SAP.Sysnr")); //系统号
            logonProperties.put("jco.client.user", BeanCtx.getSystemConfig("SAP.User")); //用户名
            JCO.addClientPool(POOL_NAME, Integer.parseInt(BeanCtx.getSystemConfig("SAP.MaxConn")), logonProperties);
        }
        this.client = JCO.getClient(POOL_NAME); //从链接池中拿一个链接
        this.client.connect(); //进行实际连接
        if (this.client != null && this.client.isAlive()) {
            BeanCtx.log("E", "SAP链接失败,请确认配置参数是否正确!"); //如果连接不为null并且处于活动状态
        }
    }

    /**
     * 执行rfc函数
     */
    public void execute() {
        this.client.execute(function);
    }

    /**
     * 获得输入或输出参数的二维表，适用于使用Table作为输入或输出参数的RFC
     * 
     * @param tableName 表名称
     * @return 返回JCO.Table表
     */
    public JCO.Table getTable(String tableName) {
        JCO.Table table = function.getTableParameterList().getTable(tableName);
        return table;
    }

    /**
     * 设置Rfc的传入参数,适用于直接设置输入参数的RFC
     * 
     * @param keyName 参数名称
     * @param keyValue 参数值
     */
    public void setInput(String keyName, String keyValue) {
        this.function.getImportParameterList().setValue(keyName, keyValue);
    }

    /**
     * 根据输出参数获得rfc的输出值,适用于直接输出参数的RFC
     * 
     * @param keyName
     * @return 返回Object对像，有可能是字符串或者是数字等格式的
     */
    public Object getOutputValue(String keyName) {
        return this.function.getExportParameterList().getValue(keyName);
    }

    /**
     * 第三步从函数存储对像中获得一个指定的 RFC函数
     * 
     * @return
     */
    public JCO.Function getRFCFunction(String rfcName) {
        JCO.Repository repository = getRepository();
        IFunctionTemplate ft = repository.getFunctionTemplate(rfcName.toUpperCase());
        if (ft == null) {
            BeanCtx.log("E", "RFC函数(" + rfcName + ")不存在!");
            close(); //关闭链接
        }
        this.function = ft.getFunction();
        return function;
    }

    /**
     * 第二步获得一个Repository函数存储对像 直接获取一个Repository对像
     * 
     * @return
     */
    public JCO.Repository getRepository() {
        JCO.Repository repository = new JCO.Repository("BPM", client);
        return repository;
    }

    /**
     * 关闭一个链接
     * 
     * @param client
     */
    public void close() {
        JCO.releaseClient(client);
    }

    public JCO.Client getClient() {
        return client;
    }

    public void setClient(JCO.Client client) {
        this.client = client;
    }

    public JCO.Function getFunction() {
        return function;
    }
}
