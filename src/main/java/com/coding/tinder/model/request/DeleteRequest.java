package com.coding.tinder.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description 删除请求类
 * @ClassName DeleteRequest
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = -7130860800426144245L;

    private Long id;

}
