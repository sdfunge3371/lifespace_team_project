package com.lifespace.line;

import com.lifespace.constant.LineBindResult;
import com.lifespace.dto.OrdersDTO;
import com.lifespace.entity.News;
import com.lifespace.repository.NewsRepository;
import com.lifespace.service.OrdersService;
import com.linecorp.bot.messaging.client.MessagingApiClient;
import com.linecorp.bot.messaging.model.ReplyMessageRequest;
import com.linecorp.bot.messaging.model.TextMessage;
import com.linecorp.bot.spring.boot.handler.annotation.EventMapping;
import com.linecorp.bot.spring.boot.handler.annotation.LineMessageHandler;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.TextMessageContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@LineMessageHandler
public class LineWebHookHandler {


    @Autowired
    NewsRepository newsRepository;

    @Autowired
    private OrdersService ordersSvc;

    @Autowired
    private MessagingApiClient messagingApiClient;

    @Autowired
    private LinePushMessageService linePushMessageService;

    @EventMapping
    public void handleTextMessage(MessageEvent messageEvent){

        //接收使用者message傳來的事件,取出要的
        //message()拿到的是抽象父類別MessageContent介面,需TextMessageContent轉型才能取得文字內容
        TextMessageContent message = (TextMessageContent) messageEvent.message();
        String replyToken = messageEvent.replyToken();
        String userId = messageEvent.source().userId();
        String sendText = message.text();

        System.out.println("User:"+ userId + "傳來的文字:" + sendText);

        //已綁定userId的使用者可使用關鍵字查詢訂單
        if (sendText.contains("查詢訂單") ){
            List<OrdersDTO> orders = ordersSvc.getOrdersByLineUserId(userId);
            if(orders.isEmpty()){
                sendTextToUser(replyToken, """
                                            沒有預訂中的訂單喔！ 
                                            趕緊點擊官網預約吧～
                                            http://localhost:8080/lifespace""");
            }else {
                StringBuilder sb = new StringBuilder("最近已預訂的訂單：\n");

                for (OrdersDTO dto : orders){
                    //訂單時間格式化
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                    String msg = """
                            訂單編號：%s
                            空間地點：%s
                            訂單金額：%s 元
                            訂單時間：
                            %s ～ %s
                            ------------------
                            """
                            .formatted(
                           dto.getOrderId(),
                            dto.getSpaceLocation(),
                            dto.getAccountsPayable(),
                            dto.getOrderStart().toLocalDateTime().format(formatter),
                            dto.getOrderEnd().toLocalDateTime().format(formatter));

                    sb.append(msg);
                }

                sendTextToUser(replyToken, sb.toString());

            }

            //首次查詢訂單,需先綁訂 姓名 + 空格 + 09開頭的手機號碼
        }else if (sendText.matches("^\\S+[\\s　]+09\\d{8}$")){

            String[] parts = sendText.split("\\s+");
            String name = parts[0];
            String phone = parts[1];
            LineBindResult result = ordersSvc.bindLineUserIdAndPushOrders(userId, name, phone);
            sendTextToUser(replyToken, result.getBindLineIdMessage());

        }else if (sendText.contains("最新消息")){
            sendLastestNews(replyToken, userId);
        }

    }

    private void sendTextToUser(String replyToken, String text){
        //建立一個 LINE SDK的純文字訊息物件
        TextMessage msg = new TextMessage(text);

        //傳入要傳送給使用者的參數(回覆user訊息的token, 回覆user的訊息內容, 是否發送通知音給使用者)
        ReplyMessageRequest req = new ReplyMessageRequest(replyToken, List.of(msg), false);
        messagingApiClient.replyMessage(req);
    }

    private void sendLastestNews(String replyToken, String lineUserId){
        News lastestNews = newsRepository.findTop1ByNewsStatus_NewsStatusIdOrderByCreatedTimeDesc(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String newsMsg = """
                %s～%s
                %s
                %s
                """.formatted(
                        lastestNews.getNewsStartDate().toLocalDateTime().format(formatter),
                lastestNews.getNewsEndDate().toLocalDateTime().format(formatter),
                lastestNews.getNewsTitle(),
                 lastestNews.getNewsContent()
        );

        sendTextToUser(replyToken, newsMsg);

    }
}
