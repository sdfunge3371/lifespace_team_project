// ============ 網頁載入後要做的事情 ============

window.addEventListener('DOMContentLoaded', () => {
    // 初始化顯示第一個 tab，其餘隱藏
    tabs.forEach((tab, index) => {
        tab.style.display = index === 0 ? 'block' : 'none';
    });
});

// ========== 處理頁籤 ==========

const navLinks = document.querySelectorAll('.main-link');
const tabs = document.querySelectorAll('.tab');

navLinks.forEach(link => {
    link.addEventListener('click', function (e) {
        e.preventDefault();

        // 切換 active 類別
        navLinks.forEach(l => l.classList.remove('active'));
        this.classList.add('active');

        // 取得目標 tab id
        const targetId = this.getAttribute('data-target');

        // 顯示對應內容、隱藏其他
        tabs.forEach(tab => {
            if (tab.id === targetId) {
                tab.style.display = 'block';
            } else {
                tab.style.display = 'none';
            }
        });
    });
});

// =============空間評論 modal=============

// 處理評論排序下拉式清單
const sortButton = document.getElementById('sortButton');
const sortDropdown = document.getElementById('sortDropdown');
const sortOptions = document.querySelectorAll('.sort-option');

sortButton.addEventListener('click', function () {
    sortDropdown.classList.toggle('active');
});

sortOptions.forEach(option => {
    option.addEventListener('click', function () {
        sortButton.textContent = this.textContent;
        sortDropdown.classList.remove('active');
    });
});

// 關閉評論排序下拉清單 (Close dropdown when clicking outside)
document.addEventListener('click', function (event) {
    if (!event.target.closest('.sort-container')) {
        sortDropdown.classList.remove('active');
    }
});

// 設定空間評論model位置
function showModalAbsolute(modal, overlay) {

    // 取得當前捲動位置
    const scrollTop = window.scrollY || document.documentElement.scrollTop;
    const scrollLeft = window.scrollX || document.documentElement.scrollLeft;

    // 設定 modal 的位置
    modal.style.top = `${scrollTop + window.innerHeight / 2 - modal.offsetHeight / 2}px`;
    modal.style.left = `${scrollLeft + window.innerWidth / 2 - modal.offsetWidth / 2}px`;

    overlay.style.top = `${scrollTop - overlay.offsetHeight / 2}px`;

    // 顯示 modal 和背景遮罩
    modal.style.display = 'block';

    overlay.classList.remove("hidden");
}

function hideModalFixed(modal, overlay) {
    modal.style.display = "none";
    overlay.classList.add("hidden");
}

// 關閉空間評論modal
const modal = document.querySelector('.modal');
const overlay = document.querySelector(".overlay");
const closeButton = document.querySelector('.close-button');

const btnOpenModal = document.querySelector('.list-all-equip');


closeButton.addEventListener('click', function () {
    hideModalFixed(modal, overlay);
    document.body.style.overflow = '';
});

overlay.addEventListener("click", function () {
    hideModalFixed(modal, overlay);
    document.body.style.overflow = '';
})

btnOpenModal.addEventListener("click", function () {
    showModalAbsolute(modal, overlay);
    document.body.style.overflow = 'hidden';
})

// ===============愛心(我的最愛)切換===============
const heartBtn = document.getElementById('heart-fav-btn');
const heartIcon = document.getElementById('heart-fav-icon');

heartBtn.addEventListener('click', () => {
    if (heartIcon.classList.contains('fa-regular')) {
        heartIcon.classList.remove('fa-regular');
        heartIcon.classList.add('fa-solid');
    } else {
        heartIcon.classList.remove('fa-solid');
        heartIcon.classList.add('fa-regular');
    }
});

