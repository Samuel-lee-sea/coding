package com.coding.tinder.model.request;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author samuel
 * @TableName QueryTeamPageRequest
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryTeamPageRequest extends PageRequest  implements Serializable {

    private static final long serialVersionUID = 8878496462281741587L;
    /**
     * 队伍名称
     */
    private String name;


    /**
     * 0 公开,1 私密,2 加密
     */
    private Integer status;

    /**
     * 搜索文本
     */
    private String searchText;
}
