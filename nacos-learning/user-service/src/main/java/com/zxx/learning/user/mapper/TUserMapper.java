package com.zxx.learning.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxx.learning.user.entity.TUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper 层
 *
 * 对应数据库表：t_user
 */
@Mapper
public interface TUserMapper extends BaseMapper<TUser> {
}

