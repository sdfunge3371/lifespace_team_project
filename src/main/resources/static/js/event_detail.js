$(document).ready(function () {

    // 1. 取得 URL 中的 eventId
    function getEventIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('eventId');
    }

    const eventId = getEventIdFromUrl();
    console.log(eventId);
    if (eventId) {
        // 2. 使用 eventId 呼叫 API 獲取活動資料
        const apiUrl = `/lifespace/event/getOne?eventId=${eventId}`;

        fetch(apiUrl)
            .then(response => {
                if (!response.ok) {
                    throw new Error('網路回應不正常，狀態碼: ' + response.status);
                }
                return response.json();
            })
            .then(event => {
                // 3. 更新 HTML 元素以顯示活動資料
                console.log(event);
                if (event) {
                    console.log(event.eventName);
                    document.getElementById('event-name').textContent = event.eventName;
                    document.getElementById('event-time').textContent = formatDateAndTime(event.eventStartTime) + ' ~ ' + formatDateAndTime(event.eventEndTime);
                    document.getElementById('event-location').textContent = event.spaceAddress; // 你可能需要調整這個，因為你的 EventEntity 中 spaceId 似乎是 ID，你需要有地址的欄位
                    document.getElementById('event-capacity').textContent = event.maximumOfParticipants + ' 人';
                    document.getElementById('event-host').textContent = event.organizer;  // 你可能需要調整這個，因為你的 EventEntity 中 memberId 似乎是 ID，你需要有舉辦人的欄位
                    document.getElementById('event-remarks').textContent = event.remarks;
                    document.getElementById('activity-description').textContent = event.hostSpeaking;
                    document.getElementById('event-briefing').textContent = event.eventBriefing;
                    document.getElementById('registered-count').textContent = event.numberOfParticipants;
                    document.getElementById('total-regis').textContent = event.maximumOfParticipants;

                    const latitude = parseFloat(event.latitude);
                    const longitude = parseFloat(event.longitude);
                                
                    // 儲存坐標到全局變數
                    window.eventCoords = {
                        latitude: parseFloat(event.latitude),
                        longitude: parseFloat(event.longitude)
                    };

                    // 檢查 Google Maps API 是否已載入
                    console.log("Google Maps 載入狀態:", typeof google !== 'undefined');
                    if (typeof google !== 'undefined' && google.maps) {
                        console.log("Google Maps API 已載入，初始化地圖");
                        initMap(event.latitude, event.longitude);
                    } else {
                        console.log("Google Maps API 尚未載入，等待回調");
                    }

                    // 處理輪播圖片顯示
                    const carousel = $('.owl-carousel.header-carousel');

                    // 首先銷毀現有的輪播實例（如果存在）
                    if (carousel.data('owl.carousel')) {
                        carousel.trigger('destroy.owl.carousel');
                    }

                    // 清空現有內容
                    carousel.empty();

                    carousel.css({
                        'max-width': '100%',
                        'height': '400px',
                        'position': 'relative', // 確保可以正確放置絕對定位的子元素
                    });

                    if (event.photoUrls && event.photoUrls.length > 0) {
                        // 添加API返回的圖片到輪播
                        event.photoUrls.forEach(photoUrl => {
                            carousel.append(`
                <div class="owl-carousel-item" style="height: 400px; display: flex; align-items: center; justify-content: center;">
                    <img class="img-fluid" src="${photoUrl}" alt="${event.eventName}" style="width: 100%; height: 350px; object-fit: contain;">
                </div>
            `);
                        });
                    } else {
                        // 如果沒有圖片，使用默認圖片
                        carousel.append(`
            <div class="owl-carousel-item">
                <img src="/images/default_for_event_and_space.jpg" alt="預設圖片">
            </div>
        `);
                    }

                    // 確保DOM完全更新後再初始化輪播
                    setTimeout(() => {
                        carousel.owlCarousel({
                            items: 1,
                            loop: true,
                            nav: true,
                            navText: ["<i class='fa fa-chevron-left'></i>", "<i class='fa fa-chevron-right'></i>"],
                            dots: true,
                            autoplay: true,
                            autoplayTimeout: 5000,
                            smartSpeed: 1000,
                            // animateIn: 'fadeIn',
                            // animateOut: 'fadeOut',
                            slideTransition: 'linear',  // 可以是'linear'或'ease'等
                            mouseDrag: true,
                            touchDrag: true

                        });


                    }, 100);
                }
            })
            .catch(error => {
                console.error('獲取活動資料時出錯:', error);
                // 在這裡可以顯示錯誤訊息給使用者
            });
    } else {
        console.error('URL 中沒有 eventId 參數');
        // 在這裡可以顯示錯誤訊息給使用者
    }

    // 格式化日期和時間的函式
    function formatDateAndTime(dateTime) {
        const date = new Date(dateTime);
        const datePart = date.toLocaleDateString();
        const timePart = date.toLocaleTimeString({ hour: '2-digit', minute: '2-digit' });
        return `${datePart} ${timePart}`;
    }


    // 監聽問號圖示的點擊事件
    $('#activity-info-trigger').click(function () {
        // 取得活動說明內容
        var activityContent = $('#event-remarks').html();

        // 將活動說明內容放入 Modal body
        $('#activityModalBody').html(activityContent);

        // 顯示 Modal
        $('#activityModal').modal('show');
    });
});

