package com.lifespace.line;

import com.lifespace.dto.OrdersDTO;
import com.lifespace.service.OrdersService;
import com.linecorp.bot.messaging.client.MessagingApiClient;
import com.linecorp.bot.messaging.model.ReplyMessageRequest;
import com.linecorp.bot.messaging.model.TextMessage;
import com.linecorp.bot.spring.boot.handler.annotation.EventMapping;
import com.linecorp.bot.spring.boot.handler.annotation.LineMessageHandler;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.TextMessageContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@LineMessageHandler
@RequiredArgsConstructor
public class LineWebHookHandler {

    private final OrdersService ordersSvc;
    private final MessagingApiClient messagingApiClient;

    @EventMapping
    public void handleTextMessage(MessageEvent messageEvent){

        TextMessageContent message = (TextMessageContent) messageEvent.message();
        String replyToken = messageEvent.replyToken();
        String userId = messageEvent.source().userId();
        String sendText = message.text();

        System.out.println("User:"+ userId + "傳來的文字:" + sendText);

        //已綁定userId的使用者可使用關鍵字查詢訂單
        if (sendText.contains("查詢訂單") || sendText.contains("訂單")){
            List<OrdersDTO> orders = ordersSvc.getOrdersByLineUserId(userId);
            if(orders.isEmpty()){
                sendTextToUser(replyToken," 沒有預訂中的訂單喔！ 趕緊點擊官網預約吧～");
            }else {
                StringBuilder sb = new StringBuilder("最近已預訂的訂單:\n");

                for (OrdersDTO dto : orders){
                sb.append("訂單編號:").append(dto.getOrderId())
                  .append("\n地點").append(dto.getSpaceLocation())
                  .append("\n時間").append(dto.getOrderStart()).append(" 至 ").append(dto.getOrderEnd())
                  .append("\n金額").append(dto.getAccountsPayable()).append("元");
                }

                sendTextToUser(replyToken, sb.toString());

            }

            //首次查詢訂單,需先綁訂 姓名 + 空格 + 09開頭的手機號碼
        }else if (sendText.matches("^\\S+[\\s　]+09\\d{8}$")){

            String[] parts = sendText.split("\\s+");
            String name = parts[0];
            String phone = parts[1];
            Boolean success = ordersSvc.bindLineUserIdAndPushOrders(userId, name, phone);
            if(success){
                sendTextToUser(replyToken,"請輸入『查詢訂單』查詢時間最近的三筆預約資訊");
            }else {
                sendTextToUser(replyToken, "查無會員姓名，請您輸入正確的格式\n例如:吳石伍 0958672727");
            }

        }
    }

    private void sendTextToUser(String replyToken, String text){
        //建立一個 LINE SDK的純文字訊息物件裝進req回覆給user
        TextMessage msg = new TextMessage(text);

        //傳入要傳送給使用者的參數(回覆user訊息的token, 回覆user的訊息內容, 是否發送通知音給使用者)
        ReplyMessageRequest req = new ReplyMessageRequest(replyToken, List.of(msg), false);
        messagingApiClient.replyMessage(req);
    }
}
