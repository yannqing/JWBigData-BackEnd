package com.wxjw.jwbigdata.vo.UserVo;

import com.wxjw.jwbigdata.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {
    private Integer userId;
    private String userName;
    private String realName;
    private String dept;
    private String duties;
    private String phone;
    private boolean status;
    private ArrayList<Boolean> auth;
    private List<Integer> selectedTables;

    public UserVo(User user, String dept,List<Integer> selectedTables) {
        this.userId = user.getId();
        this.userName = user.getUserAccount();
        this.realName = user.getUsername();
        if(!dept.isEmpty()){
            this.dept = dept;
        }
        this.duties = user.getPosition();
        this.phone = user.getPhone();
        this.status = user.getStatus()==0?true:false;
        auth = new ArrayList<>();
        this.auth.add(user.getRole()==0?false:true);
        this.auth.add(user.getPortrait()==0?false:true);
        this.auth.add(user.getSearch()==0?false:true);
        this.auth.add(user.getCompare()==0?false:true);
        this.auth.add(user.getModel()==0?false:true);
        this.selectedTables = selectedTables;
    }
}
