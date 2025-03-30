package com.itheima.mp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户管理接口")
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @ApiOperation("新增用户接口")
    @PostMapping
    public void saveUser(@RequestBody UserFormDTO userDTO) {
        // 1.把DTO拷贝到PO
        User user = BeanUtil.copyProperties(userDTO, User.class);
        // 2.新增
        userService.save(user);
    }

    @ApiOperation("删除用户接口")
    @DeleteMapping("{id}")
    public void deleteUserById(@ApiParam("用户id") @PathVariable("id") Long id) {
        userService.removeById(id);
    }

    @ApiOperation("根据id查询用户接口")
    @GetMapping("{id}")
    public UserVO queryUserById(@ApiParam("用户id") @PathVariable("id") Long id) {
//        // 1.查询用户PO
//        User user = userService.getById(id);
//        // 2.把PO拷贝到VO
//        return BeanUtil.copyProperties(user, UserVO.class);

        return userService.queryUserAndAddressById(id);
    }

    @ApiOperation("根据id批量查询用户接口")
    @GetMapping
    public List<UserVO> queryUserByIds(@ApiParam("用户id集合") @RequestParam("ids") List<Long> ids) {
//        // 1.查询用户PO
//        List<User> users = userService.listByIds(ids);
//        // 2.把PO拷贝到VO
//        return BeanUtil.copyToList(users, UserVO.class);
        return userService.queryUserAndAddressByIds(ids);
    }

    @ApiOperation("扣减用户余额接口")
    @PutMapping("/{id}/deduction/{money}")
    public void deductBalance(
            @ApiParam("用户id") @PathVariable("id") Long id,
            @ApiParam("扣减的金额") @PathVariable("money") Integer money) {
        userService.deductBalance(id, money);
    }

    @ApiOperation("根据复杂条件查询用户接口")
    @GetMapping("/list")
    public List<UserVO> queryUsers(UserQuery query) {
        // 1.查询用户PO
        List<User> users = userService.queryUsers(
                query.getName(), query.getStatus(), query.getMinBalance(), query.getMaxBalance());
        // 2.把PO拷贝到VO
        return BeanUtil.copyToList(users, UserVO.class);
    }

    @ApiOperation("根据复杂条件查询用户接口1")
    @GetMapping("/list1")
    public List<UserVO> queryUsers1(UserQuery query) {
        // 1.查询用户PO
        List<User> users = userService.queryUsers1(
                query.getName(), query.getStatus(), query.getMinBalance(), query.getMaxBalance());
        // 2.把PO拷贝到VO
        return BeanUtil.copyToList(users, UserVO.class);
    }

    @ApiOperation("根据复杂条件分页查询用户接口")
    @GetMapping("/page")
    public PageDTO<UserVO> queryUsersPage(UserQuery query) {
        return userService.queryUsersPage(query);
    }

    @ApiOperation("根据id和城市批量查询用户接口")
    @GetMapping("/{ids}/city/{city}")
    public List<UserVO> queryUsersByIdsAndAddress(
            @ApiParam("用户id集合") @PathVariable("ids") List<Long> ids,
            @ApiParam("城市") @PathVariable("city") String city) {
        // 1.查询用户PO
        List<User> users = userService.queryUsersJoinAddress(ids, city);
        // 2.把PO拷贝到VO
        return BeanUtil.copyToList(users, UserVO.class);
    }
}
