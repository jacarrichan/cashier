/*
Navicat MySQL Data Transfer

Source Server         : 10.1.102.155
Source Server Version : 50706
Source Host           : 10.1.102.155:3306
Source Database       : gxyj_cashier_db

Target Server Type    : MYSQL
Target Server Version : 50706
File Encoding         : 65001

Date: 2017-07-11 15:08:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for csr_webpage_model_cfg
-- ----------------------------
DROP TABLE IF EXISTS csr_webpage_model_cfg;
CREATE TABLE `csr_webpage_model_cfg` (
  `row_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '记录主键',
  `page_name` VARCHAR(30) DEFAULT NULL COMMENT '页面名称',
  `introduction` VARCHAR(200) DEFAULT NULL COMMENT '页面说明',
  `channel_id` INT(11) DEFAULT NULL COMMENT '业务渠道id',
  `channel_name` VARCHAR(30) DEFAULT NULL COMMENT '业务渠道名称',
  `template_id` INT(11) DEFAULT NULL COMMENT '模板ID',
  `template_name` VARCHAR(30) DEFAULT NULL COMMENT '模板名称',
  `terminal` CHAR(2) DEFAULT NULL COMMENT '适应终端:00 PC/01 app /02 微信',
  `defautl_webpage` CHAR(1) DEFAULT '0' COMMENT '是否默认页面 0 默认 1 否',
  `open_flag` TINYINT(4) DEFAULT '0' COMMENT '页面启用标志：0 未启用 1 已启用',
  `page_address` VARCHAR(50) DEFAULT NULL COMMENT '页面地址',
  `created_by` VARCHAR(32) DEFAULT 'sys' COMMENT '记录创建者 不可修改',
  `created_date` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `last_updt_date` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  `last_updt_by` VARCHAR(32) DEFAULT 'sys' COMMENT '记录最后更新人',
  `version` TINYINT(4) DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) ENGINE=INNODB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8 COMMENT='收银台页面配置表';




-- ----------------------------
-- Table structure for csr_mall_model_cfg
-- ----------------------------
DROP TABLE IF EXISTS csr_mall_model_cfg;
CREATE TABLE `csr_mall_model_cfg` (
  `row_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '记录主键',
  `mall_id` VARCHAR(10) DEFAULT NULL COMMENT '平台ID',
  `mall_name` VARCHAR(50) DEFAULT NULL COMMENT '平台名称',
  `channel_name` VARCHAR(30) DEFAULT NULL COMMENT '渠道名称',
  `channel_id` INT(11) DEFAULT NULL COMMENT '业务渠道ID',
  `pay_channel` VARCHAR(200) DEFAULT NULL COMMENT '支付渠道:01支付宝/02微信/03翼支付/04国付宝(多选用，分隔)',
  `pay_channel_name` VARCHAR(300) DEFAULT NULL COMMENT '支付渠道名称',
  `webpage_name` VARCHAR(30) DEFAULT NULL COMMENT '页面模板名称',
  `webpage_id` VARCHAR(20) DEFAULT NULL COMMENT '页面模板id',
  `model_url` VARCHAR(50) DEFAULT NULL COMMENT '模板地址',
  `terminal` CHAR(100) DEFAULT NULL COMMENT '终端 :00 PC 01移动',
  `open_flag` TINYINT(4) DEFAULT NULL COMMENT '地方平台启用标志：0 未启用 1 已启用',
  `created_by` VARCHAR(32) NOT NULL DEFAULT 'sys' COMMENT '记录创建者',
  `created_date` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `last_updt_date` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  `last_updt_by` VARCHAR(32) NOT NULL DEFAULT 'sys' COMMENT '记录最后更新人',
  `version` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) ENGINE=INNODB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8 COMMENT='地方平台配置表';


-- ----------------------------
-- Table structure for csr_pay_tempalate
-- ----------------------------
DROP TABLE IF EXISTS csr_pay_tempalate;
CREATE TABLE csr_pay_tempalate (
  row_id int(11) NOT NULL AUTO_INCREMENT COMMENT '记录主键',
  template_name varchar(30) DEFAULT NULL COMMENT '模板设计名称',
  template_url varchar(50) DEFAULT NULL COMMENT '模板地址',
  terminal char(2) DEFAULT NULL COMMENT '适应终端:00 PC/01 app /02 微信',
  open_flag tinyint(4) DEFAULT '0' COMMENT '模板设计启用标志：0 未启用 1 已启用',
  created_by varchar(32) NOT NULL DEFAULT 'sys' COMMENT '记录创建者 不可修改',
  created_date timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录创建时间 不可修改',
  last_updt_date timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  last_updt_by varchar(32) NOT NULL DEFAULT 'sys' COMMENT '记录最后更新人',
  version tinyint(4) NOT NULL COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收银台支付模板信息表';



/* 业务渠道配置信息表 */
DROP TABLE IF EXISTS csr_busi_channel;

