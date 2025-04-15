document.addEventListener('DOMContentLoaded', function () {
            // 元素參考
            const searchBtn = document.getElementById('searchBtn');
            const resetBtn = document.getElementById('resetBtn');
            const resultsContainer = document.getElementById('resultsContainer');
            const paginationContainer = document.getElementById('pagination');
            const spaceIdInput = document.getElementById('spaceId');
            const spaceNameInput = document.getElementById('spaceName');
            const branchIdInput = document.getElementById('branchId');
            const modal = document.getElementById('photoModal');
            const modalImg = document.getElementById('modalImg');
            const modalClose = document.querySelector('.close');

            // 處理側邊欄切換，為響應式設計調整
            const sidebarToggle = document.querySelector('[data-bs-toggle="collapse"][data-bs-target="#sidebarMenu"]');
            if (sidebarToggle) {
                sidebarToggle.addEventListener('click', function() {
                    document.body.classList.toggle('sidebar-open');
                });
            }
			
			// 獲取所有分點
			fetchAllBranches();

            // 分頁狀態
            let currentPage = 0;
            let pageSize = 5;
            let totalPages = 0;

            // 初始載入
            fetchComments();

            // 搜尋按鈕點擊事件
            searchBtn.addEventListener('click', function () {
                currentPage = 0;
                fetchComments();
            });

            // 重置按鈕點擊事件
            resetBtn.addEventListener('click', function () {
                spaceIdInput.value = '';
                spaceNameInput.value = '';
                document.getElementById('location').value = '';
                currentPage = 0;
				fetchComments();
            });

            // 關閉圖片模態框
            modalClose.addEventListener('click', function () {
                modal.style.display = 'none';
            });

            // 點擊模態框背景關閉
            window.addEventListener('click', function (event) {
                if (event.target === modal) {
                    modal.style.display = 'none';
                }
            });

			
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
			       branchSelect.innerHTML = '<option value="">所有分點</option>';
			       branches.forEach(branch => {
			             const newBranch = `<option value=${branch.branchId}>${branch.branchName}</option>`;
			             branchSelect.innerHTML = branchSelect.innerHTML + newBranch;  
			         });   
			}
				  
            // 獲取評論資料
            function fetchComments() {
                showLoading();

                const spaceId = spaceIdInput.value.trim();
                const spaceName = spaceNameInput.value.trim();
				const branchId = document.getElementById('location').value || "";
				
                // 構建API URL
                let url = `http://localhost:8080/spaces/comments?page=${currentPage}&size=${pageSize}`;
                if (spaceId) url += `&spaceId=${encodeURIComponent(spaceId)}`;
                if (spaceName) url += `&spaceName=${encodeURIComponent(spaceName)}`;
                if (branchId) url += `&branchId=${encodeURIComponent(branchId)}`;

                fetch(url)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('網路錯誤，請稍後再試');
                        }
                        return response.json();
                    })
                    .then(data => {
                        displayResults(data);
                    })
                    .catch(error => {
                        showError(error.message);
                    });
            }

            // 顯示載入中
            function showLoading() {
                resultsContainer.innerHTML = '<div class="loading">正在載入資料，請稍候...</div>';
                paginationContainer.innerHTML = '';
            }

            // 顯示錯誤訊息
            function showError(message) {
                resultsContainer.innerHTML = `<div class="no-results">錯誤：${message}</div>`;
                paginationContainer.innerHTML = '';
            }

            // 顯示查詢結果
			function displayResults(data) {
			    if (!data || !data.content || data.content.length === 0) {
			        resultsContainer.innerHTML = '<div class="no-results">沒有找到符合條件的評論</div>';
			        paginationContainer.innerHTML = '';
			        return;
			    }

			    totalPages = data.totalPages;

			    let tableHtml = `
			        <table>
			            <thead>
			                <tr>
			                    <th>空間名稱</th>
			                    <th>空間ID</th>
			                    <th>分店ID</th>
			                    <th>評論內容</th>
			                    <th>滿意度</th>
			                    <th>評論時間</th>
			                    <th>照片</th>
			                    <th>回覆評論</th>
			                </tr>
			            </thead>
			            <tbody>
			    `;

			    data.content.forEach(comment => {
			        const commentDate = new Date(comment.commentTime);
			        const formattedDate = `${commentDate.getFullYear()}-${padZero(commentDate.getMonth() + 1)}-${padZero(commentDate.getDate())} ${padZero(commentDate.getHours())}:${padZero(commentDate.getMinutes())}`;

			        const satisfaction = comment.satisfaction || 0;
			        let starsHtml = '';
			        for (let i = 0; i < 5; i++) {
			            starsHtml += `<span class="star" style="color: ${i < satisfaction ? '' : '#ccc'};">★</span>`;
			        }

			        let photosHtml = '無照片';
			        if (comment.photosUrls && comment.photosUrls.length > 0) {
			            photosHtml = '<div class="photos">';
			            comment.photosUrls.forEach(photoUrl => {
			                if (photoUrl && photoUrl.trim() !== '') {
			                    photosHtml += `<img src="${photoUrl}" class="photo-thumbnail" onclick="openModal('${photoUrl}')">`;
			                }
			            });
			            photosHtml += '</div>';
			        }

			        const replyBoxId = `reply-box-${comment.orderId}`;
			        const replyContent = comment.commentReply || '';

			        tableHtml += `
			            <tr>
			                <td>${comment.spaceName || '-'}</td>
			                <td>${comment.spaceId || '-'}</td>
			                <td>${comment.branchId || '-'}</td>
			                <td>${comment.commentContent || '-'}</td>
			                <td><div class="satisfaction">${starsHtml} (${satisfaction})</div></td>
			                <td>${formattedDate}</td>
			                <td>${photosHtml}</td>
			                <td>
			                    <button class="reply-toggle-btn" data-id="${comment.orderId}">回覆</button>
			                    <div class="reply-box" id="${replyBoxId}" style="display:none; margin-top:10px;">
			                        <textarea class="reply-input" rows="3" style="width: 100%;">${replyContent}</textarea>
			                        <button class="reply-submit-btn" data-id="${comment.orderId}">送出</button>
			                    </div>
			                </td>
			            </tr>
			        `;
			    });

			    tableHtml += `
			            </tbody>
			        </table>
			    `;

			    resultsContainer.innerHTML = tableHtml;
			    createPagination();

			    // 綁定圖片點擊事件
			    document.querySelectorAll('.photo-thumbnail').forEach(img => {
			        img.addEventListener('click', function () {
			            openModal(this.src);
			        });
			    });

			    // 綁定回覆區塊切換
			    document.querySelectorAll('.reply-toggle-btn').forEach(button => {
			        button.addEventListener('click', function () {
			            const orderId = this.dataset.id;
			            const box = document.getElementById(`reply-box-${orderId}`);
			            if (box) {
			                box.style.display = box.style.display === 'none' ? 'block' : 'none';
			            }
			        });
			    });

			    // 綁定送出回覆事件
			    document.querySelectorAll('.reply-submit-btn').forEach(button => {
			        button.addEventListener('click', function () {
			            const orderId = this.dataset.id;
			            const box = document.getElementById(`reply-box-${orderId}`);
			            const input = box.querySelector('.reply-input');
			            const replyContent = input.value.trim();

			            fetch('http://localhost:8080/spaces/comments/reply', {
			                method: 'POST',
			                headers: {
			                    'Content-Type': 'application/json'
			                },
			                body: JSON.stringify({
			                    orderId: orderId,
			                    commentReplyContent: replyContent
			                })
			            })
			            .then(response => {
			                if (!response.ok) throw new Error("無法儲存回覆");
			                return response.json();
			            })
			            .then(() => {
			                alert('回覆已送出');
			                fetchComments(); // 重新載入
			            })
			            .catch(error => {
			                console.error(error);
			                alert('回覆失敗');
			            });
			        });
			    });
			}


            // 建立分頁按鈕
            function createPagination() {
                let paginationHtml = '';

                // 前一頁按鈕
                paginationHtml += `<button ${currentPage === 0 ? 'disabled' : ''} onclick="changePage(${currentPage - 1})">上一頁</button>`;

                // 頁碼按鈕 (顯示最多5個頁碼)
                const startPage = Math.max(0, Math.min(currentPage - 2, totalPages - 5));
                const endPage = Math.min(startPage + 4, totalPages - 1);

                for (let i = startPage; i <= endPage; i++) {
                    paginationHtml += `<button class="${i === currentPage ? 'active' : ''}" onclick="changePage(${i})">${i + 1}</button>`;
                }

                // 下一頁按鈕
                paginationHtml += `<button ${currentPage >= totalPages - 1 ? 'disabled' : ''} onclick="changePage(${currentPage + 1})">下一頁</button>`;

                paginationContainer.innerHTML = paginationHtml;
            }

            // 切換頁碼
            window.changePage = function (page) {
                if (page < 0 || page >= totalPages) return;
                currentPage = page;
                fetchComments();
            };

            // 打開圖片模態框
            window.openModal = function (imgSrc) {
                modal.style.display = 'block';
                modalImg.src = imgSrc;
            };

            // 補零函數（格式化日期用）
            function padZero(num) {
                return num < 10 ? '0' + num : num;
            }
        });