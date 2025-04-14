// 全局變量
   let currentPage = 0; // 當前頁碼 (後端分頁從0開始)
   let totalPages = 1; // 總頁數
   const pageSize = 5; // 每頁顯示的事件數量，與後端默認一致
   let currentDisplayMode = 'popular'; // 'popular' 或 'search'
   let currentTitle = '熱門推薦'; // 當前顯示的標題
   
   // 搜尋條件變量
   let searchParams = {
       eventName: "",
       startTime: "",
       endTime: "",
       category: "",
       branch: ""
   };
   
   // 當文檔加載完成時執行
   document.addEventListener('DOMContentLoaded', function () {
       // 獲取所有活動類別
       fetchAllEventsCategory();
       
       // 獲取所有活動地點
       fetchAllBranches();
       
       // 獲取所有活動
       fetchAllEvents();

       // 綁定搜尋按鈕點擊事件
       document.getElementById('search-btn').addEventListener('click', function () {
           fetchEventsByConditions();
       });

       // 綁定清除按鈕點擊事件
       document.getElementById('clear-btn').addEventListener('click', function () {
           clearSearchForm();
           resetToPopularEvents();
       });
   });

   // 獲取所有活動類別
   async function fetchAllEventsCategory() {
       // 使用API端點路徑
       const apiUrl = 'http://localhost:8080/lifespace/event/getAllCategories';

       console.log('正在嘗試獲取活動類別數據，API 路徑:', apiUrl);

       fetch(apiUrl)
           .then(response => {
               console.log('API 響應狀態:', response.status);
               if (!response.ok) {
                   throw new Error('網路回應不正常，狀態碼: ' + response.status);
               }
               return response.json();
           })
           .then(categories => {
               // 顯示活動類別在條件搜尋區域
               displayCategories(categories);
               displayCategoriesOwl(categories);
           })
           .catch(error => {
               console.error('獲取活動類別時出錯:', error);
           });
   }

   function displayCategories(categories) {
       // 獲取活動類別下拉選單
       const categorySelect = document.getElementById('category');
       
       // 先清空原有的選項（除了預設選項）
       categorySelect.innerHTML = '<option value="">所有類別</option>';
  
       // 為每個類別添加 option
       categories.forEach(category => {
           const newCategory = `<option value=${category.eventCategoryId}>${category.eventCategoryName}</option>`;
           categorySelect.innerHTML = categorySelect.innerHTML + newCategory;  
       });   
   }
   
   function displayCategoriesOwl(categories) {
       // 獲取類別容器
       const tagContainer = document.querySelector('.tag-container');
       
       // 清空原有內容
       tagContainer.innerHTML = '';
       
       // 動態添加類別標籤
       categories.forEach(category => {
           const tagDiv = document.createElement('div');
           tagDiv.className = 'tag';
           tagDiv.textContent = category.eventCategoryName;
           
           // 可以添加點擊事件，點擊後自動篩選該類別
           tagDiv.addEventListener('click', () => {
               // 設定類別下拉選單的值
               document.getElementById('category').value = category.eventCategoryId;
               // 觸發搜尋
               fetchEventsByConditions();
           });
           
           tagContainer.appendChild(tagDiv);
       });	
       
       // 所有標籤添加完成後，初始化輪播功能
       initializeTagCarousel();
   }
   
   // 初始化標籤輪播功能
   function initializeTagCarousel() {
       const tagContainer = $(".tag-container");
       
       // 確保有標籤元素存在
       if ($(".tag").length > 0) {
           const tagWidth = $(".tag").outerWidth(true);
           
           // 先解除現有的事件綁定，再重新綁定，避免重複綁定
           $(".left-tag-btn").off('click').on('click', function() {
               tagContainer.animate({ scrollLeft: "-=" + tagWidth * 1.5 }, 100);
           });
           
           $(".right-tag-btn").off('click').on('click', function() {
               tagContainer.animate({ scrollLeft: "+=" + tagWidth * 1.5 }, 100);
           });
           
           console.log("標籤輪播功能已初始化，標籤寬度：", tagWidth);
       } else {
           console.warn("沒有找到標籤元素，輪播功能未初始化");
       }
   }

   // 獲取所有地點
   async function fetchAllBranches() {
       // 使用API端點路徑
       const apiUrl = 'http://localhost:8080/branch/getAll';

       fetch(apiUrl)
           .then(response => {
               console.log('API 響應狀態:', response.status);
               if (!response.ok) {
                   throw new Error('網路回應不正常，狀態碼: ' + response.status);
               }
               return response.json();
           })
           .then(branches => {
               // 顯示活動地點在條件搜尋區域
               displayBranches(branches);
           })
           .catch(error => {
               console.error('獲取活動地點時出錯:', error);
           });	
   }

   function displayBranches(branches) {
       const branchSelect = document.getElementById('location');
       branchSelect.innerHTML = '<option value="">所有地點</option>';
       branches.forEach(branch => {
           const newBranch = `<option value=${branch.branchId}>${branch.branchName}</option>`;
           branchSelect.innerHTML = branchSelect.innerHTML + newBranch;  
       });   
   }
   
   // 清除搜尋表單
   function clearSearchForm() {
       document.getElementById('search').value = '';
       document.getElementById('eventStartTime').value = '';
       document.getElementById('eventEndTime').value = '';
       document.getElementById('location').value = '';
       document.getElementById('category').value = ''; 
   }

   // 重置到熱門推薦
   function resetToPopularEvents() {
       currentTitle = '熱門推薦';
       // 將標題改回「熱門推薦」
       const slideOuterTitle = document.querySelector('.slide-outer h2');
       slideOuterTitle.textContent = currentTitle;

       // 重設搜尋參數
       searchParams = {
           eventName: "",
           startTime: "",
           endTime: "",
           category: "",
           branch: ""
       };

       // 重新獲取所有活動 (從第一頁開始)
       fetchAllEvents(0);

       // 更新顯示模式
       currentDisplayMode = 'popular';
   }

   // 修改獲取所有活動的函數，支持後端分頁
   async function fetchAllEvents(page = 0) {
       // 使用API端點路徑，添加分頁參數
       const apiUrl = `http://localhost:8080/lifespace/event/getAll?page=${page}&size=${pageSize}`;

       console.log('正在嘗試獲取活動數據，API 路徑:', apiUrl);

       fetch(apiUrl)
           .then(response => {
               console.log('API 響應狀態:', response.status);
               if (!response.ok) {
                   throw new Error('網路回應不正常，狀態碼: ' + response.status);
               }
               return response.json();
           })
           .then(pageData => {
               console.log('成功獲取活動數據:', pageData);
               
               // 更新當前頁和總頁數
               currentPage = pageData.number;
               totalPages = pageData.totalPages;
               
               // 顯示分頁結果
               displayEvents(pageData.content, '熱門推薦', pageData);
           })
           .catch(error => {
               console.error('獲取活動時出錯:', error);
               
               // 顯示錯誤提示
               const eventsContainer = document.getElementById('events-container');
               eventsContainer.innerHTML = `
                   <div class="empty-state">
                       <i class="fas fa-exclamation-circle"></i>
                       <p>獲取活動資料時發生錯誤，請稍後再試</p>
                   </div>
               `;
               
               // 清空分頁控制
               document.getElementById('pagination-container').innerHTML = '';
           });
   }

   // 修改按條件搜尋活動的函數，支持後端分頁
   async function fetchEventsByConditions(page = 0) {
       // 獲取搜尋條件
       const searchValue = document.getElementById('search').value || "";
       const startTime = document.getElementById('eventStartTime').value || "";
       const endTime = document.getElementById('eventEndTime').value || "";
       const branch = document.getElementById('location').value || "";
       const category = document.getElementById('category').value || ""; 

       // 檢查是否有任何搜尋條件
       if (!searchValue && !startTime && !endTime && !branch && !category) {
           alert('請至少輸入一個搜尋條件');
           return;
       }

       // 保存搜尋條件，用於分頁
       searchParams = {
           eventName: searchValue,
           startTime: startTime,
           endTime: endTime,
           branch: branch,
           category: category,
       };

       // 構建 API URL，包含分頁參數
       const apiUrl = 'http://localhost:8080/lifespace/event/search/native?' + new URLSearchParams({
           ...searchParams,
           page: page,
           size: pageSize
       }).toString();

       // 避免中文關鍵字變亂碼，decodeURI
       const newUrl = decodeURI(apiUrl);
       console.log('正在嘗試獲取活動數據，API 路徑:', newUrl);

       try {
           const response = await fetch(newUrl);
           if (response.ok) {
               const pageData = await response.json(); // 後端回傳分頁格式的活動列表
               console.log('搜尋結果:', pageData);

               // 如果沒有結果
               if (pageData.content.length === 0) {
                   alert('沒有符合條件的活動');
                   
                   // 清空顯示區域並顯示無數據提示
                   const eventsContainer = document.getElementById('events-container');
                   eventsContainer.innerHTML = `
                       <div class="empty-state">
                           <i class="fas fa-search"></i>
                           <p>沒有符合條件的活動</p>
                       </div>
                   `;
                   
                   // 清空分頁控制
                   document.getElementById('pagination-container').innerHTML = '';
                   return;
               }

               // 更新當前頁和總頁數
               currentPage = pageData.number;
               totalPages = pageData.totalPages;
               
               // 更新標題和顯示模式
               currentTitle = '查詢結果';
               currentDisplayMode = 'search';
               
               // 顯示搜尋結果
               displayEvents(pageData.content, currentTitle, pageData);
           } else {
               alert('查詢活動失敗');
           }
       } catch (error) {
           console.error('Error:', error);
           alert('發生錯誤，請稍後再試');
       }
   }

   // 更新顯示函數，配合後端分頁
   function displayEvents(events, titleText, pageData) {
       // 更新標題
       const slideOuterTitle = document.querySelector('.slide-outer h2');
       slideOuterTitle.textContent = titleText;
       
       // 獲取顯示區域
       const eventsContainer = document.getElementById('events-container');
       const paginationContainer = document.getElementById('pagination-container');
       
       // 清空現有內容
       eventsContainer.innerHTML = '';
       
       // 如果沒有活動，顯示提示訊息
       if (events.length === 0) {
           const noEventsDiv = document.createElement('div');
           noEventsDiv.className = 'empty-state';
           noEventsDiv.innerHTML = `
               <i class="fas fa-calendar-times"></i>
               <p>沒有符合條件的活動</p>
           `;
           eventsContainer.appendChild(noEventsDiv);
           paginationContainer.innerHTML = ''; // 清空分頁控制
           return;
       }
       
       // 添加活動卡片
       events.forEach(event => {
           if (!event || !event.eventName) return;
           
           // 格式化日期和時間
           const formattedStartTime = event.eventStartTime ? formatDateAndTime(event.eventStartTime) : '時間未定';
           const formattedEndTime = event.eventEndTime ? formatDateAndTime(event.eventEndTime) : '';
           
           // 取得照片 URL
           let photoUrl = '';
           if (event.photoUrls && event.photoUrls.length > 0) {
               photoUrl = 'http://localhost:8080' + event.photoUrls[0]; // 顯示第一張照片
           } else {
               photoUrl = 'http://localhost:8080/images/default_for_event_and_space.jpg'; // 使用預設圖片
           }
           
           // 創建活動卡片
           const eventCard = document.createElement('div');
		   	   
           eventCard.className = 'event-container';
		   eventCard.style.cursor = 'pointer';
  
           eventCard.innerHTML = `
               <div class="event-img">
                   <img src="${photoUrl}" alt="${event.eventName}">
               </div>
               <div class="event-info">
                   <h4>${event.eventName}</h4>
                   <div class="event-time">
                       <i class="fas fa-clock"></i>
                       ${formattedStartTime} ${formattedEndTime ? ' - ' + formattedEndTime : ''}
                   </div>
                   <div class="event-category">
                       ${event.eventCategory ? event.eventCategory.eventCategoryName : '類別未定'}
                   </div>
				   <div class="event-location">
				   		<i class="fas fa-users"></i>
				   			  參與人數: ${event.numberOfParticipants}/${event.maximumOfParticipants}
				   	</div>
                   <div class="event-actions">
                       <a href="event_detail.html?eventId=${event.eventId}" class="action-button message-button">查看詳情</a>
                   </div>
               </div>
           `;
           	 
		   eventCard.addEventListener('click', () => {
		   	   	      window.location.href = `event_detail.html?eventId=${event.eventId}`;
		   	   });
			   
           eventsContainer.appendChild(eventCard);
       });
       
       // 生成分頁控制，使用後端返回的分頁信息
       if (pageData) {
           generatePagination(pageData.totalPages, pageData.number, paginationContainer);
       }
   }
   
   // 更新分頁控制函數，適配後端分頁
   function generatePagination(totalPages, currentPage, container) {
       container.innerHTML = '';
       
       // 如果只有一頁，不顯示分頁控制
       if (totalPages <= 1) {
           return;
       }
       
       // 上一頁按鈕
       const prevBtn = document.createElement('button');
       prevBtn.className = 'pagination-btn' + (currentPage === 0 ? ' disabled' : '');
       prevBtn.textContent = '上一頁';
       prevBtn.disabled = currentPage === 0;
       prevBtn.addEventListener('click', () => {
           if (currentPage > 0) {
               // 根據當前顯示模式調用不同的函數
               if (currentDisplayMode === 'popular') {
                   fetchAllEvents(currentPage - 1);
               } else {
                   fetchEventsByConditions(currentPage - 1);
               }
           }
       });
       container.appendChild(prevBtn);
       
       // 最多顯示5個頁碼按鈕
       // 注意：後端分頁從0開始，但顯示給用戶時從1開始
       let startPage = Math.max(0, currentPage - 2);
       let endPage = Math.min(totalPages - 1, startPage + 4);
       
       if (endPage - startPage < 4) {
           startPage = Math.max(0, endPage - 4);
       }
       
       // 頁碼按鈕
       for (let i = startPage; i <= endPage; i++) {
           const pageBtn = document.createElement('button');
           pageBtn.className = 'pagination-btn' + (i === currentPage ? ' active' : '');
           pageBtn.textContent = i + 1; // 顯示給用戶的頁碼從1開始
           pageBtn.addEventListener('click', () => {
               // 根據當前顯示模式調用不同的函數
               if (currentDisplayMode === 'popular') {
                   fetchAllEvents(i);
               } else {
                   fetchEventsByConditions(i);
               }
           });
           container.appendChild(pageBtn);
       }
       
       // 下一頁按鈕
       const nextBtn = document.createElement('button');
       nextBtn.className = 'pagination-btn' + (currentPage === totalPages - 1 ? ' disabled' : '');
       nextBtn.textContent = '下一頁';
       nextBtn.disabled = currentPage === totalPages - 1;
       nextBtn.addEventListener('click', () => {
           if (currentPage < totalPages - 1) {
               // 根據當前顯示模式調用不同的函數
               if (currentDisplayMode === 'popular') {
                   fetchAllEvents(currentPage + 1);
               } else {
                   fetchEventsByConditions(currentPage + 1);
               }
           }
       });
       container.appendChild(nextBtn);
   }
   
   //格式化日期時間函數
   function formatDateAndTime(dateTime) {
       const date = new Date(dateTime);
       const datePart = date.toLocaleDateString();
       const timePart = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
       return `${datePart} ${timePart}`;
   }