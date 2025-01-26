package com.wxjw.jwbigdata.config;

import com.wxjw.jwbigdata.common.Constant;
import com.wxjw.jwbigdata.security.filter.JwtAuthenticationTokenFilter;
import com.wxjw.jwbigdata.security.handler.MyLoginFailureHandler;
import com.wxjw.jwbigdata.security.handler.MyLoginSuccessHandler;
import com.wxjw.jwbigdata.security.handler.MyLogoutSuccessHandler;
import com.wxjw.jwbigdata.service.OperlogService;
import com.wxjw.jwbigdata.utils.RedisCache;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    RedisCache redisCache;

    @Resource
    private OperlogService operlogService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //允许一些请求匿名访问，其他的均需要认证
        http.authorizeHttpRequests((authorize)->authorize
                .requestMatchers(Constant.annos).permitAll()
                .anyRequest()
                .authenticated()
        );
        //关闭session
        http.sessionManagement((sessionManagement)->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //设置用户名密码认证前的jwt过滤器?
        http.addFilterBefore(new JwtAuthenticationTokenFilter(redisCache), UsernamePasswordAuthenticationFilter.class);

        //csrf
        http.csrf(AbstractHttpConfigurer::disable);

        //登录可以选择form表单登录，也可选择发送请求，写到controller中
        //form表单登录
        http.formLogin((login)->login.
                loginProcessingUrl("/login")
                .successHandler(new MyLoginSuccessHandler(redisCache,operlogService))
                .failureHandler(new MyLoginFailureHandler())
        );
        //post请求登录

        //设置退出logout过滤器
        http.logout((logout)->logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(new MyLogoutSuccessHandler(redisCache,operlogService))
        );

        //关闭跨域拦截--适用于前后端分离，另创建跨域拦截的类
        http.cors(Customizer.withDefaults());


        return http.build();
    }
    /**
     * 对密码进行BCrypt加密
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
