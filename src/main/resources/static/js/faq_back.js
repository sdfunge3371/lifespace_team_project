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

	$(document).ready(function () {
	    const adminId = 'A001'; // 假設登入者 ID

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

});


