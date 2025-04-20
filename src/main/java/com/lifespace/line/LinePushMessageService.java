package com.lifespace.line;

import com.lifespace.dto.OrdersDTO;
import com.linecorp.bot.messaging.client.MessagingApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

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

    }
}
