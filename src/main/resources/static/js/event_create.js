// 已上傳的照片數組
        let uploadedPhotos = [];
        const MAX_PHOTOS = 3;
	
		// 當文檔加載完成時執行
		document.addEventListener('DOMContentLoaded', function () {

		   //獲取表格活動類別
		    fetchAllEventsCategory();
			
			// 取得 URL 中的 orderId 參數並填入表單欄位
			const urlParams = new URLSearchParams(window.location.search);
			const orderId = urlParams.get("orderId");
			const orderStartStr = urlParams.get("orderStart");
			const orderEndStr = urlParams.get("orderEnd");
			
			if (orderId) {
				
			    const orderInput = document.getElementById("orderId");
				
				if (orderStartStr && orderEndStr) {

				        const orderStart = new Date(orderStartStr);
				        const orderEnd = new Date(orderEndStr);

				        // 顯示提示
				        const hint = document.getElementById("orderTimeHint");
				        hint.textContent = `本訂單租借時段為：${formatDateTime(orderStart)} 至 ${formatDateTime(orderEnd)}，請於此範圍內安排活動。`;

				        // 儲存於全域變數供後續驗證
				        window.orderStartTime = orderStart;
				        window.orderEndTime = orderEnd;
												
				    }
					
			    if (orderInput) {
			        orderInput.value = orderId;
			    }
			} else {
			    alert("無法取得訂單編號，請從訂單頁面建立活動");
			    // 可選擇導回訂單頁
			    // window.location.href = "frontend_orders.html";
			}
		        	
		});
		
		// 獲取所有活動類別
		       async function fetchAllEventsCategory() {
		            // 使用API端點路徑
		            const apiUrl = '/lifespace/event/getAllCategories';

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
		                })
		                .catch(error => {
		                    console.error('獲取活動類別時出錯:', error);

		                });
		        }
				
		function displayCategories(categories){
			// 獲取活動類別下拉選單
			const categorySelect = document.getElementById('eventCategory');
				            
			// 先清空原有的選項（除了預設選項）
			categorySelect.innerHTML = '';
				       
			// 為每個類別添加 option
			categories.forEach(category => {
				      const newCategory = `<option value=${category.eventCategoryId}>${category.eventCategoryName}</option>`;
				       categorySelect.innerHTML = categorySelect.innerHTML  + newCategory;  
				    });   
			}
			
			
        // 監聽文件選擇事件
        document.getElementById('photos').addEventListener('change', function () {
            // 如果已經達到最大上傳數
            if (uploadedPhotos.length >= MAX_PHOTOS) {
                alert('已達到最大上傳數量 (3張)');
                this.value = '';
                return;
            }

            // 獲取選擇的文件
            const file = this.files[0];

            // 檢查是否為圖片
            if (!file.type.startsWith('image/')) {
                alert('請選擇圖片文件');
                this.value = '';
                return;
            }

            // 添加到上傳列表
            uploadedPhotos.push(file);

            // 更新計數器
            document.getElementById('photo-counter').textContent = `已選擇 ${uploadedPhotos.length}/3 張圖片`;

            // 顯示預覽
            const preview = document.getElementById('photo-preview');
            const img = document.createElement('div');
            img.className = 'preview-item';
            img.innerHTML = `
        <img src="${URL.createObjectURL(file)}" alt="Preview" style="max-width: 200px; max-height: 200px; margin: 10px;">
        <button type="button" class="remove-btn" data-index="${uploadedPhotos.length - 1}" style="position: absolute; top: 0; right: 0; background: #ff4d4d; color: white; border: none; border-radius: 50%; width: 24px; height: 24px; cursor: pointer;">X</button>
    `;
            img.style.position = 'relative';
            img.style.display = 'inline-block';
            preview.appendChild(img);

            // 清空文件輸入，以便再次選擇相同的文件
            this.value = '';

            // 在控制台輸出文件信息
            console.log('新添加的文件:', file);
            console.log('- 文件名:', file.name);
            console.log('- 文件大小:', file.size, 'bytes');
            console.log('- 文件類型:', file.type);
            console.log('目前所有文件:', uploadedPhotos);
        });


        // 處理移除按鈕的點擊
        document.getElementById('photo-preview').addEventListener('click', function (e) {
            if (e.target.classList.contains('remove-btn')) {
                const index = parseInt(e.target.dataset.index);

                console.log('移除文件:', uploadedPhotos[index].name);

                uploadedPhotos.splice(index, 1);

                // 重新生成預覽
                refreshPhotoPreview();
            }
        });

        // 刷新預覽
        function refreshPhotoPreview() {
            const preview = document.getElementById('photo-preview');
            preview.innerHTML = '';

            uploadedPhotos.forEach((file, index) => {
                const img = document.createElement('div');
                img.className = 'preview-item';
                img.style.position = 'relative';
                img.style.display = 'inline-block';
                img.innerHTML = `
            <img src="${URL.createObjectURL(file)}" alt="Preview" style="max-width: 200px; max-height: 200px; margin: 10px;">
            <button type="button" class="remove-btn" data-index="${index}" style="position: absolute; top: 0; right: 0; background: #ff4d4d; color: white; border: none; border-radius: 50%; width: 24px; height: 24px; cursor: pointer;">X</button>
        `;
                preview.appendChild(img);
            });

            // 更新計數器
            document.getElementById('photo-counter').textContent = `已選擇 ${uploadedPhotos.length}/3 張圖片`;
        }


		async function addEvent() {
		    const formData = new FormData();

		    // 附加照片
		    if (uploadedPhotos && uploadedPhotos.length > 0) {
		        for (let i = 0; i < uploadedPhotos.length; i++) {
		            const file = uploadedPhotos[i];
		            formData.append('photos', file);
		        }
		    }

		    // 取得表單欄位
		    const eventStartInput = document.getElementById('eventStartTime').value;
		    const eventEndInput = document.getElementById('eventEndTime').value;
		    const maxParticipantsInput = document.getElementById('maximumOfParticipants').value;

		    //基本輸入驗證
		    if (!eventStartInput || isNaN(Date.parse(eventStartInput))) {
		        alert("請輸入正確的活動開始時間");
		        return;
		    }

		    if (!eventEndInput || isNaN(Date.parse(eventEndInput))) {
		        alert("請輸入正確的活動結束時間");
		        return;
		    }

		    const maxParticipants = parseInt(maxParticipantsInput);
		    if (!maxParticipants || maxParticipants <= 0) {
		        alert("請輸入正確的活動人數上限（必須為正整數）");
		        return;
		    }

			const eventName = document.getElementById('eventName').value.trim();
			if (eventName.length === 0) {
			    alert("請輸入活動名稱");
			    return;
			}
			if (eventName.length > 30) {
			    alert("活動名稱不能超過 30 個字");
			    return;
			}
			
		    const eventRequest = {
		        orderId: document.getElementById('orderId').value,
		        eventName: document.getElementById('eventName').value,
		        eventStartTime: new Date(eventStartInput).toISOString(),
		        eventEndTime: new Date(eventEndInput).toISOString(),
		        eventCategory: document.getElementById('eventCategory').value,
		        maximumOfParticipants: maxParticipants,
		        eventStatus: "SCHEDULED",
		        eventBriefing: document.getElementById('eventBriefing').value,
		        remarks: document.getElementById('remarks').value,
		        hostSpeaking: document.getElementById('hostSpeaking').value
		    };

		    formData.append('eventRequest', new Blob([JSON.stringify(eventRequest)], {
		        type: 'application/json'
		    }));

		    try {
		        const response = await fetch('/lifespace/event/add', {
		            method: 'POST',
		            body: formData,
		            credentials: 'include'
		        });

		        if (response.ok) {
		            const data = await response.text();
		            alert(data); // 顯示後端回傳訊息
		            document.getElementById('eventForm').reset();
		            document.getElementById('photos').value = '';
		            document.getElementById('photo-preview').innerHTML = '';
		            uploadedPhotos = [];
		            document.getElementById('photo-counter').textContent = '已選擇 0/3 張圖片';
		            //alert("活動建立成功！");
					
					//新增成功後，導向活動管理頁面
		            window.location.href = "/lifespace/events_for_user";
		        } else {
		            const errorText = await response.text();
		            console.error('伺服器回應:', response.status, errorText);
		            alert('新增活動失敗: ' + errorText);
					//3秒後導向首頁
					//setTimeout(function() {
				//	  location.href = "/login.html";
					//}, 3000); 
		        }
		    } catch (error) {
		        console.error('錯誤詳情:', error);
		        alert('發生錯誤，請稍後再試: ' + error.message);
		    }
		}

		
        function formatDateAndTime(dateTime) {
            const date = new Date(dateTime);
            const datePart = date.toLocaleDateString();
            const timePart = date.toLocaleTimeString({ hour: '2-digit', minute: '2-digit' });
            return `${datePart} ${timePart}`;
        }

		function formatDateTime(date) {
		    return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
		}