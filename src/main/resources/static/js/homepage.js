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
			   
			   
			   // 取得前6筆最新活動資料
			   $.ajax({
			       url: '/lifespace/event/getNewEvents',
			       method: 'GET',
			       success: function (res) {
			           const container = $('#latestEventsContainer');
			           if (res && res.content && res.content.length > 0) {
			               res.content.forEach(event => {
			                   const photoUrl = (event.photoUrls && event.photoUrls.length > 0) ? event.photoUrls[0] : 'images/default_event.jpg';
			                   const start = new Date(event.eventStartTime).toLocaleString();
			                   const end = new Date(event.eventEndTime).toLocaleString();

							   const eventCard = document.createElement('div');
							   eventCard.className = 'col-md-4 mb-4';
			                   eventCard.innerHTML = `
			                           <div class="card h-100">
			                               <img src="${photoUrl}" class="card-img-top event-img" alt="活動圖片">
			                               <div class="card-body">
			                                   <h5 class="card-title">${event.eventName}</h5>
			                                   <p class="card-text"><i class="fa fa-clock"></i> ${start} ~ ${end}</p>
			                               </div>
			                           </div>
			                   `;
							   
							   eventCard.addEventListener('click', () => {
							   		   	   	      window.location.href = `event_detail.html?eventId=${event.eventId}`;
							   		   	   });
										   
			                   container.append(eventCard);
			               });
			           } else {
			               container.append(`<p class="text-center">目前沒有活動</p>`);
			           }
			       },
			       error: function () {
			           $('#latestEventsContainer').html('<p class="text-danger text-center">載入活動失敗</p>');
			       }
			   });
			   
			   
			   
        });