package cn.linkey.servlet;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.linkey.util.Tools;

import javax.servlet.ServletResponseWrapper;

public class XSSFilter implements Filter{
	
	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest request1, ServletResponse response1,
			FilterChain filterChain1) throws IOException, ServletException {
		// TODO Auto-generated method stub	
		filterChain1.doFilter(new MyServletRequest((HttpServletRequest)request1), response1);
		//filterChain1.doFilter(request1, response1);
	}

	public void init(FilterConfig filterConfig1) throws ServletException {
		// TODO Auto-generated method stub
	}
	
}
