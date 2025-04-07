$(document).ready(function () {
  $.ajax({
    url: '/admin/news/query',
    method: 'GET',
    dataType: 'json',
    success: function (newsList) {
      reflashNews(newsList); // 渲染畫面

    }
  });
});

// 渲染全部畫面的函式方法
function reflashNews(newsList) {
  // 如果已經初始化過 DataTable，就先銷毀它
  if ($.fn.DataTable.isDataTable('#newsTable')) {
    $('#newsTable').DataTable().destroy();
  }

  // 先清空 tbody
  $('#newsBody').empty();

  // 填入資料
  let html = '';
  newsList.forEach(news => {
    const startDate = new Date(news.newsStartDate).toLocaleDateString('zh-TW');
    const endDate = new Date(news.newsEndDate).toLocaleDateString('zh-TW');

    let statusText = "";
    const statusName = news.newsStatusName;
    if (news.newsStatusId === 0) {
      statusText = '<span style="color:red; font-weight:bold;">' + statusName + '</span>';
    } else if (news.newsStatusId === 1) {
      statusText = '<span style="color:green; font-weight:bold;">' + statusName + '</span>';
    } else if (news.newsStatusId === 2) {
      statusText = '<span style="color:orange; font-weight:bold;">' + statusName + '</span>';
    }

    const imgHtml = news.newsImg
      ? `<img src="data:image/jpeg;base64,${news.newsImg}" width="80">`
      : '無圖片';

    html += `
      <tr data-news-id="${news.newsId}">
        <td>${news.newsId}</td>
        <td>${news.newsTitle}</td>
        <td>${news.newsContent}</td>
        <td>${news.newsCategoryName}</td>
        <td>${startDate}</td>
        <td>${endDate}</td>
        <td>${news.adminId}</td>
        <td>${statusText}</td>
        <td>${imgHtml}</td>
        <td>
          <a href="backend_updatenews.html?newsId=${news.newsId}" class="btn btn-primary btn-sm">修改</a>
          <button onclick="confirmDeprecated('${news.newsId}')" class="btn btn-danger btn-sm">下架</button>
        </td>
      </tr>
    `;
  });

  $('#newsBody').html(html);

  // 重新初始化 DataTable
  $('#newsTable').DataTable({
    responsive: true,
    destroy: true, // 加這個是保險，多寫一次也不會壞
    language: {
      processing: "處理中...",
      loadingRecords: "載入中...",
      lengthMenu: "顯示 _MENU_ 筆資料",
      zeroRecords: "沒有符合的結果",
      info: "顯示第 _START_ 至 _END_ 筆結果，共 _TOTAL_ 筆",
      infoEmpty: "顯示第 0 至 0 筆結果，共 0 筆",
      infoFiltered: "(從 _MAX_ 筆資料中過濾)",
      search: "搜尋:",
      paginate: {
        first: "第一頁",
        previous: "上一頁",
        next: "下一頁",
        last: "最後一頁"
      },
      aria: {
        sortAscending: ": 升冪排列",
        sortDescending: ": 降冪排列"
      }
    }
  });
}


// 渲染分類與狀態做下拉式選單
$(document).ready(function() {
	//1.載入分類
	$.ajax({
		url: '/admin/newsCategory/query',
		method: 'GET',
		success: function(categoryList) {
			categoryList.forEach(category => {
				$('#categoryFilter').append(`
          <option value="${category.newsCategoryId}">
            ${category.categoryName}
          </option>
        `);
			});
		}
	});

	// 2️.載入狀態
	$.ajax({
		url: '/admin/newsStatus/query',
		method: 'GET',
		success: function(statusList) {
			statusList.forEach(status => {
				$('#statusFilter').append(`
          <option value="${status.newsStatusId}">
            ${status.statusName}
          </option>
        `);
			});
		}
	});
});

// 綁定分類和狀態下拉選單的change事件
$('#categoryFilter, #statusFilter').on('change', function () {
    const selectedCategoryId = $('#categoryFilter').val();
    const selectedStatusId = $('#statusFilter').val();

    // 發送 AJAX 請求到後端
    $.ajax({
        url: '/admin/news/select',
        method: 'GET',
        data: {
            newsCategoryId: selectedCategoryId,
            newsStatusId: selectedStatusId
        },
        success: function (newsList) {
            reflashNews(newsList); // 直接顯示查詢結果
        },
        error: function () {
            alert('查詢失敗，請稍後再試');
        }
    });
});


//------以下寫法只有個別篩選，不會兩者連動篩選--------
// 篩選分類消息
//$('#categoryFilter').on('change', function() {
//	const selectedCategoryId = $(this).val();
//
//	$.ajax({
//		url: '/admin/newsCategory/select',
//		method: 'GET',
//		data: { newsCategoryId: selectedCategoryId },
//		success: function(catNewsList) {
//			reflashNews(catNewsList.newsList); // 顯示查詢結果
//		}
//	});
//});
//
//
//// 篩選狀態消息
//$('#statusFilter').on('change', function() {
//	const selectedStatusId = $(this).val();
//
//	$.ajax({
//		url: '/admin/newsStatus/select',
//		method: 'GET',
//		data: { newsStatusId: selectedStatusId },
//		success: function(statusNewsList) {
//			reflashNews(statusNewsList.newsList);
//		}
//	});
//});
//------以上寫法只有個別篩選，不會兩者連動篩選--------



// 處理狀態(下架按鈕)
function confirmDeprecated(newsId) {
	if (!confirm('確定要下架嗎？')) return;

	$.ajax({
		url: '/admin/news/deprecated',
		method: 'POST',
		data: { newsId: newsId }, // 將 ID 傳給後端
		success: function() {
			alert('下架成功！');
			location.reload();
		},
		error: function() {
			alert('下架失敗');
		}
	});
}




