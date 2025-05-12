package com.it.testx.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.testx.exception.BusinessException;
import com.it.testx.exception.ErrorCode;
import com.it.testx.exception.ThrowUtils;
import com.it.testx.model.entity.User;
import com.it.testx.model.enums.UserRoleEnum;
import com.it.testx.model.vo.user.LoginUserVO;
import com.it.testx.service.UserService;
import com.it.testx.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static com.it.testx.constant.UserConstant.USER_LOGIN_STATE;

/**
 * User Service Impl
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    /**
     * User register
     *
     * @param account       User account
     * @param password      User password
     * @param checkPassword Check password
     * @return User ID
     */
    @Override
    public long register(String account, String password, String checkPassword) {
        // 1. 校验参数
        ThrowUtils.throwIf(StrUtil.hasBlank(account), ErrorCode.PARAMS_ERROR, "用户账号不能为空");
        ThrowUtils.throwIf(StrUtil.hasBlank(password), ErrorCode.PARAMS_ERROR, "用户密码不能为空");
        ThrowUtils.throwIf(!password.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");

        // 2. 用户合法性校验
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", account);
        long count = this.baseMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "用户账号重复");

        // 3. 密码加密
        String encryptPassword = getEncryptPassword(password);

        // 4. 插入数据
        User user = new User();
        user.setUserAccount(account);
        user.setUserPassword(encryptPassword);
        user.setUserName("");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        return user.getId();
    }

    /**
     * @param password User password
     * @return Encrypt password
     */
    @Override
    public String getEncryptPassword(String password) {
        final String SALT = "test-x";
        return DigestUtils.md5DigestAsHex((SALT + password).getBytes()).toUpperCase();
    }

    /**
     * User login
     * @param account  User account
     * @param password User password
     * @param request   http request
     * @return  脱敏后的用户信息
     */
    @Override
    public LoginUserVO userLogin(String account, String password, HttpServletRequest request) {
        // 1. 参数校验
        ThrowUtils.throwIf(StrUtil.hasBlank(account, password), ErrorCode.PARAMS_ERROR, "账号或密码为空");

        // 2. 密码加密
        String encryptPassword = getEncryptPassword(password);

        // 3. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", account);
        queryWrapper.eq("user_password", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取脱敏后的用户信息
     * @param user  用户信息
     * @return  脱敏后的用户信息
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null)
            return null;
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }
}




