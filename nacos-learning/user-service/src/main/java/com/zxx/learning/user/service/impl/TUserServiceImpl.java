package com.zxx.learning.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxx.learning.user.entity.TUser;
import com.zxx.learning.user.mapper.TUserMapper;
import com.zxx.learning.user.service.TUserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户表服务实现类
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements TUserService {

    @Override
    public TUser getByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return lambdaQuery()
                .eq(TUser::getUsername, username)
                .one();
    }

    @Override
    public TUser createUser(TUser user) {
        if (user == null) {
            return null;
        }
        // 默认状态：1-正常
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        // 默认角色：user
        if (!StringUtils.hasText(user.getRole())) {
            user.setRole("user");
        }
        save(user);
        return user;
    }
}

