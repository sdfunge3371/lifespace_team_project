$(document).ready(function () {

    // 取得當下時間（格式為 yyyy-MM-ddTHH:mm）
    const now = new Date().toISOString().slice(0, 16);

    // 表單送出時，自行檢查起始日是否小於 now
    $('#yourFormId').on('submit', function (e) {
        const start = $('#newsStartDate').val();

        if (start && start < now) {
            e.preventDefault(); // 阻止送出
            $('#update-newsStartDate-error').text("起始時間不能早於現在！");
            $('#newsStartDate').addClass('is-invalid'); // Bootstrap 樣式
        } else {
            $('#update-newsStartDate-error').text("");
            $('#newsStartDate').removeClass('is-invalid');
        }

        // 可選：也一起檢查結束時間 > 起始時間
        const end = $('#newsEndDate').val();
        if (start && end && end < start) {
            e.preventDefault();
            $('#update-newsEndDate-error').text("結束時間不能早於起始時間！");
            $('#newsEndDate').addClass('is-invalid');
        } else {
            $('#update-newsEndDate-error').text("");
            $('#newsEndDate').removeClass('is-invalid');
        }
    });
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
 
    // 先定義兩個 Promise 任務：載入分類與載入狀態
    const loadCategory = $.ajax({
        url: '/admin/newsCategory/query',
        method: 'GET',
        success: function (categoryList) {
            categoryList.forEach(category => {
                $('#newsCategoryId').append(`
                    <option value="${category.newsCategoryId}">
                        ${category.categoryName}
                    </option>
                `);
            });
        }
    });

    const loadStatus = $.ajax({
        url: '/admin/newsStatus/query',
        method: 'GET',
        success: function (statusList) {
            statusList.forEach(status => {
                $('#newsStatusId').append(`
                    <option value="${status.newsStatusId}">
                        ${status.statusName}
                    </option>
                `);
            });
        }
    });

    // 等分類與狀態都載入完成後，再查詢資料並填入欄位
    $.when(loadCategory, loadStatus).done(function () {
        const urlParams = new URLSearchParams(window.location.search);
        const newsId = urlParams.get('newsId');

        if (newsId) {
            $.ajax({
                url: '/admin/news/queryById',
                method: 'GET',
                data: { newsId: newsId },
                success: function (news) {
                    $('#newsTitle').val(news.newsTitle);
                    $('#newsContent').val(news.newsContent);
					// 轉換日期格式為 input[type="datetime-local"] 可接受的格式
					const formatDate = dateStr => {
					    const date = new Date(dateStr);
					    // 調整為本地時間
					    const localDate = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
					    return localDate.toISOString().slice(0, 16); // yyyy-MM-ddTHH:mm
					};
				
					$('#newsStartDate').val(formatDate(news.newsStartDate));
					$('#newsEndDate').val(formatDate(news.newsEndDate));
					
					// 觸發起始日 change（重新設置結束日最小值）
					$('#newsStartDate').trigger('change');
					
                    $('#newsCategoryId').val(news.newsCategoryId); // 現在選單已經存在才設值！
                    $('#newsStatusId').val(news.newsStatusId);
					if (news.newsImg) {
					  $('#previewImg')
					    .attr('src', `data:image/jpeg;base64,${news.newsImg}`)
					    .show(); // 顯示圖片
					} else {
					  $('#previewImg').hide(); // 沒有圖片就隱藏
					}
                },
                error: function () {
                    alert('查詢消息失敗');
                }
            });
        } else {
            alert("沒有帶入 newsId！");
        }
    });

$('#newsImg').on('change', function () {
  const file = this.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = function (e) {
      $('#previewImg').attr('src', e.target.result).show();
    };
    reader.readAsDataURL(file);
  }
});




//修改NEWS
$('#updateNewsForm').on('submit', function(e) {
		console.log("表單提交事件被觸發");
		e.preventDefault();

//		const updateNewsDto = {
//			newsTitle: $('#newsTitle').val().trim(),
//			newsContent: $('#newsContent').val().trim(),
//			newsCategoryId: $('#newsCategoryId').val().trim(),
//			newsStartDate: $('#newsStartDate').val().trim(),
//			newsEndDate: $('#newsEndDate').val().trim(),
//			newsStatusId: $('#newsStatusId').val().trim(),
//			
//		};
		const urlParams = new URLSearchParams(window.location.search);
		const newsId = urlParams.get('newsId');
		$('#updateNewsForm input[name="newsId"]').val(newsId);
		const form = document.getElementById('updateNewsForm');
		//  FormData(form) 會掃描所有有 name 的欄位並自動包進來
		const formData = new FormData(form); 
		$.ajax({
			url: '/admin/news/updatevalid',
			method: 'POST',
			data: formData,
			//不把 FormData 轉成字串格式（保持原始的二進位傳送）
			processData: false,
			//讓瀏覽器自動設定 multipart/form-data 和 boundary
			contentType: false,
			success: function(res) {
				        alert('修改成功'); // 彈出提示框
				          window.location.href = "/backend_news.html";
			},
			error: function(xhr) {
				const errors = xhr.responseJSON;
				$('#update-newsTitle-error').text(errors.newsTitle || '');
				$('#update-newsContent-error').text(errors.newsContent || '');
				$('#update-newsCategoryId-error').text(errors.newsCategoryId || '');
				$('#update-newsStartDate-error').text(errors.newsStartDate || '');
				$('#update-newsEndDate-error').text(errors.newsEndDate || '');
				$('#update-newsStatusId-error').text(errors.newsStatusId || '');
			}
		});
	});
// 使用者輸入內容時就清除錯誤訊息
$('#newsTitle, #newsContent, #newsCategoryId, #newsStartDate, #newsEndDate, #newsStatusId').on('input change', function () {
    const fieldId = $(this).attr('id');
    $('#update-' + fieldId + '-error').text('');
});


// 取消回首頁
$('#cancelBtn').on('click', function () {
    window.location.href = '/backend_news.html'; 
});

