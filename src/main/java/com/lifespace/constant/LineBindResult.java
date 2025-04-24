package com.lifespace.constant;

public enum LineBindResult {
    SUCCESS("""
            lifespace會員推播綁定成功！
            請輸入"查詢訂單"
            即可查看最新三筆空間預訂資訊！
            後續在官網預約或取消訂單都會推播通知給您喔！"""),
    ALREADY_BIND("""
            此帳號已綁定過囉！
            請輸入"查詢訂單"
            即可查看最新三筆空間預訂資訊！"""),
    MEMBER_NOT_FOUND("""
            查無會員資料
            請輸入正確的姓名及手機號碼格式
            如：大吳吳 0987654321"""),
    NO_ORDERS_BIND("""
            查無已付款訂單，
            如有問題請聯絡官網客服人員，
            此帳號僅提供：
            綁定會員帳號/查詢訂單/建立訂單/取消訂單
            """);


    private final String bindLineIdMessage;

    LineBindResult(String binkLineIdMessage) {
        this.bindLineIdMessage = binkLineIdMessage;
    }

    public String getBindLineIdMessage() {
        return bindLineIdMessage;
    }

}
