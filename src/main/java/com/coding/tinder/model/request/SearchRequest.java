package com.coding.tinder.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description
 * @ClassName SearchRequest
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 3100796314240462171L;

    private String userAccount;
}
