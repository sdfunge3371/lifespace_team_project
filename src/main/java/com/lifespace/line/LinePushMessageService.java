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


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    public void autoPushCancelMsg(OrdersDTO ordersDTO) {
        if (ordersDTO.getLineUserId() == null){
            return;
        }
        String cancelMsg = """
              此為自動推播，請勿回覆
              已成功取消您的訂單～期待下次再為您服務！
              訂單編號：%s
              空間地點：%s
              訂單金額：%s 元
              訂單時間：
              %s ～ 
              %s
              """.formatted(
              ordersDTO.getOrderId(),
               ordersDTO.getSpaceLocation(),
               ordersDTO.getAccountsPayable(),
               ordersDTO.getOrderStart().toLocalDateTime().format(formatter),
               ordersDTO.getOrderEnd().toLocalDateTime().format(formatter)
        );

        TextMessage msg = new TextMessage(cancelMsg);
        //參數(使用者LINEID/自動推播的文字/提示音/合併推播(default),若是要自動推播同事件獨立兩則以上(可空值或null)
        PushMessageRequest pushCancelMsg = new PushMessageRequest(ordersDTO.getLineUserId(), List.of(msg),false, List.of("default"));

        //自動推播需要一個隨機的UUID做為第一個參數,防止重複推播(給編號的意思)
        // (和回覆使用者指令一樣,只是使用者傳送過來的文字是用replyToken,不需要自己寫一個隨機數)
        UUID lineRetryKey = UUID.randomUUID();
        //lineRetryKey = pushCancelMsg裝的LineUserId
        messagingApiClient.pushMessage(lineRetryKey,pushCancelMsg);
    }

    public void autoPushCreateOrderMsg(OrdersDTO ordersDTO) {
        if (ordersDTO.getLineUserId() == null){
            return;
        }
        String successMsg = """
              此為自動推播，請勿回覆
              感謝您的預訂，如果您喜歡這次體驗，別忘了回來留下五星評價！
              訂單編號：%s
              空間地點：%s
              訂單金額：%s 元
              訂單時間：
              %s ～ 
              %s
              """.formatted(
              ordersDTO.getOrderId(),
              ordersDTO.getSpaceLocation(),
              ordersDTO.getAccountsPayable(),
              ordersDTO.getOrderStart().toLocalDateTime().format(formatter),
              ordersDTO.getOrderEnd().toLocalDateTime().format(formatter)
        );

        TextMessage msg = new TextMessage(successMsg);
        PushMessageRequest pushCreateSuccessMsg = new PushMessageRequest(ordersDTO.getLineUserId(), List.of(msg),false, List.of("default"));

        UUID lineRetryKey = UUID.randomUUID();
        messagingApiClient.pushMessage(lineRetryKey,pushCreateSuccessMsg);
    }


}
