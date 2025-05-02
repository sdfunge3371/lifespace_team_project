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
            查不到您的會員資料喔！
            請再確認資料是否正確，
            或輸入正確的姓名及手機號碼格式，
            如：大吳吳 0987654321"""),
    NO_ORDERS_BIND("""
            查無訂單可綁定(請至少曾經有一筆訂單)，
            如果有訂單問題請聯絡官網客服人員，
            此官方帳號僅提供：
            綁定會員帳號/查詢訂單/建立及取消訂單推播
            """),
    NO_PAID_ORDERS("""
            沒有預訂中的訂單喔！
            如果有訂單問題請聯絡官網客服人員，
            若是尚未預訂，
            趕緊點擊官網預約吧～
            http://localhost:8080/lifespace
            """),
    INVALID_ERROR("""
            您輸入的格式錯誤喔！
            請輸入正確的姓名及手機號碼格式，
            如：大吳吳 0987654321
            """);



    private final String bindLineIdMessage;

    LineBindResult(String binkLineIdMessage) {
        this.bindLineIdMessage = binkLineIdMessage;
    }

    public String getBindLineIdMessage() {
        return bindLineIdMessage;
    }

}
