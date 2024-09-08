package com.coding.tinder.model.request;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author samuel
 * @TableName CreateTeamRequest
 */
@Data
public class CreateTeamRequest implements Serializable {

    private static final long serialVersionUID = 1860549467312723728L;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 队伍最多人数
     */
    private Integer maxUser;

    /**
     * 0 公开,1 私密,2 加密
     */
    private Integer status;

    /**
     * 队伍密码
     */
    private String password;

    /**
     * 过期时间
     */
    @JsonFormat(pattern= "yyyy/MM/dd",timezone = "GMT+8")
    private Date expirationTime;


}
