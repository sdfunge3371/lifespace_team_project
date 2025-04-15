// DOM 載入完成後執行
document.addEventListener('DOMContentLoaded', function() {
    // 檢查會員登入狀態
    checkLoginStatus();
    
    // 綁定搜尋空間按鈕事件
    document.getElementById('search-space-btn').addEventListener('click', function() {
        window.location.href = 'spaceoverview.html';
    });
});

// 檢查會員登入狀態
function checkLoginStatus() {
    fetch("http://localhost:8080/member/profile", {
        method: "GET",
        credentials: "include" // 記得加這個，才會帶 session cookie
    })
    .then(res => {
        if (res.status === 401) {
            // 沒有登入，跳轉到登入頁面
            alert("尚未登入，請先登入");
            window.location.href = "/login.html";
        } else {
            return res.json();
        }
    })
    .then(data => {
        if (data) {
            // 已登入，檢查收藏狀態
            checkFavoriteStatus();
        }
    })
    .catch(err => {
        console.error("驗證失敗", err);
    });
}

// 檢查會員是否有收藏空間
function checkFavoriteStatus() {
    fetch("/favorite-space/has-favorites", {
        method: "GET",
        credentials: "include"
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("無法獲取收藏狀態");
        }
        return res.json();
    })
    .then(data => {
        if (data.hasFavorites) {
            // 有收藏空間，顯示收藏列表
            document.getElementById('no-favorite-container').style.display = 'none';
            document.getElementById('favorite-container').style.display = 'grid';
            loadFavoriteSpaces();
        } else {
            // 沒有收藏空間，顯示空白狀態
            document.getElementById('no-favorite-container').style.display = 'block';
            document.getElementById('favorite-container').style.display = 'none';
        }
    })
    .catch(error => {
        console.error('檢查收藏狀態失敗:', error);
    });
}

// 載入收藏的空間列表
function loadFavoriteSpaces() {
    fetch("/favorite-space", {
        method: "GET",
        credentials: "include"
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("無法獲取收藏空間");
        }
        return res.json();
    })
    .then(favoriteSpaces => {
        renderFavoriteSpaces(favoriteSpaces);
    })
    .catch(error => {
        console.error('載入收藏空間失敗:', error);
    });
}

// 渲染收藏的空間卡片
function renderFavoriteSpaces(favoriteSpaces) {
    const container = document.getElementById('favorite-container');
    container.innerHTML = ''; // 清空容器
    
    // 如果沒有收藏空間，顯示空白狀態
    if (favoriteSpaces.length === 0) {
        document.getElementById('no-favorite-container').style.display = 'block';
        document.getElementById('favorite-container').style.display = 'none';
        return;
    }
    
    // 生成空間卡片
    favoriteSpaces.forEach(space => {
        const spaceCard = document.createElement('div');
        spaceCard.className = 'space-card';
        spaceCard.dataset.id = space.spaceId;
        
        // 組合完整地址
        const location = `${space.branchAddr || ''}${space.spaceFloor ? space.spaceFloor + '樓' : ''}`;
        console.log(space.spacePhoto);

        spaceCard.innerHTML = `
            <div class="space-image">    
                <img src="data:image/jpeg;base64,${space.spacePhoto}" alt="空間圖片">
            </div>
            <div class="space-info">
                <div class="space-title">
                    <span>${space.spaceName}</span>
                    <button class="favorite-btn active" data-id="${space.spaceId}">
                        <i class="fas fa-heart"></i>
                    </button>
                </div>
                <div class="space-location">
                    <div class="location-text">
                        <i class="fas fa-map-marker-alt"></i> ${location}
                    </div>
                    <div class="people-count">
                        <i class="fas fa-user"></i> ${space.spacePeople || 0}
                    </div>
                </div>
                <div class="space-rating">
                    <span class="space-price">$${space.spaceHourlyFee || 0}/hr</span>
                    <div class="rating-stars">
                        <i class="fas fa-star"></i> ${(space.spaceRating || 0).toFixed(1)}
                    </div>
                </div>
            </div>
        `;
        
        container.appendChild(spaceCard);
        
        // 綁定取消收藏事件
        const favoriteBtn = spaceCard.querySelector('.favorite-btn');
        favoriteBtn.addEventListener('click', function(event) {
            event.stopPropagation(); // 阻止冒泡，避免觸發卡片點擊
            toggleFavorite(space.spaceId, this);
        });
        
        // 綁定卡片點擊事件，跳轉到空間詳情頁
        spaceCard.addEventListener('click', function() {
            window.location.href = `individual_space.html?spaceId=${space.spaceId}`;
        });
    });
}

// 切換收藏狀態
function toggleFavorite(spaceId, btnElement) {
    // 移除收藏
    fetch(`/favorite-space/${spaceId}`, {
        method: "DELETE",
        credentials: "include"
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("取消收藏失敗");
        }
        return res.text();
    })
    .then(() => {
        // 從DOM中移除該卡片
        const card = btnElement.closest('.space-card');
        card.classList.add('fadeOut');
        
        // 添加淡出動畫效果
        setTimeout(() => {
            card.remove();
            
            // 檢查是否還有收藏空間
            if (document.getElementById('favorite-container').children.length === 0) {
                document.getElementById('no-favorite-container').style.display = 'block';
                document.getElementById('favorite-container').style.display = 'none';
            }
        }, 300);
        
        // 同步更新空間總覽頁面的收藏狀態（如果有的話）
        updateSpaceOverviewFavoriteStatus(spaceId, false);
    })
    .catch(error => {
        console.error('取消收藏失敗:', error);
    });
}

// 更新空間總覽頁面的收藏狀態（通過LocalStorage實現頁面間通信）
function updateSpaceOverviewFavoriteStatus(spaceId, isFavorite) {
    // 保存當前修改的收藏狀態到LocalStorage
    const favoriteChanges = JSON.parse(localStorage.getItem('favoriteChanges') || '{}');
    favoriteChanges[spaceId] = isFavorite;
    localStorage.setItem('favoriteChanges', JSON.stringify(favoriteChanges));
}

// ArrayBuffer 轉 Base64 (用於顯示圖片)
// function arrayBufferToBase64(buffer) {
//     if (!buffer) return '';
//
//     let binary = '';
//     const bytes = new Uint8Array(buffer);
//     const len = bytes.byteLength;
//
//     for (let i = 0; i < len; i++) {
//         binary += String.fromCharCode(bytes[i]);
//     }
//
//     return window.btoa(binary);
// }

// 添加淡出動畫樣式
const style = document.createElement('style');
style.textContent = `
.fadeOut {
    opacity: 0;
    transform: scale(0.9);
    transition: opacity 0.3s ease, transform 0.3s ease;
}
`;
document.head.appendChild(style);