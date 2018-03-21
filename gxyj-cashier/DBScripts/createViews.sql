/*
*
Source Database       : gxyj_cashier_db

Target Server Type    : MYSQL

Date: 2017-07-18 17:37:49
*/
/*==============================================================*/
/* view: 订单支付交易view--对账查询                                     */
/*==============================================================*/
drop view if exists order_payment_view;
CREATE VIEW order_payment_view AS 
  SELECT csr_order_info.row_id AS row_id,
	csr_order_info.trans_id  AS trans_id,
	csr_order_info.order_id AS order_id,	
	csr_payment.row_id AS payment_id,
	csr_payment.refund_flag AS refund_flag,
	csr_order_info.order_type AS order_type,
	csr_order_info.channel_id AS channel_id,
	csr_order_info.trans_amt AS trans_amt,
	csr_order_info.charge_fee AS charge_fee,
	csr_order_info.trans_time AS trans_time,
	csr_payment.payer_name AS payer_name,
	csr_payment.payer_acct_no AS payer_acct_no,
	csr_payment.payer_insti_no AS payer_insti_no,
	csr_payment.payer_insti_nm AS payer_insti_nm,
	csr_payment.insti_pay_type AS insti_pay_type,
	csr_payment.trans_code AS trans_code,
	csr_payment.insti_resp_cd AS insti_resp_cd,
	csr_payment.insti_rsp_des AS insti_rsp_des,
	csr_payment.insti_rsp_time AS insti_rsp_time 
  FROM (csr_order_info JOIN csr_payment) WHERE csr_payment.trans_id = csr_order_info.trans_id;