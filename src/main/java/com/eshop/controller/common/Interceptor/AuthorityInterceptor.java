package com.eshop.controller.common.Interceptor;

import com.eshop.common.ServerResponse;
import com.eshop.enums.UserRoleEnum;
import com.eshop.pojo.User;
import com.eshop.service.IUserService;
import com.eshop.util.CookieUtil;
import com.eshop.util.JSONUtil;
import com.eshop.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by windvalley on 2018/9/2.
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("pre handle");

        //获得Controller中的方法名
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //解析HandleMathod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        //解析参数, 具体的参数Key以及Value
        StringBuffer requestParamaterBuffer = new StringBuffer();
        Map paramaterMap = request.getParameterMap();
        Iterator iterator = paramaterMap.entrySet().iterator();
        while (iterator.hasNext() == true){
            Map.Entry entry = (Map.Entry) iterator.next();
            String mapKey = (String) entry.getKey();
            String mapValue = StringUtils.EMPTY;

            //request这个参数的Map， 里面的Value返回的是一个String数组
            Object object = entry.getValue();
            if (object instanceof String[]){
                String[] strings = (String[]) object;
                mapValue = Arrays.toString(strings);
            }
            requestParamaterBuffer.append(mapKey).append("=").append(mapValue);
        }

        if (StringUtils.equals(className, "UserController") && StringUtils.equals(methodName, "login")){
            log.info("拦截器拦截到Login请求, className:{}, methodName:{}", className, methodName);
            return true;
        }

        User user = null;
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotEmpty(loginToken) == true){
            String userJSONString = RedisShardedPoolUtil.get(loginToken);
            user = JSONUtil.string2Object(userJSONString, User.class);
        }

        if ((user == null) || (user.getRole() == UserRoleEnum.ADMIN.getCode())){
            //此处为user未登录，或者用户的权限不是管理员， 返回False，对应于此处，则为不会调用Controller里的方法

            //必须调用, 否则会触发异常， getwrite() has already benn called for this response
            response.reset();

            //必须设置，否则会产生乱码
            response.setCharacterEncoding("UTF-8");

            //必须设置返回类型，这里全是JSON接口，返回的都是JSON的字符串
            response.setContentType("application/json;charset=UTF-8");

            PrintWriter printWriter = response.getWriter();
            //resultMap 是由于富文本的特殊返回格式，所以在拦截器中特殊处理
            if (user == null){
                if (StringUtils.equals(className, "ProductManagerController") && StringUtils.equals(methodName, "richTextUpload")){
                    Map resultMap = new HashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "用户未登录 ");
                    printWriter.print(JSONUtil.object2String(resultMap));
                }
                printWriter.print(JSONUtil.object2String(ServerResponse.createByErrorMessage("拦截器拦截--->用户未登录")));
            } else {
                if (StringUtils.equals(className, "ProductManagerController") && StringUtils.equals(methodName, "richTextUpload")){
                    Map resultMap = new HashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无管理员权限");
                    printWriter.print(JSONUtil.object2String(resultMap));
                }
                else {
                    printWriter.print(JSONUtil.object2String(ServerResponse.createByErrorMessage("拦截器拦截--->用户无权限操作")));
                }
            }
            //必须要清空输出流
            printWriter.flush();

            //必须要关闭输出
            printWriter.close();

            return false;
        }
         return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        log.info("post handle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        log.info("after completion");
    }
}
