// 聊天室彈窗功能
document.addEventListener('DOMContentLoaded', function() {
    // DOM 元素
    const chatButton = document.getElementById('chatButton');
    const chatPopup = document.getElementById('chatPopup');
    const closeChat = document.getElementById('closeChat');
    const messageInput = document.getElementById('messageInput');
    const sendMessageBtn = document.getElementById('sendMessageBtn');
    const chatMessages = document.getElementById('chatMessages');
    
    // 記錄聊天室的開關狀態
    let isChatOpen = false;
    
    // 定時器，用於自動檢查新訊息
    let messageCheckInterval = null;
    
    // 綁定聊天圖標點擊事件
    if (chatButton) {
        chatButton.addEventListener('click', function() {
            // 如果聊天室已打開，則關閉它
            if (isChatOpen) {
                chatPopup.classList.remove('show');
                isChatOpen = false;
                
                // 停止消息檢查
                if (messageCheckInterval) {
                    clearInterval(messageCheckInterval);
                    messageCheckInterval = null;
                }
            } else {
                // 檢查會員登入狀態
                checkLoginStatus();
            }
        });
    }
    
    // 綁定關閉聊天室按鈕事件
    if (closeChat) {
        closeChat.addEventListener('click', function() {
            chatPopup.classList.remove('show');
            isChatOpen = false;
            
            // 停止消息檢查
            if (messageCheckInterval) {
                clearInterval(messageCheckInterval);
                messageCheckInterval = null;
            }
        });
    }
    
    // 綁定發送消息按鈕事件
    if (sendMessageBtn) {
        sendMessageBtn.addEventListener('click', function() {
            sendMessage();
        });
    }
    
    // 綁定文本框回車發送事件
    if (messageInput) {
        messageInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });
    }
    
    // 檢查會員登入狀態
    function checkLoginStatus() {
        fetch("/member/profile", {
            method: "GET",
            credentials: "include" // 記得加這個，才會帶 session cookie
        })
        .then(res => {
            if (res.status === 401) {
                // 沒有登入，跳轉到登入頁面
                alert("尚未登入，請先登入");
                window.location.href = "/lifespace/login";
            } else {
                return res.json();
            }
        })
        .then(data => {
            if (data) {
                // 已登入，檢查是否有聊天記錄
                checkChatMessages();
                // 顯示聊天室彈窗
                chatPopup.classList.add('show');
                isChatOpen = true;
                
                // 設置定期檢查新訊息 (每5秒檢查一次)
                messageCheckInterval = setInterval(function() {
                    loadChatMessages();
                }, 5000);
            }
        })
        .catch(err => {
            console.error("驗證失敗", err);
        });
    }
    
    // 檢查是否有聊天記錄
    function checkChatMessages() {
        fetch("/chatroom/has-messages", {
            method: "GET",
            credentials: "include"
        })
        .then(res => {
            if (!res.ok) {
                throw new Error("無法獲取聊天記錄狀態");
            }
            return res.json();
        })
        .then(data => {
            if (data.hasMessages) {
                // 有聊天記錄，加載聊天記錄
                loadChatMessages();
            } else {
                // 沒有聊天記錄，顯示預設消息
                showDefaultMessage();
            }
        })
        .catch(error => {
            console.error('檢查聊天記錄失敗:', error);
            showDefaultMessage();
        });
    }
    
    // 加載聊天記錄
    function loadChatMessages() {
        fetch("/chatroom/my-messages", {
            method: "GET",
            credentials: "include"
        })
        .then(res => {
            if (!res.ok) {
                throw new Error("無法獲取聊天記錄");
            }
            return res.json();
        })
        .then(messages => {
            // 如果當前沒有消息或消息數量與新獲取的不同，則更新UI
            if (chatMessages.children.length === 0 || chatMessages.children.length !== messages.length) {
                // 清空聊天消息區域
                chatMessages.innerHTML = '';
                
                // 渲染消息
                messages.forEach(message => {
                    renderMessage(message);
                });
                
                // 滾動到最新消息
                scrollToBottom();
            }
        })
        .catch(error => {
            console.error('加載聊天記錄失敗:', error);
        });
    }
    
    // 顯示預設消息
    function showDefaultMessage() {
        // 清空聊天消息區域
        chatMessages.innerHTML = '';
        
        // 添加預設消息
        const defaultMessage = {
            content: "您好，請問要詢問什麼事情？\n\n常見QA:\n1.LifeSpace使用規範\n2.租賃空間如何計價\n3.付款以及發票的開立方式\n4.使用加時、超時規則說明\n\n請點擊連結查看常見QA，或使用線上客服解答您的疑惑",
            status: 1,
            sendTime: new Date()
        };
        
        renderMessage(defaultMessage);
        
        // 滾動到最新消息
        scrollToBottom();
    }
    
    // 渲染消息
    function renderMessage(message) {
        const messageElement = document.createElement('div');
        
        // 根據消息方向決定顯示位置
        if (message.status === 0) {
            // 會員發送的消息 (顯示在右側)
            messageElement.className = 'message sent';
        } else {
            // 管理員發送的消息 (顯示在左側)
            messageElement.className = 'message received';
        }
        
        // 消息內容
        if (message.chatPhoto || message.chatPhotoBase64) {
            // 照片消息
            const img = document.createElement('img');
            if (message.chatPhotoBase64) {
                img.src = `data:image/jpeg;base64,${message.chatPhotoBase64}`;
            } else {
                img.src = `/chatroom/image/${message.chatroomMessageId}`;
            }
            img.alt = '照片';
            img.onerror = function() {
                this.onerror = null;
                this.src = '/images/image-placeholder.png';
            };
            messageElement.appendChild(img);
        } else if (message.content) {
            // 文字消息
            messageElement.textContent = message.content;
        }
        
        // 時間
        if (message.sendTime) {
            const timeElement = document.createElement('span');
            timeElement.className = 'message-time';
            
            // 格式化時間
            const date = new Date(message.sendTime);
            timeElement.textContent = date.toLocaleString('zh-TW', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
            });
            
            messageElement.appendChild(timeElement);
        }
        
        // 添加到聊天消息區域
        chatMessages.appendChild(messageElement);
    }
    
    // 發送文字消息
    function sendMessage() {
        const message = messageInput.value.trim();
        
        if (!message) {
            return; // 不發送空消息
        }
        
        // 發送消息到服務器
        fetch("/chatroom/send-message", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `content=${encodeURIComponent(message)}`,
            credentials: "include"
        })
        .then(res => {
            if (!res.ok) {
                throw new Error("發送消息失敗");
            }
            return res.json();
        })
        .then(data => {
            // 清空輸入框
            messageInput.value = '';
            
            // 渲染新消息
            renderMessage(data);
            
            // 滾動到最新消息
            scrollToBottom();
        })
        .catch(error => {
            console.error('發送消息失敗:', error);
            alert('發送消息失敗，請稍後再試');
        });
    }
    
    // 滾動到最新消息
    function scrollToBottom() {
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }
    
    // 在頁面關閉前清除定時器
    window.addEventListener('beforeunload', function() {
        if (messageCheckInterval) {
            clearInterval(messageCheckInterval);
        }
    });
});