$(document).ready(function() {
    // 公共設備相關功能
    const $publicEquipModal = $('#publicEquipModal');
    const $publicEquipName = $('#publicEquipName');
    const $publicEquipContainer = $('.public-equipment-container');
    
    // 點擊新增公共設備按鈕，顯示彈窗
    $('#addPublicEquipBtn').on('click', function() {
        $publicEquipName.val(''); // 清空輸入框
        $publicEquipModal.modal('show');
    });
    
    // 點擊確認新增公共設備
    $('#addEquipConfirmBtn').on('click', function() {
        const equipName = $publicEquipName.val().trim();
        
        if (equipName === '') {
            alert('請輸入公共設備名稱');
            return;
        }
        
        // 新增設備標籤
        addEquipmentTag(equipName);
        
        // 關閉彈窗
        $publicEquipModal.modal('hide');
    });
    
    // 新增設備標籤的函數
    function addEquipmentTag(name) {
        const tagHtml = `
            <div class="equipment-tag">
                ${name}
                <button type="button" class="btn-close btn-close-sm remove-tag"></button>
                <input type="hidden" name="publicEquipNames" value="${name}">
            </div>
        `;
        
        $publicEquipContainer.append(tagHtml);
    }
    
    // 移除設備標籤 (使用事件委託)
    $publicEquipContainer.on('click', '.remove-tag', function() {
        $(this).closest('.equipment-tag').remove();
    });
    
    // 表單提交處理
    $('#addBranchForm').on('submit', function(e) {
        e.preventDefault();
        
        // 驗證表單
        if (!validateForm()) {
            return;
        }
        
        // 收集表單數據
        const formData = {
            branchName: $('#branchName').val().trim(),
            branchAddr: $('#branchAddr').val().trim(),

            latitude: parseFloat($('#latitude').val()),
            longitude: parseFloat($('#longitude').val()),
            branchStatus: parseInt($('input[name="branchStatus"]:checked').val()),
        };
        
        // 收集公共設備
        const publicEquipmentDTOs = [];
        $('input[name="publicEquipNames"]').each(function() {
            publicEquipmentDTOs.push({
                publicEquipName: $(this).val()
            });
        });
        
        // 發送 AJAX 請求
        $.ajax({
            url: '/branch/add',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                branchDTO: formData,
                publicEquipmentDTOs: publicEquipmentDTOs
            }),
            success: function(response) {
                alert('新增分點成功');
                window.location.href = '/branch.html';
            },
            error: function(xhr, status, error) {
                alert('新增分點失敗: ' + error);
            }
        });
    });
    
    // 表單驗證函數
    function validateForm() {
        // 檢查必填欄位
        const branchName = $('#branchName').val().trim();
        const branchAddr = $('#branchAddr').val().trim();
        const latitude = $('#latitude').val().trim();
        const longitude = $('#longitude').val().trim();
        
        if (!branchName || !branchAddr || !latitude || !longitude) {
            alert('未輸入資料');
            return false;
        }
        
        // 檢查分點狀態
        if (!$('input[name="branchStatus"]:checked').length) {
            alert('未選擇');
            return false;
        }
        
        // 檢查是否有公共設備
        if ($('.equipment-tag').length === 0) {
            alert('未新增公共設備');
            return false;
        }
        
        return true;
    }

    function initGoogleAutocomplete() {
        const input = document.getElementById('branchAddr');
        const autocomplete = new google.maps.places.Autocomplete(input, {
            componentRestrictions: { country: "tw" }, // 限制在台灣（可選）
            fields: ["formatted_address", "geometry"],
        });

        // 當使用者選擇地址建議後，自動填入經緯度
        autocomplete.addListener("place_changed", function () {
            const place = autocomplete.getPlace();

            if (!place.geometry) {
                alert("無法找到該地址的經緯度");
                return;
            }

            // 強制將完整地址填入輸入框（避免只顯示店名）
            if (place.formatted_address) {
                $('#branchAddr').val(getAddressAfterCharacter(place.formatted_address));
            }

            // 填入經緯度
            const location = place.geometry.location;
            $('#latitude').val(location.lat().toFixed(6));
            $('#longitude').val(location.lng().toFixed(6));
        });
    }

    function getAddressAfterCharacter(address, keyword = "灣") {
        const index = address.indexOf(keyword);
        if (index !== -1) {
            return address.substring(index + 1); // +1 是為了排除「灣」這個字
        }
        return address; // 如果沒有找到「灣」，就回傳原始字串
    }

    fetch("/api/config/google-maps-key")
        .then(res => res.json())
        .then(data => {
            const script = document.createElement('script');
            script.src = `https://maps.googleapis.com/maps/api/js?key=${data.key}&libraries=places`;
            script.async = true;
            script.defer = true;

            script.onload = function () {
                initGoogleAutocomplete(); // 等載入完成後才執行
            };
            document.head.appendChild(script);
        });
});