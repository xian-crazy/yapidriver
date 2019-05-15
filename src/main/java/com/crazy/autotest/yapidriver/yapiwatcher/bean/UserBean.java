package com.crazy.autotest.yapidriver.yapiwatcher.bean;

import lombok.Data;

@Data
public class UserBean {
   private String email;
    private String role;
    private int uid;
    private String username;
    private String _id;
    public UserBean thiz(){
        return  this;
    }
}
