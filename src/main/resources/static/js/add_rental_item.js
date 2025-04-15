$(document).ready(function() {
    // 載入所有分點
    loadBranches();
    
    // 表單提交處理
    $('#addRentalItemForm').on('submit', function(e) {
        e.preventDefault();
        
        // 驗證表單
        if (!validateForm()) {
            return;
        }
        
        // 收集表單數據
        const totalQuantity = parseInt($('#totalQuantity').val());
        
        const formData = {
            rentalItemName: $('#rentalItemName').val().trim(),
            rentalItemPrice: parseInt($('#rentalItemPrice').val()),
            totalQuantity: totalQuantity,
            availableRentalQuantity: totalQuantity, // 將商品總數設為可租借數量
            branchId: $('#branchId').val(),
            rentalItemStatus: parseInt($('input[name="rentalItemStatus"]:checked').val())
        };
        
        // 發送 AJAX 請求
        $.ajax({
            url: '/rental_item/add',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {
                alert('新增租借品項成功');
                window.location.href = '/rental_item.html';
            },
            error: function(xhr, status, error) {
                alert('新增租借品項失敗: ' + error);
            }
        });
    });
    
    // 載入分點數據函數
    function loadBranches() {
        $.ajax({
            url: '/branch/getAll',
            type: 'GET',
            success: function(data) {
                const $branchSelect = $('#branchId');
                
                // 清空現有選項（保留「請選擇分點」選項）
                $branchSelect.find('option:not(:first)').remove();
                
                // 添加分點選項
                data.forEach(function(branch) {
                    $branchSelect.append(`<option value="${branch.branchId}">${branch.branchId} - ${branch.branchName}</option>`);
                });
            },
            error: function(xhr, status, error) {
                alert('載入分點數據失敗: ' + error);
            }
        });
    }
    
    // 表單驗證函數
    function validateForm() {
        // 檢查必填欄位
        const rentalItemName = $('#rentalItemName').val().trim();
        const rentalItemPrice = $('#rentalItemPrice').val().trim();
        const totalQuantity = $('#totalQuantity').val().trim();
        const branchId = $('#branchId').val();
        
        if (!rentalItemName || !rentalItemPrice || !totalQuantity) {
            alert('未輸入資料');
            return false;
        }
        
        // 檢查分點是否已選擇
        if (!branchId) {
            alert('未選擇');
            return false;
        }
        
        // 檢查租借品項狀態是否已選擇
        if (!$('input[name="rentalItemStatus"]:checked').length) {
            alert('未選擇');
            return false;
        }
        
        return true;
    }
});