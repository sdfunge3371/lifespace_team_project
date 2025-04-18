let showMapCheckbox, mapContainer, filterButton, filterPanel, spacesContainer;
let priceRange, minPriceDisplay, maxPriceDisplay, distanceRange;
let minDistanceDisplay, maxDistanceDisplay;
let targetSearchingElement;

let filters = {
    minPrice: 0,
    maxPrice: 2000,
    minDistance: 0,
    maxDistance: 10000,
    peopleCount: [],
    usage: []
};

// 地圖相關
let map;
let markersData = [];   // 座標位置顯示
let activeInfoWindow = null; // Keep track of the currently open InfoWindow


let activeCardId = null;
let spaces = [];
let usages = [];

// 初始化，網頁載入完成時要做的事
document.addEventListener('DOMContentLoaded', () => {
    showMapCheckbox = document.getElementById('show-map-checkbox');
    mapContainer = document.getElementById('map-container');
    filterButton = document.getElementById('filter-button');
    filterPanel = document.getElementById('filter-panel');
    spacesContainer = document.getElementById('spaces-container');
    priceRange = document.getElementById('price-range');
    minPriceDisplay = document.getElementById('min-price');
    maxPriceDisplay = document.getElementById('max-price');
    distanceRange = document.getElementById('distance-range');
    minDistanceDisplay = document.getElementById('min-distance');
    maxDistanceDisplay = document.getElementById('max-distance');
    targetSearchingElement = document.querySelector(".target-searching");

    // 初始化價格滑桿
    noUiSlider.create(priceRange, {
        start: [0, 2000],
        connect: true,
        step: 10,
        range: {
            min: 0,
            max: 2000
        },
        format: {
            to: value => Math.round(value),
            from: value => parseInt(value)
        }
    });

    priceRange.noUiSlider.on('update', (values) => {
        filters.minPrice = values[0];
        filters.maxPrice = values[1];
        minPriceDisplay.textContent = `$${filters.minPrice}`;
        maxPriceDisplay.textContent = `$${filters.maxPrice}`;
        // applyFilters();
    });

    // 初始化距離滑桿
    noUiSlider.create(distanceRange, {
        start: [0, 10000],
        connect: true,
        step: 100,
        range: {
            min: 0,
            max: 10000
        },
        format: {
            to: value => Math.round(value),
            from: value => parseInt(value)
        }
    });

    distanceRange.noUiSlider.on('update', (values) => {
        filters.minDistance = values[0];
        filters.maxDistance = values[1];
        minDistanceDisplay.textContent = filters.minDistance >= 1000
            ? `${(filters.minDistance / 1000).toFixed(1)}km`
            : `${filters.minDistance}m`;
        maxDistanceDisplay.textContent = filters.maxDistance >= 1000
            ? `${(filters.maxDistance / 1000).toFixed(1)}km`
            : `${filters.maxDistance}m`;
        // applyFilters();
    });


    fetchFavoriteSpaces();
    fetchSpaceUsages();
    fetchSpaces();
    setupEventListeners();

    // 取得目前位置
    setTimeout(getUserLocation, 500);
});

// 開始抓後端的資料
let branches = [];  // 用來存所有分店的資訊（地圖彈窗會用到）
function fetchSpaces() {
    fetch('/spaces')
        .then(response => response.json())
        .then(data => {
            spaces = data.map(space => ({
                spaceId: space.spaceId,
                name: space.spaceName,
                branchId: space.branchId,
                branchAddr: space.branchAddr,
                branchName: space.branchName,
                location: `${space.branchAddr}${space.spaceFloor + (space.spaceFloor ? "樓" : "")}`, // 可從 space.branchId 取得更多資訊
                price: space.spaceHourlyFee,
                rating: space.spaceRating,
                capacity: space.spacePeople,
                status: space.spaceStatus,
                branchStatus: space.branchStatus,
                usage: space.spaceUsageMaps.map(map => map.spaceUsage.spaceUsageName),
                photo: space.spacePhotos.map(map => map.photo),
                coordinates: [space.latitude, space.longitude], // 模擬座標，之後會利用google maps API抓出
                distance: null
            }));

            // 提取分店資訊
            branches = extractBranchInfo(spaces);

            console.log("抓後端時呼叫");

            // 如果已經獲取到用戶位置，則更新距離
            if (userLocation) {
                updateSpacesDistance();
            } else {
                renderSpaces(spaces);
            }

            if (map) {  // 地圖初始化後，更新座標
                updateMapMarkers(branches);
            }
        })
        .catch(error => console.error('取得空間資料失敗:', error));
}

