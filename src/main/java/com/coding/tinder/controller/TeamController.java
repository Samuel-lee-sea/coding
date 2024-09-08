package com.coding.tinder.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.coding.tinder.common.BaseResponse;
import com.coding.tinder.common.ErrorCode;
import com.coding.tinder.common.ResultUtil;
import com.coding.tinder.exception.BusinessException;
import com.coding.tinder.model.domain.Team;
import com.coding.tinder.model.domain.User;
import com.coding.tinder.model.request.*;
import com.coding.tinder.model.vo.UserTeamVo;
import com.coding.tinder.service.TeamService;
import com.coding.tinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description team
 * @ClassName TeamController
 */
@RestController
@RequestMapping("/team")
public class TeamController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    /**
     *  pagequery
     * @param queryTeamPageRequest
     * @param request
     * @return  status
     */
    @GetMapping("/recommend")
    public BaseResponse<List<UserTeamVo>> recommendTeam(QueryTeamPageRequest queryTeamPageRequest, HttpServletRequest request){
        User currentUser = userService.getCurrentUserIsNull(request);
        List<UserTeamVo> teamList = teamService.recommendTeam(queryTeamPageRequest,currentUser);
        if (currentUser != null){
            //todo
            System.out.println("waiting for update");
        }
        return ResultUtil.success(teamList);
    }

    /**
     * 创建队伍
     * @param createTeamRequest
     * @param request
     * @return teamId
     */
    @PostMapping("/create")
    public BaseResponse<Long> createTeam(HttpServletRequest request, @RequestBody CreateTeamRequest createTeamRequest){
        User currentUser = userService.getCurrentUser(request);
        Team team = BeanUtil.toBean(createTeamRequest, Team.class);
        long teamId = teamService.createTeam(team, currentUser);
        return ResultUtil.success(teamId);
    }

    /**
     * modify team
     * @param editTeamRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody EditTeamRequest editTeamRequest, HttpServletRequest request){
        User currentUser = userService.getCurrentUser(request);
        if (ObjectUtil.isNull(editTeamRequest) || editTeamRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍id不能为空");
        }
        Team team = BeanUtil.toBean(editTeamRequest, Team.class);
        boolean res = teamService.editTeam(team, currentUser);
        return ResultUtil.success(res);
    }

    /**
     * add team
     * @param joinTeamRequest
     * @param request
     * @return team id
     */
    @PostMapping("/join")
    public BaseResponse<Long> joinTeam(@RequestBody JoinTeamRequest joinTeamRequest, HttpServletRequest request){
        Long teamId = joinTeamRequest.getId();
        String password = joinTeamRequest.getPassword();
        if (ObjectUtil.isNull(teamId) || teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍id为空");
        }
        User currentUser = userService.getCurrentUser(request);
        teamService.joinTeam(teamId, currentUser, password);
        return ResultUtil.success(teamId);
    }

    /**
     * dissolve team
     * @param deleteRequest team id
     * @param request
     * @return status
     */
    @PostMapping("/dissolve")
    public BaseResponse<Boolean> dissolveTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request){
        if (ObjectUtil.isNull(deleteRequest) || deleteRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍id不能为空");
        }
        Long teamId = deleteRequest.getId();
        User currentUser = userService.getCurrentUser(request);
        boolean result = teamService.dissolveTeam(teamId, currentUser);
        return ResultUtil.success(result);
    }

    /**
     * quit
     * @param deleteRequest team id
     * @param request
     * @return status
     */
    @PostMapping("/out")
    public BaseResponse<Boolean> outTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request){

        if (ObjectUtil.isNull(deleteRequest) || deleteRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍id不能为空");
        }
        Long teamId = deleteRequest.getId();
        User currentUser = userService.getCurrentUser(request);
        boolean res = teamService.outTeam(teamId, currentUser);
        return ResultUtil.success(res);
    }

    @GetMapping("/by/id")
    public BaseResponse<Team> queryById(Long id,HttpServletRequest request){
        User currentUser = userService.getCurrentUser(request);
        Team team = teamService.queryById(id);
        return ResultUtil.success(team);
    }

    @GetMapping("/my/create")
    public BaseResponse<List<UserTeamVo>> queryMyCreateTeam(HttpServletRequest request){
        User currentUser = userService.getCurrentUser(request);
        List<UserTeamVo> teams = teamService.queryMyCreateTeam(currentUser.getId());
        return ResultUtil.success(teams);
    }

    @GetMapping("/my/join")
    public BaseResponse<List<UserTeamVo>> queryMyJoinTeam(HttpServletRequest request){
        User currentUser = userService.getCurrentUser(request);
        List<UserTeamVo> teams = teamService.queryMyJoinTeam(currentUser.getId());
        return ResultUtil.success(teams);
    }

}
