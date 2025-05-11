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
							   		   	   	      window.location.href = `/lifespace/event_detail?eventId=${event.eventId}`;
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


				// 取得6筆空間
				fetch("/spaces")
                    .then(response => response.json())
                    .then(data => data.filter(space => space.spaceStatus !== 0 && space.branchStatus !== 0))
                    .then(data => {
                        console.log(data);
						const randomSpaces = getRandomElements(data, 6);
                        const spaceContainer = document.querySelector("#spaceContainer");

						randomSpaces.forEach(space => {
                            const spaceCard = document.createElement('div');
                            spaceCard.className = 'space-card';
                            spaceCard.dataset.id = space.spaceId;
                            spaceCard.dataset.branchId = space.branchId;
                            const addressText = `${space.branchAddr}${space.spaceFloor}${space.spaceFloor ? "樓" : ""}`;

                            spaceCard.innerHTML = `
								<div class="space-image">    
									<img src="${getFirstPhoto(space.spacePhotos)}" alt="空間圖片">
								</div>
								<div class="space-info">
									<div class="space-title">
										<span>${space.spaceName}</span>
									</div>
									<div class="space-location">
										<div class="location-text">
											<i class="fas fa-map-marker-alt"></i> ${addressText}
										</div>
										<div class="people-count">
											<i class="fas fa-user"></i> ${space.spacePeople}
										</div>
									</div>
									<div class="space-rating">
										<span class="space-price">$${space.spaceHourlyFee}/hr</span>
										<div class="rating-stars">
											<i class="fas fa-star"></i> ${space.spaceRating.toFixed(1)}</div>
									</div>
								</div>
							`;
                            spaceContainer.appendChild(spaceCard);

                            spaceCard.addEventListener('click', () => {
                                window.location.href = `/lifespace/individual_space?spaceId=${space.spaceId}`;
                            });
                        })
                    })
                    .catch(error => {
                        console.log(error);
                    })

				function getRandomElements(array, count) {
					const shuffled = array.slice(); // 複製一份陣列，避免改變原陣列
					for (let i = shuffled.length - 1; i > 0; i--) {
						const j = Math.floor(Math.random() * (i + 1));
						[shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
					}
					return shuffled.slice(0, count);
				}

				function getFirstPhoto(spacePhoto) {
					if (spacePhoto.length === 0) {
						return "default.jpg";
					}
					return "data:image/jpeg;base64," + spacePhoto[0].photo;
				}
			   
			   
        });