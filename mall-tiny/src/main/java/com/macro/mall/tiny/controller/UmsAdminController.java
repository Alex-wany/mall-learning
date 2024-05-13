package com.macro.mall.tiny.controller;

import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.dto.UmsAdminLoginParam;
import com.macro.mall.tiny.mbg.model.UmsAdmin;
import com.macro.mall.tiny.mbg.model.UmsPermission;
import com.macro.mall.tiny.service.UmsAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台用户管理
 * Created by macro on 2018/4/26.
 */
@Controller
@Api(tags = "UmsAdminController", description = "后台用户管理")//用于接口分组 用于swagger文档分类
@RequestMapping("/admin")
public class UmsAdminController {
    @Autowired
    private UmsAdminService adminService;//自动注入UmsAdminService
    @Value("${jwt.tokenHeader}")//@Value注解用于获取配置文件中的属性值 用于存储jwt.tokenHeader的值
    private String tokenHeader;//定义一个字符串变量tokenHeader 用于存储jwt.tokenHeader的值
    @Value("${jwt.tokenHead}")//获取配置文件中的jwt.tokenHead的值 用于存储jwt.tokenHead的值
    private String tokenHead;//定义一个字符串变量tokenHead 用于存储jwt.tokenHead的值
    //@Value使用方法：@Value("${属性名}") 用于获取配置文件中的属性值 需要在类上加上@Component注解 用于将类注册到spring容器中
    //@Component注解用于将类注册到spring容器中 也可以用@Component的派生注解 例如@Controller @Service @Repository
    //spring容器会自动扫描这些注解的类并注册到spring容器中 用于实现依赖注入 例如@Autowired注解 用于自动注入依赖对象


    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    //value = "/register"表示请求路径 method = RequestMethod.POST表示请求方法为POST
    //为什么要用POST请求呢？因为注册用户是对服务器资源的增加操作 为什么增加操作要用POST请求呢？
    //因为POST请求是幂等的 也就是说多次请求结果是一样的 但是GET请求是不幂等的 也就是说多次请求结果是不一样的 所以增加操作要用POST请求
    @ResponseBody//用于将返回值转换为json格式的数据 用于返回json格式的数据
    public CommonResult<UmsAdmin> register(@RequestBody UmsAdmin umsAdminParam, BindingResult result) {
        UmsAdmin umsAdmin = adminService.register(umsAdminParam);
        if (umsAdmin == null) {
            CommonResult.failed();
        }
        return CommonResult.success(umsAdmin);
    }

    @ApiOperation(value = "登录以后返回token")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult login(@RequestBody UmsAdminLoginParam umsAdminLoginParam, BindingResult result) {
        String token = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation("获取用户所有权限（包括+-权限）")
    @RequestMapping(value = "/permission/{adminId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsPermission>> getPermissionList(@PathVariable Long adminId) {
        List<UmsPermission> permissionList = adminService.getPermissionList(adminId);
        return CommonResult.success(permissionList);
    }
}