// 產生空間的card
function renderSpaces(spacesToRender) {
    spacesContainer.innerHTML = '';

    // 利用迴圈一個一個生出資料
    spacesToRender.forEach(space => {
        console.log(space.name);

        if (space.status === 0 || space.branchStatus === 0) {    // 如果是分點或空間「未上架」，則不生成此空間資料
            return;   // forEach需要用return以執行迴圈continue功能
        }

        const spaceCard = document.createElement('div');
        spaceCard.className = 'space-card';
        spaceCard.dataset.id = space.spaceId;
        spaceCard.dataset.branchId = space.branchId;

        // 偵測該空間是否已加入該會員的最愛清單
        const isFavorite = favoriteSpaceIds.includes(space.spaceId);

        // 處理目前的距離
        let distanceText = '';
        if (space.distance != null) {
            distanceText = space.distance < 1000 ? `${space.distance}m` : `${(space.distance / 1000).toFixed(1)}km`;
        }

        spaceCard.innerHTML = `
            <div class="space-image">    
                <img src="${getFirstPhoto(space.photo)}" alt="空間圖片">
            </div>
            <div class="space-info">
                <div class="space-title">
                    <span>${space.name}</span>
                    <button class="favorite-btn" data-id="${space.spaceId}">
                        <i class="${isFavorite ? 'fas' : 'far'} fa-heart"></i>
                    </button>
                </div>
                <div class="space-location">
                    <div class="location-text">
                        <i class="fas fa-map-marker-alt"></i> ${distanceText}
                    </div>
                    <div class="people-count">
                        <i class="fas fa-user"></i> ${space.capacity}
                    </div>
                </div>
                <div class="space-rating">
                    <span class="space-price">$${space.price}/hr</span>
                    <div class="rating-stars">
                        <i class="fas fa-star"></i> ${space.rating.toFixed(1)}</div>
                </div>
            </div>
        `;
        spacesContainer.appendChild(spaceCard);

        // 地圖放大
        spaceCard.addEventListener('click', () => {
            if (showMapCheckbox.checked && space.branchId) {
                highlightBranchMarker(space.branchId);
            }
        });

        // 加入/移除最愛
        spaceCard.querySelector('.favorite-btn').addEventListener('click', (event) => {
            event.stopPropagation();

            const icon = event.currentTarget.querySelector('i');
            const button = event.currentTarget;
            const spaceId = button.dataset.id;
            const isFavorite = icon.classList.contains("fas");  // 是否已加入最愛

            if (!isFavorite) {
                // 加入最愛
                fetch(`/favorite-space/${spaceId}`, {
                    method: "POST",
                    credentials: "include"
                }).then(res => {
                    icon.classList.remove("far");
                    icon.classList.add("fas");
                    button.classList.add("active");
                    showToast(`已將「${space.name}」加入最愛`);
                }).catch(error => {
                    alert("加入最愛失敗");
                    console.log("加入最愛失敗：", error);
                    }
                )
            } else {
                // 移除最愛
                fetch(`/favorite-space/${spaceId}`, {
                    method: "DELETE",
                    credentials: "include"
                }).then(res => {
                    icon.classList.remove("fas");
                    icon.classList.add("far");
                    button.classList.remove("active");
                    showToast(`已將「${space.name}」移除最愛`);
                }).catch(error => {
                        alert("移除最愛失敗");
                        console.log("移除最愛失敗：", error);
                    }
                )
            }
        });

        // 點擊卡片後，跳轉到個別空間頁面
        spaceCard.addEventListener('click', () => {
            window.location.href = `individual_space.html?spaceId=${space.spaceId}`;
        });
    });
    checkLoginAndToggleHearts();
}

function getFirstPhoto(photo) {
    if (photo.length === 0) {
        return "default.jpg";
    }
    return "data:image/jpeg;base64," + photo[0];
}

