package com.wxjw.jwbigdata.vo.LogVo;

import com.wxjw.jwbigdata.domain.Operlog;
import com.wxjw.jwbigdata.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;

/**
 * @author black
 * @version 1.0.0
 * @className LogVo
 * @description TODO
 * @date 2024-12-23 21:07
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogVo {
    private Integer id;
    private String operator;
    private String operateType;
    private String operateData;
    private String operateTime;

    public LogVo(Operlog operlog, User user){
        this.id = operlog.getId();
        this.operator = user == null?"":user.getUsername();
        this.operateData = operlog.getOperData();
        this.operateType = operlog.getOperType();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.operateTime = sdf.format(operlog.getOperTime());
    }
}
