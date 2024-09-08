package com.coding.tinder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coding.tinder.model.domain.Team;
import com.coding.tinder.model.domain.User;
import com.coding.tinder.model.request.QueryTeamPageRequest;
import com.coding.tinder.model.vo.UserTeamVo;


import java.util.List;


/**
* @author samuel
* @description team Service
* @createDate 2024-09-11 10:53:16
*/
public interface TeamService extends IService<Team> {

    /**
     *  推荐队伍列表
     * @param queryTeamPageRequest 查询推荐队伍参数
     * @param currentUser 当前用户
     * @return 队伍列表
     */
   List<UserTeamVo> recommendTeam(QueryTeamPageRequest queryTeamPageRequest, User currentUser);

    /**
     *  创建队伍
     * @param team 队伍信息
     * @param currentUser 创建用户
     * @return 队伍id
     */
    long createTeam(Team team, User currentUser);

    /**
     *  加入队伍
     * @param teamId 队伍id
     * @param currentUser 加入用户
     * @param password 加入密码(不必须)
     * @return 队伍id
     */
    long joinTeam(Long teamId, User currentUser, String password);

    /**
     * 解散队伍
     * @param teamId 队伍id
     * @param currentUser 当前用户
     */
    boolean dissolveTeam(Long teamId,User currentUser);

    /**
     * 退出队伍
     * @param teamId 队伍id
     * @param currentUser 当前用户
     * @return 执行成功
     */
    boolean outTeam(Long teamId,User currentUser);

    /**
     *  修改队伍
     * @param currentUser 登录用户
     * @param team 修改的队伍信息
     * @return
     */
    boolean editTeam(Team team,User currentUser);

    /**
     * 根据队伍id查询队伍信息
     * @param id 队伍id
     * @return 队伍
     */
    Team queryById(Long id);

    /**
     * 获取用户创建的队伍
     * @param userId 用户id
     * @return 创建队伍的集合
     */
    List<UserTeamVo> queryMyCreateTeam(Long userId);

    /**
     *  获取用户加入队伍的列表
     * @param id 用户id
     * @return 我加入队伍的列表
     */
    List<UserTeamVo> queryMyJoinTeam(Long id);
}