// ===============選擇時租、日租===============
function setActive(element, id) {
    // 清除所有按鈕的 active 樣式
    document.querySelectorAll('.rent-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    // 對當前被點擊的那個元素加上active class
    element.classList.add('active');

    // 對當前被點擊的那個radio設為checked
    document.getElementById(id).checked = true;

    // 若選擇時租，才可以選擇開始、結束時間
    const timeSelects = document.querySelector('.time-selects');
    if (id === 'daily') {
        timeSelects.style.display = 'none';
    } else {
        timeSelects.style.display = 'flex';
    }

    // 根據租用類型切換價格及下方費率之顯示
    const hourlyPrice = document.querySelector('.main-hourly-price');
    const dailyPrice = document.querySelector('.main-daily-price');

    const durationRow = document.getElementById('durationRow');
    const rate = document.getElementById('rate');  // 費率那一行

    if (id === 'daily') {
        // 大字費率顯示日租
        hourlyPrice.style.display = 'none';
        dailyPrice.style.display = 'block';

        // 隱藏使用時間
        durationRow.style.display = 'none';
        // 費率文字改成日租 (抓取上面 .main-daily-price 裡的值)
        const dailyPricetext = document.getElementById('daily-price').textContent;
        rate.innerHTML = `$ <span class="rate-span">${dailyPricetext}</span>/d`;
    } else {
        // 大字費率顯示時租
        hourlyPrice.style.display = 'block';
        dailyPrice.style.display = 'none';

        // 顯示使用時間
        durationRow.style.display = 'flex';
        // 費率文字改成時租 (抓取上面 .main-hourly-price 裡的值)
        const hourlyPricetext = document.getElementById('hourly-price').textContent;
        rate.innerHTML = `$ <span class="rate-span">${hourlyPricetext}</span>/hr`;
    }

    updateTotal();
}

// ================ 日期選擇顯示/隱藏 ================
const dateToggleButton = document.getElementById('dateToggleButton');
const calendarContainer = document.getElementById('calendarContainer');
const monthNames = ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'];
const calendarDays = document.getElementById('calendarDays');
const monthName = document.getElementById('monthName');
const yearElement = document.getElementById('year');
const prevMonthButton = document.getElementById('prevMonth');
const nextMonthButton = document.getElementById('nextMonth');

let currentDate = new Date();
let isCalendarVisible = false;

dateToggleButton.addEventListener('click', function () {

    if (isCalendarVisible) {
        // 隱藏日曆
        calendarContainer.classList.add('fade-out');
        setTimeout(() => {
            calendarContainer.style.display = 'none';
            calendarContainer.classList.remove('fade-out');
        }, 450);
    } else {
        // 顯示日曆
        calendarContainer.style.display = 'block';
        calendarContainer.classList.add('fade-in');

        // 確保日曆顯示時更新為當前月份
        renderCalendar(currentDate.getFullYear(), currentDate.getMonth());
    }

    isCalendarVisible = !isCalendarVisible;
});

function renderCalendar(year, month) {
    // 清空日曆日期
    calendarDays.innerHTML = '';

    // 更新月份和年份顯示
    monthName.textContent = monthNames[month];
    yearElement.textContent = year;

    // 獲取當月第一天和最後一天
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);

    // 前一個月的填充日期
    const startDay = firstDay.getDay();
    for (let i = 0; i < startDay; i++) {
        const emptyDay = document.createElement('div');
        emptyDay.classList.add('calendar-day');
        calendarDays.appendChild(emptyDay);
    }

    // 當月的日期
    for (let i = 1; i <= lastDay.getDate(); i++) {
        const dayElement = document.createElement('div');
        dayElement.classList.add('calendar-day');
        dayElement.textContent = i;

        // 標記今天
        const today = new Date();
        if (today.getDate() === i &&
            today.getMonth() === month &&
            today.getFullYear() === year) {
            dayElement.classList.add('current-day');
        }

        // 點擊事件
        dayElement.addEventListener('click', function () {
            const selectedDate = new Date(year, month, i);
            dateToggleButton.innerHTML = selectedDate.toLocaleDateString('zh-TW') + `
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                                    fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                    stroke-linejoin="round">
                                    <polyline points="6 9 12 15 18 9"></polyline>
                                    </svg>`;
            calendarContainer.classList.add('fade-out');
            setTimeout(() => {
                calendarContainer.style.display = 'none';
                calendarContainer.classList.remove('fade-out');
            }, 450);
            isCalendarVisible = !isCalendarVisible;
        });

        calendarDays.appendChild(dayElement);
    }
}

// 上個月按鈕
prevMonthButton.addEventListener('click', function () {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar(currentDate.getFullYear(), currentDate.getMonth());
});

