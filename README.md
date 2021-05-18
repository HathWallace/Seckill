# Seckill
个人Java学习项目，以秒杀为项目需求

项目构思与代码来源：[半天带你完全吃透电商项目秒杀系统-SpringBoot、Redis轻松实现Java高并发秒杀系统](https://www.bilibili.com/video/BV1Ma4y1H7KJ)

感谢发布者分享



## 项目搭建

- 技术栈
  - 前端：Thymeleaf、Bootstrap、Jquery
  - 后端：SpringBoot、MyBaitsPlus、Lombok
  - 中间件：~~RabbitMQ~~、Redis

- MyBaits Plus

  逆向生成工具代码：[HathWallace/Generator](https://github.com/HathWallace/Generator)



## 具体开发

- 两次 MD5 加盐加密

  工具类：`com.hath_zhou.seckill.utils.MD5Util`

  ```java
  return md5(addSalt(inputPass, salt)); // 分别封装加盐与md5校验代码
  ```
  
- 部分参数(如验证手机号格式)，使用 validation 组件校验，简化代码

  自定义注解：`com.hath_zhou.seckill.validator.IsMobile`

  ```java
  @Constraint(validatedBy = {IsMobileValidator.class}) // 添加自定义验证注解
  ```

  自定义验证类：`com.hath_zhou.seckill.vo.IsMobileValidator`

  ```java
  /**
   * 重写验证逻辑
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
      if (!required && StringUtils.isEmpty(value)) {
          return true;
      }
      return ValidatorUtil.isMobile(value);//封装手机号验证的方法
  }
  ```

- 自定义一些异常参数

  枚举：`com.hath_zhou.seckill.vo.RespBeanEnum`

  ```java
  //通用
  SUCCESS(200, "SUCCESS"),
  ERROR(500, "服务端异常"),
  //登录模块5002xx
  LOGIN_ERROR(500210, "用户名或密码不正确"),
  MOBILE_ERROR(500211, "手机号码格式不正确"),
  BIND_ERROR(500212, "参数校验异常"),
  MOBILE_NOT_EXIST(500213, "手机号码不存在"),
  PASSWORD_UPDATE_FAIL(500214, "密码更新失败"),
  SESSION_ERROR(500215, "用户不存在"),
  //秒杀模块5005xx
  EMPTY_STOCK(500500, "库存不足"),
  REPEATE_ERROR(500501, "该商品每人限购一件"),
  REQUEST_ILLEGAL(500502, "请求非法，请重新尝试"),
  ERROR_CAPTCHA(500503, "验证码错误，请重新输入"),
  ```

  自定义全局异常

  异常类：`com.hath_zhou.seckill.exception.GlobalException`

  ```java
  /**
   * 全局异常
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public class GlobalException extends RuntimeException {
      private RespBeanEnum respBeanEnum;
  }
  ```

- 分布式 Session 问题，解决方案：后端集中存储

  工具类：`com.hath_zhou.seckill.utils.CookieUtil`

  获取Cookie对应的值(`getCookieValue`)：

  ```pseudocode
  foreach cookie in request.getCookies() // 历遍Web请求中的cookie列表
  	if cookie.getName() == cookieName then // 找到想要的cookie名
  		return cookie.getValue() // 返回对应的cookie值
  end
  return null // 没有找到，返回空
  ```

  设置Cookie的值(`setCookie`)：

  ```pseudocode
  Cookie cookie = new Cookie(cookieName, cookieValue)
  if cookieMaxage > 0 then // 设置cookie最大存活时间，为0则为无限制
  	cookie.setMaxAge(cookieMaxage)
  String domainName = getDomainName(request) // 从Web请求中获取域名
  response.addCookie(cookie) // 将cookie添加到Web响应中
  ```

- 解决库存超卖问题，基本思路：乐观锁

  服务实现类：`com.hath_zhou.seckill.service.impl.OrderServiceImpl`

  写入 MySQL 数据库：

  ```java
  @Transactional // 标记方法为事务型，其中一步失败全部回滚
  @Override
  public Order seckill(User user, GoodsVo goodsVo)
  ```

  ```java
  boolean result = seckillGoodsService.update(
      new UpdateWrapper<SeckillGoods>()
      .eq("goods_id", goodsVo.getId())
      .gt("stock_count", 0) // 库存大于0时才可执行
      .setSql("stock_count = stock_count-1") // 库存减一
  );
  if (!result) return null; // 如果更新失败，说明购买失败，直接返回
  ```

  写入Redis：

  ```java
  // 订单信息类seckillOrder创建成功后
  //加入redis，防止同一用户抢同种多个商品
  redisTemplate.opsForValue().set(
      String.format("order:%d:%d", user.getId(), goodsVo.getId()),
      seckillOrder
  );
  ```

  验证是否重复抢购

  控制器类：`com.hath_zhou.seckill.controller.SeckillController`
  
  ```java
  // redis判断
  SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get(
      String.format("order:%d:%d", user.getId(), goodsVo.getId())
  );
  if (seckillOrder != null) { // 已存在redis中
      // 返回枚举500501，"该商品每人限购一件"
      model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
      return "seckillFail";
  }
  ```



## 问题小记

- Linux使用注意

  设置本机与虚拟机( Vmare 下的 CentOS )网络连接的办法：[CentOS 7教程（二）-网络设置](https://zhuanlan.zhihu.com/p/79361590)
  
  如果需要用可视化工具连接数据库( MySQL 和 Redis )，则需要关闭防火墙
  
- 生成测试使用的用户信息

  原项目单独写了一个静态 Main 函数，连接数据库生成数千条用户数据

  但个人觉得写这样的代码过于繁琐，所以决定直接在服务中加一个接口，直接通过 MyBaitsPlus 插入数据。

  前端控制器类：`com.hath_zhou.seckill.controller.UserController`

  ```java
  /**
   * 数据库t_user表Mapper接口
   */
  @Autowired
  private UserMapper userMapper;
  
  /**
   * 生成用户测试用例
   *
   * @param model
   */
  @RequestMapping("/examples")
  @ResponseBody
  public RespBean createUsers(Model model) {
      // 封装创建用户的代码，生成两千条用户数据
      UserUtil.createUser(2000, userMapper);
      return RespBean.success();
  }
  ```

- 生成用户对应的cookie

  为实现多个不同用户的测试，需要为数据库中的用户生成对应的 Cookie ，并使用这些 Cookie 进行测试。单独写了一个静态 Main 函数，在原项目代码的基础上略做修改

  工具类：`com.hath_zhou.seckill.utils.UserUtil`

  ```java
  public static void main(String[] args) {
      List<Long> idList = new ArrayList<>(); 
      for (long i = 0; i < 2000; i++) {
          idList.add(15800000000L + i);
      }
      idList.add(19912345678L);
      writeCookies(idList, "192.168.109.88");
  }
  ```

- 获取域名

  原项目代码有个小 bug，导致在 CentOS 上部署后浏览器获取不到服务端返回的 cookie。
  
  查明是在 `setCookie` 中从 Web 请求中获取域名时的代码逻辑有误，导致域名的第一位消失(比如域名为 `127.0.0.1`，而通过 `getDomainName` 获取的域名为 `0.0.1` )。Bing 一下后决定直接用 Java 自带的 URL 类来解析域名。
  
  ```java
  URL url = new URL(request.getRequestURL().toString());
  return url.getHost();
  ```



## 运行效果

- 基本功能

  ![seckill2](https://raw.githubusercontent.com/HathWallace0/MyPicGo/master/img/seckill2.gif)

- 测试可用性与一致性

  测试文件：`\Seckill\other\seckill.jmx`

  虚拟机配置如图所示

  ![](https://raw.githubusercontent.com/HathWallace0/MyPicGo/master/img/image-20210518121210178.png)

  因为主机的性能不是很好，设置的虚拟机配置也比较低，而且项目本身前后端不分离，没有对性能做进一步的优化，所以吞吐量比较低。但可以看出，基本实现了在一定并发量下的可用性，并且可以保证最终结果符合业务逻辑(比如不会出现库存超卖、同一用户抢多个同一商品的情况)。

  ![test](https://raw.githubusercontent.com/HathWallace0/MyPicGo/master/img/test.gif)

