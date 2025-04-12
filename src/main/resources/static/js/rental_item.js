$(document).ready(function() {
    // 全局變量
    let currentPage = 1;
    const itemsPerPage = 5;
    let totalPages = 1;
    let allRentalItems = [];
    let currentSearchType = 'rentalItemId';
    let currentFilter = 'all'; // all, active, inactive

    // 初始載入所有租借品項
    loadAllRentalItems();

    // 搜尋類型下拉選單事件
    $('.search-type').on('click', function() {
        const searchType = $(this).data('type');
        $('#selectedSearchType').text($(this).text());
        currentSearchType = searchType;
    });

    // 搜尋按鈕點擊事件
    $('#searchBtn').on('click', function() {
        const searchValue = $('#searchInput').val().trim();
        if (!searchValue) {
            alert('請輸入搜尋內容');
            return;
        }

        if (currentSearchType === 'rentalItemId') {
            searchRentalItemsByRentalItemId(searchValue);
        } else if (currentSearchType === 'rentalItemName') {
            searchRentalItemsByRentalItemName(searchValue);
        }
    });

    // 篩選按鈕事件 - 所有租借品項
    $('#allRentalItemsBtn').on('click', function() {
        $(this).addClass('active').css('color', 'green');
        $('#activeRentalItemsBtn, #inactiveRentalItemsBtn').removeClass('active').css('color', 'black');
        currentFilter = 'all';
        loadAllRentalItems();
    });

    // 篩選按鈕事件 - 上架中租借品項
    $('#activeRentalItemsBtn').on('click', function() {
        $(this).addClass('active').css('color', 'green');
        $('#allRentalItemsBtn, #inactiveRentalItemsBtn').removeClass('active').css('color', 'black');
        currentFilter = 'active';
        loadRentalItemsByStatus(1); // 1表示上架中
    });

    // 篩選按鈕事件 - 下架租借品項
    $('#inactiveRentalItemsBtn').on('click', function() {
        $(this).addClass('active').css('color', 'green');
        $('#allRentalItemsBtn, #activeRentalItemsBtn').removeClass('active').css('color', 'black');
        currentFilter = 'inactive';
        loadRentalItemsByStatus(0); // 0表示下架
    });

    // 新增租借品項按鈕事件
    $('#addRentalItemBtn').on('click', function() {
        window.location.href = '/add-rental-item.html';
    });

    // 上下頁切換
    $('#prevPageBtn').on('click', function() {
        if (currentPage > 1) {
            currentPage--;
            updateRentalItemTable();
            updatePagination();
        }
    });

    $('#nextPageBtn').on('click', function() {
        if (currentPage < totalPages) {
            currentPage++;
            updateRentalItemTable();
            updatePagination();
        }
    });

    // 載入所有租借品項數據
    function loadAllRentalItems() {
        $.ajax({
            url: '/rental-item/getAll',
            type: 'GET',
            success: function(data) {
                allRentalItems = data;
                totalPages = Math.ceil(allRentalItems.length / itemsPerPage);
                currentPage = 1;
                updateRentalItemTable();
                updatePagination();
            },
            error: function(xhr, status, error) {
                console.error('Error loading rental items:', error);
                alert('載入租借品項數據失敗');
            }
        });
    }

    // 根據狀態載入租借品項
    function loadRentalItemsByStatus(status) {
        $.ajax({
            url: `/rental-item/getByStatus/${status}`,
            type: 'GET',
            success: function(data) {
                allRentalItems = data;
                totalPages = Math.ceil(allRentalItems.length / itemsPerPage);
                currentPage = 1;
                updateRentalItemTable();
                updatePagination();
            },
            error: function(xhr, status, error) {
                console.error('Error loading rental items by status:', error);
                alert('載入租借品項數據失敗');
            }
        });
    }

    // 根據租借品項編號搜尋
    function searchRentalItemsByRentalItemId(rentalItemId) {
        $.ajax({
            url: `/rental-item/getByRentalItemId/${rentalItemId}`,
            type: 'GET',
            success: function(data) {
                allRentalItems = data;
                totalPages = Math.ceil(allRentalItems.length / itemsPerPage);
                currentPage = 1;
                updateRentalItemTable();
                updatePagination();
            },
            error: function(xhr, status, error) {
                console.error('Error searching rental items by ID:', error);
                alert('搜尋租借品項失敗');
            }
        });
    }

    // 根據租借品項名稱搜尋
    function searchRentalItemsByRentalItemName(rentalItemName) {
        $.ajax({
            url: `/rental-item/getByRentalItemName/${rentalItemName}`,
            type: 'GET',
            success: function(data) {
                allRentalItems = data;
                totalPages = Math.ceil(allRentalItems.length / itemsPerPage);
                currentPage = 1;
                updateRentalItemTable();
                updatePagination();
            },
            error: function(xhr, status, error) {
                console.error('Error searching rental items by name:', error);
                alert('搜尋租借品項失敗');
            }
        });
    }

    // 更新租借品項表格
    function updateRentalItemTable() {
        const tableBody = $('#rentalItemTable tbody');
        tableBody.empty();

        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;
        const currentPageItems = allRentalItems.slice(startIndex, endIndex);

        if (currentPageItems.length === 0) {
            tableBody.append('<tr><td colspan="10" class="text-center">無符合條件的資料</td></tr>');
            return;
        }

        currentPageItems.forEach(function(item) {
            const statusText = item.rentalItemStatus === 1 ? '上架' : '下架';
            const statusBtnText = item.rentalItemStatus === 1 ? '下架' : '上架';
            
            const row = `
                <tr>
                    <td>${item.rentalItemId}</td>
                    <td>${item.rentalItemName}</td>
                    <td>${item.rentalItemPrice}</td>
                    <td>${item.totalQuantity}</td>
                    <td>${item.availableRentalQuantity}</td>
                    <td>${item.branchId}</td>
                    <td>${statusText}</td>
                    <td>${formatDate(item.createdTime)}</td>
                    <td>
                        <button class="btn btn-primary btn-sm edit-btn" data-id="${item.rentalItemId}">編輯</button>
                    </td>
                    <td>
                        <button class="btn btn-warning btn-sm status-btn" data-id="${item.rentalItemId}" data-status="${item.rentalItemStatus}">
                            ${statusBtnText}
                        </button>
                    </td>
                </tr>
            `;
            tableBody.append(row);
        });

        // 綁定編輯按鈕事件
        $('.edit-btn').on('click', function() {
            const rentalItemId = $(this).data('id');
            window.location.href = `/edit-rental-item.html?id=${rentalItemId}`;
        });

        // 綁定狀態切換按鈕事件
        $('.status-btn').on('click', function() {
            const rentalItemId = $(this).data('id');
            updateRentalItemStatus(rentalItemId);
        });
    }

    // 更新分頁顯示
    function updatePagination() {
        $('#currentPage').text(currentPage);
        
        // 禁用或啟用上下頁按鈕
        if (currentPage <= 1) {
            $('#prevPageBtn').prop('disabled', true);
        } else {
            $('#prevPageBtn').prop('disabled', false);
        }

        if (currentPage >= totalPages) {
            $('#nextPageBtn').prop('disabled', true);
        } else {
            $('#nextPageBtn').prop('disabled', false);
        }
    }

    // 更新租借品項狀態（上下架）
    function updateRentalItemStatus(rentalItemId) {
        $.ajax({
            url: `/rental-item/updateStatus/${rentalItemId}`,
            type: 'POST',
            success: function(response) {
                alert(response);
                
                // 根據當前篩選重新載入數據
                if (currentFilter === 'all') {
                    loadAllRentalItems();
                } else if (currentFilter === 'active') {
                    loadRentalItemsByStatus(1);
                } else if (currentFilter === 'inactive') {
                    loadRentalItemsByStatus(0);
                }
            },
            error: function(xhr, status, error) {
                console.error('Error updating rental item status:', error);
                alert('更新租借品項狀態失敗');
            }
        });
    }

    // 格式化日期時間
    function formatDate(dateString) {
        if (!dateString) return '';
        
        const date = new Date(dateString);
        return date.toLocaleString('zh-TW', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
        });
    }
});