let newsTable = null; // 全域變數，儲存 DataTable 實例

$(document).ready(function () {
  $.ajax({
    url: '/member/news/query',
    method: 'GET',
    success: function (newsList) {
      renderNewsList(newsList);
    },
    error: function () {
      alert('載入最新消息失敗');
    }
  });

  // 載入分類下拉選單
  $.ajax({
    url: '/member/newsCategory/query',
    method: 'GET',
    success: function (categoryList) {
      categoryList.forEach(category => {
        $('#categoryFilter').append(`
          <option value="${category.newsCategoryId}">
            ${category.categoryName}
          </option>
        `);
      });
    }
  });

  // 篩選分類
  $('#categoryFilter').on('change', function () {
    const selectedCategoryId = $(this).val();
    $.ajax({
      url: '/member/newsCategory/select',
      method: 'GET',
      data: { newsCategoryId: selectedCategoryId },
      success: function (category) {
        renderNewsList(category.newsList);
      }
    });
  });
});

// 將 AJAX 回傳的資料渲染到表格中
function renderNewsList(newsList) {
  const tableData = newsList.map(news => {
    const imgHtml = news.newsImg
      ? `<img src="data:image/jpeg;base64,${news.newsImg}" width="100">`
      : '無圖片';
    return [
      news.newsTitle,
      news.newsContent,
      news.newsCategoryName,
      imgHtml
    ];
  });

  // 如果 DataTable 已存在就銷毀並重建
  if (newsTable !== null) {
    newsTable.clear().rows.add(tableData).draw();
  } else {
    newsTable = $('#newsTable').DataTable({
      data: tableData,
      columns: [
        { title: "消息標題" },
        { title: "消息內容" },
        { title: "消息種類" },
        { title: "消息圖片" }
      ],
      language: {
        search: "搜尋：",
        lengthMenu: "顯示 _MENU_ 筆",
        info: "顯示第 _START_ 到 _END_ 筆，共 _TOTAL_ 筆",
		infoFiltered: "（由 _MAX_ 筆資料中篩選）",
        paginate: {
          first: "第一頁",
          last: "最後一頁",
          next: "下一頁",
          previous: "上一頁"
        },
        zeroRecords: "找不到符合的資料"
      }
    });
  }
}
