package com.tian.li.entity;


import com.tian.li.annotation.LogRecord;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AppUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long enterpriseId;

    @LogRecord(name = "企业名称")
    private String enterpriseName;

    private Long userId;

    @LogRecord(name = "用户名")
    private String userName;

    @LogRecord(name = "年龄", divide = "2")
    private Integer age;

    @LogRecord(name = "密码")
    private String password;

    @LogRecord(name = "用户类型，0:内部运营账户，1:物业公司员工账户，2:业主（客户）账户")
    private Integer userType;

    @LogRecord(name = "上次登录时间")
    private Date lastLoginTime;

    @LogRecord(name = "是否激活", remark = 1)
    private Integer isActive;
}