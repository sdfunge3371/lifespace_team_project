//-------------------------------------按鈕縮展功能-------------------------------------
// 搜尋欄位的縮展功能
const toggleBtn = document.getElementById("toggleSearchBtn");
const searchContainer = document.getElementById("searchContainer");

toggleBtn.addEventListener("click", function() {
	if (searchContainer.style.display === "none") {
		searchContainer.style.display = "block";
		toggleBtn.textContent = "🔽 隱藏搜尋";
	} else {
		searchContainer.style.display = "none";
		toggleBtn.textContent = "🔍 搜尋";
	}
});


// ----------------------------------按修改按鈕進入個人修改頁面----------------------------------
function editAdmin(adminId) {
	window.location.href = `adminUpdate.html?adminId=${adminId}`;
}



// ------------------------------------一進頁面就抓後端資料------------------------------------
fetch('/admin') //這要看你的 REST API endpoint 是什麼
	.then(response => response.json())
	.then(data => {
		const tbody = document.getElementById('adminBody');
		data.forEach(admin => {
			const row = document.createElement('tr');
			row.innerHTML = `
       <td>${admin.adminId}</td>
       <td>${admin.adminName}</td>
       <td>${admin.email}</td>
       <td>${admin.accountStatus}</td>
       <td>${admin.registrationTime}</td>
       <td>
         <button onclick="editAdmin('${admin.adminId}')">修改</button>
       </td>
     `;
			tbody.appendChild(row);
		});
	})
	.catch(error => console.error('載入會員資料錯誤:', error));



//---------------------------------------查詢資訊---------------------------------------------------------
const BASE_URL = "";

document.getElementById("searchBtn").addEventListener("click", function() {

	//(1)取得所有欄位的值	
	const adminId = document.getElementById("adminId").value.trim();
	const adminName = document.getElementById("adminName").value.trim();
	const email = document.getElementById("email").value.trim();
	const accountStatus = document.getElementById("accountStatus").value.trim();
	const registrationTime = document.getElementById("registrationTime").value.trim();

	//(2)將這些欄位組成JSON物件
	let queryData = {
		adminId,
		adminName,
		email,
		accountStatus,
		registrationTime
	};

	//錯誤處理
	const allFieldsEmpty = !adminId && !adminName && !email && !accountStatus && !registrationTime;
	if (allFieldsEmpty) {
		alert("請至少輸入一個查詢條件！");
		return;
	}

	//(3)發送POST請求到後端的/member/search
	fetch(`/admin/search`, {
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify(queryData)
	})
	.then(res => {
	if(!res.ok)throw new Error("查無資料");
	return res.json();
	})
		.then(data => {
			//(4)顯示查詢結果
			document.getElementById("adminBody").style.display = "none";
			let resultArea = document.getElementById("resultArea");
			resultArea.innerHTML = "";

			if (data.length === 0) {
				resultArea.innerHTML = `<tr><td colspan="10" style="color:orange;">沒有符合的會員資料</td></tr>`;
				return;
			}

			data.forEach(admin => {
				let row = document.createElement("tr");
				row.innerHTML = `
					<td>${admin.adminId}</td>
					         <td>${admin.adminName}</td>
					         <td>${admin.email}</td>
					         <td>${admin.accountStatus}</td>
					         <td>${admin.registrationTime}</td>
					         <td><button onclick="editAdmin('${admin.adminId}')">修改</button></td>
					`;
				resultArea.appendChild(row);
			});
		})
		.catch(error => {
			document.getElementById("resultArea").innerHTML =
				`<tr><td colspan="10" style="color:red;">查詢失敗：${error.message}</td></tr>`;
		});
});





















