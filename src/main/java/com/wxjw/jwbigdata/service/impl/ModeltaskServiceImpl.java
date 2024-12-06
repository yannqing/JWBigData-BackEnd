package com.wxjw.jwbigdata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.domain.Modeltask;
import com.wxjw.jwbigdata.service.ModeltaskService;
import com.wxjw.jwbigdata.mapper.ModeltaskMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
* @author Paul
* @description 针对表【modeltask】的数据库操作Service实现
* @createDate 2024-09-04 21:47:20
*/
@Service
public class ModeltaskServiceImpl extends ServiceImpl<ModeltaskMapper, Modeltask>
    implements ModeltaskService{

    @Override
//    @Scheduled(cron = "*/5 * * * * ?") // 每天凌晨1点执行
    public void Task() {
        System.out.println("定时任务执行了");
    }
}




