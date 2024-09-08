package com.coding.tinder.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * user
 *
 * @author samuel
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;


    /**
     * 个人简介
     */
    private String profile;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 性别 0男 1女
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户状态 0-正常
     */
    private Integer userStatus;

    /**
     * 用户标签列表(json)
     */
    private String tags;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 逻辑删除 0-正常 1-删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 用户角色 0-普通用户 1-管理员
     */
    private Integer userRole;

    private String planetCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
