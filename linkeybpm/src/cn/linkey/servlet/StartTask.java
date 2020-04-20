package cn.linkey.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.quartz.ee.servlet.QuartzInitializerServlet;
import org.quartz.impl.StdSchedulerFactory;

import cn.linkey.factory.BeanCtx;

/**
 * 启动时加载的任务
 * 
 * @author Administrator
 *
 */
public class StartTask extends HttpServlet {
    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {
        try {
            BeanCtx.init("", null, null);
            StdSchedulerFactory sf = (StdSchedulerFactory) getServletConfig().getServletContext().getAttribute(QuartzInitializerServlet.QUARTZ_FACTORY_KEY);
            BeanCtx.getSchedulerEngine().run(sf);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            BeanCtx.close();
        }
    }
    //
    //	  public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    //	{
    //		    response.getWriter().println("QUARTZ Task Started...");
    //		  
    //			BeanCtx.init("", request, response);
    //			StdSchedulerFactory sf = (StdSchedulerFactory)getServletConfig().getServletContext().getAttribute(QuartzInitializerServlet.QUARTZ_FACTORY_KEY);
    //			BeanCtx.getSchedulerEngine().run(sf);
    //			BeanCtx.close();
    //			
    //	}

}
