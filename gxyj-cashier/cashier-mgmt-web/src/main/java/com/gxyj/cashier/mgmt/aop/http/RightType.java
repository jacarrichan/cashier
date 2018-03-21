package com.gxyj.cashier.mgmt.aop.http;

public enum RightType {
    def,
    /** 过滤，不检查权限*/
    filter,
    /** 查询 */
    query,
    /** 新增 */
    add, 
    /** 修改 */
    edit,
    /** 删除 */
    delete,
    /** 审核 */
    check,
    /** 导出 */
    export,
    /** 返还 */
    refund,
    /** 缴纳 */
    pay,
    /** 发送 */
    send,
    /** 下发 */
    issue,
    /** 预览 */
    preview,
    /** 开启 */
    open,
    /** 关闭 */
    close,
    /**发布 */
    deploy,
    /**数据导入*/
    importin
}
