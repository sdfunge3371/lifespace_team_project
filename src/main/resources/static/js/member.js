//-------------------------------------按鈕縮展功能-------------------------------------
// 搜尋欄位的縮展功能
const toggleBtn = document.getElementById("toggleSearchBtn");
const searchContainer = document.getElementById("searchContainer");

toggleBtn.addEventListener("click", function () {
  if (searchContainer.style.display === "none") {
    searchContainer.style.display = "block";
    toggleBtn.textContent = "🔽 隱藏搜尋";
  } else {
    searchContainer.style.display = "none";
    toggleBtn.textContent = "🔍 搜尋";
  }
});


// ----------------------------------按修改按鈕進入個人修改頁面----------------------------------
function editMember(memberId) {
  window.location.href = `/admin/memberupdate?memberId=${memberId}`;
}



// ------------------------------------一進頁面就抓後端資料------------------------------------
fetch('/member') //這要看你的 REST API endpoint 是什麼
  .then(response => response.json())
  .then(data => {
    const tbody = document.getElementById('memberBody');
    data.forEach(member => {
      const row = document.createElement('tr');
      row.innerHTML = `
       <td>${member.memberId}</td>
       <td>${member.memberName}</td>
       <td>${member.email}</td>
       <td>${member.phone}</td>
       <td>${member.accountStatus}</td>
       <td>${member.birthday}</td>
       <td>${member.registrationTime}</td>
       <td><img src="/member/image/${member.memberId}" width="50px" height="50px"></td>
       <td>
         <button onclick="editMember('${member.memberId}')">修改</button>
       </td>
     `;
      tbody.appendChild(row);
    });
  })
  .catch(error => console.error('載入會員資料錯誤:', error));

  
// --------------------------------------重整----------------------------------
document.getElementById("resetBtn").addEventListener("click", function(){
	
	fetch('/member') //這要看你的 REST API endpoint 是什麼
	  .then(response => response.json())
	  .then(data => {
	    const tbody = document.getElementById('memberBody');
		tbody.innerHTML = "";  //清空原本的資料
		
	    data.forEach(member => {
	      const row = document.createElement('tr');
	      row.innerHTML = `
	       <td>${member.memberId}</td>
	       <td>${member.memberName}</td>
	       <td>${member.email}</td>
	       <td>${member.phone}</td>
	       <td>${member.accountStatus}</td>
	       <td>${member.birthday}</td>
	       <td>${member.registrationTime}</td>
	       <td><img src="/member/image/${member.memberId}" width="50px" height="50px"></td>
	       <td>
	         <button onclick="editMember('${member.memberId}')">修改</button>
	       </td>
	     `;
	      tbody.appendChild(row);
	    });
	  })
	  .catch(error => console.error('載入會員資料錯誤:', error));
	
});






//---------------------------------------查詢資訊---------------------------------------------------------
//url要記得連到"正確的位置"，貓那邊是8080，前端原本預設5050，難怪連不到
const BASE_URL = "http://localhost:8080";

document.getElementById("searchBtn").addEventListener("click", function () {
	
  //(1)取得所有欄位的值	
  const memberId = document.getElementById("memberId").value.trim();
  const memberName = document.getElementById("memberName").value.trim();
  const email = document.getElementById("email").value.trim();
  const phone = document.getElementById("phone").value.trim();
  const accountStatus = document.getElementById("accountStatus").value.trim();
  const registrationTime = document.getElementById("registrationTime").value.trim();
  const birthday = document.getElementById("birthday").value.trim();
  
  //(2)將這些欄位組成JSON物件
  let queryData = {
	memberId,
	memberName,
	email,
	phone,
	accountStatus,
	registrationTime,
	birthday
  };
  
  //錯誤處理
  const allFieldsEmpty = !memberId && !memberName && !email && !phone && !accountStatus && !registrationTime && !birthday;
  if (allFieldsEmpty) {
    alert("請至少輸入一個查詢條件！");
    return;
  }
  
  //(3)發送POST請求到後端的/member/search
  fetch(`${BASE_URL}/member/search`,{
	method:"POST",
	headers:{
		"Content-Type":"application/json"
	},
	body: JSON.stringify(queryData)
  })
  .then(res => {
	if(!res.ok)throw new Error("查無資料");
	return res.json();
  })
  .then(data => {
	//(4)顯示查詢結果
	document.getElementById("memberBody").style.display = "none";
	let resultArea = document.getElementById("resultArea");
	resultArea.innerHTML = "";
	
	if(data.length === 0){
		resultArea.innerHTML = `<tr><td colspan="10" style="color:orange;">沒有符合的會員資料</td></tr>`;
		return;
	}
	
	data.forEach(member => {
		let row = document.createElement("tr");
		row.innerHTML = `
		<td>${member.memberId}</td>
		         <td>${member.memberName}</td>
		         <td>${member.email}</td>
		         <td>${member.phone}</td>
		         <td>${member.accountStatus}</td>
		         <td>${member.birthday}</td>
		         <td>${member.registrationTime}</td>
		         <td><img src="${BASE_URL}/member/image/${member.memberId}" width="50px" height="50px"></td>
		         <td><button onclick="editMember('${member.memberId}')">修改</button></td>
		`;
		resultArea.appendChild(row);
	});
  })
  .catch(error => {
        document.getElementById("resultArea").innerHTML =
          `<tr><td colspan="10" style="color:red;">查詢失敗：${error.message}</td></tr>`;
      });
  });
  