function fetchSpaceUsages() {
    fetch("/space-usages")
        .then(response => response.json())
        .then(data => {
            usages = data.map(space => space.spaceUsageName);
            renderUsages(usages);
        })
}

function renderUsages(usages) {
    const usageOptions = document.querySelector(".usage-options");
    usageOptions.innerHTML = '';

    usages.forEach(usage => {
        // 建立 label 元素
        const label = document.createElement("label");
        label.classList.add("checkbox-container");
        label.textContent = usage;

        // 建立 input 元素
        const input = document.createElement("input");
        input.type = "checkbox";
        input.name = "usage";
        input.value = usage;

        // 建立 span 元素（樣式勾選用）
        const span = document.createElement("span");
        span.classList.add("checkmark");

        // 把 input 和 span 加到 label 裡
        label.appendChild(input);
        label.appendChild(span);

        // 把 label 加到 usageOptions 容器裡
        usageOptions.appendChild(label);
    })
}

let favoriteSpaceIds = [];

function checkLoginAndToggleHearts() {
    fetch("/member/profile", {
        method: "GET",
        credentials: "include"
    })
        .then(res => {
            if (res.status === 401) {
                // 尚未登入，隱藏所有愛心按鈕
                document.querySelectorAll('.favorite-btn').forEach(btn => {
                    btn.style.display = 'none';
                });
            }
        })
        .catch(err => {
            console.error("檢查登入失敗", err);
        });
}

function fetchFavoriteSpaces() {
    fetch("/favorite-space", {
        method: "GET",
        credentials: 'include'
    })
        .then(res => {
            return res.json();
        })
        .then (data => {
            favoriteSpaceIds = data.map(item => item.spaceId);
        })
        .catch(error => console.error("載入最愛失敗", error));
}

// ============= 地圖相關 =============
let userLocation = null;
let locationRequestInProgress = false;
let locationRetries = 0;
const MAX_LOCATION_RETRIES = 3;
const LOCATION_CACHE_KEY = 'user_location_cache';
const LOCATION_CACHE_EXPIRY = 30 * 60 * 1000; // 30分鐘，單位為毫秒


// ======= 取得定位 =======

// 抓快取中的地點
function getCachedLocation() {
    const cachedLocationData = localStorage.getItem(LOCATION_CACHE_KEY);
    if (!cachedLocationData) return null;

    try {
        const { location, timestamp } = JSON.parse(cachedLocationData);
        // 檢查緩存是否過期
        if (Date.now() - timestamp < LOCATION_CACHE_EXPIRY) {
            return location;
        }
    } catch (error) {
        console.warn('無法解析位置數據:', error);
    }

    // 緩存過期或無效，清除它
    localStorage.removeItem(LOCATION_CACHE_KEY);
    return null;
}

function cacheLocation(location) {
    const locationData = {
        location: location,
        timestamp: Date.now()
    };
    localStorage.setItem(LOCATION_CACHE_KEY, JSON.stringify(locationData));
}

function getUserLocation() {
    // 如果已經有位置數據或正在獲取中，直接返回
    if (userLocation || locationRequestInProgress) {
        return;
    }

    locationRequestInProgress = true;

    // 首先嘗試使用cache內存過的位置
    const cachedLocation = getCachedLocation();
    if (cachedLocation) {
        userLocation = cachedLocation;
        updateSpacesDistance();
        updateMapIfInitialized();
        locationRequestInProgress = false;
        return;
    }

    // 如果cache無效，使用瀏覽器的定位API
    if (navigator.geolocation) {
        const geoOptions = {
            enableHighAccuracy: true,
            timeout: 15000,       // 增加超時時間
            maximumAge: 300000    // cache保持5分鐘
        };

        navigator.geolocation.getCurrentPosition(
            (position) => {
                locationRequestInProgress = false;
                locationRetries = 0;

                userLocation = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude
                };

                // 緩存位置數據
                cacheLocation(userLocation);

                // 更新距離和地圖
                updateSpacesDistance();
                updateMapIfInitialized();
            },
            (error) => {
                locationRequestInProgress = false;

                console.warn(`定位失敗 (${locationRetries + 1}/${MAX_LOCATION_RETRIES}): ${error.message}`);

                // 如果還有重試次數，則重試
                if (locationRetries < MAX_LOCATION_RETRIES) {
                    locationRetries++;
                    setTimeout(getUserLocation, 1000); // 1秒後重試
                } else {
                    console.error(`達到最大重試次數 (${MAX_LOCATION_RETRIES})，無法獲取位置`);
                    // 使用默認位置（例如台北市中心）
                    userLocation = { lat: 25.0497, lng: 121.5380 };
                    console.log("getUserLocation時呼叫");
                    renderSpaces(spaces);
                }
            },
            geoOptions
        );
    } else {
        console.warn("瀏覽器不支援地理定位");
        locationRequestInProgress = false;
        console.log("getUserLocation時呼叫");
        renderSpaces(spaces);
    }
}

