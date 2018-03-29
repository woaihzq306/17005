package cn.yunhu.utils;

/**
 * 配置文件
 */
public class Constants {

    /** 数据密钥 */
    public static String SECRET_KEY = "KSManduayd89q31&*kdmAIDn";
    /** 数据向量 */
    public static String IV         = "01234567";
    /** 解密加密编码 */
    public static String ENCODE     = "utf-8";
    /** 3des加密 */
    public static String ALGORITHM  = "desede";
    /** 文件夹名称 */
    public static String APP_NAME   = "yunhu";
    /** 签名密钥 */
    public static String SIGN_KEY   = "J89103KDAMDJSDKKALDLDLAKDKDKAKDK";


    // +-----------------------------------
    // | 接口路径定义
    // +-----------------------------------
    /**
     * 获取APP基本信息接口
     */
    public static String API_PATH_GET_APP_INFO           = "api/public/get_app_info";
    /**
     * 登录接口
     */
    public static String API_PATH_LOGIN                  = "api/passport/login";
    /**
     * 注册接口
     */
    public static String API_PATH_REGISTER               = "api/passport/register";
    /**
     * 找回密码接口
     */
    public static String API_PATH_FIND_PWD               = "api/passport/find_pwd";
    /**
     * 发送邮件获取注册码接口
     */
    public static String API_PATH_REGISTER_CODE_BY_EMAIL = "api/passport/get_register_code_by_email";
    /**
     * 获取会员信息接口
     */
    public static String API_PATH_ACCOUNT_GET_INFO       = "api/account/get_info";
    /**
     * 上传图片接口
     */
    public static String API_UPLOAD_PICTURE              = "api/feedback/upload";
    /**
     * 提交反馈接口
     */
    public static String API_UPLOAD_FEED_BACK            = "api/feedback/submit";
    /**
     * 获得反馈列表接口
     */
    public static String API_GET_FEED_BACK               = "api/feedback/index";
    /**
     * 购买账号接口
     */
    public static String API_BUY_ACCOUNT                 = "api/account/buy";
    /**
     * 解锁账号接口
     */
    public static String API_UNCLOCK_ACCOUNT             = "api/account/unlock";
    /**
     * 开始呼叫接口
     */
    public static String API_CALL_SUBMIT                 = "api/call/submit";
    /**
     * 停止呼叫接口
     */
    public static String API_CALL_STOP                   = "api/call/stop";
    /**
     * 接呼叫任务
     */
    public static String API_CALL_TAKE                   = "api/call/take";
    /**
     * 获取入口的网址
     */
    public static String GET_ADDRESS                     = "http://blog.sina.cn/dpool/blog/s/blog_17fc3d9aa0102x0su.html";
}
