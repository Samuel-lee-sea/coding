package com.coding.tinder.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description UserTeamVo
 * @ClassName UserTeamVo
 */
@Data
public class UserTeamVo {

    /**
     * id
     */
    private Long id;

    /**
     * team name
     */
    private String name;

    /**
     * desc
     */
    private String description;

    /**
     * team max user
     */
    private Integer maxUser;

    /**
     * 0 public,1 private,2 decode
     */
    private Integer status;

    /**
     * team pwd
     */
    private String password;

    /**
     * teamleader id
     */
    private Long userId;


    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expirationTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 是否已加入队伍
     */
    private Boolean isSaveTeam;

    /**
     * 加入队伍的人数
     */
    private Integer  hasJoinNumber;

}