function updateMapIfInitialized() {
    if (map && userLocation) {
        map.setCenter(userLocation);
    }
}

function updateSpacesDistance() {
    if (!userLocation) return;
    spaces.forEach(space => {
        try {
            if (isValidCoordinates(space.coordinates)) {
                space.distance = calculateDistance(
                    userLocation.lat,
                    userLocation.lng,
                    parseFloat(space.coordinates[0]),
                    parseFloat(space.coordinates[1])
                );
            } else {
                space.distance = null;
                console.warn(`空間 ${space.spaceId} 座標無效:`, space.coordinates);
            }
        } catch (error) {
            space.distance = null;
            console.error(`計算空間 ${space.spaceId} 距離時出錯:`, error);
        }
    });

    // 更新完距離後重新渲染空間列表
    console.log("updateSpacesDistance時呼叫");
    renderSpaces(spaces);
    // applyFilters();
}

function calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371e3; // 地球半徑（公尺）
    const a1 = lat1 * Math.PI / 180;
    const a2 = lat2 * Math.PI / 180;
    const da = (lat2 - lat1) * Math.PI / 180;
    const dl = (lon2 - lon1) * Math.PI / 180;

    const a = Math.sin(da / 2) * Math.sin(da / 2) +
        Math.cos(a1) * Math.cos(a2) *
        Math.sin(dl / 2) * Math.sin(dl / 2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return Math.round(R * c); // 單位：公尺
}

// ======= 初始化地圖 =======

function initMap() {
    if (map)
        return;

    // 載入地圖
    try {
        const Map = google.maps.Map;   // 導入Google Maps

        map = new Map(document.getElementById("map"), {
            center: { lat: 25.0497, lng: 121.5380 },   // 預設以台北市為中心，下面定位時才會改
            zoom: 13,
            mapId: "37060db895f0c169",
            disableDefaultUI: true,
            zoomControl: true
        });

        // 如果成功定位，則設置地圖到中心
        if (userLocation) {
            map.setCenter(userLocation);
        }

        // 更新分點地圖標記
        if (branches && branches.length > 0) {
            updateMapMarkers(branches);
        }


    } catch (error) {
        console.error("Error initializing Google Map:", error);
        alert("無法載入地圖，請稍後再試。");
    }
}
window.initMap = initMap;

// 取得分點位置資訊
function extractBranchInfo(spacesData) {
    const branchMap = new Map(); // 使用 Map 避免重複


    spacesData.forEach(space => {
        // 假設 space 有 branchId 和 branchAddr 屬性
        // 如果沒有 branchId, 則使用座標作為識別
        if (space.branchStatus === 0) {
            console.log("分點已下架");
            return;
        }

        const branchId = space.branchId || `loc_${space.coordinates[0]}_${space.coordinates[1]}`;
        const branchName = space.branchName;
        const branchAddr = space.branchAddr || space.location;

        // 如果座標無效，則跳過
        if (!isValidCoordinates(space.coordinates)) {
            return;
        }

        // 如果該分店尚未加入 Map，則加入
        if (!branchMap.has(branchId)) {
            branchMap.set(branchId, {
                branchId: branchId,
                name: branchName,
                address: branchAddr,
                coordinates: space.coordinates,
                spaces: []
            });
        }

        // 將空間加入該分店的空間列表
        branchMap.get(branchId).spaces.push(space.spaceId);
    });

    // 將 Map 轉換為陣列
    return Array.from(branchMap.values());
}

