package com.lifespace.line;

import com.lifespace.dto.OrdersDTO;
import com.linecorp.bot.messaging.client.MessagingApiClient;
import com.linecorp.bot.messaging.model.PushMessageRequest;
import com.linecorp.bot.messaging.model.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service("LinePushMessageService")
public class LinePushMessageService {

    @Autowired
    private MessagingApiClient messagingApiClient;

    public void autoPushCancelMsg(OrdersDTO ordersDTO) {
        if (ordersDTO.getLineUserId() == null){
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String cancelMsg = """
                此為自動推播，請勿回覆
                您的訂單已取消：
                訂單編號：%s
              　空間地點：%s
              　訂單金額：%s 元
              　訂單時間：%s ～ %s
              """.formatted(
              ordersDTO.getOrderId(),
               ordersDTO.getSpaceLocation(),
               ordersDTO.getAccountsPayable(),
               ordersDTO.getOrderStart().toLocalDateTime().format(formatter),
               ordersDTO.getOrderEnd().toLocalDateTime().format(formatter)
        );

        TextMessage msg = new TextMessage(cancelMsg);
        //參數(使用者LINEID/自動推播的文字/提示音/合併推播(default),若是要自動推播童事件獨立兩則以上(空值或null)
        PushMessageRequest pushCancelMsg = new PushMessageRequest(ordersDTO.getLineUserId(), List.of(msg),false, List.of("default"));
        //自動推播需要一個隨機的UUID做為第一個參數,防止重複推播
        // (和回覆使用者指令一樣,只是使用者傳送過來的文字是用replyToken,不需要自己寫一個)
        UUID lineRetryKey = UUID.randomUUID();
        messagingApiClient.pushMessage(lineRetryKey,pushCancelMsg);
    }


}
