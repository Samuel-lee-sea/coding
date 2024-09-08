package com.coding.tinder.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author samuel
 * @TableName CreateTeamRequest
 */
@Data
public class JoinTeamRequest implements Serializable {


    private static final long serialVersionUID = -7089265750191020769L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 队伍密码
     */
    private String password;



}
