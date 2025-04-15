$(document).ready(function() {

	// 限制起始日不能小於今天
	const today = new Date().toISOString().slice(0, 16);
	$('#newsStartDate').attr('min', today);

	// 起始日改變時，設定結束日最小值
	$('#newsStartDate').on('change', function() {
		const startDate = $(this).val();
		$('#newsEndDate').attr('min', startDate);
	});
	// 預設觸發一次，確保限制生效
	$('#newsStartDate').trigger('change');

	// 渲染分類與狀態做下拉式選單  
	//1.載入分類
	$.ajax({
		url: '/admin/newsCategory/query',
		method: 'GET',
		success: function(categoryList) {
			categoryList.forEach(category => {
				$('#newsCategoryId').append(`
          <option value="${category.newsCategoryId}">
            ${category.categoryName}
          </option>
        `);
			});
		}
	});


	// 管理員登入
	let adminId = '';  // 假設登入者 ID

	$.ajax({
		url: "http://localhost:8080/admin/news/profile", 
		method: "GET",
		xhrFields: {
			withCredentials: true // 等同於 fetch 的 credentials: "include"
		},
		success: function(response) {
			adminId = response.adminId;
			console.log("登入的管理員ID：", adminId);

		},
		error: function(xhr) {
			if (xhr.status === 401) {
				alert("尚未登入，請先登入");
				window.location.href = "/loginAdmin.html";
			} else {
				console.error("無法取得會員資料", xhr);
			}
		}
	});
	
	// 2️.載入狀態
	$.ajax({
		url: '/admin/newsStatus/query',
		method: 'GET',
		success: function(statusList) {
			statusList.forEach(status => {
				$('#newsStatusId').append(`
          <option value="${status.newsStatusId}">
            ${status.statusName}
          </option>
        `);
			});
		}
	});
});



// 篩選分類消息
$('#categoryFilter').on('change', function() {
	const selectedCategoryId = $(this).val();

	$.ajax({
		url: '/admin/newsCategory/select',
		method: 'GET',
		data: { newsCategoryId: selectedCategoryId },
		success: function(catNewsList) {
			reflashNews(catNewsList.newsList); // 顯示查詢結果
		}
	});
});

// 篩選狀態消息
$('#statusFilter').on('change', function() {
	const selectedStatusId = $(this).val();

	$.ajax({
		url: '/admin/newsStatus/select',
		method: 'GET',
		data: { newsStatusId: selectedStatusId },
		success: function(statusNewsList) {
			reflashNews(statusNewsList.newsList);
		}
	});
});


//新增NEWS
$('#addNewsForm').on('submit', function(e) {
	console.log("表單提交事件被觸發");
	e.preventDefault();

	//		const addNewsDto = {
	//			newsTitle: $('#newsTitle').val().trim(),
	//			newsContent: $('#newsContent').val().trim(),
	//			newsCategoryId: $('#newsCategoryId').val().trim(),
	//			newsStartDate: $('#newsStartDate').val().trim(),
	//			newsEndDate: $('#newsEndDate').val().trim(),
	//			newsStatusId: $('#newsStatusId').val().trim(),
	//			
	//		};

	const form = document.getElementById('addNewsForm');
	//  FormData(form) 會掃描所有有 name 的欄位並自動包進來
	const formData = new FormData(form);


	$.ajax({
	    url: '/admin/news/insertvalid',
	    method: 'POST',
	    data: formData,
	    // 不把 FormData 轉成字串格式（保持原始的二進位傳送）
	    processData: false,
	    // 讓瀏覽器自動設定 multipart/form-data 和 boundary
	    contentType: false,
	    success: function(res) {
	        alert('新增成功'); // 彈出提示框
	          window.location.href = "/backend_news.html";
	    },
	    error: function(xhr) {
	        const errors = xhr.responseJSON;
	        $('#add-newsTitle-error').text(errors.newsTitle || '');
	        $('#add-newsContent-error').text(errors.newsContent || '');
	        $('#add-newsCategoryId-error').text(errors.newsCategoryId || '');
	        $('#add-newsStartDate-error').text(errors.newsStartDate || '');
	        $('#add-newsEndDate-error').text(errors.newsEndDate || '');
	        $('#add-newsStatusId-error').text(errors.newsStatusId || '');
	    }
	});

// 使用者輸入內容時就清除錯誤訊息
$('#newsTitle, #newsContent, #newsCategoryId, #newsStartDate, #newsEndDate, #newsStatusId').on('input change', function() {
	const fieldId = $(this).attr('id');
	$('#add-' + fieldId + '-error').text('');
});


// 取消回首頁
$('#cancelBtn').on('click', function() {
	window.location.href = '/backend_news.html';
});
});