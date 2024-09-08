package com.coding.tinder.model.enums;

/**
 * @author samuel
 */

public enum TeamStatusEnum {

    /**
     *  team status
     */
    PUBLIC_STATUS("public", 0),
    PRIVATE_STATUS("private", 1),
    ENCRYPTION_STATUS("decode", 3);


    private String statusName;

    private int statusId;

    TeamStatusEnum(String statusName, int statusId) {
        this.statusName = statusName;
        this.statusId = statusId;
    }

    public String getStatusNameByStatusId(Integer statusId){
        TeamStatusEnum[] statusEnums = TeamStatusEnum.values();
        for (TeamStatusEnum statusEnum : statusEnums) {
            if (statusEnum.getStatusId() == statusId) {
                return statusEnum.getStatusName();
            }
        }
        return null;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}