// 下個月按鈕
nextMonthButton.addEventListener('click', function () {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar(currentDate.getFullYear(), currentDate.getMonth());
});

// ============開始、結束時間清單選擇============

/**
 * 切換顯示「開始時間」或「結束時間」的清單
 * type: 'start' 或 'end'
 * 結束時間需先選擇開始時間，且僅顯示大於開始時間的選項
 */
function toggleTimeSelect(type) {
    let containerId, buttonId, inputId;

    if (type === 'start') {
        // 在顯示開始時間前先檢查是否已選擇日期
        const dateToggleButton = document.getElementById('dateToggleButton');
        if (dateToggleButton.textContent.trim() === "選擇日期") {
            alert("請先選擇日期");
            return;
        }
        containerId = 'startTimeContainer';
        buttonId = 'startTimeButton';
        inputId = 'startTimeInput';
    } else if (type === 'end') {
        // 在顯示結束時間前先檢查是否已選擇開始時間
        const startButton = document.getElementById('startTimeButton');
        if (dateToggleButton.textContent.trim() === "選擇日期") {
            alert("請先選擇日期");
            return;
        }
        if (startButton.textContent.trim() === "開始時間") {
            alert("請先選擇開始時間");
            return;
        }
        containerId = 'endTimeContainer';
        buttonId = 'endTimeButton';
        inputId = 'endTimeInput';
    }

    const container = document.getElementById(containerId);

    // 若該容器還未生成清單（注意：因為註解或空白也可能導致 hasChildNodes() 為 true，可改用 children 判斷是否已經有元素子節點）
    if (container.children.length === 0) {
        if (type === 'start') {
            generateTimeOptions(containerId, buttonId, inputId);
        } else if (type === 'end') {
            // 傳入 selectedStartTime 作為下限，只顯示大於它的時間選項
            generateTimeOptions(containerId, buttonId, inputId, selectedStartTime);
        }
    }

    // 顯示/隱藏切換
    if (container.style.display === 'block') {
        container.style.display = 'none';
    } else {
        container.style.display = 'block';
    }
}

/**
 * 產生 24 小時、每 30 分鐘的清單
 * containerId: 容器的 ID（例如 'startTimeContainer'）
 * buttonId:    按鈕的 ID（例如 'startTimeButton'），用來更新顯示文字
 * inputId:     隱藏輸入欄的 ID (e.g. 'startTimeInput')
 * minTime (選填): 若有提供，僅顯示大於此時間的選項 (格式 "HH:MM")
 */

let selectedStartTime = null;

function generateTimeOptions(containerId, buttonId, inputId, minTime) {
    const container = document.getElementById(containerId);
    let html = '<ul class="time-list">';
    // 若有設定最小時間，轉換成分鐘數
    let minTimeMinutes = 0;
    if (minTime) {
        const parts = minTime.split(':');
        minTimeMinutes = parseInt(parts[0]) * 60 + parseInt(parts[1]);
    }

    // 顯示出8:00~22:00的開始時間選擇清單
    for (let hour = 8; hour <= 22; hour++) {
        for (let minute = 0; minute < 60; minute += 30) {
            // 若到 22:00 時，僅允許 minute 為 0
            if (hour === 22 && minute > 0) {
                break;
            }

            // 若提供最小時間，僅顯示大於該時間的選項
            let currentTimeMinutes = hour * 60 + minute;
            if (minTime && currentTimeMinutes <= minTimeMinutes) {
                continue;
            }

            // 補 0 成兩位數字
            const hh = hour.toString().padStart(2, '0');
            const mm = minute.toString().padStart(2, '0');
            const timeStr = `${hh}:${mm}`;

            // 點擊 li 時，呼叫 selectTime()，將時間帶入
            html += `
                <li onclick="selectTime('${containerId}', '${buttonId}', '${inputId}', '${timeStr}')">
                    ${timeStr}
                </li>`;
        }
    }

    html += '</ul>';
    container.innerHTML = html;
}

/**
 * 選擇時間後：
 * 1. 更新按鈕文字
 * 2. 隱藏時間清單
 * 3. 若為開始時間，記錄所選值，並重置結束時間選項
 * 4. 將時間存入hidden input
 */
