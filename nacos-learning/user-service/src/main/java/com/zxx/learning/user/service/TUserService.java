package com.zxx.learning.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxx.learning.user.entity.TUser;

/**
 * 用户表服务接口（操作 t_user）
 */
public interface TUserService extends IService<TUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体，未找到时返回 null
     */
    TUser getByUsername(String username);

    /**
     * 创建用户（设置默认角色和状态等）
     *
     * @param user 待创建的用户
     * @return 创建后的用户（含主键）
     */
    TUser createUser(TUser user);
}