function updateMapMarkers(branchesList) {
    if (!map) {
        console.warn("還沒初始化地圖");
        return;
    }

    const AdvancedMarkerElement = google.maps.marker?.AdvancedMarkerElement;
    const InfoWindow = google.maps.InfoWindow;
    const Size = google.maps.Size;

    // 若資訊小彈窗開著，把他關了，並清除所有資訊小彈窗的資料
    if (activeInfoWindow) {
        activeInfoWindow.close();
        activeInfoWindow = null;
    }

    // 清除所有座標
    markersData.forEach(data => {
        if (data.markerElement) {
            data.markerElement.map = null;
        }
    });
    markersData = [];

    // 建立新的座標、資訊
    branchesList.forEach(branch => {
        // 座標超出範圍之檢查
        if (!isValidCoordinates(branch.coordinates)) {
            console.warn(`Skipping marker for branch "${branch.name}" due to invalid coordinates:`, branch.coordinates);
            return;
        }

        // 建立座標
        const position = {
            lat: parseFloat(branch.coordinates[0]),
            lng: parseFloat(branch.coordinates[1])
        };

        let markerElement;
        try {
            markerElement = new AdvancedMarkerElement({
                position: position,
                map: map,
                title: branch.name
            });
        } catch (error) {
            console.error(`Error creating AdvancedMarkerElement for branch ${branch.branchId}:`, error, "Position:", position);
            return;
        }

        // 建立資訊小彈窗
        const spacesCount = branch.spaces.length;
        const infoWindowContent = `
            <div style="min-width: 150px;">
                <h4 style="margin: 5px 0;">${branch.name || '未命名分店'}</h4>
                <p style="margin: 5px 0;">${branch.address || '未命名分店'}</p>
<!--                <p style="margin: 5px 0;">包含 ${spacesCount} 個空間</p>-->
                <button id="filter-branch-btn" 
                    style="background-color: #4CAF50; color: white; border: none; padding: 5px 10px; cursor: pointer; border-radius: 4px; margin-top: 5px;">
                    查看此分店空間
                </button>
            </div>
        `;

        const infoWindow = new InfoWindow({
            content: infoWindowContent,
            pixelOffset: new Size(0, -10)
        });


        // 點擊座標時觸發
        let isFilterClickBound = false;

        markerElement.addEventListener("mouseenter", () => {
            if (activeInfoWindow) {
                activeInfoWindow.close();
            }
            infoWindow.open({ map: map, anchor: markerElement });
            activeInfoWindow = infoWindow;

            if (!isFilterClickBound) {
                setTimeout(() => {
                    const filterBtn = document.getElementById('filter-branch-btn');
                    if (filterBtn) {
                        filterBtn.addEventListener('click', () => {
                            filterSpacesByBranch(branch.branchId, branch.spaces);
                            infoWindow.close();
                        });
                        isFilterClickBound = true;  // 設定為已綁定
                    }
                }, 100);
            }
        });

        // 儲存座標、資訊彈窗、spaceId
        markersData.push({
            markerElement: markerElement,
            infoWindow: infoWindow,
            branchId: branch.branchId,
            spaces: branch.spaces
        });
    });
}

// 根據branchId篩選空間
function filterSpacesByBranch(branchId, branchSpaces) {
    // 篩選出該分店的空間
    const filteredSpaces = spaces.filter(space =>
        branchSpaces.includes(space.spaceId)
    );

    // 如果沒有空間，則顯示提示
    if (filteredSpaces.length === 0) {
        alert("此分店目前沒有可用空間。");
        return;
    }

    // 重置其他篩選條件
    document.querySelectorAll(".reset-button").forEach(button => {
        button.click();
    });
    document.querySelectorAll('input[type="checkbox"][name="people"]').forEach(checkbox => {
        checkbox.checked = false;
    });
    document.querySelectorAll('input[type="checkbox"][name="usage"]').forEach(checkbox => {
        checkbox.checked = false;
    });

    // 顯示篩選結果
    console.log("filterSpacesByBranch時呼叫");
    renderSpaces(filteredSpaces);
}