function selectTime(containerId, buttonId, inputId, time) {
    const container = document.getElementById(containerId);
    const button = document.getElementById(buttonId);
    const input = document.getElementById(inputId);

    // 將使用者選到的時間字串 (例如 '09:30') 存入 hidden input
    input.value = time;

    // 更新按鈕上的文字（你可以改成放到 input 或其他地方）
    button.innerHTML = time + ` <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                                        fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                        stroke-linejoin="round">
                                        <polyline points="6 9 12 15 18 9"></polyline>
                                </svg>`;

    // 收起下拉選單
    container.style.display = 'none';

    // 如果為開始時間選擇，儲存所選時間並重置結束時間選項
    if (containerId === 'startTimeContainer') {
        selectedStartTime = time;   // selectedStartTime為全域變數
        // 重置結束時間按鈕文字與清單
        const endButton = document.getElementById('endTimeButton');
        endButton.innerHTML = '結束時間 <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 12 15 18 9"></polyline></svg>';
        document.getElementById('endTimeContainer').innerHTML = '';
    }

    // 呼叫計算函式
    updateTotal();
}

// ============租賃品項數量選擇============
function changeQuantity(id, change) {
    const input = document.getElementById(id);
    let value = parseInt(input.value) + change;
    if (value < 0) value = 0;
    input.value = value;
    updateTotal();
}

// ============計算總金額============
function updateTotal() {
    // 判斷目前是哪種租用類型
    const isDaily = document.getElementById('daily').checked;

    // 先抓取目前下方顯示的費率(因為 setActive 已經同步調整了 .rate-span)
    // 或者也可以直接抓 #daily-price / #hourly-price
    let rateValue = parseInt(document.querySelector('.rate-span').textContent);

    let durationHours = 0;
    if (!isDaily) {  // 若為時租
        // 解析開始時間與結束時間，並計算使用時數
        const startTimeStr = document.getElementById('startTimeInput').value; // e.g. "10:30"
        const endTimeStr = document.getElementById('endTimeInput').value;   // e.g. "15:00"
        const endButton = document.getElementById('endTimeButton');
    
        if (startTimeStr && endTimeStr && !endButton.innerHTML.startsWith("結束")) {  // 確保都已有值再做解析
            // 解析 "HH:MM"
            const [startHour, startMin] = startTimeStr.split(':').map(Number);
            const [endHour, endMin] = endTimeStr.split(':').map(Number);
    
            // 轉換成「總分鐘數」再相減
            const startTotalMin = startHour * 60 + startMin;
            const endTotalMin = endHour * 60 + endMin;
    
            // 若有需要，先判斷是否 end > start
            const diffMin = endTotalMin - startTotalMin;
            if (diffMin > 0) {
                durationHours = diffMin / 60; // 轉回小時
            }
            console.log(startTimeStr, endTimeStr, durationHours);
        }
    
        // 將「使用時間」顯示在網頁上
        document.querySelector('.duration-span').textContent = durationHours;
    } else {  // 若為日租
        document.querySelector('.duration-span').textContent = 0;
    }   


    // 計算租借品項
    const equip1 = parseInt(document.getElementById('equip1').value) * 30;
    const equip2 = parseInt(document.getElementById('equip2').value) * 50;
    const equip3 = parseInt(document.getElementById('equip3').value) * 100;
    const equipmentTotal = equip1 + equip2 + equip3;
    document.getElementById('equipmentCost').textContent = '$ ' + equipmentTotal;

    // 計算總金額
      // --- 空間使用費 & 總金額 ---
    let spaceCost = 0;   
    let total = 0;

    if (isDaily) {
        // 日租：直接取日租費率
        spaceCost = rateValue;
        total = spaceCost + equipmentTotal;  // 空間使用費 + 租借品項
    } else {
        // 時租：費率 × 使用時數
        spaceCost = rateValue * durationHours;
        total = spaceCost + equipmentTotal;
    }
    // const total = spaceTotal * durationHours + equipmentTotal;

    
    document.getElementById('spaceCost').textContent = '$ ' + spaceCost;
    document.getElementById('totalCost').textContent = '$ ' + total;
}