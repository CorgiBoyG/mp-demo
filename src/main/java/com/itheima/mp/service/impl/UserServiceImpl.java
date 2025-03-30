package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    // 如果需要自定义方法，可以这样使用
    private UserMapper getUserMapper() {
        return this.baseMapper;
    }

    @Override
    @Transactional
    public void deductBalance(Long id, Integer money) {
        /*
            这里可能有并发安全风险
            多个线程查到的余额是一样的
            多个线程扣余额 余额相同 那有可能只扣了一个
            建议这里加锁
            乐观锁：先比较 再更新 更新时数据库中用户余额要==刚才查询到的余额
            悲观锁：先锁定 再操作 当事务开始并执行此查询，数据库会对目标记录加排他锁

         */
        // 1.查询用户
        User user = getById(id);

//        // 1.查询用户，并加悲观锁
//        User user = lambdaQuery()
//                .eq(User::getId, id)
//                .last("FOR UPDATE")  // 添加悲观锁
//                .one();


        // 2.校验用户状态
        if (user == null || user.getStatus() == UserStatus.FROZEN) {
            throw new RuntimeException("用户状态异常！");
        }
        // 3.校验余额是否充足
        if (user.getBalance() < money) {
            throw new RuntimeException("用户余额不足！");
        }
        // 4.扣减余额 update tb_user set balance = balance - ?
        int remainBalance = user.getBalance() - money;
        lambdaUpdate()
                .set(User::getBalance, remainBalance)
                .set(remainBalance == 0, User::getStatus, UserStatus.FROZEN)
                .eq(User::getId, id)
                .eq(User::getBalance, user.getBalance()) // 乐观锁
                .update();

//        // 4.扣减余额 悲观锁
//        int remainBalance = user.getBalance() - money;
//        lambdaUpdate()
//                .set(User::getBalance, remainBalance)
//                .set(remainBalance == 0, User::getStatus, UserStatus.FROZEN)
//                .eq(User::getId, id)
//                .update();
    }

    @Override
    public List<User> queryUsers(String name, Integer status, Integer minBalance, Integer maxBalance) {
        // 底层仍然是调用了Mapper的基础方法
        return lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance)
                .list(); //最终调用list()方法时，底层会调用baseMapper.selectList(queryWrapper)执行查询
        //查询一个用one()
    }

    @Override
    public List<User> queryUsers1(String name, Integer status, Integer minBalance, Integer maxBalance) {
        // 1.构建查询条件 传函数
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance);
        // 2.将此wrapper传到mapper里面去实现
        return this.getUserMapper().queryUsers1(wrapper);
    }

    @Override
    public List<User> queryUsersJoinAddress(List<Long> ids, String city) {
        // 1.构建查询条件 传函数
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .in("u.id", ids)
                .like("a.city", city);
        // 2.将此wrapper传到mapper里面去实现
        return this.getUserMapper().queryUserByWrapper(wrapper);
    }

    @Override
    public UserVO queryUserAndAddressById(Long id) {
        // 1.查询用户
        User user = getById(id);
        if (user == null || user.getStatus() == UserStatus.FROZEN) {
            throw new RuntimeException("用户状态异常！");
        }
        // 2.查询地址
        List<Address> addresses = Db.lambdaQuery(Address.class).eq(Address::getUserId, id).list();
        // 3.封装VO
        // 3.1.转User的PO为VO
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        // 3.2.转地址VO
        if (CollUtil.isNotEmpty(addresses)) {
            userVO.setAddresses(BeanUtil.copyToList(addresses, AddressVO.class));
        }
        return userVO;
    }

    @Override
    public List<UserVO> queryUserAndAddressByIds(List<Long> ids) {
        // 1.查询用户
        List<User> users = listByIds(ids);
        if (CollUtil.isEmpty(users)) {
            return Collections.emptyList();
        }
        // 2.查询地址
        // 2.1.获取用户id集合
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        // 2.2.根据用户id查询地址
        List<Address> addresses = Db.lambdaQuery(Address.class).in(Address::getUserId, userIds).list();
        // 2.3.转换地址VO
        List<AddressVO> addressVOList = BeanUtil.copyToList(addresses, AddressVO.class);
        // 2.4.用户地址集合分组处理，相同用户的放入一个集合（组）中
        Map<Long, List<AddressVO>> addressMap = new HashMap<>(0);
        if (CollUtil.isNotEmpty(addressVOList)) {
            addressMap = addressVOList.stream().collect(Collectors.groupingBy(AddressVO::getUserId));
        }

        // 3.转换VO返回
        List<UserVO> list = new ArrayList<>(users.size());
        for (User user : users) {
            // 3.1.转换User的PO为VO
            UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
            list.add(vo);
            // 3.2.转换地址VO
            vo.setAddresses(addressMap.get(user.getId()));
        }
        return list;
    }

    @Override
    public PageDTO<UserVO> queryUsersPage(UserQuery query) {

        String name = query.getName();
        Integer status = query.getStatus();

        // 1.构建分页条件
        Page<User> page = query.toMpPageDefaultSortByUpdateTime();

        // 2.分页查询
        Page<User> p = lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .page(page);

        // 3.封装VO结果
        return PageDTO.of(p, user -> {
            // 1.拷贝基础属性
            UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
            // 2.处理特殊逻辑
            vo.setUsername(vo.getUsername().substring(0, vo.getUsername().length() - 2) + "**");
            return vo;
        });
    }
}