$(document).ready(function () {
    // 獲取目前的 eventId
    function getEventIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('eventId');
    }
    
    const eventId = getEventIdFromUrl();
    
    // 檢查用戶參與狀態
    function checkUserParticipationStatus() {
        fetch(`/lifespace/event/check/eventMemberStatus?eventId=${eventId}`, {
            method: 'GET',
            credentials: 'include' // 確保請求包含 cookies
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 401) {
                // 未登入狀態下，兩個按鈕都可以點選，但點選時會要求登入
                $("#register-btn").prop("disabled", false);
                $("#cancell-regis-btn").prop("disabled", true);
                return null;
            } else {
                throw new Error('獲取用戶參與狀態失敗');
            }
        })
        .then(data => {
            if (data) {
                // 根據用戶參與狀態設置按鈕
                if (data.participateStatus === "PARTICIPATING") {
                    // 已參加或已候補，停用參加按鈕，啟用取消按鈕
                    $("#register-btn").prop("disabled", true);
                    $("#cancell-regis-btn").prop("disabled", false);
                    $("#participation-status").text("(已參加)").show();
                } else {
                    // 未參加或已取消，只能按參加按鈕
                    $("#register-btn").prop("disabled", false);
                    $("#cancell-regis-btn").prop("disabled", true);
                    $("#participation-status").text("").hide();
                }
            }
        })
        .catch(error => {
            console.error('檢查參與狀態錯誤:', error);
        });
    }
    
    // 頁面載入時檢查用戶參與狀態
    checkUserParticipationStatus();
    
    // 我要參加按鈕點擊事件
    $("#register-btn").click(function () {
		// 彈出確認視窗
		if (confirm("確定要參加這個活動嗎？")){
        // 發送 API 請求
        	fetch(`/lifespace/event/addMemToEvent?eventId=${eventId}`, {
            	method: 'PUT',
            	credentials: 'include' // 確保請求包含 cookies
        	})
        .then(response => {
            if (response.ok) {
                // 成功加入活動
                const totalRegis = parseInt($("#total-regis").text());
                const regisCount = parseInt($("#registered-count").text());
                
                if (regisCount < totalRegis) {
                    // 報名成功
                    $("#successModal").modal("show");
                    $("#registered-count").text(regisCount + 1); // 更新人數
                    $("#cancell-regis-btn").prop('disabled', false);
                    $("#register-btn").prop('disabled', true);
                } else {
                    // 候補
                    $("#waitingModal").modal("show");
                    $("#waiting-num").text(regisCount - totalRegis + 1);
                    $("#registered-count").text(regisCount + 1);
                }
                
                return response.text();
            } else if (response.status === 401) {
                // 未授權，顯示提示並導向登入頁面
                alert("請先登入才能參加活動");
                window.location.href = "login.html";
                throw new Error('未登入');
            } else {
                // 其他錯誤
                return response.text().then(text => {
                    throw new Error(text || '請求失敗');
                });
            }
        })
        .then(data => {
            console.log('成功:', data);
            // 更新按鈕狀態
            checkUserParticipationStatus();
        })
        .catch(error => {
            console.error('錯誤:', error);
            if (error.message !== '未登入') {
                alert("操作失敗: " + error.message);
            }
        });
	}else{
		//使用者確認不參加活動
		console.log("取消動作");	
		}
   });

    // 取消參加按鈕點擊事件
    $("#cancell-regis-btn").click(function () {
		
		// 彈出確認視窗
		if (confirm("確定要取消參加這個活動嗎？")){
        // 發送 API 請求
        	fetch(`/lifespace/event/removeMemFromEvent?eventId=${eventId}`, {
            	method: 'PUT',
            	credentials: 'include' // 確保請求包含 cookies
        	})
        .then(response => {
            if (response.ok) {
                // 成功取消參加
                const regisCount = parseInt($("#registered-count").text());
                $("#registered-count").text(regisCount - 1);
                $("#cancellRegisModal").modal("show");
                $("#register-btn").prop('disabled', false);
                $("#cancell-regis-btn").prop('disabled', true);
                
                return response.text();
            } else if (response.status === 401) {
                // 未授權，顯示提示並導向登入頁面
                alert("請先登入才能取消參加活動");
                window.location.href = "login.html";
                throw new Error('未登入');
            } else {
                // 其他錯誤
                return response.text().then(text => {
                    throw new Error(text || '請求失敗');
                });
            }
        })
        .then(data => {
            console.log('成功:', data);
            // 更新按鈕狀態
            checkUserParticipationStatus();
        })
        .catch(error => {
            console.error('錯誤:', error);
            if (error.message !== '未登入') {
                alert("操作失敗: " + error.message);
            }
        });
	}else{
		//使用者確認不取消活動
		 console.log("取消動作");	
		}
   });

    // 原有的候補取消按鈕點擊事件
    $("#waiting-cancel").click(function () {
        // 調用 API 取消候補
        fetch(`/lifespace/event/removeMemFromEvent?eventId=${eventId}`, {
            method: 'PUT',
            credentials: 'include' // 確保請求包含 cookies
        })
        .then(response => {
            if (response.ok) {
                let RegisCount = parseInt($("#registered-count").text());
                $("#registered-count").text(RegisCount - 1);
                $("#waitingModal").modal("hide");
                // 更新按鈕狀態
                checkUserParticipationStatus();
                return response.text();
            } else if (response.status === 401) {
                alert("請先登入才能取消候補");
                window.location.href = "login.html";
                throw new Error('未登入');
            } else {
                return response.text().then(text => {
                    throw new Error(text || '請求失敗');
                });
            }
        })
        .catch(error => {
            console.error('錯誤:', error);
            if (error.message !== '未登入') {
                alert("操作失敗: " + error.message);
            }
        });
    });
});

