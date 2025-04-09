// 已上傳的照片數組
        let uploadedPhotos = [];
        const MAX_PHOTOS = 3;

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
            // 創建一個新的FormData對象
            const formData = new FormData();

            //照片最大數量
            const MAX_PHOTOS = 3;
            // 獲取照片輸入
            // const photoInput = document.querySelector('input[type="file"][name="photos"]');

            // 檢查選擇的文件大小（偵錯用）
            if (uploadedPhotos && uploadedPhotos.length > 0) {
                for (let i = 0; i < uploadedPhotos.length; i++) {
                    const file = uploadedPhotos[i];
                    console.log(`文件 ${i + 1}: ${file.name}, 大小: ${file.size} bytes`);

                    // 添加照片到FormData
                    formData.append('photos', file);
                }
                console.log(`所有文件總大小: ${uploadedPhotos.reduce((total, file) => total + file.size, 0)} bytes`);
            }

            // 創建不包含photos欄位的eventRequest對象
            const eventRequest = {
                organizerId: document.getElementById('memberId').value,
                eventName: document.getElementById('eventName').value,
                eventStartTime: new Date(document.getElementById('eventStartTime').value).toISOString(),
                eventEndTime: new Date(document.getElementById('eventEndTime').value).toISOString(),
                eventCategory: document.getElementById('eventCategory').value,
                maximumOfParticipants: document.getElementById('maximumOfParticipants').value,
                eventStatus: "SCHEDULED",
                eventBriefing: document.getElementById('eventBriefing').value,
                remarks: document.getElementById('remarks').value,
                hostSpeaking: document.getElementById('hostSpeaking').value
            };

            // 將eventRequest轉換為JSON字符串並添加到FormData
            formData.append('eventRequest', new Blob([JSON.stringify(eventRequest)], {
                type: 'application/json'
            }));

            // 輸出整個FormData的內容（偵錯用）
            console.log('FormData內容:', formData);

            try {
                const response = await fetch('http://localhost:8080/lifespace/event/add', {
                    method: 'POST',
                    body: formData,
                });

                if (response.ok) {
                    const data = await response.text();
                    alert(data); // 顯示後端回傳的訊息
                    document.getElementById('eventForm').reset();
                    document.getElementById('photos').value = '';
                 	// 清除預覽圖片區塊
                    document.getElementById('photo-preview').innerHTML = '';
                    
                    // 清空已上傳照片陣列
                    uploadedPhotos = [];
                    
                    // 更新計數器
                    document.getElementById('photo-counter').textContent = '已選擇 0/3 張圖片';
                } else {
                    const errorText = await response.text();
                    console.error('伺服器回應:', response.status, errorText);
                    alert('新增活動失敗: ' + response.status + ' ' + errorText);
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
