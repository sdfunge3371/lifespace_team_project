$(document).ready(function() {
	// 管理員登入
	        let adminId = '';  // 假設登入者 ID

	        $.ajax({
	            url: "http://localhost:8080/admin/faq/profile",
	            method: "GET",
	            xhrFields: {
	                withCredentials: true // 等同於 fetch 的 credentials: "include"
	            },
	            success: function (response) {
	                adminId = response.adminId;
	                console.log("登入的管理員ID：", adminId);

	            },
	            error: function (xhr) {
	                if (xhr.status === 401) {
	                    alert("尚未登入，請先登入");
	                    window.location.href = "/loginAdmin.html";
	                } else {
	                    console.error("無法取得會員資料", xhr);
	                }
	            }
	        });
	// 全局變量
    let currentPage = 1;
    const itemsPerPage = 5;
    let totalPages = 1;
    let allBranches = [];
    let currentSearchType = 'branchId';
    let currentFilter = 'all'; // all, active, inactive

    // 初始載入所有分點
    loadAllBranches();

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

        if (currentSearchType === 'branchId') {
            searchBranchesByBranchId(searchValue);
        } else if (currentSearchType === 'branchName') {
            searchBranchesByBranchName(searchValue);
        }
    });

    // 篩選按鈕事件 - 所有分點
    $('#allBranchesBtn').on('click', function() {
        $(this).addClass('active').css('color', 'green');
        $('#activeBranchesBtn, #inactiveBranchesBtn').removeClass('active').css('color', 'black');
        currentFilter = 'all';
        loadAllBranches();
    });

    // 篩選按鈕事件 - 上架分點
    $('#activeBranchesBtn').on('click', function() {
        $(this).addClass('active').css('color', 'green');
        $('#allBranchesBtn, #inactiveBranchesBtn').removeClass('active').css('color', 'black');
        currentFilter = 'active';
        loadBranchesByStatus(1); // 1表示上架
    });

    // 篩選按鈕事件 - 下架分點
    $('#inactiveBranchesBtn').on('click', function() {
        $(this).addClass('active').css('color', 'green');
        $('#allBranchesBtn, #activeBranchesBtn').removeClass('active').css('color', 'black');
        currentFilter = 'inactive';
        loadBranchesByStatus(0); // 0表示下架
    });

    // 新增分點按鈕事件
    $('#addBranchBtn').on('click', function() {
        window.location.href = '/add_branch.html';
    });

    // 上下頁切換
    $('#prevPageBtn').on('click', function() {
        if (currentPage > 1) {
            currentPage--;
            updateBranchTable();
            updatePagination();
        }
    });

    $('#nextPageBtn').on('click', function() {
        if (currentPage < totalPages) {
            currentPage++;
            updateBranchTable();
            updatePagination();
        }
    });

    // 載入所有分點數據
    function loadAllBranches() {
        $.ajax({
            url: '/branch/getAll',
            type: 'GET',
            success: function(data) {
                allBranches = data;
                totalPages = Math.ceil(allBranches.length / itemsPerPage);
                currentPage = 1;
                updateBranchTable();
                updatePagination();
            },
            error: function(xhr, status, error) {
                console.error('Error loading branches:', error);
                alert('載入分點數據失敗');
            }
        });
    }

    // 根據狀態載入分點
    function loadBranchesByStatus(status) {
        $.ajax({
            url: `/branch/getByStatus/${status}`,
            type: 'GET',
            success: function(data) {
                allBranches = data;
                totalPages = Math.ceil(allBranches.length / itemsPerPage);
                currentPage = 1;
                updateBranchTable();
                updatePagination();
            },
            error: function(xhr, status, error) {
                console.error('Error loading branches by status:', error);
                alert('載入分點數據失敗');
            }
        });
    }

    // 根據分點編號搜尋
    function searchBranchesByBranchId(branchId) {
        $.ajax({
            url: `/branch/getByBranchId/${branchId}`,
            type: 'GET',
            success: function(data) {
                allBranches = data;
                totalPages = Math.ceil(allBranches.length / itemsPerPage);
                currentPage = 1;
                updateBranchTable();
                updatePagination();
            },
            error: function(xhr, status, error) {
                console.error('Error searching branches by ID:', error);
                alert('搜尋分點失敗');
            }
        });
    }

    // 根據分點名稱搜尋
    function searchBranchesByBranchName(branchName) {
        $.ajax({
            url: `/branch/getByBranchName/${branchName}`,
            type: 'GET',
            success: function(data) {
                allBranches = data;
                totalPages = Math.ceil(allBranches.length / itemsPerPage);
                currentPage = 1;
                updateBranchTable();
                updatePagination();
            },
            error: function(xhr, status, error) {
                console.error('Error searching branches by name:', error);
                alert('搜尋分點失敗');
            }
        });
    }

    // 更新分點表格
    function updateBranchTable() {
        const tableBody = $('#branchTable tbody');
        tableBody.empty();

        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;
        const currentPageItems = allBranches.slice(startIndex, endIndex);

        if (currentPageItems.length === 0) {
            tableBody.append('<tr><td colspan="10" class="text-center">無符合條件的資料</td></tr>');
            return;
        }

        currentPageItems.forEach(function(branch) {
            const statusText = branch.branchStatus === 1 ? '上架' : '下架';
            const statusBtnText = branch.branchStatus === 1 ? '下架' : '上架';
            
            // 處理公共設備名稱顯示
            let publicEquipmentNames = '';
            if (branch.publicEquipmentDTOList && branch.publicEquipmentDTOList.length > 0) {
                publicEquipmentNames = branch.publicEquipmentDTOList
                    .map(item => item.publicEquipName)
                    .join(', ');
            } else {
                publicEquipmentNames = '無公共設備';
            }
            
            const row = `
                <tr>
                    <td>${branch.branchId}</td>
                    <td>${branch.branchName}</td>
                    <td>${branch.branchAddr}</td>
                    <td>${branch.latitude}</td>
                    <td>${branch.longitude}</td>
                    <td>${statusText}</td>
                    <td>${formatDate(branch.createdTime)}</td>
                    <td class="public-equipment">${publicEquipmentNames}</td>
                    <td>
                        <button class="btn btn-primary btn-sm edit-btn" data-id="${branch.branchId}">編輯</button>
                    </td>
                    <td>
                        <button class="btn btn-warning btn-sm status-btn" data-id="${branch.branchId}" data-status="${branch.branchStatus}">
                            ${statusBtnText}
                        </button>
                    </td>
                </tr>
            `;
            tableBody.append(row);
        });

        // 綁定編輯按鈕事件
        $('.edit-btn').on('click', function() {
            const branchId = $(this).data('id');
            window.location.href = `/edit_branch.html?id=${branchId}`;
        });

        // 綁定狀態切換按鈕事件
        $('.status-btn').on('click', function() {
            const branchId = $(this).data('id');
            updateBranchStatus(branchId);
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

    // 更新分點狀態（上下架）
    function updateBranchStatus(branchId) {
        $.ajax({
            url: `/branch/updateStatus/${branchId}`,
            type: 'POST',
            success: function(response) {
                alert(response);
                
                // 根據當前篩選重新載入數據
                if (currentFilter === 'all') {
                    loadAllBranches();
                } else if (currentFilter === 'active') {
                    loadBranchesByStatus(1);
                } else if (currentFilter === 'inactive') {
                    loadBranchesByStatus(0);
                }
            },
            error: function(xhr, status, error) {
                console.error('Error updating branch status:', error);
                alert('更新分點狀態失敗');
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