// 地圖相關
let map;
let markersData = [];   // 座標位置顯示
let activeInfoWindow = null; // Keep track of the currently open InfoWindow


let activeCardId = null;
let spaces = [];
let usages = [];
    
// ============= 地圖相關 =============

// 定義全局 initMap 函數
    function initMap(latitude, longitude) {
        // 確保座標是數值類型
        const lat = parseFloat(latitude);
        const lng = parseFloat(longitude);
        
        if (isNaN(lat) || isNaN(lng)) {
            console.error('無效的坐標值:', latitude, longitude);
            return;
        }
        
        // 檢查 map 元素是否存在
        const mapElement = document.getElementById("map");
        if (!mapElement) {
            console.error('找不到地圖容器元素 #map');
            return;
        }
        
        // 位置坐標
        const position = { lat: lat, lng: lng };
        
        // 創建地圖
        const map = new google.maps.Map(mapElement, {
            zoom: 15,
            center: position
        });

        // 創建標記
        const marker = new google.maps.Marker({
            map: map,
            position: position,
            title: "活動位置"
        });
    }
	
	
	// 全局變數儲存坐標
	  window.eventCoords = {
	      latitude: null,
	      longitude: null
	  };
		
	// 回調函數，當 Google Maps API 載入完成時調用
		function initMapCallback() {
		    console.log("Google Maps API 已載入");
		    if (window.eventCoords.latitude && window.eventCoords.longitude) {
		        console.log("初始化地圖，坐標:", window.eventCoords);
		        initMap(window.eventCoords.latitude, window.eventCoords.longitude);
		    }
		}
		
		// 透過後端取得 Google Maps API key
		 $.get("/api/config/google-maps-key", function(response) {
		      const apiKey = response.key;
		       // 使用script標籤載入 Google Maps API
		       const script = document.createElement("script");
		       script.src = `https://maps.googleapis.com/maps/api/js?key=${apiKey}&callback=initMapCallback`;
		       script.async = true;
		       script.defer = true;
		       document.head.appendChild(script);
		 });