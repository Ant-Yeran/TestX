package com.it.testx.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.testx.exception.BusinessException;
import com.it.testx.exception.ErrorCode;
import com.it.testx.exception.ThrowUtils;
import com.it.testx.mapper.UserMapper;
import com.it.testx.model.dto.admin.UserQueryRequest;
import com.it.testx.model.entity.User;
import com.it.testx.model.enums.UserRoleEnum;
import com.it.testx.model.vo.admin.UserVO;
import com.it.testx.model.vo.user.LoginUserVO;
import com.it.testx.service.UserService;
import com.it.testx.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.it.testx.constant.UserConstant.LOGIN_TOKEN_EXPIRE;
import static com.it.testx.constant.UserConstant.LOGIN_TOKEN_PREFIX;

/**
 * User Service Impl
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
        user.setUserName("ant");
        user.setUserProfile("这个家伙很懒，啥也没填～");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        return user.getId();
    }

    /**
     * User login
     *
     * @param account  User account
     * @param password User password
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO login(String account, String password) {
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
            log.info("User login failed, account cannot match password");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        // 4. 生成登录 Token 并存储到 Redis
        // String token = generateAndStoreToken(user);
        // 生成 JWT
        String token = jwtUtils.generateToken(String.valueOf(user.getId()));

        // 5. 存储到 Redis 并返回脱敏用户信息（携带Token）
        // return getLoginUserVO(user, token);
        return storeToken(user, token);
    }

    /**
     * Get current login user
     *
     * @param token token
     * @return Current login user
     */
    @Override
    public User getLoginUser(String token) {
        // 1. 检验参数
        ThrowUtils.throwIf(StrUtil.isBlank(token), ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(!jwtUtils.validateToken(token), ErrorCode.NOT_LOGIN_ERROR);

        // 2. 从 Redis 获取用户信息
        String redisKey = LOGIN_TOKEN_PREFIX + token;
        LoginUserVO loginUserVO = (LoginUserVO) redisTemplate.opsForValue().get(redisKey);
        ThrowUtils.throwIf(loginUserVO == null, ErrorCode.NOT_LOGIN_ERROR);

        // 3. 自动续期
        redisTemplate.expire(redisKey, LOGIN_TOKEN_EXPIRE, TimeUnit.MINUTES);

        // 4. 返回用户信息（可根据需要决定是否查询数据库获取最新数据）
        return this.getById(loginUserVO.getId());
    }

    /**
     * User logout
     *
     * @param token token
     * @return success
     */
    @Override
    public boolean logout(String token) {
        ThrowUtils.throwIf(StrUtil.isBlank(token), ErrorCode.NOT_LOGIN_ERROR);
        if (StrUtil.isNotBlank(token)) {
            // 删除 Redis 中的 Token
            redisTemplate.delete(LOGIN_TOKEN_PREFIX + token);
            return true;
        }
        return false;
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

    @Override
    public LoginUserVO userLogin(String account, String password, HttpServletRequest request) {
        return null;
    }

    /**
     * 生成并存储 Token
     *
     * @param user User
     * @return token
     */
    private String generateAndStoreToken(User user) {
        // 生成 UUID 作为 Token
        String token = UUID.randomUUID().toString().replace("-", "");

        // 构建登录用户 VO
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);

        // 存储到 Redis 并设置过期时间
        String redisKey = LOGIN_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(
                redisKey,
                loginUserVO,
                LOGIN_TOKEN_EXPIRE,
                TimeUnit.MINUTES
        );

        return token;
    }

    /**
     * 存储 Token
     *
     * @param user User
     * @return token
     */
    private LoginUserVO storeToken(User user, String token) {
        // 构建登录用户 VO
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);

        // 存储到 Redis 并设置过期时间
        String redisKey = LOGIN_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(
                redisKey,
                loginUserVO,
                LOGIN_TOKEN_EXPIRE,
                TimeUnit.MINUTES
        );
        return getLoginUserVO(user, token);
    }

    /**
     * 获取脱敏后的用户信息（携带 token）
     *
     * @param user  用户信息
     * @param token 用户 token
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO getLoginUserVO(User user, String token) {
        LoginUserVO vo = getLoginUserVO(user);
        vo.setToken(token);  // 将 Token 返回给前端
        return vo;
    }

    /**
     * 获取脱敏后的用户信息
     *
     * @param user 用户信息
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null)
            return null;
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 【管理员】获取脱敏后的用户信息
     * @param user  用户信息
     * @return  脱敏后的用户信息
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 【管理员】获取脱敏后的用户信息列表
     * @param userList  用户信息列表
     * @return  脱敏后的用户信息列表
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * 【管理员】用户查询
     * @param userQueryRequest  用户查询请求
     * @return  用户
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "user_role", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "user_account", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "user_name", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "user_profile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }



//    /**
//     * User login
//     * @param account  User account
//     * @param password User password
//     * @param request   http request
//     * @return  脱敏后的用户信息
//     */
//    @Override
//    public LoginUserVO userLogin(String account, String password, HttpServletRequest request) {
//        // 1. 参数校验
//        ThrowUtils.throwIf(StrUtil.hasBlank(account, password), ErrorCode.PARAMS_ERROR, "账号或密码为空");
//
//        // 2. 密码加密
//        String encryptPassword = getEncryptPassword(password);
//
//        // 3. 查询用户是否存在
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_account", account);
//        queryWrapper.eq("user_password", encryptPassword);
//        User user = this.baseMapper.selectOne(queryWrapper);
//        // 用户不存在
//        if (user == null) {
//            log.info("user login failed, userAccount cannot match userPassword");
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
//        }
//        // 4. 记录用户的登录态
//        request.getSession().setAttribute(USER_LOGIN_STATE, user);
//        return this.getLoginUserVO(user);
//    }


//    /**
//     * 获取当前登录用户
//     * @param request   请求
//     * @return  当前登录用户
//     */
//    @Override
//    public User getLoginUser(HttpServletRequest request) {
//        // 1. 判断是否已登录
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User currentUser = (User) userObj;
//        if (currentUser == null || currentUser.getId() == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
//
//        // 2. 从数据库查询（追求性能的话可以注释，直接返回上述结果）
//        long userId = currentUser.getId();
//        currentUser = this.getById(userId);
//        if (currentUser == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
//        return currentUser;
//    }

}




