package com.lifespace.repository;

import com.lifespace.entity.ChatroomMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatroomMessageRepository extends JpaRepository<ChatroomMessage, Integer> {
    
    // 根據會員ID查詢所有消息，按發送時間排序
    List<ChatroomMessage> findByMemberIdOrderBySendTimeAsc(String memberId);
    
    // 根據會員ID模糊查詢
    List<ChatroomMessage> findByMemberIdContainingOrderBySendTimeAsc(String memberId);
    
    // 查詢每個會員的最後一條訊息，按最後訊息時間降序排序
    @Query(value = "SELECT * FROM chatroom_message cm WHERE (cm.member_id, cm.send_time) IN " +
           "(SELECT member_id, MAX(send_time) FROM chatroom_message GROUP BY member_id) " +
           "ORDER BY cm.send_time DESC", nativeQuery = true)
    List<ChatroomMessage> findLatestMessageForEachMember();
    
    // 更新點擊狀態
    @Transactional
    @Modifying
    @Query("UPDATE ChatroomMessage c SET c.clickstatus = ?2 WHERE c.memberId = ?1 AND c.chatroomMessageId = " +
           "(SELECT MAX(cm.chatroomMessageId) FROM ChatroomMessage cm WHERE cm.memberId = ?1)")
    void updateClickStatusForLatestMessage(String memberId, Integer clickstatus);
    
    // 查詢特定會員的最後一條訊息
    @Query("SELECT c FROM ChatroomMessage c WHERE c.memberId = ?1 AND c.sendTime = " +
           "(SELECT MAX(cm.sendTime) FROM ChatroomMessage cm WHERE cm.memberId = ?1)")
    ChatroomMessage findLatestMessageByMemberId(String memberId);
    
    // 根據模糊會員ID查詢出每個會員的最後一條訊息，按最後訊息時間降序排序
    @Query(value = "SELECT * FROM chatroom_message cm WHERE (cm.member_id, cm.send_time) IN " +
           "(SELECT member_id, MAX(send_time) FROM chatroom_message WHERE member_id LIKE %?1% GROUP BY member_id) " +
           "ORDER BY cm.send_time DESC", 
           nativeQuery = true)
    List<ChatroomMessage> findLatestMessageForEachMemberByMemberIdContaining(String memberId);
}