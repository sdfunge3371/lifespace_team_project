package com.lifespace.controller;


import com.lifespace.dto.OrdersDTO;
import com.lifespace.dto.SpaceCommentRequest;
import com.lifespace.ecpay.payment.integration.AllInOne;
import com.lifespace.ecpay.payment.integration.domain.AioCheckOutOneTime;
import com.lifespace.ecpay.payment.integration.ecpayOperator.EcpayFunction;
import com.lifespace.entity.Orders;
import com.lifespace.service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersSvc;
    @Autowired
    private OrdersService ordersService;

    public OrdersController(OrdersService ordersSvc) {

        this.ordersSvc = ordersSvc;
    }


    @GetMapping("/getAll")
    public List<OrdersDTO> getAllOrders() {

        return ordersSvc.getAllOrdersDTOs();
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrders(@PathVariable String orderId) {

        try {
            ordersSvc.updateOrderStatusByOrderId(orderId); // 改變訂單狀態
            return ResponseEntity.ok("已成功取消訂單" + orderId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //會員查詢訂單session
     @GetMapping("/member/orders")
    public ResponseEntity<?> getOrdersByLoginMember(HttpSession session) {
        String memberId = (String) session.getAttribute("loginMember");


        if (memberId == null) {
            return ResponseEntity.status(401).body("尚未登入會員");
        }

        List<OrdersDTO> memberOrders = ordersSvc.getAllOrdersByMemberId(memberId);
        return ResponseEntity.ok(memberOrders);
     }

    //會員查詢訂單測試
//    @GetMapping("/member/{memberId}")
//    public List<OrdersDTO> getOrdersByMemberId(@PathVariable String memberId) {
//
//        return ordersSvc.getAllOrdersByMemberId(memberId);
//    }

    @PostMapping("/createOrders")
    public ResponseEntity<OrdersDTO> createOrder(@RequestBody OrdersDTO ordersDTO, HttpSession session) {
        String memberId = (String) session.getAttribute("loginMember");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ordersDTO.setMemberId(memberId);
        OrdersDTO newOrder = ordersSvc.createOrder(ordersDTO);
        return ResponseEntity.ok(newOrder);
    }

    @PostMapping("/ecpay-checkout/{orderId}")
    public ResponseEntity<String> checkoutWithEcpay(@PathVariable String orderId) {
        OrdersDTO order = ordersSvc.getOrdersDTOByOrderId(orderId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ecpay-查無訂單");
        }try {
            URL fileURL = getClass().getClassLoader().getResource("payment_conf.xml");
            if (fileURL == null) {
                return ResponseEntity.status(500).body("payment_conf.xml未載入");
            } else {
                System.out.println("payment_conf.xml 載入成功：" + fileURL);
            }

            AllInOne all = new AllInOne("");
            AioCheckOutOneTime aio = new AioCheckOutOneTime();

            String tradeNo = order.getOrderId() + System.currentTimeMillis();
            aio.setMerchantTradeNo(tradeNo);
            aio.setMerchantTradeDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            aio.setTotalAmount(order.getAccountsPayable().toString());
            aio.setTradeDesc("LifeSpace 空間租借");
            aio.setItemName("空間租借費用");
            aio.setReturnURL("https://1c22-1-164-244-104.ngrok-free.app/orders/ecpay/return");
            aio.setClientBackURL("http://localhost:8080/payment_success.html");
            aio.setIgnorePayment("WebATM#ATM#CVS#BARCODE");
            aio.setNeedExtraPaidInfo("N");

            System.out.println(tradeNo);

            String form = all.aioCheckOut(aio, null);
            return ResponseEntity.ok(form);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("訂單建立成功, 但金流表單建立失敗");
        }
    }

    @PostMapping("/ecpay/return")
    public ResponseEntity<String> handleEcpayReturn(HttpServletRequest request) {
        Map<String, String[]> paramsMap = request.getParameterMap();
        Map<String, String> ecpayParams = new HashMap<>();
        paramsMap.forEach((key, value) -> {
            if(value.length > 0) {
                ecpayParams.put(key, value[0]);
            }
        });

        System.out.println("====== 綠界回傳參數 ======");
        ecpayParams.forEach((k, v) -> System.out.println(k + " = " + v));
        System.out.println("====== End ======");

        // 保存並移除 CheckMacValue
        String backCheckMacValue = ecpayParams.get("CheckMacValue");
        ecpayParams.remove("CheckMacValue");

        // 準備比對用的Map
        Map<String, String> newEcpayParams = new TreeMap<>();
        for (Map.Entry<String, String> entry : ecpayParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null && !value.trim().isEmpty()) {
                newEcpayParams.put(key, value);
            }
        }

        // 如果 EncryptType 沒有回傳，手動補上（防止比對失敗）
        newEcpayParams.putIfAbsent("EncryptType", "1");

        System.out.println("newEcpayParams = " + newEcpayParams);

        // 金鑰設定
        String HashKey = "pwFHCqoQZGmho4w6";
        String HashIV = "EkRm7iFT261dpevs";

        try {
            // 重新產生 CheckMacValue
            String localCheckMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, newEcpayParams);

            // 比對 CheckMacValue
            if (backCheckMacValue != null && backCheckMacValue.equalsIgnoreCase(localCheckMacValue)) {
                System.out.println("回傳比對成功");

                String rtnCode = newEcpayParams.get("RtnCode");
                String tradeNo = newEcpayParams.get("MerchantTradeNo");
                String orderId = tradeNo.substring(0, 5); // 假設訂單編號是前5碼

                if ("1".equals(rtnCode)) {
                    ordersSvc.paidOrders(orderId);
                    System.out.println("更新訂單狀態為已付款：" + orderId);
                } else {
                    System.out.println("付款失敗，訂單不更新：" + orderId + "，原因：" + newEcpayParams.get("RtnMsg"));
                }

                return ResponseEntity.ok("1|OK");
            } else {
                System.out.println("回傳比對失敗");
                return ResponseEntity.ok("0|FAIL");
            }

        } catch (Exception e) {
            System.err.println(" CheckMacValue 計算失敗：" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("CheckMacValue 計算錯誤：" + e.getMessage());
        }
    }

    
    @PostMapping("/addComment")
    public String addSpaceComments(
            @RequestPart("eventRequest") SpaceCommentRequest commentRequest,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

    	ordersSvc.addSpaceComments(commentRequest, photos);
        return "執行 insert sapce comment jpa 方法";
    }


//     睿寓：新增訂單
    @PostMapping
    public ResponseEntity<OrdersDTO> createOrder(@RequestBody OrdersDTO ordersDTO) {
        OrdersDTO newOrder = ordersService.createOrder(ordersDTO);
        return ResponseEntity.ok(newOrder);
    }

    @GetMapping("/reserved-times")
    public ResponseEntity<List<?>> getReservedTimes(
            @RequestParam String spaceId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate date) {

        List<Orders> reservedOrders = ordersSvc.findReservedOrdersBySpaceIdAndDate(spaceId, date);

        // 回傳格式：[{start: "10:00", end: "12:00"}, ...]
        List<Map<String, String>> timeRanges = reservedOrders.stream()
                .map(order -> {
                    java.util.Map<String, String> map = new java.util.HashMap<>();
                    map.put("start", order.getOrderStart().toLocalDateTime().toLocalTime().toString().substring(0, 5));
                    map.put("end", order.getOrderEnd().toLocalDateTime().toLocalTime().toString().substring(0, 5));
                    return map;
                })
                .toList();
        System.out.println(timeRanges);
        return ResponseEntity.ok(timeRanges);
    }


}


