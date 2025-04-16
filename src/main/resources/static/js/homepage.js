$(document).ready(function () {
            // 監聽跑馬燈訊息的點擊事件
            $('#marquee-alert').click(function () {
                // 取得跑馬燈內容
                var marqueeContent = $('#marquee-content').html();

                // 將跑馬燈內容放入 Modal body
                $('#marqueeModalBody').html(marqueeContent);

                // 顯示 Modal
                $('#marqueeModal').modal('show');
            });
			
			$.ajax({
			       url: '/member/news/top3', 
			       method: "GET",
			       success: function (newsList) {
			           let html = '';
			           newsList.forEach((news, index) => {
			               html += `
			                   <tr>
			                       <th scope="row">${index + 1}</th>
			                       <td>${news.newsTitle}</td>
			                       <td>${news.newsContent}</td>
			                   </tr>
			               `;
			           });

			           // 如果回傳是空的，就顯示提示文字
			           if (html === '') {
			               html = `<tr><td colspan="3">目前沒有消息</td></tr>`;
			           }

			           $('#newsTableBody').html(html);
			       },
			       error: function () {
			           $('#newsTableBody').html('<tr><td colspan="3">載入失敗</td></tr>');
			       }
			   });
        });