CREATE TABLE csr_busi_channel (
  row_id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  channel_code varchar(10) NOT NULL COMMENT '业务渠道识别码',
  channel_name varchar(70) NOT NULL COMMENT '业务渠道名称',
  using_date char(20) DEFAULT NULL COMMENT '启用/关闭日期',
  using_status tinyint(4) DEFAULT '0' COMMENT '启用状态:0未启用,1启用,2维护',
  app_id varchar(100) NOT NULL COMMENT '应用id[统一收银台分配给业务渠道]',
  app_key varchar(100) NOT NULL COMMENT '应用key[统一收银台分配给业务渠道]',
  created_by varchar(32) DEFAULT 'sys' COMMENT '创建人',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  last_updt_by varchar(32) DEFAULT 'sys' COMMENT '修改人',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务渠道配置信息表';

/* 业务渠道维护信息表 */
DROP TABLE IF EXISTS csr_busi_chnnl_vind;

CREATE TABLE csr_busi_chnnl_vind (
  row_id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  channel_id int(11) NOT NULL COMMENT '业务渠道Id',
  proc_state char(2) NOT NULL COMMENT '处理状态:00处理中,01处理完成',
  begin_date char(20) NOT NULL COMMENT '维护开始时间',
  end_date char(20) DEFAULT NULL COMMENT '维护结束时间',
  closed_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '关闭时间',
  change_type tinyint(4) DEFAULT '0' COMMENT '变更类型:0:开启:1关闭;2维护',
  vind_explain varchar(256) DEFAULT NULL COMMENT '状态说明：关闭原因，维护原因',
  emails_to varchar(300) DEFAULT NULL COMMENT '维护信息通知目标邮箱',
  created_by varchar(32) DEFAULT 'sys' COMMENT '创建人',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  last_updt_by varchar(32) DEFAULT 'sys' COMMENT '修改人',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务渠道维护信息表';

/* 支付接口地址配置表 */
DROP TABLE IF EXISTS csr_interfaces_url;
CREATE TABLE `csr_interfaces_url` (
  `row_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `interface_url` varchar(256) NOT NULL COMMENT '接口地址',
  `interface_name` varchar(100) NOT NULL COMMENT '接口名称',
  `interface_code` varchar(100) NOT NULL COMMENT '接口代码',
  `srv_file_path` varchar(255) DEFAULT NULL COMMENT '服务器文件路径',
  `interface_explain` varchar(200) NOT NULL COMMENT '接口说明',
  `interface_status` varchar(2) DEFAULT '0' COMMENT '接口地址状态：0关闭，1启用，2删除',
  `payment_channel_id` int(11) NOT NULL COMMENT '支付渠道ID',
  `created_by` varchar(32) DEFAULT 'sys' COMMENT '创建人',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_updt_by` varchar(32) DEFAULT 'sys' COMMENT '修改人',
  `last_updt_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `version` tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COMMENT='支付接口地址配置表';

/* 收银台参数表 */
DROP TABLE IF EXISTS csr_param_settings;

CREATE TABLE csr_param_settings (
  row_id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  param_code varchar(30) NOT NULL COMMENT '参数代码',
  param_name varchar(60) DEFAULT NULL COMMENT '参数名称',
  param_value varchar(256) NOT NULL COMMENT '参数值',
  param_desc varchar(256) DEFAULT NULL COMMENT '参数说明',
  param_type tinyint(4) DEFAULT '0' COMMENT '参数类型 0系统参数,1 业务参数',
  val_flag tinyint(4) DEFAULT '0' COMMENT '有效标示 0无效,1 有效',
  created_by varchar(32) DEFAULT 'sys' COMMENT '创建人',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  last_updt_by varchar(32) DEFAULT 'sys' COMMENT '修改人',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收银台参数表';

/* 支付渠道维护信息表 */
DROP TABLE IF EXISTS csr_pay_chnnl_vind;

CREATE TABLE csr_pay_chnnl_vind (
  row_id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  channel_id int(11) NOT NULL COMMENT '支付渠道Id',
  proc_state char(2) NOT NULL COMMENT '处理状态:00处理中,01处理完成',
  change_type tinyint(4) DEFAULT '0' COMMENT '变更类型:0:开启:1关闭;2维护',
  begin_date char(20) NOT NULL COMMENT '维护开始时间',
  end_date char(20) DEFAULT NULL COMMENT '维护结束时间',
  closed_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '关闭时间',
  vind_explain varchar(256) DEFAULT NULL COMMENT '状态说明：关闭原因，维护原因',
  emails_to varchar(300) DEFAULT NULL COMMENT '维护信息通知目标邮箱',
  created_by varchar(32) DEFAULT 'sys' COMMENT '创建人',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  last_updt_by varchar(32) DEFAULT 'sys' COMMENT '修改人',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='支付渠道维护信息表';

/* 支付渠道配置信息表 */
DROP TABLE IF EXISTS csr_payment_channel;
CREATE TABLE `csr_payment_channel` (
  `row_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `channel_code` varchar(10) NOT NULL COMMENT '业务渠道识别码',
  `channel_name` varchar(70) NOT NULL COMMENT '业务渠道名称',
  `using_date` char(20) DEFAULT NULL COMMENT '启用/关闭日期',
  `using_status` tinyint(4) DEFAULT '0' COMMENT '启用状态:0未启用,1启用,2维护',
  `channel_platform` char(2) DEFAULT NULL COMMENT '支付终端:01:PC,02:WAP,03:APP,04其它',
  `merch_account` varchar(20) DEFAULT NULL COMMENT '商户账户',
  `merch_acct_pwd` varchar(255) DEFAULT NULL COMMENT '商户账户密码',
  `merchant_id` varchar(100) DEFAULT NULL COMMENT '支付渠道分配的商户ID',
  `app_id` varchar(100) DEFAULT NULL COMMENT '应用id[支付渠道分配给统一收银台]',
  `private_key` text COMMENT '应用key[支付渠道分配给统一收银台]',
  `public_key` text COMMENT '支付渠道公钥',
  `channel_type` varchar(3) DEFAULT '00' COMMENT '支付渠道类型:00其它，01个人网银，02企业网银',
  `channel_logo` varchar(100) DEFAULT NULL COMMENT '支付渠道logo',
  `ajax_url` varchar(200) DEFAULT NULL COMMENT '支付渠道对应的controller',
  `created_by` varchar(32) DEFAULT 'sys' COMMENT '创建人',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_updt_by` varchar(32) DEFAULT 'sys' COMMENT '修改人',
  `last_updt_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `version` tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='支付渠道配置信息表';

-- -------------------------------------------
-- Table structure for csr_wechat_recn_cl
-- 微信对账汇总账单表
-- --------------------------------------------
DROP TABLE IF EXISTS csr_wechat_recn_cl;
CREATE TABLE csr_wechat_recn_cl (
  row_id int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ROW_ID',
  check_date varchar(10) NOT NULL COMMENT '对账日期',
  proc_state char(2) NOT NULL DEFAULT '0' COMMENT '处理状态',
  trans_ttl_cnt int(11) DEFAULT NULL COMMENT '总交易单数',
  trans_ttl_amt decimal(18,2) DEFAULT NULL COMMENT '总交易额',
  refund_ttl_amt decimal(18,2) DEFAULT NULL COMMENT '总退款金额',
  refund_pck_ttl_amt decimal(18,2) DEFAULT NULL COMMENT '总企业红包退款金额',
  charge_ttl_amt decimal(8,2) DEFAULT NULL COMMENT '手续费总金额',
  recon_file_id int(11) DEFAULT NULL COMMENT ' 对账文件记录ID',
  created_by varchar(32) DEFAULT 'sys' COMMENT '创建人',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  last_updt_by varchar(32) DEFAULT 'sys' COMMENT '修改人',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8  COMMENT='微信对账汇总账单表';

-- -------------------------------------------
-- Table structure for csr_wechat_recn_cl
-- 微信对账账单明细表
-- --------------------------------------------
DROP TABLE IF EXISTS csr_wechat_recn_lt;
CREATE TABLE csr_wechat_recn_lt (
  row_id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ROW_ID',
  recnl_cl_id int(10) DEFAULT NULL COMMENT '微信汇总记录ID',
  proc_state char(2) NOT NULL DEFAULT '0' COMMENT '对账处理状态',
  check_date varchar(10) NOT NULL COMMENT '对账日期',
  trans_date varchar(20) NOT NULL COMMENT '交易时间',
  public_user_id varchar(20) DEFAULT NULL COMMENT '公众账号ID',
  merch_id varchar(10) DEFAULT NULL COMMENT '商户号',
  sub_merch_id varchar(10) DEFAULT NULL COMMENT '子商户号',
  mac_id varchar(10) DEFAULT NULL COMMENT '设备号',
  wx_order_no varchar(32) DEFAULT NULL COMMENT '微信订单号',
  order_no varchar(32) DEFAULT NULL COMMENT '商户订单号',
  user_id varchar(30) DEFAULT NULL COMMENT '用户标识',
  trans_type varchar(10) DEFAULT NULL COMMENT '交易类型',
  trans_status varchar(8) DEFAULT NULL COMMENT '交易状态',
  payer_bank_code varchar(14) DEFAULT NULL COMMENT '付款银行',
  cny char(3) DEFAULT NULL COMMENT '货币种类',
  trans_amt decimal(18,2) DEFAULT NULL COMMENT '总金额',
  trans_pkg_amt decimal(6,2) DEFAULT NULL COMMENT '企业红包金额',
  wx_refund_no varchar(32) DEFAULT NULL COMMENT '微信退款单号',
  refund_no varchar(64) DEFAULT NULL COMMENT '商户退款单号',
  refund_amt decimal(6,2) DEFAULT NULL COMMENT '退款金额',
  refund_pkg_amt decimal(6,2) DEFAULT NULL COMMENT '企业红包退款金额',
  refund_type varchar(10) DEFAULT NULL COMMENT '退款类型',
  refund_status varchar(10) DEFAULT NULL COMMENT '退款状态',
  goods_name varchar(32) DEFAULT NULL COMMENT '商品名称',
  merch_name varchar(60) DEFAULT NULL COMMENT '商品名称',
  charge_amt decimal(8,2) DEFAULT NULL COMMENT '手续费',
  charge_rate varchar(8) DEFAULT NULL COMMENT '费率-格式###.####%',
  created_by varchar(32) DEFAULT 'sys' COMMENT '创建人',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  last_updt_by varchar(32) DEFAULT 'sys' COMMENT '修改人',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8 COMMENT='微信对账账单明细表';

-- -------------------------------------------
-- Table structure for csr_recon_file
-- 对账文件下载状态表
-- --------------------------------------------
DROP TABLE IF EXISTS csr_recon_file;
CREATE TABLE csr_recon_file (
  row_id int(11) NOT NULL AUTO_INCREMENT COMMENT 'ROW_ID',
  channel_id int(11) NOT NULL COMMENT '支付渠道ID',
  channel_code varchar(10) NOT NULL COMMENT '渠道代码',
  recon_date char(10) NOT NULL COMMENT '对账日期',
  data_sts tinyint(1) NOT NULL DEFAULT '0' COMMENT '对账文件状态',
  data_file varchar(255) NOT NULL COMMENT '数据文件',
  srv_ip char(16) DEFAULT NULL COMMENT '数据文件所在服务器IP',
  proc_state char(2) NOT NULL DEFAULT '0' COMMENT '处理状态',
  created_by varchar(32) DEFAULT 'sys' COMMENT '创建人',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  last_updt_by varchar(32) DEFAULT 'sys' COMMENT '修改人',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='对账文件下载状态表';

-- -------------------------------------------
-- Table structure for csr_refund_order
-- 退款交易表
-- --------------------------------------------
DROP TABLE IF EXISTS csr_refund_order;
CREATE TABLE csr_refund_order (
  row_id int(20) NOT NULL AUTO_INCREMENT COMMENT 'ROW_ID',
  trans_id varchar(30) NOT NULL COMMENT '交易支付序号-收银台交易唯一标识号',
  refund_id varchar(32) NOT NULL COMMENT '退款订单编号',
  orgn_order_id varchar(32) NOT NULL COMMENT '原订单号',
  channel_id int(11) NOT NULL COMMENT '业务渠道ID',
  channel_cd char(6) DEFAULT NULL COMMENT '业务渠道标识',
  client_ip varchar(15) DEFAULT NULL COMMENT '客户IP',
  refund_amt decimal(18,2) NOT NULL COMMENT '退款金额',
  orgn_trans_amt decimal(18,2) DEFAULT NULL COMMENT '原支付金额',
  refund_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '退款申请时间',
  proc_state char(2) NOT NULL DEFAULT '00' COMMENT '交易处理状态',
  err_flag tinyint(4) NOT NULL DEFAULT '0' COMMENT '异常标志 0-正常  1-异常',
  recon_flag varchar(2) DEFAULT NULL COMMENT '对账状态',
  remark varchar(256) DEFAULT NULL COMMENT '备注，预留字段',
  created_by varchar(32) NOT NULL DEFAULT 'Sys' COMMENT '记录创建者',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  last_updt_by varchar(32) NOT NULL DEFAULT 'Sys' COMMENT '记录最后更新人',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
  UNIQUE KEY `trans_id` (`trans_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='退款交易表';

-- -------------------------------------------
-- Table structure for csr_recon_result_cl
-- 对账结果交易汇总表
-- --------------------------------------------
DROP TABLE IF EXISTS csr_recon_result_cl;
CREATE TABLE csr_recon_result_cl (
  row_id int(11) NOT NULL AUTO_INCREMENT COMMENT 'ROW_ID',
  channel_code varchar(10) NOT NULL COMMENT '业务渠道识别码',
  channel_name varchar(70) NOT NULL COMMENT '业务渠道名称',
  begin_chk_date varchar(15) NOT NULL COMMENT '对账开始时间',
  end_chk_date varchar(15) NOT NULL COMMENT '对账结束时间',
  refund_sum_amt decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '退款总金额',
  pay_sum_amt decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '支付总金额',
  pay_total_cnt int(11) NOT NULL DEFAULT '0' COMMENT '支付总笔数',
  refund_total_cnt int(11) NOT NULL DEFAULT '0' COMMENT '退款总笔数',
  sum_charge_fee decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '手续费总金额',
  check_state char(2) DEFAULT NULL COMMENT '对账状态',
  proc_state char(2) NOT NULL DEFAULT '00' COMMENT '处理状态',
  created_by varchar(32) NOT NULL DEFAULT 'Sys' COMMENT '记录创建者',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  last_updt_by varchar(32) NOT NULL DEFAULT 'Sys' COMMENT '记录最后更新人',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='对账结果交易汇总表';


-- -------------------------------------------
-- Table structure for csr_recon_result_lt
-- 对账结果交易明细表
-- --------------------------------------------
DROP TABLE IF EXISTS csr_recon_result_lt;
CREATE TABLE csr_recon_result_lt (
  row_id int(11) NOT NULL AUTO_INCREMENT COMMENT 'ROW_ID',
  cl_key int(11) DEFAULT NULL COMMENT '汇总表ID',
  pay_recon_key int(11) DEFAULT NULL COMMENT '支付渠道对账结果ID',
  check_date char(10) DEFAULT NULL COMMENT '对账日期',
  trans_id varchar(32) NOT NULL COMMENT '交易支付序号-收银台交易唯一标识号',
  order_no varchar(32) NOT NULL COMMENT '订单号-退款时为原订单号',
  refund_no varchar(32) NOT NULL COMMENT '退款订单编号',
  channel_id int(11) NOT NULL COMMENT '业务渠道ID',
  order_type tinyint(4) NOT NULL COMMENT '订单类型 0-付款订单 1-退款订单',
  channel_code char(6) NOT NULL COMMENT '业务渠道标识',
  trans_amt decimal(18,2) DEFAULT NULL COMMENT '支付金额--退款单为原订单交易金额',
  refund_amt decimal(18,2) NOT NULL COMMENT '退款金额',
  charge_fee decimal(8,2) DEFAULT '0.00' COMMENT '手续费金额',
  begin_date varchar(15) DEFAULT NULL COMMENT '交易起始时间',
  end_date varchar(15) DEFAULT NULL COMMENT '交易结束时间',
  insti_pay_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '支付渠道支付时间',
  insti_proc_cd varchar(32) DEFAULT NULL COMMENT '支付渠道支付状态',
  payer_name varchar(32) DEFAULT NULL COMMENT '付款人名称',
  payer_acct_no varchar(32) DEFAULT NULL COMMENT '付款人账号',
  payer_insti_id int(11) DEFAULT NULL COMMENT '支付渠道ID',
  payer_insti_cd varchar(32) DEFAULT NULL COMMENT '支付渠道号',
  payer_insti_nm varchar(128) DEFAULT NULL COMMENT '支付渠道名称',
  insti_pay_type char(3) DEFAULT NULL COMMENT '支付方式',
  check_state char(2) DEFAULT NULL COMMENT '对账状态',
  created_by varchar(32) NOT NULL DEFAULT 'Sys' COMMENT '记录创建者',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  last_updt_by varchar(32) NOT NULL DEFAULT 'Sys' COMMENT '记录最后更新人',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='对账结果交易明细表';

-- -------------------------------------------
-- Table structure for csr_recln_payment_result
-- 支付渠道对账结果信息表
-- --------------------------------------------
DROP TABLE IF EXISTS csr_recln_payment_result;
CREATE TABLE csr_recln_payment_result (
  row_id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  channel_code varchar(32) NOT NULL COMMENT '支付渠道编码',
  channel_id int(11) NOT NULL COMMENT '支付渠道ID',
  recln_date varchar(10) DEFAULT NULL COMMENT '对账日期',
  proc_state char(2) DEFAULT NULL COMMENT '对账完成状态',
  error_count int(11) DEFAULT '0' COMMENT '对账错误交易笔数',
  table_row_id bigint(20) DEFAULT NULL COMMENT '业务表rowId',
  table_name varchar(32) DEFAULT NULL COMMENT '业务表名称',
  trans_ttl_cnt int(11) DEFAULT '0' COMMENT '支付总笔数',
  refund_ttl_cnt int(11) DEFAULT '0' COMMENT '退款总笔数',
  trans_ttl_amt decimal(18,2) DEFAULT '0.00' COMMENT '支付总金额',
  refund_ttl_amt decimal(18,2) DEFAULT '0.00' COMMENT '退款总金额',
  charge_fee decimal(8,2) DEFAULT '0.00' COMMENT '手续费总额',
  start_recln_date varchar(20) DEFAULT NULL COMMENT '对账开始时间',
  end_recln_date varchar(20) DEFAULT NULL COMMENT '对账结束时间',
  created_by varchar(32) DEFAULT 'sys' COMMENT '创建人',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  last_updt_by varchar(32) DEFAULT 'sys' COMMENT '修改人',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COMMENT='支付渠道对账结果信息表';


/* 国付宝对账返回对账详情 */
drop table if exists csr_gopay_recn_lt;
CREATE TABLE csr_gopay_recn_lt (
  row_id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ROW_ID',
  gopay_order_id varchar(45) NOT NULL COMMENT '国付宝订单号',
  gopay_txn_tm varchar(20) DEFAULT NULL COMMENT '国付宝交易时间',
  mer_order_num varchar(45) NOT NULL COMMENT '商户订单号',
  mer_txn_tm varchar(20) DEFAULT NULL COMMENT '商户交易时间',
  tran_code varchar(20) DEFAULT NULL COMMENT '交易代码',
  txn_amt varchar(32) DEFAULT NULL COMMENT '交易金额',
  biz_sts_cd varchar(30) DEFAULT NULL COMMENT '业务状态码:S-交易成功，P-处理中，F-失败',
  biz_sts_desc varchar(30) DEFAULT NULL COMMENT '业务状态描述:S-交易成功，P-处理中，F-失败',
  finish_tm varchar(20) DEFAULT NULL COMMENT '交易完成时间yyyymmddHHMMSS',
  pay_chn varchar(10) DEFAULT NULL COMMENT '支付渠道取值范围:B2C或B2B',
  stlm_date varchar(20) DEFAULT NULL COMMENT '结算日期:yyyymmdd',
  qry_tran_code varchar(20) NOT NULL comment'查询的交易代码:11 网关支付;12 委托代收;13 虚拟账户充值;21 提现;22 付款到银行;31 付款到国付宝;41 收款交易退款;42 付款交易退款;',
  proc_state varchar(2) NOT NULL DEFAULT '0' COMMENT '对账处理状态',
  created_by varchar(32) DEFAULT 'sys' COMMENT '创建人',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  last_updt_by varchar(32) DEFAULT 'sys' COMMENT '修改人',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8 COMMENT='国付宝对账返回对账详情';

-- -------------------------------------------
-- Table structure for csr_pay_mer_relation
-- 支付渠道商户号和平台对应关系表
-- --------------------------------------------
DROP TABLE IF EXISTS csr_pay_mer_relation;

CREATE TABLE csr_pay_mer_relation
(
   row_id               BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `busi_channel_code` VARCHAR(10) NOT NULL COMMENT '业务渠道识别码',
   channel_code       		VARCHAR(32) COMMENT '支付渠道',
   mall_id			VARCHAR(32) COMMENT '电商平台主键ID',
   mall_name			VARCHAR(64)  COMMENT '平台名称',
  `merch_account` VARCHAR(20) DEFAULT NULL COMMENT '商户账户',
  `merch_acct_pwd` VARCHAR(255) DEFAULT NULL COMMENT '商户账户密码',
  `merchant_id` VARCHAR(100) DEFAULT NULL COMMENT '支付渠道分配的商户ID',
  `app_id` VARCHAR(100) DEFAULT NULL COMMENT '应用id[支付渠道分配给统一收银台]',
  `private_key` TEXT COMMENT '应用key[支付渠道分配给统一收银台]',
  `public_key` TEXT COMMENT '支付渠道公钥',
  
  `created_by` VARCHAR(32) DEFAULT 'sys' COMMENT '创建人',
  `created_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_updt_by` VARCHAR(32) DEFAULT 'sys' COMMENT '修改人',
  `last_updt_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `version` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '版本号',
   PRIMARY KEY (row_id)
)COMMENT = '支付渠道商户号和平台对应关系表';


/* 翼支付对账返回对账详情 */
drop table if exists csr_epay_recn_lt;
CREATE TABLE csr_epay_recn_lt (
  row_id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ROW_ID',
  epay_order_id varchar(45) NOT NULL COMMENT '翼支付订单号',
  trans_amt  decimal(15,6) DEFAULT NULL COMMENT '交易金额',
  fee_amt decimal(15,6) DEFAULT NULL COMMENT '手续费',
  trans_type char(1) DEFAULT NULL comment'交易类型:1支付,2退款',
  acct_date varchar(20) DEFAULT NULL comment'对账日期',
  order_id varchar(45) NOT NULL COMMENT '商户订单号[业务渠道的]', 
  proc_state varchar(2) NOT NULL DEFAULT '0' COMMENT '对账处理状态',
  created_by varchar(32) DEFAULT 'sys' COMMENT '创建人',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  last_updt_by varchar(32) DEFAULT 'sys' COMMENT '修改人',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8 COMMENT='翼支付对账返回对账详情';





-- -------------------------------------------
-- Table structure for csr_alipay_recn_cl
-- 支付宝对账汇总表表
-- --------------------------------------------

DROP TABLE IF EXISTS `csr_alipay_recn_cl`;

CREATE TABLE `csr_alipay_recn_cl` (
  `row_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ROW_ID',
  `check_date` varchar(10) NOT NULL COMMENT '对账日期',
  `proc_state` char(2) NOT NULL DEFAULT '00' COMMENT '对账处理状态',
  `store_id` varchar(32) DEFAULT NULL COMMENT '门店编号',
  `store_name` varchar(32) DEFAULT NULL COMMENT '门店名称',
  `trans_ttl_cnt` int(11) DEFAULT NULL COMMENT '交易订单总笔数',
  `refund_ttl_cnt` int(11) DEFAULT NULL COMMENT '退款订单总笔数',
  `trans_ttl_amt` decimal(18,2) DEFAULT NULL COMMENT '订单金额（元）',
   refund_ttl_amt	decimal(18,2) DEFAULT 0.00	COMMENT '退款总金额',
  `mer_real_amt` decimal(18,2) DEFAULT NULL COMMENT '商家实收（元）',
  `alipay_discount` decimal(18,2) DEFAULT NULL COMMENT '支付宝优惠（元）',
  `mer_discount` decimal(18,2) DEFAULT NULL COMMENT '商家优惠（元）',
  `card_pay_amt` decimal(18,2) DEFAULT NULL COMMENT '卡消费金额（元）',
  `service_amt` decimal(18,2) DEFAULT NULL COMMENT '服务费（元）',
  `profit_amt` decimal(18,2) DEFAULT NULL COMMENT '分润（元）',
  `recv_real_amt` decimal(18,2) DEFAULT NULL COMMENT '实收净额（元）',
  `recon_file_id` int(11) DEFAULT NULL COMMENT ' 对账文件记录ID',
  `created_by` varchar(32) DEFAULT 'sys' COMMENT '创建人',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_updt_by` varchar(32) DEFAULT 'sys' COMMENT '修改人',
  `last_updt_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `version` tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) COMMENT = '支付宝对账汇总表';

/*Data for the table `csr_alipay_recn_cl` */

/*Table structure for table `csr_alipay_recn_lt` */
-- -------------------------------------------
-- Table structure for csr_alipay_recn_lt
-- 支付宝对账明细表
-- --------------------------------------------

DROP TABLE IF EXISTS `csr_alipay_recn_lt`;

CREATE TABLE `csr_alipay_recn_lt` (
  `row_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ROW_ID',
  `recnl_cl_id` int(10) DEFAULT NULL COMMENT '汇总记录ID',
  `proc_state` char(2) NOT NULL DEFAULT '00' COMMENT '对账处理状态',
  `check_date` varchar(10) NOT NULL COMMENT '对账日期',
  `trans_date` varchar(20) NOT NULL COMMENT '交易时间',
  `alipay_trans_no` varchar(30) DEFAULT NULL COMMENT '支付宝交易号',
  `order_no` varchar(32) DEFAULT NULL COMMENT '商户订单号',
  `merch_id` varchar(10) DEFAULT NULL COMMENT '商户号',
  `busi_type` varchar(10) DEFAULT NULL COMMENT '业务类型',
  `goods_name` varchar(32) DEFAULT NULL COMMENT '商品名称',
  `alipay_create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `alipay_finish_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '完成时间',
  `store_id` varchar(32) DEFAULT NULL COMMENT '门店编号',
  `store_name` varchar(128) DEFAULT NULL COMMENT '门店名称',
  `operater` varchar(32) DEFAULT NULL COMMENT '操作员',
  `terminal_no` varchar(32) DEFAULT NULL COMMENT '终端号',
  `oth_account` varchar(32) DEFAULT NULL COMMENT '对方账户',
  `trans_ttl_amt` decimal(18,2) DEFAULT NULL COMMENT '订单金额（元）',
  `mer_real_amt` decimal(18,2) DEFAULT NULL COMMENT '商家实收（元）',
  `alipay_red_amt` decimal(18,2) DEFAULT NULL COMMENT '支付宝红包（元）',
  `jf_pay_amt` decimal(18,2) DEFAULT NULL COMMENT '集分宝（元）',
  `alipay_discount` decimal(18,2) DEFAULT NULL COMMENT '支付宝优惠（元）',
  `mer_discount` decimal(18,2) DEFAULT NULL COMMENT '商家优惠（元）',
  `coupon_amt` decimal(18,2) DEFAULT NULL COMMENT '券核销金额（元）',
  `coupon_name` varchar(128) DEFAULT NULL COMMENT '券名称',
  `mer_red_amt` decimal(18,2) DEFAULT NULL COMMENT '商家红包消费金额（元）',
  `card_pay_amt` decimal(18,2) DEFAULT NULL COMMENT '卡消费金额（元）',
  `refund_batch_no` varchar(30) DEFAULT NULL COMMENT '退款批次号/请求号',
  `service_amt` decimal(18,2) DEFAULT NULL COMMENT '服务费（元）',
  `profit_amt` decimal(18,2) DEFAULT NULL COMMENT '分润（元）',
  `remark` varchar(128) DEFAULT NULL COMMENT '备注',
  `trans_type` varchar(10) DEFAULT NULL COMMENT '交易类型',
  `trans_status` varchar(8) DEFAULT NULL COMMENT '交易状态',
  `trans_amt` decimal(18,2) DEFAULT NULL COMMENT '总金额',
  `refund_no` varchar(32) DEFAULT NULL COMMENT '商户退款单号',
  `refund_amt` decimal(6,2) DEFAULT NULL COMMENT '退款金额',
  `refund_type` varchar(10) DEFAULT NULL COMMENT '退款类型',
  `refund_status` varchar(10) DEFAULT NULL COMMENT '退款状态',
  `created_by` varchar(32) DEFAULT 'sys' COMMENT '创建人',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_updt_by` varchar(32) DEFAULT 'sys' COMMENT '修改人',
  `last_updt_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `version` tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
)  COMMENT = '支付宝对账明细表';

-- -------------------------------------------
-- Table structure for csr_epay_recn_lt
-- 翼支付对账明细表
-- --------------------------------------------

DROP TABLE IF EXISTS `csr_epay_recn_lt`;

CREATE TABLE `csr_epay_recn_lt` (
  `row_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ROW_ID',
  `epay_order_id` varchar(45) NOT NULL COMMENT '翼支付订单号',
  `trans_amt` decimal(15,6) DEFAULT NULL COMMENT '交易金额',
  `fee_amt` decimal(15,6) DEFAULT NULL COMMENT '手续费',
  `trans_type` char(1) DEFAULT NULL COMMENT '交易类型:1支付,2退货',
  `acct_date` varchar(20) DEFAULT NULL COMMENT '对账日期',
  `order_id` varchar(45) NOT NULL COMMENT '商户订单号[业务渠道的]',
  `proc_state` char(2) NOT NULL DEFAULT '0' COMMENT '对账处理状态',
  `created_by` varchar(32) DEFAULT 'sys' COMMENT '创建人',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_updt_by` varchar(32) DEFAULT 'sys' COMMENT '修改人',
  `last_updt_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `version` tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8 COMMENT='翼支付对账明细表';


-- -------------------------------------------
-- Table structure for csr_gopay_recn_lt
-- 国付宝对账明细表
-- --------------------------------------------

DROP TABLE IF EXISTS `csr_gopay_recn_lt`;

CREATE TABLE `csr_gopay_recn_lt` (
  `row_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ROW_ID',
  `gopay_order_id` varchar(45) NOT NULL COMMENT '国付宝订单号',
  `gopay_txn_tm` varchar(20) DEFAULT NULL COMMENT '国付宝交易时间',
  `mer_order_num` varchar(45) NOT NULL COMMENT '商户订单号',
  `mer_txn_tm` varchar(20) DEFAULT NULL COMMENT '商户交易时间',
  `tran_code` varchar(20) DEFAULT NULL COMMENT '交易代码',
  `txn_amt` varchar(32) DEFAULT NULL COMMENT '交易金额',
  `biz_sts_cd` varchar(30) DEFAULT NULL COMMENT '业务状态码:S-交易成功，P-处理中，F-失败',
  `biz_sts_desc` varchar(30) DEFAULT NULL COMMENT '业务状态描述:S-交易成功，P-处理中，F-失败',
  `finish_tm` varchar(20) DEFAULT NULL COMMENT '交易完成时间yyyymmddHHMMSS',
  `pay_chn` varchar(10) DEFAULT NULL COMMENT '支付渠道取值范围:B2C或B2B',
  `stlm_date` varchar(20) DEFAULT NULL COMMENT '结算日期:yyyymmdd',
  `qry_tran_code` varchar(20) NOT NULL COMMENT '查询的交易代码:11 网关支付;12 委托代收;13 虚拟账户充值;21 提现;22 付款到银行;31 付款到国付宝;41 收款交易退款;42 付款交易退款;',
  `proc_state` char(2) NOT NULL DEFAULT '0' COMMENT '对账处理状态',
  `created_by` varchar(32) DEFAULT 'sys' COMMENT '创建人',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_updt_by` varchar(32) DEFAULT 'sys' COMMENT '修改人',
  `last_updt_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `version` tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8 COMMENT='国付宝对账明细表';


-- -------------------------------------------
-- Table structure for csr_ifs_message
-- 收银台与商城往来报文表
-- --------------------------------------------

DROP TABLE IF EXISTS `csr_ifs_message`;
CREATE TABLE `csr_ifs_message` (
  `msg_id` VARCHAR(30) NOT NULL COMMENT '消息编号',
  `msg_create_time` VARCHAR(21) DEFAULT NULL COMMENT '消息发送时间',
  `sender` VARCHAR(3) DEFAULT NULL,
  `receiver` VARCHAR(3) DEFAULT NULL,
  `sign` VARCHAR(256) DEFAULT NULL COMMENT '签名',
  `encryption` VARCHAR(10) DEFAULT NULL COMMENT '加密方式',
  `interface_code` VARCHAR(12) DEFAULT NULL COMMENT '接口代码',
  `source` VARCHAR(8) DEFAULT NULL COMMENT '业务渠道',
  `return_url` VARCHAR(256) DEFAULT NULL COMMENT '回调URL',
  `request_ip` VARCHAR(15) DEFAULT NULL COMMENT '发送方IP',
  `rtn_code` VARCHAR(6) DEFAULT NULL COMMENT '返回码 000000 成功 999999失败',
  `rtn_msg` VARCHAR(256) DEFAULT NULL COMMENT '返回信息',
  `msg_data` LONGTEXT COMMENT '报文内容',
  `created_date` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `last_updt_date` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  `version` TINYINT(4) DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`msg_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

-- -------------------------------------------
-- Table structure for csr_mall_info
-- 平台表
-- --------------------------------------------

DROP TABLE IF EXISTS `csr_mall_info`;

CREATE TABLE `csr_mall_info` (
  `row_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mall_id` varchar(32) DEFAULT NULL COMMENT '电商平台主键ID',
  `brch_id` varchar(32) DEFAULT NULL COMMENT '机构ID',
  `mall_name` varchar(64) DEFAULT NULL COMMENT '平台名称',
  `parent_id` varchar(32) DEFAULT NULL COMMENT '上级平台编号',
  `mall_type` char(2) DEFAULT NULL COMMENT '平台类型1=全国平台,2=省平台,3=市平台,4=县平台',
  `area_code` varchar(16) DEFAULT NULL COMMENT '地区编码',
  `create_year` varchar(20) DEFAULT NULL COMMENT '成立时间',
  `company_name` varchar(128) DEFAULT NULL COMMENT '公司名称',
  `company_legal_person` varchar(32) DEFAULT NULL COMMENT '公司法人',
  `bus_limit_date` varchar(20) DEFAULT NULL COMMENT '营业执照有效时间（yyyy-mm-dd）',
  `mall_status` varchar(2) DEFAULT NULL COMMENT '商城状态',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modified_by` varchar(32) DEFAULT NULL COMMENT '修改人',
  `whenmodified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`),
  KEY `csr_mall_info_index_mall_id` (`mall_id`),
  KEY `csr_mall_info_index_parent_id` (`parent_id`),
  KEY `csr_mall_info_index_mall_name` (`mall_name`)
) ENGINE=InnoDB AUTO_INCREMENT=677 DEFAULT CHARSET=utf8 COMMENT='平台表';

-- -------------------------------------------
-- Table structure for csr_message
-- 收银台与支付渠道往来报文表
-- --------------------------------------------
DROP TABLE IF EXISTS `csr_message`;

CREATE TABLE `csr_message` (
  `row_id` int(50) NOT NULL AUTO_INCREMENT COMMENT '记录主键',
  `outin_type` tinyint(5) DEFAULT NULL COMMENT '来往标识 ',
  `msg_id` varchar(50) DEFAULT NULL COMMENT '消息标识号',
  `orgn_msg_id` varchar(50) DEFAULT NULL COMMENT '原消息编号',
  `msg_desc` varchar(100) DEFAULT NULL COMMENT '消息描述',
  `channel_cd` varchar(10) DEFAULT NULL COMMENT '支付渠道',
  `client_ip` varchar(15) DEFAULT NULL COMMENT '客户IP',
  `sign_data` varchar(4096) DEFAULT NULL COMMENT '消息加签串',
  `sign_type` char(3) DEFAULT NULL COMMENT '加签算法类型:',
  `proc_state` char(2) DEFAULT NULL,
  `err_flag` tinyint(2) DEFAULT NULL COMMENT '异常标志 0-正常  1-异常',
  `err_desc` varchar(256) DEFAULT NULL COMMENT '异常描述',
  `sender` varchar(32) DEFAULT NULL COMMENT '消息发送者',
  `recver` varchar(32) DEFAULT NULL COMMENT '消息接收者',
  `msg_data` longtext COMMENT '消息原报文内容',
  `created_by` varchar(32) NOT NULL DEFAULT 'sys' COMMENT '记录创建者',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `last_updt_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  `last_updt_by` varchar(255) NOT NULL DEFAULT 'sys' COMMENT '记录最后更新人',
  `version` tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) ENGINE=InnoDB AUTO_INCREMENT=828 DEFAULT CHARSET=utf8 COMMENT='收银台与支付渠道往来报文表';

-- -------------------------------------------
-- Table structure for csr_message_order_rel
-- 订单与接口报文表的关系表
-- --------------------------------------------
DROP TABLE IF EXISTS `csr_message_order_rel`;

CREATE TABLE `csr_message_order_rel` (
  `row_id` int(11) NOT NULL AUTO_INCREMENT,
  `trans_id` varchar(30) NOT NULL COMMENT '订单流水号',
  `msg_id` varchar(30) NOT NULL COMMENT 'ifs_message主键',
  `status` varchar(3) NOT NULL COMMENT '回调状态，0未推送，1 回调成功 2 回调失败',
  `rtn_url` longtext COMMENT '回调结果通知接口URL',
  `notify_url` longtext COMMENT '回调成功页面URL',
  `created_by` varchar(32) NOT NULL DEFAULT 'sys' COMMENT '记录创建者',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `last_updt_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  `last_updt_by` varchar(255) NOT NULL DEFAULT 'sys' COMMENT '记录最后更新人',
  `version` tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='订单与接口报文表的关系表';

-- -------------------------------------------
-- Table structure for csr_module_function
-- 功能模块配置表
-- --------------------------------------------
DROP TABLE IF EXISTS `csr_module_function`;

CREATE TABLE `csr_module_function` (
  `row_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录主键',
  `code` varchar(20) DEFAULT NULL COMMENT '编码',
  `types` tinyint(4) DEFAULT NULL COMMENT '类型,1模块，2功能',
  `func_desc` varchar(100) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`row_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='功能模块配置表';

-- -------------------------------------------
-- Table structure for csr_order_info
-- 支付订单表
-- --------------------------------------------
DROP TABLE IF EXISTS `csr_order_info`;
CREATE TABLE `csr_order_info` (
  `row_id` INT(20) NOT NULL AUTO_INCREMENT COMMENT '记录主键',
  `trans_id` VARCHAR(30) NOT NULL COMMENT '交易支付序号 ，收银台交易唯一标识号',
  `order_id` VARCHAR(32) NOT NULL COMMENT '订单编号',
  `prod_name` VARCHAR(200) DEFAULT NULL COMMENT '商品名称',
  `terminal` CHAR(4) DEFAULT NULL COMMENT '支付终端:01:PC,02:WAP,03:APP,04其它',
  `order_type` CHAR(2) DEFAULT NULL COMMENT '01:零售订单,02:团购订单,03:秒杀订单,04:限时抢购订单,09:基团购,10:金融产品',
  `channel_id` INT(11) DEFAULT NULL COMMENT '业务渠道ID',
  `channel_cd` CHAR(6) DEFAULT NULL COMMENT '业务渠道标识',
  `mall_id` VARCHAR(10) DEFAULT NULL COMMENT '平台编号 00000000 全国平台',
  `pay_phone` VARCHAR(15) DEFAULT NULL COMMENT '支付手机号',
  `client_ip` VARCHAR(15) DEFAULT NULL COMMENT '客户IP',
  `trans_amt` DECIMAL(18,2) DEFAULT NULL COMMENT '支付金额',
  `charge_fee` DECIMAL(6,2) DEFAULT NULL COMMENT '支付手续费',
  `trans_time` TIMESTAMP NULL DEFAULT NULL COMMENT '交易时间',
  `proc_state` CHAR(2) DEFAULT NULL COMMENT '交易处理状态',
  `err_flag` TINYINT(4) DEFAULT '0' COMMENT '异常标志 0-正常  1-异常',
  `err_desc` VARCHAR(256) DEFAULT NULL COMMENT '异常描述',
  `recon_flag` varchar(2) DEFAULT NULL COMMENT '对账状态',
  `remark` VARCHAR(256) DEFAULT NULL COMMENT '备注，预留字段',
  `created_by` VARCHAR(32) DEFAULT 'sys' COMMENT '记录创建者 不可修改',
  `created_date` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `last_updt_date` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  `last_updt_by` VARCHAR(255) DEFAULT 'sys' COMMENT '记录最后更新人',
  `version` TINYINT(4) DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `trans_id` (`trans_id`),
  KEY `idx_order_order_id_channel` (`order_id`,`channel_cd`),
  KEY `idx_order_trans_id` (`trans_id`),
  KEY `idx_order_order_id` (`order_id`)
) ENGINE=INNODB AUTO_INCREMENT=208 DEFAULT CHARSET=utf8 COMMENT='支付订单表';


-- -------------------------------------------
-- Table structure for csr_pay_template
-- 收银台支付模板信息表
-- --------------------------------------------

DROP TABLE IF EXISTS `csr_pay_template`;

CREATE TABLE `csr_pay_template` (
  `row_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录主键',
  `template_name` varchar(30) DEFAULT NULL COMMENT '模板设计名称',
  `template_url` varchar(50) DEFAULT NULL COMMENT '模板地址',
  `terminal` char(2) DEFAULT NULL COMMENT '适应终端:00 PC/01 app /02 微信',
  `open_flag` tinyint(4) DEFAULT '0' COMMENT '模板设计启用标志：0 未启用 1 已启用',
  `created_by` varchar(32) NOT NULL DEFAULT 'sys' COMMENT '记录创建者 不可修改',
  `created_date` timestamp NULL DEFAULT NULL COMMENT '记录创建时间 不可修改',
  `last_updt_date` timestamp NULL DEFAULT NULL COMMENT '记录最后更新时间',
  `last_updt_by` varchar(32) NOT NULL DEFAULT 'sys' COMMENT '记录最后更新人',
  `version` tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8 COMMENT='收银台支付模板信息表';

-- -------------------------------------------
-- Table structure for csr_payment
-- 订单支付表
-- --------------------------------------------
DROP TABLE IF EXISTS `csr_payment`;

CREATE TABLE `csr_payment` (
  `row_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录主键',
  `trans_id` varchar(30) NOT NULL COMMENT '交易支付序号，收银台交易唯一标识号',
  `refund_flag` tinyint(4) DEFAULT '0' COMMENT '退款标识 0-否，1-是',
  `sync_flag` tinyint(4) DEFAULT '0' COMMENT '同步结果至商城状态  0:初始 1:未同步 2:已同步 3:变更未同步',
  `payer_name` varchar(32) DEFAULT NULL COMMENT '付款人名称',
  `payer_acct_no` varchar(32) DEFAULT NULL COMMENT '付款人账号',
  `payer_insti_no` varchar(32) DEFAULT NULL COMMENT '支付渠道号',
  `payer_insti_nm` varchar(128) DEFAULT NULL COMMENT '支付渠道名称',
  `insti_pay_type` char(10) DEFAULT NULL COMMENT '支付渠道支付类型 B2C-个人网银 B2B-企业网银 OTH-其他类型',
  `sign_data` varchar(256) DEFAULT NULL COMMENT '加签串',
  `sign_type` char(3) DEFAULT NULL COMMENT '加签类型 MD5 RSA-证书公私钥',
  `trans_code` varchar(12) DEFAULT NULL COMMENT '支付渠道交易码',
  `req_timestamp` varchar(256) DEFAULT NULL COMMENT '请求支付渠道时间戳',
  `insti_trans_id` varchar(60) DEFAULT NULL COMMENT '支付渠道支付流水号',
  `insti_resp_cd` varchar(32) DEFAULT NULL COMMENT '支付渠道处理码',
  `insti_rsp_des` varchar(256) DEFAULT NULL COMMENT '支付渠道处理描述',
  `insti_rsp_time` varchar(14) DEFAULT NULL COMMENT '支付渠道处理时间（格式：yyyyMMddHHmmss）',
  `remark` varchar(256) DEFAULT NULL COMMENT '备注，预留字段',
  `created_by` varchar(32) DEFAULT 'sys' COMMENT '记录创建者',
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `last_updt_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  `last_updt_by` varchar(32) DEFAULT 'sys' COMMENT '记录最后更新人',
  `version` tinyint(4) DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`,`trans_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COMMENT='订单支付表';

-- -------------------------------------------
-- Table structure for csr_payment_log
-- 支付日志表
-- --------------------------------------------
DROP TABLE IF EXISTS `csr_payment_log`;

CREATE TABLE `csr_payment_log` (
  `row_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ROW_ID',
  `trans_date` varchar(10) NOT NULL COMMENT '交易日期 格式：yyyy-MM-dd',
  `channel_code` char(6) NOT NULL COMMENT '业务渠道编码',
  `pay_channel_code` varchar(32) NOT NULL COMMENT '支付渠道编码',
  `created_by` varchar(32) DEFAULT 'sys' COMMENT '创建人',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_updt_by` varchar(32) DEFAULT 'sys' COMMENT '修改人',
  `last_updt_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `version` tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='支付日志表';

-- -------------------------------------------
-- Table structure for csr_recln_payment_exce
-- 对账异常交易信息表
-- --------------------------------------------
DROP TABLE IF EXISTS csr_recln_payment_exce;

CREATE TABLE csr_recln_payment_exce (
  row_id int(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  table_row_id int(20) unsigned DEFAULT NULL COMMENT '业务表rowId',
  table_name varchar(128) DEFAULT NULL COMMENT '业务表名称',
  pay_status varchar(32) DEFAULT NULL COMMENT '通道交易状态',
  order_status char(2)   default null comment '订单支付状态',
  charge_fee decimal(10,2) DEFAULT NULL COMMENT '手续费金额',
  currency varchar(10) DEFAULT NULL COMMENT '货币种类',
  trans_id varchar(30) DEFAULT NULL COMMENT '交易参考号',
  order_no varchar(32) DEFAULT NULL COMMENT '商户订单号',
  pay_insti_code varchar(32) DEFAULT NULL COMMENT '支付渠道',
  pay_chnnl_id			int        not null comment '支付渠道ID',
  insti_rsp_time        varchar(14) NULL DEFAULT null COMMENT '支付渠道应答时间 格式：yyyyMMddHHmmss',
  insti_trans_id  varchar(60) NULL DEFAULT NULL COMMENT '支付渠道流水号',
  error_info varchar(1028) DEFAULT NULL COMMENT '错误信息',
  recln_date varchar(20) DEFAULT NULL COMMENT '对账时间=workDate',
  order_type varchar(2) DEFAULT NULL COMMENT '订单类型  01--正常订单   02--退款订单',
  channel_id   int default null comment '业务渠道ID',
  channel_code  varchar(6) not null comment '业务渠道代码',
  channel_name varchar(70) default null comment '业务渠道名称',  
  recon_status char(2) DEFAULT '01' COMMENT '分类状态 01=待处理 02=对账完成 03=对账失败',
  orgin_trans_id	varchar(30) default null comment '原订单支付交易序号',
  orgin_order_id    varchar(32) default null comment '原订单号',
  created_by varchar(32) DEFAULT 'sys' COMMENT '创建人',
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  last_updt_by varchar(32) DEFAULT 'sys' COMMENT '修改人',
  last_updt_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  version tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id),
  KEY csr_recln_payment_exce_table_row_id (table_row_id),
  KEY csr_recln_payment_exce_mer_table_name (table_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='对账异常交易信息表';

-- -------------------------------------------
-- Table structure for csr_sys_dict
-- 数据字典表
-- --------------------------------------------
DROP TABLE IF EXISTS `csr_sys_dict`;

CREATE TABLE `csr_sys_dict` (
  `row_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '数据字典ID',
  `data_code` varchar(32) NOT NULL COMMENT '数据代码',
  `data_name` varchar(32) DEFAULT NULL COMMENT '数据名称',
  `data_value` varchar(128) DEFAULT NULL COMMENT '数据值',
  `data_desc` varchar(32) DEFAULT NULL COMMENT '中文名称',
  `is_val` varchar(2) DEFAULT '0' COMMENT '有效标识',
  `input_user` varchar(32) DEFAULT NULL COMMENT '录入用户',
  `check_user` varchar(32) DEFAULT NULL COMMENT '复核用户',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modified_by` varchar(32) DEFAULT NULL COMMENT '修改人',
  `whenmodified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8 COMMENT='数据字典表';

-- -------------------------------------------
-- Table structure for csr_user_info
-- 用户信息表
-- --------------------------------------------
DROP TABLE IF EXISTS `csr_user_info`;


CREATE TABLE `csr_user_info` (
  `row_id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` varchar(18) NOT NULL COMMENT '用户编号',
  `mall_id` varchar(30) DEFAULT NULL COMMENT '所属平台',
  `user_name` varchar(32) DEFAULT NULL COMMENT '用户名称',
  `true_name` varchar(128) DEFAULT NULL COMMENT '用户真实姓名',
  `password` varchar(128) NOT NULL COMMENT '登录密码',
  `user_type` varchar(8) DEFAULT NULL COMMENT '用户类型',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
  `telphone` varchar(64) DEFAULT NULL COMMENT '手机号码',
  `landline_phone` varchar(64) DEFAULT NULL COMMENT '固定电话',
  `address` varchar(512) DEFAULT NULL COMMENT '地址',
  `sex` char(1) DEFAULT NULL COMMENT '性别',
  `birthday` varchar(10) DEFAULT NULL COMMENT '生日',
  `source` char(2) DEFAULT NULL COMMENT '数据来源：00=清结算系统，01=电商平台，02=测土配肥',
  `login_status` char(2) DEFAULT NULL COMMENT '登录状态  00 未登录  01 已登录  02 登录异常',
  `head_img` varchar(512) DEFAULT NULL COMMENT '用户头像',
  `status` char(2) DEFAULT NULL COMMENT '状态',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_updt_by` varchar(32) DEFAULT NULL COMMENT '修改人',
  `last_updt_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `idx_uia_user_info_user_id` (`user_id`),
  UNIQUE KEY `idx_uia_user_info_usrname` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- -------------------------------------------
-- Table structure for sequence
-- 序列表
-- --------------------------------------------
DROP TABLE IF EXISTS `sequence`;

CREATE TABLE `sequence` (
  `seq_name` varchar(50) NOT NULL COMMENT '序列名称',
  `current_val` bigint(20) unsigned NOT NULL COMMENT '当前值',
  `increment_val` int(11) NOT NULL DEFAULT '1' COMMENT '步长(跨度)',
  `circle` int(11) DEFAULT '0' COMMENT '是否循环 0否 1是',
  `max_val` bigint(20) unsigned DEFAULT '0' COMMENT '最大值，当循环为是时,超过此最大值从1重新开始计算',
  PRIMARY KEY (`seq_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='序列表';


-- -------------------------------------------
-- Table structure for csr_ccb_recln_lt
-- 建行对账明细信息表
-- --------------------------------------------
DROP TABLE IF EXISTS csr_ccb_recln_lt;

CREATE TABLE csr_ccb_recln_lt (
  row_id 					int(20) 	unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  recln_date 			varchar(20) 		DEFAULT NULL COMMENT '对账日期',
  trans_date     	timestamp  			not null comment '交易时间',
  trans_id 				varchar(32) 		DEFAULT NULL COMMENT '交易参考号',
  refund_seqno		varchar(32) 		DEFAULT NULL  comment '退款流水号',
  order_type 			varchar(2) 			DEFAULT NULL COMMENT '订单类型  00--支付   01--退款交易',
  payer_acct_no		varchar(32)			DEFAULT NULL COMMENT '付款方账号',   
  trans_amt				decimal(18,2) 	not null comment '支付金额',
  refund_amt			decimal(18,2) 	DEFAULT NULL comment '退款金额',
  pos_code				varchar(9)    	DEFAULT NULL comment '柜台号',
  payment_type    varchar(6)	  	DEFAULT NULL comment '付款方式',
  trans_status    varchar(6)    	DEFAULT NULL comment '订单状态',
  recln_file_id		int						  not null comment '文件记录ID',
  bookg_date			varchar(8)    	DEFAULT NULL comment '记账日期 格式:yyyyMMdd',  
  proc_state	 		char(2) 				DEFAULT '01' COMMENT '分类状态 01=待处理 02=对账完成 03=对账失败',
  created_by 			varchar(32) 		DEFAULT 'sys' COMMENT '创建人',
  created_date 		timestamp 			NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  last_updt_by 		varchar(32) 		DEFAULT 'sys' COMMENT '修改人',
  last_updt_date 	timestamp 			NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  version 				tinyint(4) 			NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='建行对账明细信息表';

-- -------------------------------------------
-- Table structure for csr_payment_merchant
-- 订单支付渠道信息表
-- --------------------------------------------
DROP TABLE IF EXISTS csr_payment_merchant;
CREATE TABLE `csr_payment_merchant` (
  `row_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `trans_id` varchar(32) NOT NULL COMMENT '支付渠道识别码',
  `channel_code` varchar(10) NOT NULL COMMENT '支付渠道识别码',
  `channel_name` varchar(70) NOT NULL COMMENT '支付渠道名称',
  `merchant_id` varchar(100) DEFAULT NULL COMMENT '支付渠道分配的商户ID',
  `app_id` varchar(100) DEFAULT NULL COMMENT '应用id[支付渠道分配给统一收银台]',
  `created_by` varchar(32) DEFAULT 'sys' COMMENT '创建人',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_updt_by` varchar(32) DEFAULT 'sys' COMMENT '修改人',
  `last_updt_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `version` tinyint(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COMMENT='订单支付渠道信息表';
-- -------------------------------------------
-- Table structure for csr_ceb_recln_lt
-- 光大银行对账明细信息表
-- --------------------------------------------
DROP TABLE IF EXISTS csr_ceb_recln_lt;

CREATE TABLE csr_ceb_recln_lt (
  row_id 					int(20) 	unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  trans_code      char(4)       NOT NULL COMMENT '交易代码 ZF01-支付 ZF02-退货',
  recln_date 			varchar(20) 		DEFAULT NULL COMMENT '对账日期 格式:yyyyMMdd',
  trans_date     	timestamp  			not null comment '交易时间',
  trans_id 				varchar(32) 		DEFAULT NULL COMMENT '交易参考号',
  merch_no    		varchar(32) 		DEFAULT NULL  comment '商户号',
  termal_no       varchar(10)     DEFAULT NULL  COMMENT '终端号',
  seq_no			varchar(32)   not null comment '网关流水号 交易代码为 ZF01： 为支付系统的流水号 ZF02：为原支付订单号',
  order_type 			varchar(2) 			DEFAULT NULL COMMENT '订单类型  00--支付   01--退款交易',
  trans_amt				decimal(18,2) 	not null comment '交易金额',
  charge_fee      decimal(18,2)   DEFAULT 0.00  COMMENT '手续费',
  settle_amt			decimal(18,2)   default 0.00  COMMENT '净清算金额',  
  resp_code				varchar(10)      default null comment '响应码',
  bak1						varchar(255)    DEFAULT NULL COMMENT '商户保留 1',
  bak2						varchar(255)    DEFAULT NULL COMMENT '商户保留 2',
  recln_file_id		int						  not null comment '文件记录ID',
  proc_state	 		char(2) 				DEFAULT '01' COMMENT '分类状态 01=待处理 02=对账完成 03=对账失败',
  created_by 			varchar(32) 		DEFAULT 'sys' COMMENT '创建人',
  created_date 		timestamp 			NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  last_updt_by 		varchar(32) 		DEFAULT 'sys' COMMENT '修改人',
  last_updt_date 	timestamp 			NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  version 				tinyint(4) 			NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (row_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='光大银行对账明细信息表';



DROP TABLE IF EXISTS csr_tnterbank_info;
CREATE TABLE `csr_tnterbank_info` (
  `row_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '记录主键',
  `channel_code` varchar(10) NOT NULL COMMENT '支付渠道识别码',
  `bank_type` VARCHAR(2) DEFAULT '01' COMMENT '网银标示:01个人网银,02 企业网银',
  `bank_code` VARCHAR(30) DEFAULT NULL COMMENT '银行标示',
  `bank_name` VARCHAR(60) DEFAULT NULL COMMENT '银行名称',
  `bank_desc` VARCHAR(150) DEFAULT NULL COMMENT '银行描述',
  `bank_logo_url` varchar(100) DEFAULT NULL COMMENT '银行logo地址',
  `created_by` VARCHAR(32) NOT NULL DEFAULT 'sys' COMMENT '记录创建者',
  `created_date` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `last_updt_date` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  `last_updt_by` VARCHAR(32) NOT NULL DEFAULT 'sys' COMMENT '记录最后更新人',
  `version` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`row_id`)
) ENGINE=INNODB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8 COMMENT='银行跨行支付银行信息表';


CREATE INDEX idx_order_trans_id ON csr_order_info(trans_id);
CREATE INDEX idx_order_order_id_channel ON csr_order_info(order_id,channel_cd);
CREATE INDEX idx_order_order_id ON csr_order_info(order_id);

CREATE INDEX idx_refund_trans_id ON csr_refund_order(trans_id);
CREATE INDEX idx_refund_order_id ON csr_refund_order(orgn_order_id,channel_cd);

CREATE INDEX idx_payment_trans_id ON csr_payment(trans_id);

CREATE INDEX idx_paylog_date_mall_id ON csr_payment_log(trans_date,pay_channel_code,mall_id);

CREATE INDEX idx_paymerchant_trans_id ON csr_payment_merchant(trans_id);

CREATE INDEX idx_webpage_model_channel_tmnal ON csr_webpage_model_cfg(channel_id,terminal);
CREATE INDEX idx_mall_model_mall_id ON csr_mall_model_cfg(mall_id);
CREATE INDEX idx_mall_model_channel_id ON csr_mall_model_cfg(channel_id);
CREATE INDEX idx_mall_model_pay_channel ON csr_mall_model_cfg(pay_channel);
CREATE INDEX idx_mall_model_pay_mall_channel ON csr_mall_model_cfg(mall_id,pay_channel,channel_id);

alter table csr_message add index idx_message_msg_id (`msg_id`);
alter table csr_message_order_rel add index idx_message_trans_id_msg_id (`trans_id`, `msg_id`);
alter table csr_pay_mer_relation add index idx_pay_mer_busi_channel_code (`busi_channel_code`);
alter table csr_pay_mer_relation add index idx_pay_mer_channel_code (`channel_code`);
alter table csr_pay_mer_relation add index idx_pay_mer_mall_id (`mall_id`);
alter table csr_pay_mer_relation add index idx_pay_mer_combination (`busi_channel_code`, `channel_code`, `mall_id`);
alter table csr_refund_order add index idx_refund_refund_id (`refund_id`, `channel_cd`);
alter table csr_refund_order add index idx_refund_refund_and_orgn_order_id (`refund_id`, `orgn_order_id`, `channel_cd`);




CREATE INDEX idx_pay_tem_open_flag ON csr_pay_template(open_flag,terminal,template_name); 
CREATE INDEX idx_pay_tem_terminal_temp ON csr_pay_template(terminal,template_name); 
CREATE INDEX idx_web_mod_open_flag ON csr_webpage_model_cfg(open_flag,terminal,channel_id,page_name); 
CREATE INDEX idx_web_mod_defautl_webpage ON csr_webpage_model_cfg(defautl_webpage,terminal,channel_id,page_name); 
CREATE INDEX idx_mal_mod_mall_channel_id ON csr_mall_model_cfg(mall_id,channel_id,terminal); 
CREATE INDEX idx_mal_mod_open_pay  ON csr_mall_model_cfg(open_flag,pay_channel); 

