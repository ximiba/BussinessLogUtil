package com.tian.li.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author: lixiaotian
 * @date: 2021/2/26 14:54
 * @description:
 */
@Data
public class BizLogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long enterpriseId;

    private Long precictId;

    private Long userId;

    private String userName;

    /**
     * 日志操作类型
     */
    private String operateType;

    /**
     * 日志操作内容
     */
    private String operateContent;

    /**
     * 操作数据的主键id
     */
    private Long dataId;

    /**
     * 操作日期
     */
    private Date operateDate;


    private String remark;
}
