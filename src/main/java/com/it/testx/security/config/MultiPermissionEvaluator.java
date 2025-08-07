package com.licensed.remitprod.web.security.config;

import com.licensed.remitprod.common.enums.IorpCommonResultCodeEnum;
import com.licensed.remitprod.common.exception.IorpCommonException;
import com.licensed.remitprod.core.model.enums.operation.PermissionEnum;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

/**
 * 多权限校验器
 *
 * @author yeran
 */
public class MultiPermissionEvaluator implements PermissionEvaluator {

    private static final String SUPER_PERMISSION = PermissionEnum.OPERATOR_MANAGE.getCode();

    /**
     * 主校验逻辑（支持String或String[]传参）
     */
    @Override
    public boolean hasPermission(Authentication auth, Object target, Object permission) {
        // 1. 超级权限检查
        if (hasSuperAdmin(auth)) {
            return true;
        }

        // 2. 转换权限参数为数组
        String[] requiredPermissions = convertToPermissionArray(permission);

        // 3. 检查权限
        return Arrays.stream(requiredPermissions)
                .anyMatch(perm -> checkSinglePermission(auth, perm));
    }

    /**
     * 转换权限参数为统一格式
     */
    private String[] convertToPermissionArray(Object permission) {
        if (permission == null) {
            return new String[0];
        }
        if (permission instanceof String) {
            return new String[]{(String) permission};
        }
        if (permission instanceof String[]) {
            return (String[]) permission;
        }
        if (permission instanceof Collection) {
            return ((Collection<?>) permission).stream()
                    .map(Object::toString)
                    .toArray(String[]::new);
        }
        // 处理数组类型（非String数组）
        if (permission.getClass().isArray()) {
            return Arrays.stream((Object[]) permission)
                    .map(Object::toString)
                    .toArray(String[]::new);
        }
        throw new IorpCommonException(IorpCommonResultCodeEnum.UNKNOWN_EXCEPTION, "权限参数必须是String或String[]类型");
    }

    /**
     * 检查单个权限
     */
    private boolean checkSinglePermission(Authentication auth, String permissionCode) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(code -> code.equals(permissionCode));
    }

    /**
     * 检查超级权限
     */
    private boolean hasSuperAdmin(Authentication auth) {
        return checkSinglePermission(auth, SUPER_PERMISSION);
    }

    @Override
    public boolean hasPermission(Authentication auth, 
                               Serializable targetId, 
                               String targetType, 
                               Object permission) {
        return hasPermission(auth, targetId, permission);
    }
}