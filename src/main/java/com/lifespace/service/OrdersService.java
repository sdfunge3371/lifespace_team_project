package com.lifespace.service;


import com.lifespace.constant.LineBindResult;
import com.lifespace.dto.OrdersDTO;
import com.lifespace.dto.RentalItemDetailsDTO;
import com.lifespace.dto.SpaceCommentRequest;
import com.lifespace.ecpay.payment.integration.AllInOne;
import com.lifespace.ecpay.payment.integration.domain.AioCheckOutOneTime;
import com.lifespace.entity.*;
import com.lifespace.line.LinePushMessageService;
import com.lifespace.repository.*;

import com.linecorp.bot.messaging.client.MessagingApiClient;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lifespace.mapper.OrdersMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service("ordersService")
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private SpaceCommentPhotoRepository spaceCommentPhotoRepository;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MessagingApiClient messagingApiClient;

    @Autowired
    private LinePushMessageService linePushMessageService;

    public void updateOrderStatusByOrderId(String orderId) {

        Orders orders = ordersRepository.findById(orderId)
                       .orElseThrow(() -> new IllegalArgumentException("訂單編號" + orderId + "不存在"));
        if (orders.getOrderStatus() == 0) {
            throw new IllegalStateException("訂單編號" + orderId + "已取消, 無法再次取消訂單");
        }

        if(orders.getOrderStatus() == 2) {
            throw new IllegalStateException("訂單編號" + orderId + "已完成, 無法取消該訂單");
        }

        orders.setOrderStatus(0);
        ordersRepository.save(orders);

        OrdersDTO chancelOrder = OrdersMapper.toOrdersDTO(orders);
        linePushMessageService.autoPushCancelMsg(chancelOrder);
    }


    public List<OrdersDTO> getAllOrdersDTOs() {

        return ordersRepository.findAll()
                .stream()
                .map(OrdersMapper::toOrdersDTO)
                .collect(Collectors.toList());
    }

    public List<OrdersDTO> getAllOrdersByMemberId(String memberId) {
        List<Orders> ordersList = ordersRepository.findAllByMemberId(memberId);
        System.out.println("目前查詢 memberId: " + memberId);
        for (Orders o : ordersList) {
            System.out.println("訂單：" + o.getOrderId() + " / 狀態：" + o.getOrderStatus());
        }

        return  ordersList
                .stream()
                .map(OrdersMapper::toOrdersDTO)
                .collect(Collectors.toList());
    }

    //結束時間到且狀態為已付款訂單 for排程器/啟動spring boot時更新
    public void expiredOrdersToComplete() {
        Timestamp now = Timestamp.from(Instant.now());
        List<Orders> expiredOrdersToComplete = ordersRepository.findByOrderStatusAndOrderEndBefore(1, now);

        if(!expiredOrdersToComplete.isEmpty()){
            for (Orders orders : expiredOrdersToComplete) {
                orders.setOrderStatus(2);
            }
            ordersRepository.saveAll(expiredOrdersToComplete);
            System.out.println("已自動更新" + expiredOrdersToComplete.size() + "筆訂單為已完成");
        } else {
            System.out.println("無訂單需要更新");
        }
    }


    //自動更新排程器
    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional
    public void autoCompleteOrdersByScheduled(){
        expiredOrdersToComplete();
//        System.out.println("排程器自動更新到期訂單");
    }

    @PostConstruct
    @Transactional
    public void autoCompleteOrdersByStartUp(){
        expiredOrdersToComplete();
//        System.out.println("啟動Spring後, 自動更新未更新到的到期訂單");
    }

    public OrdersDTO getOrdersDTOByOrderId(String orderId) {
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("無此訂單"));
        return OrdersMapper.toOrdersDTO(orders);
    }

    //綠界 送form表單
    public ResponseEntity<String> checkoutWithEcpay(String orderId) {
        OrdersDTO order = getOrdersDTOByOrderId(orderId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ecpay-查無訂單");
        }

        try {
            //確認可以讀到綠界的EcpayPayment.xml
            URL fileURL = getClass().getClassLoader().getResource("payment_conf.xml");
            if (fileURL != null) {
                System.out.println("有讀到payment_conf.xml：" + fileURL);
            } else {
                return ResponseEntity.status(500).body("沒讀到payment_conf.xml");
            }

            AllInOne all = new AllInOne("");
            AioCheckOutOneTime aio = new AioCheckOutOneTime();

            String tradeNo = order.getOrderId() + System.currentTimeMillis();
            aio.setMerchantTradeNo(tradeNo);
            aio.setMerchantTradeDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            aio.setTotalAmount(order.getAccountsPayable().toString());
            aio.setTradeDesc("LifeSpace 空間租借");
            aio.setItemName("空間租借費用");
            aio.setCustomField1(order.getOrderId());
            aio.setClientBackURL("http://localhost:8080/lifespace/payment_success?orderId=" + order.getOrderId());
            aio.setReturnURL("https://ba6b-124-218-199-62.ngrok-free.app/orders/ecpay/return");
            aio.setIgnorePayment("WebATM#ATM#CVS#BARCODE");
            aio.setNeedExtraPaidInfo("N");


            String form = all.aioCheckOut(aio, null);
            return ResponseEntity.ok().body(form);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("訂單建立成功, 但金流表單建立失敗");
        }
    }

    //綠界回傳驗證
    public ResponseEntity<String> handleEcpayReturn(HttpServletRequest req) {
        Map<String, String[]> paramsMap = req.getParameterMap();

        //建立一個SDK需要的HashTable裝進綠界的回傳參數
        Hashtable<String, String> ecpayParams = new Hashtable<>();
        paramsMap.forEach((key, value) -> {
            if (value.length > 0) {
                ecpayParams.put(key, value[0]);
            }
        });

        System.out.println("====== 綠界回傳參數 ======");
        ecpayParams.forEach((k, v) -> System.out.println(k + " = " + v));
        System.out.println("====== End ======");

        try {
            //初始化SDK後 建立一個AllInOne物件 使用驗證器排除CheckMacValue 再重新加密比對CheckMacValue
            AllInOne all = new AllInOne("");
            boolean isValid = all.compareCheckMacValue(ecpayParams);

            if (isValid) {
                System.out.println("CheckMacValue 比對成功");

                String rtnCode = ecpayParams.get("RtnCode");
//                String tradeNo = ecpayParams.get("MerchantTradeNo");
                //訂單編號還原
//                String orderId = tradeNo.substring(0, 5);
                String orderId = ecpayParams.get("CustomField1");
                if ("1".equals(rtnCode)) {
                    Orders order = paidOrders(orderId);
                    OrdersDTO dto = OrdersMapper.toOrdersDTO(order);
                    System.out.println("更新訂單狀態為已付款：" + orderId);
                    linePushMessageService.autoPushCreateOrderMsg(dto);
                } else {
                    System.out.println("付款失敗，不更新訂單：" + orderId);
                }

                return ResponseEntity.ok("1|OK");
            } else {
                System.out.println("CheckMacValue 比對失敗");
                return ResponseEntity.ok("0|FAIL");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("比對失敗：" + e.getMessage());
        }
    }

    //更改綠界付款完成後的訂單狀態
    @Transactional
    public Orders paidOrders(String orderId) {
        Orders orders = ordersRepository.findById(orderId).orElse(null);
        if(orders != null && orders.getOrderStatus() != 1) {
            orders.setOrderStatus(1);
            orders.setPaymentDatetime(Timestamp.valueOf(LocalDateTime.now()));
            ordersRepository.save(orders);
        }

        return orders;
    }

    public  List<OrdersDTO> getOrdersByLineUserId(String lineUserId) {
        List<Orders> orders = ordersRepository.findTop3ByLineUserIdAndOrderStatusOrderByOrderStartDesc(lineUserId, 1);
        return orders.stream()
                     .map(OrdersMapper::toOrdersDTO)
                     .collect(Collectors.toList());
    }

    public LineBindResult bindLineUserIdAndPushOrders(String lineUserId, String memberName, String phone) {

        //是否綁定過
        boolean exists = ordersRepository.existsByLineUserId(lineUserId);
        if (exists) {
            return LineBindResult.ALREADY_BIND;
        }

        Member member = memberRepository.findByMemberNameAndPhone(memberName, phone);
        if (member == null) {
            return LineBindResult.MEMBER_NOT_FOUND;
        }

        //取出三筆最靠近開始時間及狀態是已付款的訂單
        List<Orders> orders = ordersRepository.findTop3ByMemberIdAndOrderStatusOrderByOrderStartDesc(member.getMemberId(), 1);
        if (orders == null) {
            return LineBindResult.NO_ORDERS_BIND;
        }

        //取出未綁定lineUserId的訂單筆數收集成List,將這些訂單綁定lineUserId
        List<String> orderIds = orders.stream()
                .filter(order -> order.getLineUserId() == null)
                .map(Orders::getOrderId)
                .collect(Collectors.toList());

        if (orderIds.isEmpty()) {
            return LineBindResult.NO_ORDERS_BIND;
        }

        int lineUserIdUpdated = ordersRepository.bulkInsertLineUserIdIfNull(lineUserId, orderIds);

        if(lineUserIdUpdated > 0){
            return LineBindResult.SUCCESS;
        }else {
            return LineBindResult.NO_ORDERS_BIND;
        }
    }

        private String getLineUserIdFromPastOrders(OrdersDTO dto) {
            List<String> results = ordersRepository.findTop1LineUserIdByMemberId(dto.getMemberId());
            return results.isEmpty() ? null : results.get(0);
        }



    //訂單完成後，新增空間評論
    public void addSpaceComments(SpaceCommentRequest commentRequest,
    		List<MultipartFile> photos) {
    	
    	String orderId = commentRequest.getOrderId();
    	Orders orders = ordersRepository.findById(orderId)
                 .orElseThrow(() -> new IllegalArgumentException("訂單編號: " + orderId + "不存在"));
    	
    	 if (orders.getCommentTime() != null) {
    	        throw new IllegalStateException("該訂單已經提交過評論，無法重複評論");
    	    }
    	 
    	Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    	
    	orders.setCommentContent(commentRequest.getComments());
    	orders.setCommentTime(currentTime);
    	orders.setSatisfaction(commentRequest.getRating());
    	ordersRepository.save(orders);
    
        // 處理照片上傳
        if (photos != null && !photos.isEmpty()) {
        	System.out.println(photos.size());
            for (MultipartFile photo : photos) {
                try {
                    // 儲存檔案到指定位置，並取得檔案路徑
                    String photoPath = savePhoto(photo);

                    SpaceCommentPhoto spaceCommentPhoto = new SpaceCommentPhoto();
                    spaceCommentPhoto.setOrders(orders);
                    spaceCommentPhoto.setCreatedTime(currentTime);
                    spaceCommentPhoto.setSpacePhoto(photoPath);   
                    spaceCommentPhotoRepository.save(spaceCommentPhoto);

                } catch (Exception e) {
                    // 處理檔案儲存失敗的例外
                    e.printStackTrace();
                    // 可以選擇拋出例外或記錄錯誤
                }
            }
        }

        // for睿寓：更新空間滿意度平均
        String spaceId = orders.getSpaceId(); // ← 這裡取得空間 ID
        spaceService.updateSpaceRating(spaceId);
    }


    //初始化空間評論圖片資料夾
    @PostConstruct
    public void initCommentImageFolder() {
        String rootPath = System.getProperty("user.dir");
        File commentImageDir = new File(rootPath, "uploads/space-comment-images");
        if (!commentImageDir.exists()) commentImageDir.mkdirs();
        System.out.println("空間評論圖片資料夾初始化完成於: " + commentImageDir.getAbsolutePath());
    }
    
    // 呼叫入口
    private String savePhoto(MultipartFile photo) throws Exception {
        return savePhoto(photo, "space-comment-images");
    }


    	// 儲存到 /uploads/space-comment-images/
    private String savePhoto(MultipartFile photo, String subFolder) throws Exception {
        String originalFileName = photo.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IOException("檔案名稱為空");
        }

        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        String rootPath = System.getProperty("user.dir");
        File uploadDir = new File(rootPath, "uploads" + File.separator + subFolder);

        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IOException("無法建立目錄: " + uploadDir.getAbsolutePath());
        }

        String fileName = baseName + extension;
        File file = new File(uploadDir, fileName);
        int counter = 1;
        while (file.exists()) {
            fileName = baseName + "(" + counter + ")" + extension;
            file = new File(uploadDir, fileName);
            counter++;
        }

        photo.transferTo(file);
        return "/" + subFolder + "/" + fileName;
    }








    @Autowired
    private RentalItemRepository rentalItemRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    // 睿寓：新增訂單
    @Transactional
    public OrdersDTO createOrder(OrdersDTO ordersDTO) {
        Orders order = new Orders();

        order.setSpaceId(ordersDTO.getSpaceId());
        order.setBranchId(ordersDTO.getBranchId());
        order.setOrderStart(ordersDTO.getOrderStart());
        order.setOrderEnd(ordersDTO.getOrderEnd());
        order.setTotalPrice(ordersDTO.getTotalPrice());
        order.setAccountsPayable(ordersDTO.getAccountsPayable());
        order.setMemberId(ordersDTO.getMemberId());
        order.setPaymentDatetime(ordersDTO.getPaymentDatetime());
        order.setOrderStatus(0);
        order.setLineUserId(getLineUserIdFromPastOrders(ordersDTO));

        // 建立Branch與Member的關聯，讓Mapper取得
        Branch branch = new Branch();
        branch.setBranchId(ordersDTO.getBranchId());
        order.setBranch(branch);

        Member member = new Member();
        member.setMemberId(ordersDTO.getMemberId());
        order.setMember(member);

        Space space = spaceRepository.findById(ordersDTO.getSpaceId())
                .orElseThrow(() -> new RuntimeException("找不到空間"));
        space.setSpaceId(ordersDTO.getSpaceId());
        order.setSpace(space);

        int spaceFee;
        if (ordersDTO.getOrderStart() != null && ordersDTO.getOrderEnd() != null & Boolean.TRUE.equals(ordersDTO.getIsDaily())) {
            spaceFee = space.getSpaceDailyFee();
        } else {
            long millis = ordersDTO.getOrderEnd().getTime() - ordersDTO.getOrderStart().getTime();
            long halfHours = (long) Math.ceil(millis / (1000.0 * 60 * 30));
            int halfHourPrice = space.getSpaceHourlyFee() / 2;
            spaceFee = (int) (halfHours * halfHourPrice);
        }

        // 若有加購項目
        int rentalItemFee = 0 ;
        List<RentalItemDetailsDTO> rentalItemList = ordersDTO.getRentalItemDetailsDTOList();

        if (rentalItemList != null && !rentalItemList.isEmpty()) {

            List<RentalItemDetails> rentalItems = new ArrayList<>();

            for (RentalItemDetailsDTO dto : ordersDTO.getRentalItemDetailsDTOList()) {
                // 先檢查租借品項是否可用
                RentalItem rentalItem = rentalItemRepository.findById(dto.getRentalItemId())
                        .orElseThrow(() -> new RuntimeException("找不到租借品項"));

                if (rentalItem.getAvailableRentalQuantity() < dto.getRentalItemQuantity()) {
                    throw new IllegalArgumentException("可租借數量不足：" + rentalItem.getRentalItemName());
                }

                int quantity = dto.getRentalItemQuantity();
                int Price = rentalItem.getRentalItemPrice();

                // 更新剩餘可租借數量
                rentalItem.setAvailableRentalQuantity(rentalItem.getAvailableRentalQuantity() - dto.getRentalItemQuantity());
                rentalItemRepository.save(rentalItem);

                rentalItemFee += Price * quantity;

                // 建立關聯
                RentalItemDetails item = new RentalItemDetails();
                item.setRentalItem(rentalItem);
                item.setRentalItemQuantity(dto.getRentalItemQuantity());

                item.setCreatedTime(new Timestamp(System.currentTimeMillis()));
                item.setOrders(order); // 關聯訂單
                rentalItems.add(item);
            }


            order.setRentalItemDetails(rentalItems);
            order.setTotalPrice(spaceFee);
            order.setAccountsPayable(spaceFee + rentalItemFee);

        }
        ordersRepository.save(order);
        return OrdersMapper.toOrdersDTO(order);

    }

    // 查詢已預訂時段
    public List<Orders> findReservedOrdersBySpaceIdAndDate(String spaceId, LocalDate date) {
        return ordersRepository.findReservedOrdersBySpaceIdAndDate(spaceId, date);
    }


}
