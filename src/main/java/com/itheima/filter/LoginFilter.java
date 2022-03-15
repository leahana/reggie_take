package com.itheima.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.io.PrintWriter;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;


/**
 * 检查用户是否完成登录 过滤所有信息
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginFilter implements Filter {

    //路径配置器 支持通配符
    //AntPathMatcher提供了丰富的API，主要以doMatch为主
    private static AntPathMatcher PATH_MATCH = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

//      long id = Thread.currentThread().getId();
//      log.info("doFilter" + "线程id为：{}", id);

        //1 获取本次请求的url
        String requestURI = req.getRequestURI();

        //记录日志
        log.info("拦截到请求:{}", requestURI);

        //定义不需要拦截的请求路径静态资源..登录路径..
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"};
/*
        for (String url : urls) {
            boolean match = PATH_MATCH.match(url, requestURI);
            if (match){
                filterChain.doFilter(servletRequest,servletResponse);
                return;
            }
        }
 */

        //2 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3 无需处理,则放行
        if (check) {
            log.info("此次请求{}无需处理", requestURI);
            //放行
            filterChain.doFilter(req, resp);
            return;
        }

        //4 判断登录状态,如果登录,则直接放行(用session判断
        if (req.getSession().getAttribute("employee") != null) {
            //记录日志
            log.info("用户已登录,用户id为{}", req.getSession().getAttribute("employee"));

            //在拦截器的放行方法doFilter方法中放行之前获取HttpSession中的登录用户信息set到此线程的局部对象ThreadLocal中

            Long empId = (Long) req.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(req, resp);
            return;
        }


        //添加了user登录状态的过滤器放行判断
        if (req.getSession().getAttribute("user") != null) {
            //记录日志
            log.info("用户已登录，用户id为：{}", req.getSession().getAttribute("user"));

            Long userId = (Long) req.getSession().getAttribute("user");

            BaseContext.setCurrentId(userId);

            //放行
            filterChain.doFilter(req, resp);
            return;
        }


        log.info("用户未登录");


        //5 如果未登录则返回登录结果

        resp.getWriter().write(JSON.toJSONString(R.error("MOTLOGIN")));
        return;


    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     *
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCH.match(url, requestURI);
            if (match) return true;
        }
        return false;
    }
}
