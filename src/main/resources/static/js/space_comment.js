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
                branchIdInput.value = '';
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

            // 獲取評論資料
            function fetchComments() {
                showLoading();

                const spaceId = spaceIdInput.value.trim();
                const spaceName = spaceNameInput.value.trim();
                const branchId = branchIdInput.value.trim();

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

                // 更新分頁資訊
                totalPages = data.totalPages;

                // 構建表格
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
                            </tr>
                        </thead>
                        <tbody>
                `;

                data.content.forEach(comment => {
                    // 格式化日期
                    const commentDate = new Date(comment.commentTime);
                    const formattedDate = `${commentDate.getFullYear()}-${padZero(commentDate.getMonth() + 1)}-${padZero(commentDate.getDate())} ${padZero(commentDate.getHours())}:${padZero(commentDate.getMinutes())}`;

                    // 產生星星評分
                    const satisfaction = comment.satisfaction || 0;
                    let starsHtml = '';
                    for (let i = 0; i < 5; i++) {
                        if (i < satisfaction) {
                            starsHtml += '<span class="star">★</span>';
                        } else {
                            starsHtml += '<span class="star" style="color: #ccc;">★</span>';
                        }
                    }

                    // 處理照片
                    let photosHtml = '';
                    if (comment.photosUrls && comment.photosUrls.length > 0) {
                        photosHtml = '<div class="photos">';
                        comment.photosUrls.forEach(photoUrl => {
                            if (photoUrl && photoUrl.trim() !== '') {
                                photosHtml += `<img src="${photoUrl}" class="photo-thumbnail" onclick="openModal('${photoUrl}')">`;
                            }
                        });
                        photosHtml += '</div>';
                    } else {
                        photosHtml = '無照片';
                    }

                    tableHtml += `
                        <tr>
                            <td>${comment.spaceName || '-'}</td>
                            <td>${comment.spaceId || '-'}</td>
                            <td>${comment.branchId || '-'}</td>
                            <td>${comment.commentContent || '-'}</td>
                            <td><div class="satisfaction">${starsHtml} (${satisfaction})</div></td>
                            <td>${formattedDate}</td>
                            <td>${photosHtml}</td>
                        </tr>
                    `;
                });

                tableHtml += `
                        </tbody>
                    </table>
                `;

                resultsContainer.innerHTML = tableHtml;

                // 建立分頁按鈕
                createPagination();

                // 綁定照片點擊事件
                document.querySelectorAll('.photo-thumbnail').forEach(img => {
                    img.addEventListener('click', function () {
                        openModal(this.src);
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