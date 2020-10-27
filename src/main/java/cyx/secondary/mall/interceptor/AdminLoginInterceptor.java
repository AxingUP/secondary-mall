package cyx.secondary.mall.interceptor;

import cyx.secondary.mall.common.Constants;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminLoginInterceptor implements HandlerInterceptor {

//    在请求的预处理过程中读取当前 session 中是否存在 loginUser 对象
//    如果不存在则返回 false 并跳转至登录页面，如果已经存在则返回 true，继续做后续处理流程
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object o)throws  Exception{
//        System.out.println("进入拦截器(后台)...");
//        String uri = request.getRequestURI();
//        if(uri.startsWith("/admin") && null == request.getSession().getAttribute("loginUser")){
//            request.getSession().setAttribute("errorMsg","请登录");
//            response.sendRedirect(request.getContextPath()+"/admin/login");
//            System.out.println("未登录，拦截成功");
//            return  false;
//        }else {
//            request.getSession().removeAttribute("errorMsg");
//            System.out.println("已登录，成功放行");
//            return true;
//        }
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        System.out.println("进入拦截器(后台)...");
        if (null == request.getSession().getAttribute(Constants.ADMIN_USER_SESSION_KEY)) {
            response.sendRedirect(request.getContextPath() + "/admin"+"/login");
            System.out.println("未登录，拦截成功");
            return false;
        } else {
            System.out.println("已登录，成功放行");
            return true;
        }
    }
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
