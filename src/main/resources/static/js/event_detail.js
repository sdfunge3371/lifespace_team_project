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
                const apiUrl = `http://localhost:8080/lifespace/event/getOne?eventId=${eventId}`;

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
                            <img class="img-fluid" src="${'http://localhost:8080' + photoUrl}" alt="${event.eventName}" style="width: 100%; height: 350px; object-fit: contain;">
                        </div>
                    `);
                                });
                            } else {
                                // 如果沒有圖片，使用默認圖片
                                carousel.append(`
                    <div class="owl-carousel-item">
                        <img src="http://localhost:8080/default.jpg" alt="預設圖片">
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
                var activityContent = $('#activity-description').html();

                // 將活動說明內容放入 Modal body
                $('#activityModalBody').html(activityContent);

                // 顯示 Modal
                $('#activityModal').modal('show');
            });
        });

        $(document).ready(function () {
            $("#register-btn").click(function () {
                let totalRegis = parseInt($("#total-regis").text());
                let RegisCount = parseInt($("#registered-count").text());
                if (RegisCount < totalRegis) {
                    // 報名成功
                    $("#successModal").modal("show");
                    $("#registered-count").text(RegisCount + 1); //更新人數
                    $("#cancell-regis-btn").prop('disabled', false);
                    $("#register-btn").prop('disabled', true);
                } else {
                    // 候補
                    $("#waitingModal").modal("show");
                    $("#waiting-num").text(RegisCount - totalRegis + 1);
                    $("#registered-count").text(RegisCount + 1);
                }
            });

            $("#waiting-cancel").click(function () {
                let RegisCount = parseInt($("#registered-count").text());
                $("#registered-count").text(RegisCount - 1);
                $("#waitingModal").modal("hide");
            });

            $("#cancell-regis-btn").click(function () {
                let RegisCount = parseInt($("#registered-count").text());
                $("#registered-count").text(RegisCount - 1);
                $("#cancellRegisModal").modal("show");
                $("#register-btn").prop('disabled', false);
                $("#cancell-regis-btn").prop('disabled', true);
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

