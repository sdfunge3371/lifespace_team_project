//網頁載入完成後執行裡面的程式碼
$(document).ready(function () {
	//向後端發出 AJAX 請求
    $.ajax({	
		//呼叫後端Controller的@GetMapping("query")
        url: '/admin/faq/query',
        method: 'GET',
        dataType: 'json',
		//當後端回傳成功（HTTP 200），就會執行faqList函式
        success: function (faqList) {
            let html = '';

            faqList.forEach(faq => {
                const statusText = faq.faqStatus === 1
                    ? '<span style="color:#28a745; font-weight:bold;">顯示中</span>'
                    : '<span style="color:#dc3545; font-weight:bold;">已下架</span>';
				//時間轉成可讀格式
                const createTime = new Date(faq.createTime).toLocaleString('zh-TW');

                html += `
                    <tr data-faq-id="${faq.faqId}">
                        <td>${faq.faqId}</td>
                        <td>${faq.adminId}</td>
                        <td>${faq.faqAsk}</td>
                        <td>${faq.faqAnswer}</td>
                        <td>${createTime}</td>
                        <td>${statusText}</td>
                        <td><button class="btn-edit">編輯</button></td>
                        <td><button class="btn-deprecated">下架</button></td>
                    </tr>
                `;
            });
			//將整段組好的表格放進 <tbody id="faqBody">，獲得完整表格
            $('#faqBody').html(html);

            //  資料填入後再初始化 DataTable
            new DataTable("#faqTable", {
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
    });
	
	// 處理狀態(下架按鈕)
	$('#faqBody').on('click', '.btn-deprecated', function () {
	    if (!confirm('確定要下架嗎？')) return;

	    const row = $(this).closest('tr');
	    const faqId = row.data('faq-id');

	    $.ajax({
	        url: '/admin/faq/deprecated',
	        method: 'POST',
			data: { faqId: faqId },
	        success: function () {
	            alert('下架成功！');
				location.reload();
	        },
	        error: function () {
	            alert('下架失敗');
	        }
	    });
	});

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
	                window.location.href = "/lifespace/loginAdmin";
	            } else {
	                console.error("無法取得會員資料", xhr);
	            }
	        }
	    });

		

	    // 開啟新增 modal
	    $('#btn-add-faq').on('click', function () {
	        $('#add-faqAsk').val('');
	        $('#add-faqAnswer').val('');
	        $('#add-faqAsk-error').text('');
	        $('#add-faqAnswer-error').text('');

	        new bootstrap.Modal(document.getElementById('addFaqModal')).show();
	    });

	    //新增FAQ
	    $('#addFaqForm').on('submit', function (e) {
	        e.preventDefault();

	        const faqDto = {
	            faqAsk: $('#add-faqAsk').val().trim(),
	            faqAnswer: $('#add-faqAnswer').val().trim(),
	            adminId: adminId
	        };

	        fetch('/admin/faq/insertvalid', {
	            method: 'POST',
	            headers: { 'Content-Type': 'application/json' },
	            body: JSON.stringify(faqDto)
	        }).then(res => {
	            if (res.ok) {
	                alert('新增成功');
	                bootstrap.Modal.getInstance(document.getElementById('addFaqModal')).hide();
	                location.reload();
	            } else {
					//取得Controller驗證的錯誤訊息
	                res.json().then(data => {
	                    $('#add-faqAsk-error').text(data.faqAsk || '');
	                    $('#add-faqAnswer-error').text(data.faqAnswer || '');
						
	                });
	            }
	        }).catch(() => alert('新增失敗'));
	    });

	    // 開啟編輯 modal
	    $('#faqBody').on('click', '.btn-edit', function () {
	        const row = $(this).closest('tr');
	        $('#edit-faqId').val(row.data('faq-id'));
	        $('#edit-faqAsk').val(row.find('td').eq(2).text());
	        $('#edit-faqAnswer').val(row.find('td').eq(3).text());
	        $('#edit-faqAsk-error').text('');
	        $('#edit-faqAnswer-error').text('');

	        new bootstrap.Modal(document.getElementById('editFaqModal')).show();
			
			
	    });
		
		
		// Modal 關閉時觸發清除
		document.getElementById('addFaqModal').addEventListener('hidden.bs.modal', function () {
		    $('#add-faqAsk').val('');
		    $('#add-faqAnswer').val('');
		    $('#add-faqAsk-error').text('');
		    $('#add-faqAnswer-error').text('');
		});
	
		
		
	    // 修改FAQ
	    $('#editFaqForm').on('submit', function (e) {
	        e.preventDefault();

	        const faqDto = {
	            faqId: $('#edit-faqId').val(),
	            faqAsk: $('#edit-faqAsk').val().trim(),
	            faqAnswer: $('#edit-faqAnswer').val().trim()
	        };

	        fetch('/admin/faq/updatevalid', {
	            method: 'POST',
	            headers: { 'Content-Type': 'application/json' },
	            body: JSON.stringify(faqDto)
	        }).then(res => {
	            if (res.ok) {
	                alert('修改成功');
	                bootstrap.Modal.getInstance(document.getElementById('editFaqModal')).hide();
	                location.reload();
	            } else {
					//取得Controller驗證的錯誤訊息
	                res.json().then(data => {
	                    $('#edit-faqAsk-error').text(data.faqAsk || '');
	                    $('#edit-faqAnswer-error').text(data.faqAnswer || '');
	                });
	            }
	        }).catch(() => alert('修改失敗'));
	    });
		
		  // 熱門點擊
		  $('#btn-view-popular-faq').on('click', function () {
		    const endDate = new Date().toISOString().slice(0, 10); // 今天
		    const startDate = new Date(Date.now() - 30 * 24 * 3600 * 1000).toISOString().slice(0, 10); // 過去30天

		    $('#popularFaqDateRange').text(`（${startDate} ~ ${endDate}）`);

		    $.get('/admin/faq/ga/popular-events', {
		      startDate: startDate,
		      endDate: endDate,
		      eventName: 'faq_click',
		      limit: 10   // 設多一點，避免前面出現 (not set) 會影響資料筆數
		    }, function (data) {
		      const $body = $('#popularFaqBody');
		      $body.empty();

		      //過濾掉 not set與空字串，再取前5筆 
			  const filtered = data
			    .filter(item => item.faqId !== '(not set)' && item.faqTitle && item.faqTitle.trim() !== '')
			    .slice(0, 5);

		      // 正常資料顯示在表格中
		      if (filtered.length === 0) {
		        $body.append(`<tr><td colspan="3" class="text-center text-muted">目前無熱門點擊資料</td></tr>`);
		      } else {
		        filtered.forEach(item => {
		          $body.append(`
		            <tr>
		              <td>${item.faqTitle}</td>
		              <td>${item.faqId}</td>
		              <td class="text-end">${item.eventCount} 次</td>
		            </tr>
		          `);
		        });
		      }

		      // 顯示 Modal
		      new bootstrap.Modal(document.getElementById('popularFaqModal')).show();
		    })
		    .fail(function () {
		      alert('❌ 取得熱門 FAQ 失敗，請稍後再試');
		    });
		  });
		});
			
	// 新增時，當使用者輸入常見問題欄位時，自動清除錯誤訊息
	$('#add-faqAsk').on('input', function () {
	    if ($(this).val().trim() !== '') {
	        $('#add-faqAsk-error').text('');
	    }
	});

	// 新增時，當使用者輸入回答欄位時，自動清除錯誤訊息
	$('#add-faqAnswer').on('input', function () {
	    if ($(this).val().trim() !== '') {
	        $('#add-faqAnswer-error').text('');
	    }
	});

	// 修改時，當使用者輸入常見問題欄位時，自動清除錯誤訊息
	$('#edit-faqAsk').on('input', function () {
	    if ($(this).val().trim() !== '') {
	        $('#edit-faqAsk-error').text('');
	    }
	});

	// 修改時，當使用者輸入回答欄位時，自動清除錯誤訊息
	$('#edit-faqAnswer').on('input', function () {
	    if ($(this).val().trim() !== '') {
	        $('#edit-faqAnswer-error').text('');
	    }
	});




