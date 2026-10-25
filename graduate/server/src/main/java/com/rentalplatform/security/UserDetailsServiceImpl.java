package com.rentalplatform.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rentalplatform.mapper.UserMapper;
import com.rentalplatform.model.entity.SysUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Web 登录按 username 查询；JWT 过滤器按 "id:{userId}" 查询（约定前缀区分）。 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String key) throws UsernameNotFoundException {
        SysUser user;
        if (key.startsWith("id:")) {
            user = userMapper.selectById(Long.parseLong(key.substring(3)));
        } else {
            user = userMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, key));
        }
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new LoginUser(user);
    }
}
