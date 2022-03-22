package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.common.R;
import com.itheima.entity.User;
import com.itheima.service.UserService;
import com.itheima.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机短信验证码
     *
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    //这里需要将生成的验证码保存到session,所以参数有HttpSession
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();

        //判断手机号是否为空
        if (StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            //记录日志
            log.info("code={}", code);

            //调用阿里云短信服务api发送信息
            //SMSUtils.sendMessage("瑞吉外卖", "18711111111", phone, code);

            //将要生成的验证码保存到session
            session.setAttribute(phone, code);

            //将生成的验证码缓存到redis中,并且设置有效期为5min
            //四个参数(Object key, object value ,long timeout,TimeUtil unit)
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);


            return R.success("手机验证码发送成功");
        }
        return R.error("信息发送失败,请稍后再试");
    }


    /**
     * 移动端用户登录
     *
     * @param map
     * @return
     */
    @PostMapping("/login")
    //这里使用map接收前端传过来的JSON数据
    public R<User> login(@RequestBody Map map, HttpSession session) {
        //记录日志
        log.info("用户登录:{}", map.toString());

        //获取手机号(从map中
        String phone = map.get("phone").toString();

        //获取验证码(从map中
        String code = map.get("code").toString();

        //获取验证码(从session中 用于做判断
        //取出字符串phone的值 不是这个字符串"phone"
        //Object codeInSession = session.getAttribute(phone);

        //从Redis中获取保存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //进行验证码校验(页面提交的验证码是否和session中的验证码一致,首先要判断是不是空
        if (codeInSession != null && codeInSession.equals(code)) {
            //如果对比成功,说明可以成功登录

            //新建条件查询器
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();

            //设定查询条件,根据手机号查询用户
            lqw.eq(User::getPhone, phone);

            //进行查询
            User user = userService.getOne(lqw);

            //说明是新用户 ,自动完成注册
            if (user == null) {
                user = new User();
                user.setName("测试用户");
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            //是老用户,将查询到的消息返回之前将id存入session
            session.setAttribute("user", user.getId());

            //如果用户登录成功,删除Redis中缓存的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }

        //验证码输入错误
        return R.error("登陆失败");
    }
}










