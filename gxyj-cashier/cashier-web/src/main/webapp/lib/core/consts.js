/**
 * ##常量定义模块，包含一些框架用到的常量##
 * @module core/consts
 * @author yisin
 */
define({
    /**
     * **项目根目录**
     *
     * **如果页面通过服务器访问，可能是这样的：http://localhost:8080/webapp/**
     *
     * **如果页面在本地访问，可能是这样的：file:///C:/Users/Administrator/Desktop/webapp/**
     * @type string
     */
    WEB_BASE: seajs.data.base,

    /**
     * **项目上下文名称，相对路径**
     *
     * **如果页面通过服务器访问，可能是这样的：/webapp/**
     * @type string
     */
    WEB_ROOT: seajs.data.root,//'http://192.168.20.32:8080/gxyjfss/'

    /**
     * **获取用户信息后台接口地址**
     * @type string
     */
    TOKEN_INFO_URL: 'http://192.168.20.32:8080/uia/',
    
    /**
     * **获取用户信息后台接口地址**
     * @type string
     */
    IMAGE_SERVER_URL: 'http://192.168.20.23/fsshimages/',
    
    /**
     * **获取用户信息后台接口地址**
     * @type string
     */
    USER_INFO_URL: seajs.data.base + "base/getUserInfo",

    /**
     * **encrypkey后台接口地址**
     * @type string
     */
    ENCRYPT_KEY_URL: seajs.data.base + "base/getEncryptKey",

    /**
     * **login后台接口地址**
     * @type string
     */
    LOGIN_URL: seajs.data.base + "base/login",

    /**
     * **获取session用户信息接口地址**
     * @type string
     */
    SESSION_URL: seajs.data.base + "m800/f80000",//'http://192.168.20.32:8080/gxyjfss/'

    /**
     * **logout后台接口地址**
     * @type string
     */
    LOGOUT_URL: seajs.data.base + "base/logout",
    /**
     * ** unlock后台接口地址 **
     * @type string
     */
    UNLOCK: seajs.data.base + "base/unlock",

    /**
     * **标识一个字符串常量"service"**
     * @type string
     */
    SERVICE: "service",

    /**
     * **标识一个字符串常量"json"**
     * @type string
     */
    REQ_TYPE_JSON: "json",

    /**
     * **标识一个字符串常量"xml"**
     * @type string
     */
    REQ_TYPE_XML: "xml",
    
    /**
     * 页面加载超时时间
     */
    PAGE_LOAD_TIME: 15000,
    
    CODE : {
    	
    	/* ================= 请求状态常量 ================ */
    	/**处理成功 */
    	"MSG_CODE_000000": "000000",   //处理成功
        /**消息数据重复 */
        "MSG_CODE_100000": "100000",   //消息数据重复
        /**服务器异常 */
        "MSG_CODE_000001": "000001",   //服务器异常
        /**处理超时 */
        "MSG_CODE_000002": "000002",   //处理超时
        /**未登陆或会话超时 */
        "MSG_CODE_888888": "888888",   //未登陆或会话超时
        /**权限不足 */
        "MSG_CODE_888999": "888999",   //权限不足
        /**处理失败 */
        "MSG_CODE_999999": "999999",   //处理失败
        /**处理失败 */
        "MSG_CODE_300000": "300000",   //服务不支持
    	
    	/* ================= 业务状态常量 ================ */
    		
        /* =================审核状态================= */
        /** 未提交 */
        "CONFIRM_STATUS_00": "00",

        /** 待审核 */
        "CONFIRM_STATUS_01": "01",

        /** 审核通过 */
        "CONFIRM_STATUS_02": "02",

        /** 审核不通过 */
        "CONFIRM_STATUS_03": "03",

        /** 待生效 */
        "CONFIRM_STATUS_04": "04",

        /** 未设置 */
        "CONFIRM_STATUS_05": "05",

        /** 已失效 */
        "CONFIRM_STATUS_06": "06",

        /* =================批次号状态================= */
        /** 未绑定数据 */
        "BATCHID_STATUS_NOMAL": "01",

        /** 待生效 */
        "BATCHID_STATUS_NOEFFECT": "02",

        /** 已生效 */
        "BATCHID_STATUS_EFFECTED": "03",

        /** 已失效 */
        "BATCHID_STATUS_INVALIDED": "04",

        /* =================处理状态================= */
        /** 未处理 */
        "PROCESS_STATUS_0": "00",

        /** 已处理 */
        "PROCESS_STATUS_1": "01",

        /* =================是否有效状态================= */
        /** 无效 */
        "EFFECTIVE_STATUS_0": "00",

        /** 有效 */
        "EFFECTIVE_STATUS_1": "01",

        /* =================是否删除状态================= */
        /** 未删除 */
        "DELETE_STATUS_0": "00",

        /** 已删除 */
        "DELETE_STATUS_1": "01",

        /* =================清算状态================= */
        /** 待清算 */
        "CLEAR_STATUS_01": "01",

        /** 清算成功 */
        "CLEAR_STATUS_02": "02",

        /** 清算失败 */
        "CLEAR_STATUS_03": "03",

        /* =================财务结算状态================= */
        /** 已申请结款 */
        "STTL_STATUS_01": "01",

        /** 结款待审核 */
        "STTL_STATUS_02": "02",

        /** 已结款 */
        "STTL_STATUS_03": "03",

        /** 结款失败 */
        "STTL_STATUS_04": "04",

        /** 拒绝 */
        "STTL_STATUS_05": "05",

        /* =================变更操作状态================= */
        /** 新增 */
        "OPERATION_STATUS_01": "01",

        /** 修改 */
        "OPERATION_STATUS_02": "02",

        /** 删除 */
        "OPERATION_STATUS_03": "03",

        /* =================对账状态================= */
        /** 待处理 */
        "RECLN_STATUS_01": "01",

        /** 对账成功 */
        "RECLN_STATUS_02": "02",

        /** 对账异常 */
        "RECLN_STATUS_03": "03",

        /* =================对账通知状态================= */
        /** 未同步 */
        "RETURN_C2C_STATUS_01": "01",

        /** 已发送 */
        "RETURN_C2C_STATUS_02": "02",

        /** 同步成功 */
        "RETURN_C2C_STATUS_03": "03",

        /** 同步失败 */
        "RETURN_C2C_STATUS_04": "04",

        /* =================备付金状态================= */
        /** 冻结待审核 */
        "STOCK_STATUS_01": "01",

        /** 已冻结 */
        "STOCK_STATUS_02": "02",

        /** 解冻待审核 */
        "STOCK_STATUS_03": "03",

        /** 已解冻 */
        "STOCK_STATUS_04": "04",

        /** 冻结拒绝 */
        "STOCK_STATUS_05": "05",

        /** 解冻拒绝 */
        "STOCK_STATUS_06": "06",

        /** 未冻结 */
        "STOCK_STATUS_07": "07",

        /* =================账期类型================= */
        /** 账期按天 */
        "STORE_SETTLEMENT_PERIOD_01": "01",

        /** 账期一周 */
        "STORE_SETTLEMENT_PERIOD_02": "02",

        /** 账期半月 */
        "STORE_SETTLEMENT_PERIOD_03": "03",

        /** 账期一月 */
        "STORE_SETTLEMENT_PERIOD_04": "04",

        /* =================结算周期状态================= */
        /** 已失效 */
        "STORE_SETTLEMENT_PERIOD_EFFECTIVE_STATUS_INVALID": "01",

        /** 已生效 */
        "STORE_SETTLEMENT_PERIOD_EFFECTIVE_STATUS_EFFECTIVE": "02",

        /** 有效 */
        "STORE_SETTLEMENT_PERIOD_EFFECTIVE_STATUS_WAIT_EFFECTIVE": "03",

        /* =================结算单下发状态================= */
        /** 待下发 */
        "SETTLEMENT_STATEMENT_ISSUE_STATUS_01": "01",

        /** 下发待确认 */
        "SETTLEMENT_STATEMENT_ISSUE_STATUS_02": "02",

        /** 下发已确认 */
        "SETTLEMENT_STATEMENT_ISSUE_STATUS_03": "03",

        /* =================支付渠道================= */
        /** 微信支付 */
        "CHANNEL_WECHAT": "004",
        
        "CHANNEL_WECHAT_ZH_CN":"微信支付",
        
        /** 微信支付APP */
        "CHANNEL_WECHAT_APP": "0041",
        
        "CHANNEL_WECHAT_APP_ZH_CN":"微信支付APP",
        
        /** 翼支付 */
        "CHANNEL_WING": "002",
        
        "CHANNEL_WING_ZH_CN":"翼支付",
        
        /** 国付宝 */
        "CHANNEL_CHINAPAY": "003",
        
        "CHANNEL_CHINAPAY_ZH_CN":"国付宝",
        
        /* =================翼支付渠道业务类型================= */
        /** 支付 */
        "PAYMENT_WING_01": "01",

        /** 退款 */
        "PAYMENT_WING_02": "02",

        /* =================结算单明细 类型================= */
        /** 订单金额 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_DDJE": "01",

        /** 优惠 - 供销优惠券 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_YH_GXYHQ": "02",

        /** 优惠 - 供销红包 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_YH_GXHB": "03",

        /** 优惠 - 平台活动 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_YH_PTHD": "04",

        /** 优惠 - 店铺优惠券 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_YH_DPYHQ": "05",

        /** 优惠 - 店铺红包 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_YH_DPHB": "06",

        /** 优惠 - 店铺活动 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_YH_DPHD": "07",

        /** 使用费 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_SYF": "08",

        /** 佣金 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_YJ": "09",

        /** 退款 - 现金 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_TK_XJ": "10",

        /** 退款 - 电子券 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_TK_DZQ": "11",

        /** 退款 - 供销红包 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_TK_GXHB": "12",

        /** 退款 - 店铺红包 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_TK_DPHB": "13",

        /** 退款 - 使用费 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_TK_SYF": "14",

        /** 退款 - 佣金 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_TK_YJ": "15",

        /** 通道费 */
        "FSS_SETTLEMENT_STATEMENT_DETAIL_BUSI_TYPE_TDF": "13",

        _STATUS_TEXT: {
            "审核状态": {
                "00": "未提交",
                "01": "待审核",
                "02": "审核通过",
                "03": "审核不通过",
                "04": "待生效",
                "05": "未设置",
                "06": "已失效"
            }, 
            "批次号状态": {
                "01": "未绑定数据",
                "02": "待生效",
                "03": "已生效",
                "04": "已失效"
            }, 
            "处理状态": {
                "00": "未处理",
                "01": "已处理"
            }, 
            "是否有效状态": {
                "00": "无效",
                "01": "有效"
            }, 
            "是否删除状态": {
                "00": "未删除",
                "01": "已删除"
            }, 
            "清算状态": {
                "01": "待清算",
                "02": "清算成功",
                "03": "清算失败"
            }, 
            "财务结算状态": {
                "01": "已申请结款",
                "02": "结款待审核",
                "03": "已结款",
                "04": "结款失败",
                "05": "拒绝"
            }, 
            "变更操作状态": {
                "01": "新增",
                "02": "修改",
                "03": "删除"
            }, 
            "对账状态": {
                "01": "待处理",
                "02": "对账成功",
                "03": "对账异常"
            }, 
            "对账通知状态": {
                "01": "未同步",
                "02": "已发送",
                "03": "同步成功",
                "04": "同步失败"
            }, 
            "备付金状态": {
                "01": "冻结待审核",
                "02": "已冻结",
                "03": "解冻待审核",
                "04": "已解冻",
                "05": "冻结拒绝",
                "06": "解冻拒绝",
                "07": "未冻结"
            }, 
            "账期类型": {
                "01": "账期按天",
                "02": "账期一周",
                "03": "账期半月",
                "04": "账期一月"
            }, 
            "结算周期状态": {
                "01": "已失效",
                "02": "已生效",
                "03": "有效"
            }, 
            "结算单下发状态": {
                "01": "待下发",
                "02": "下发待确认",
                "03": "下发已确认"
            }, 
            "支付渠道": {
                "004": "微信支付",
                "002": "翼支付",
                "003": "国付宝",
                "0041": "微信APP"
            }, 
            "翼支付渠道业务类型": {
                "01": "支付",
                "02": "退款"
            }, 
            "结算单明细 类型": {
                "01": "订单金额",
                "02": "优惠 - 供销优惠券",
                "03": "优惠 - 供销红包",
                "04": "优惠 - 平台活动",
                "05": "优惠 - 店铺优惠券",
                "06": "优惠 - 店铺红包",
                "07": "优惠 - 店铺活动",
                "08": "使用费",
                "09": "佣金",
                "10": "退款 - 现金",
                "11": "退款 - 电子券",
                "12": "退款 - 供销红包",
                "13": "退款 - 店铺红包",
                "14": "退款 - 使用费",
                "15": "退款 - 佣金",
                "13": "通道费"
            }
        },

        // 获取状态中文描述，用法事例：consts.CODE.getText('支付渠道', status);
        getText: function(type, status){
            var text = status;
            if(consts.CODE._STATUS_TEXT[type]){
                text = consts.CODE._STATUS_TEXT[type][status];
            }
            return text;
        }
        
    }
    
});