function highlightBranchMarker(branchId) {
    if (!map) return;

    const data = markersData.find(d => d.branchId === branchId);
    if (data && data.markerElement) {
        const position = data.markerElement.position;
        if(position) {
            map.setCenter(position);
            map.setZoom(15);

            // 確保一次只能開一個彈窗
            if (activeInfoWindow) {
                activeInfoWindow.close();
            }
            data.infoWindow.open({ map: map, anchor: data.markerElement });
            activeInfoWindow = data.infoWindow;

            // 添加按鈕點擊事件
            setTimeout(() => {
                const filterBtn = document.getElementById('filter-branch-btn');
                if (filterBtn) {
                    filterBtn.addEventListener('click', () => {
                        // 篩選出該分店的空間
                        filterSpacesByBranch(branchId, data.spaces);
                        // 關閉資訊視窗
                        data.infoWindow.close();
                    });
                }
            }, 100);
        } else {
            console.warn(`Could not get position for marker with branchId: ${branchId}`);
        }
    } else {
        console.warn(`Marker data not found for branchId: ${branchId}`);
    }
}

// 經緯度檢查
function isValidCoordinates(coords) {
    return Array.isArray(coords) &&
        coords.length === 2 &&
        !isNaN(parseFloat(coords[0])) && isFinite(coords[0]) && Math.abs(coords[0]) <= 90 &&
        !isNaN(parseFloat(coords[1])) && isFinite(coords[1]) && Math.abs(coords[1]) <= 180;
}

// 點擊座標時，該空間card會有光暈
function highlightCard(spaceId) {
    document.querySelectorAll('.space-card').forEach(card => card.style.boxShadow = 'none');
    const card = document.querySelector(`.space-card[data-id="${spaceId}"]`);
    if (card) {
        card.style.boxShadow = '0 0 10px #4CAF50';
        card.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
    activeCardId = spaceId;
}

// ============= Event Listeners集中設定 =============
function setupEventListeners() {
    // 顯示/隱藏地圖
    showMapCheckbox.addEventListener('change', () => {
        if (showMapCheckbox.checked) {
            mapContainer.classList.remove('hidden');
            setTimeout(() => google.maps.event.trigger(map, 'resize'), 100);
        } else {
            mapContainer.classList.add('hidden');
        }
    });

    // ======= 篩選條件相關 =======
    // 篩選條件
    filterButton.addEventListener('click', () => {
        filterPanel.classList.toggle('hidden');
    });

    // 價錢範圍
    priceRange.addEventListener('input', () => {
        filters.maxPrice = parseInt(priceRange.value);
        maxPriceDisplay.textContent = `$${filters.maxPrice}`;
        // applyFilters();
    });

    // 距離範圍
    distanceRange.addEventListener('input', () => {
        filters.maxDistance = parseInt(distanceRange.value);
        maxDistanceDisplay.textContent = filters.maxDistance >= 1000
            ? `${(filters.maxDistance / 1000).toFixed(1)}km`
            : `${filters.maxDistance}m`;
        // applyFilters();
    });

    // 人數勾選
    document.querySelectorAll('input[name="people"]').forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            filters.peopleCount = Array.from(document.querySelectorAll('input[name="people"]:checked')).map(input => input.value);
            // applyFilters();
        });
    });

    // 重置篩選
    document.querySelectorAll('.reset-button').forEach(button => {
        button.addEventListener('click', event => {
            const parentClass = event.currentTarget.parentElement.className;
            if (parentClass.includes('price-range')) {
                priceRange.noUiSlider.set([0, 2000]);
            } else if (parentClass.includes('distance-range')) {
                distanceRange.noUiSlider.set([0, 10000]);
            }
            // applyFilters();
        });
    });

    // 用途篩選
    document.querySelectorAll('input[name="usage"]').forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            filters.usage = Array.from(document.querySelectorAll('input[name="usage"]:checked')).map(input => input.value);
            // applyFilters();
        });
    });

    // 依上述條件搜尋
    document.getElementById('apply-filter-btn').addEventListener('click', () => {
        applyFilters();
    });


    // === 篩選條件相關 END ===
}

