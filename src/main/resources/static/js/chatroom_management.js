// 聊天室管理頁面功能
$(document).ready(function() {
    // 全局變量
    let currentMemberId = null;
    let isSearching = false;
    let messageCheckInterval = null; // 定期檢查新訊息
    
    // DOM 元素
    const memberList = $('#memberList');
    const chatMessages = $('#chatMessages');
    const memberSearchInput = $('#memberSearchInput');
    const memberSearchBtn = $('#memberSearchBtn');
    const messageInput = $('#messageInput');
    const sendMessageBtn = $('#sendMessageBtn');
    const chatHeader = $('#chatHeader');
    const chatInputArea = $('#chatInputArea');
    
    // 初始加載
    init();
    
    // 綁定搜尋按鈕點擊事件
    memberSearchBtn.on('click', function() {
        searchMembers();
    });
    
    // 綁定搜尋框回車事件
    memberSearchInput.on('keypress', function(e) {
        if (e.which === 13) {
            searchMembers();
        }
    });
    
    // 綁定發送消息按鈕點擊事件
    sendMessageBtn.on('click', function() {
        sendMessage();
    });
    
    // 綁定消息輸入框回車事件
    messageInput.on('keypress', function(e) {
        if (e.which === 13 && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });
    
    // 管理員登入
    function checkAdminLogin() {
        // 模擬管理員登入接口
        $.ajax({
            url: "/admin/profile",
            method: "GET",
            xhrFields: {
                withCredentials: true // 等同於 fetch 的 credentials: "include"
            },
            success: function(response) {
                console.log("登入的管理員ID：", response.adminId);
            },
            error: function(xhr) {
                if (xhr.status === 401) {
                    alert("尚未登入，請先登入");
                    window.location.href = "/lifespace/loginAdmin";
                } else {
                    console.error("無法取得管理員資料", xhr);
                }
            }
        });
    }
    
    // 初始化
    function init() {
        // 檢查管理員登入狀態
        checkAdminLogin();
        
        // 加載所有會員的最後一條訊息
        loadLatestMessages();
        
        // 設置定期檢查新訊息 (每5秒檢查一次)
        messageCheckInterval = setInterval(function() {
            if (currentMemberId) {
                // 如果有選中的會員，檢查該會員的新訊息
                checkNewMessages(currentMemberId);
            }
            
            // 無論是否有選中會員，都刷新所有會員的最後一條訊息
            // 修正問題3：這樣可以及時更新其他會員的新訊息狀態
            loadLatestMessagesWithoutUI();
        }, 5000);
    }
    
    // 檢查特定會員是否有新訊息
    function checkNewMessages(memberId) {
        $.ajax({
            url: `/chatroom/messages/${memberId}`,
            method: "GET",
            success: function(data) {
                // 檢查消息數量是否有變化
                if (chatMessages.children().length !== data.length) {
                    // 如果有新消息，更新聊天區域
                    renderChatMessages(data);
                }
            },
            error: function(xhr, status, error) {
                console.error("檢查新訊息失敗：", error);
            }
        });
    }
    
    // 加載所有會員的最後一條訊息 (不更新UI)
    function loadLatestMessagesWithoutUI() {
        $.ajax({
            url: "/chatroom/latest-messages",
            method: "GET",
            success: function(data) {
                // 獲取當前列表中的所有會員
                const currentMembers = {};
                $('.member-item').each(function() {
                    const memberId = $(this).attr('data-id');
                    currentMembers[memberId] = {
                        lastMessage: $(this).find('.last-message').text(),
                        isUnread: $(this).hasClass('unread')
                    };
                });
                
                // 檢查新增的會員或變化的訊息
                let hasChanges = false;
                for (let i = 0; i < data.length; i++) {
                    const message = data[i];
                    const existingItem = $(`.member-item[data-id="${message.memberId}"]`);
                    
                    let lastMessageText = '';
                    if (message.chatPhoto) {
                        lastMessageText = '已傳送照片';
                    } else if (message.content) {
                        lastMessageText = message.content;
                    }
                    
                    if (existingItem.length === 0) {
                        // 新會員
                        hasChanges = true;
                    } else {
                        const currentMemberInfo = currentMembers[message.memberId];
                        
                        // 修正問題2：檢查最後訊息是否變化，並且更新顯示內容
                        if (currentMemberInfo.lastMessage !== lastMessageText) {
                            existingItem.find('.last-message').text(lastMessageText);
                            hasChanges = true;
                        }
                        
                        // 修正問題3：檢查點擊狀態是否變化
                        if (message.clickstatus === 0 && !existingItem.hasClass('unread')) {
                            // 從已讀變為未讀
                            existingItem.addClass('unread');
                            existingItem.prepend('<span class="message-badge"></span>');
                            
                            // 將未讀訊息移至列表頂部
                            memberList.prepend(existingItem);
                            hasChanges = true;
                        } else if (message.clickstatus === 1 && existingItem.hasClass('unread')) {
                            // 從未讀變為已讀
                            existingItem.removeClass('unread');
                            existingItem.find('.message-badge').remove();
                            hasChanges = true;
                        }
                    }
                }
                
                // 如果有變化，重新渲染會員列表
                if (hasChanges) {
                    renderMemberList(data);
                }
            },
            error: function(xhr, status, error) {
                console.error("加載訊息失敗：", error);
            }
        });
    }
    
    // 加載所有會員的最後一條訊息
    function loadLatestMessages() {
        $.ajax({
            url: "/chatroom/latest-messages",
            method: "GET",
            success: function(data) {
                renderMemberList(data);
            },
            error: function(xhr, status, error) {
                console.error("加載訊息失敗：", error);
            }
        });
    }
    
    // 搜尋會員
    function searchMembers() {
        const searchValue = memberSearchInput.val().trim();
        
        if (!searchValue) {
            // 如果搜尋框為空，恢復顯示所有會員
            if (isSearching) {
                isSearching = false;
                loadLatestMessages();
            }
            return;
        }
        
        isSearching = true;
        
        $.ajax({
            url: `/chatroom/search/${searchValue}`,
            method: "GET",
            success: function(data) {
                renderMemberList(data);
            },
            error: function(xhr, status, error) {
                console.error("搜尋會員失敗：", error);
            }
        });
    }
    
    // 渲染會員列表
    function renderMemberList(messages) {
        // 保存當前選中的會員ID
        const selectedMemberId = currentMemberId;
        
        memberList.empty();
        
        if (messages.length === 0) {
            memberList.html('<div class="text-center p-3">沒有符合的會員</div>');
            return;
        }
        
        messages.forEach(function(message) {
            const memberItem = $('<div></div>')
                .addClass('member-item')
                .attr('data-id', message.memberId);
            
            // 修正問題1：判斷點擊狀態 (clickstatus === 0 表示未讀/藍色)
            if (message.clickstatus === 0) {
                memberItem.addClass('unread');
                memberItem.append('<span class="message-badge"></span>');
            }
            
            // 如果是當前選中的會員，添加選中標記
            if (message.memberId === selectedMemberId) {
                memberItem.addClass('active');
            }
            
            // 會員名稱
            memberItem.append(
                $('<span></span>')
                    .addClass('member-name')
                    .text(message.memberName || '未知會員')
            );
            
            // 會員ID
            memberItem.append(
                $('<span></span>')
                    .addClass('member-id')
                    .text(message.memberId)
            );
            
            // 最後訊息
            let lastMessageText = '';
            if (message.chatPhoto) {
                lastMessageText = '已傳送照片';
            } else if (message.content) {
                lastMessageText = message.content;
            }
            
            memberItem.append(
                $('<div></div>')
                    .addClass('last-message')
                    .text(lastMessageText)
            );
            
            // 綁定點擊事件
            memberItem.on('click', function() {
                selectMember(message.memberId, $(this));
            });
            
            memberList.append(memberItem);
        });
    }
    
    // 選擇會員
    function selectMember(memberId, memberElement) {
        // 更新當前選中的會員ID
        currentMemberId = memberId;
        
        // 更新UI選中狀態
        $('.member-item').removeClass('active');
        memberElement.addClass('active');
        
        // 修正問題1：判斷是否有藍色未讀標記，再進行更新點擊狀態
        if (memberElement.hasClass('unread')) {
            updateClickStatus(memberId);
            memberElement.removeClass('unread');
            memberElement.find('.message-badge').remove();
        }
        
        // 顯示聊天區域輸入框
        chatInputArea.show();
        
        // 更新聊天頭部
        updateChatHeader(memberId, memberElement.find('.member-name').text());
        
        // 加載該會員的所有聊天記錄
        loadMemberMessages(memberId);
    }
    
    // 更新聊天頭部
    function updateChatHeader(memberId, memberName) {
        chatHeader.find('.member-info h3').text(memberName || '未知會員');
        chatHeader.find('.member-info span').text(`ID: ${memberId}`);
    }
    
    // 更新點擊狀態
    function updateClickStatus(memberId) {
        $.ajax({
            url: `/chatroom/update-click-status/${memberId}`,
            method: "POST",
            success: function() {
                console.log("點擊狀態已更新");
            },
            error: function(xhr, status, error) {
                console.error("更新點擊狀態失敗：", error);
            }
        });
    }
    
    // 加載會員的所有聊天記錄
    function loadMemberMessages(memberId) {
        $.ajax({
            url: `/chatroom/messages/${memberId}`,
            method: "GET",
            success: function(data) {
                renderChatMessages(data);
            },
            error: function(xhr, status, error) {
                console.error("加載聊天記錄失敗：", error);
            }
        });
    }
    
    // 渲染聊天記錄
    function renderChatMessages(messages) {
        chatMessages.empty();
        
        if (messages.length === 0) {
            chatMessages.html('<div class="text-center p-3">沒有聊天記錄</div>');
            return;
        }
        
        messages.forEach(function(message) {
            renderChatMessage(message);
        });
        
        // 滾動到最新消息
        scrollToBottom();
    }
    
    // 渲染單條聊天消息
    function renderChatMessage(message) {
        const messageElement = $('<div></div>')
            .addClass('message');
        
        // 根據消息方向決定顯示位置
        if (message.status === 0) {
            // 會員發送的消息 (顯示在左側)
            messageElement.addClass('member');
        } else {
            // 管理員發送的消息 (顯示在右側)
            messageElement.addClass('admin');
        }
        
        // 照片消息
        if (message.chatPhoto || message.chatPhotoBase64) {
            const img = $('<img>')
                .attr('alt', '照片')
                .on('error', function() {
                    $(this).attr('src', '/images/image-placeholder.png');
                });
            
            if (message.chatPhotoBase64) {
                img.attr('src', `data:image/jpeg;base64,${message.chatPhotoBase64}`);
            } else {
                img.attr('src', `/chatroom/image/${message.chatroomMessageId}`);
            }
            
            messageElement.append(img);
        }
        // 文字消息
        else if (message.content) {
            messageElement.append($('<p></p>').text(message.content));
        }
        
        // 時間
        if (message.sendTime) {
            const date = new Date(message.sendTime);
            const timeStr = date.toLocaleString('zh-TW', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
            });
            
            messageElement.append(
                $('<span></span>')
                    .addClass('message-time')
                    .text(timeStr)
            );
        }
        
        chatMessages.append(messageElement);
    }
    
    // 發送文字消息
    function sendMessage() {
        if (!currentMemberId) return;
        
        const content = messageInput.val().trim();
        if (!content) return;
        
        $.ajax({
            url: `/chatroom/admin/send-message/${currentMemberId}`,
            method: "POST",
            data: { content: content },
            success: function(data) {
                // 添加新消息到聊天區
                renderChatMessage(data);
                
                // 滾動到最新消息
                scrollToBottom();
                
                // 清空輸入框
                messageInput.val('');
                
                // 更新會員列表中的最後一條訊息
                updateMemberLastMessage(currentMemberId, content);
            },
            error: function(xhr, status, error) {
                console.error("發送消息失敗：", error);
            }
        });
    }
    
    // 更新會員列表中的最後一條訊息
    function updateMemberLastMessage(memberId, content, isPhoto) {
        const memberItem = $(`.member-item[data-id="${memberId}"]`);
        
        if (memberItem.length) {
            if (isPhoto) {
                memberItem.find('.last-message').text('已傳送照片');
            } else {
                memberItem.find('.last-message').text(content);
            }
            
            // 將此會員移到列表最上方
            memberList.prepend(memberItem);
        }
    }
    
    // 滾動到最新消息
    function scrollToBottom() {
        chatMessages.scrollTop(chatMessages[0].scrollHeight);
    }
    
    // 在頁面關閉前清除定時器
    $(window).on('beforeunload', function() {
        if (messageCheckInterval) {
            clearInterval(messageCheckInterval);
        }
    });
});