// ========== 執行篩選 ==========
function applyFilters() {
    const filteredSpaces = spaces.filter(space => {
        // 篩選價格
        if (space.price < filters.minPrice || space.price > filters.maxPrice) return false;

        // 篩選距離
        if (userLocation && space.distance != null) {
            // 檢查距離是否在指定範圍內
            if (space.distance < filters.minDistance || space.distance > filters.maxDistance) {
                return false;
            }
        }


        if (filters.peopleCount.length > 0) {
            let passes = false;
            for (const range of filters.peopleCount) {
                if ((range === '2' && space.capacity <= 2) ||
                    (range === '2-6' && space.capacity > 2 && space.capacity <= 6) ||
                    (range === '7-9' && space.capacity >= 7 && space.capacity <= 9) ||
                    (range === '10-19' && space.capacity >= 10 && space.capacity <= 19) ||
                    (range === '20' && space.capacity >= 20)) {
                    passes = true;
                    break;
                }
            }
            if (!passes) return false;
        }

        if (filters.usage.length > 0) {
            console.log(space.usage);
            if (!filters.usage.some(u => space.usage.includes(u))) return false;
        }

        return true;
    });
    renderSpaces(filteredSpaces);
}


// ========= 搜尋相關 =========

document.querySelector(".search-button").addEventListener("click", function(e) {
    e.preventDefault();

    const keyword = document.querySelector(".search-input").value.trim();
    const date = document.getElementById("datepicker").value.trim();
    let startTime = document.getElementById("start-time").value;
    let endTime = document.getElementById("end-time").value;
    let targetSearching = "正在尋找 ";


    if (!keyword && !date) {
        alert("請至少輸入關鍵字或選擇日期！");
        return;
    }

    if (keyword) {
        targetSearching += `含有「${keyword}」`;
    }

    if (date) {
        if (keyword) targetSearching += ", ";
        targetSearching += `可預訂時間為 ${date} `;
    }

    if (!startTime || startTime === "選擇開始時間") {
        startTime = "08:00";
    } else {
        targetSearching += `${startTime} `;
    }

    if (!endTime || endTime === "請先選擇開始時間" || endTime === "選擇結束時間") {
        endTime = "22:00";
    } else {
        targetSearching += `~ ${endTime}`;
    }

    targetSearching += " 的空間";


    // 設定URL參數
    const queryParams = new URLSearchParams();
    if (keyword) queryParams.append("keyword", keyword);
    if (date) queryParams.append("date", date);
    queryParams.append("startTime", startTime);
    queryParams.append("endTime", endTime);


    // 從後端開始抓符合條件的空間
    fetch(`/spaces/available?${queryParams.toString()}`)
        .then(response => {
            if (!response.ok) {
                return response.json().catch(() => null).then(errorData => {
                    if (errorData && errorData.message) {
                        throw new Error(errorData.message);
                    }
                });
            }
            return response.json();
        })
        .then(data => {
            // 將搜尋到的空間資料轉換成前端需要的格式
            const searchedSpaces = data.map(space => ({
                spaceId: space.spaceId,
                name: space.spaceName,
                location: `台北市松山區${space.spaceFloor}`,
                price: space.spaceHourlyFee,
                rating: space.spaceRating,
                capacity: space.spacePeople,
                status: space.spaceStatus,
                usage: space.spaceUsageMaps.map(map => map.spaceUsage.spaceUsageName),
                photo: space.spacePhotos.map(map => map.photo),
                coordinates: [space.latitude, space.longitude],
                distance: null
            }));

            // 重設所有篩選條件
            document.querySelectorAll(".reset-button").forEach(button => {
                button.click();
            });

            spaces = searchedSpaces;

            targetSearchingElement.innerHTML = targetSearching;

            console.log("按下搜尋時呼叫");
            if (userLocation) {
                updateSpacesDistance();
            } else {
                renderSpaces(spaces);
            }

        })
        .catch(error => {
            alert(error.message);
        });
});

// 按Enter也可以搜尋
document.querySelector(".search-input").addEventListener("keydown", function (e) {
    if (e.key === "Enter") {
        document.querySelector(".search-button").click();
    }
});

function showToast(message) {
    const toastContainer = document.getElementById('toast-container');
    const toast = document.createElement("div");
    toast.className = 'toast';
    toast.textContent = message;

    toastContainer.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 3000);
}