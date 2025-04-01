//-------------------------------------按鈕縮展功能-------------------------------------
// 單一搜尋欄位的縮展功能
const toggleBtn = document.getElementById("toggleSearchBtn");
const searchContainer = document.getElementById("searchContainer");

toggleBtn.addEventListener("click", function () {
  if (searchContainer.style.display === "none") {
    searchContainer.style.display = "block";
    toggleBtn.textContent = "🔽 隱藏單一搜尋";
  } else {
    searchContainer.style.display = "none";
    toggleBtn.textContent = "🔍 顯示單一搜尋";
  }
});

// 多樣搜尋欄位的縮展功能
const toggleManyBtn = document.getElementById("toggleSearchManyBtn");
const searchManyContainer = document.getElementById("searchManyContainer");

toggleManyBtn.addEventListener("click", function () {
  if (searchManyContainer.style.display === "none") {
    searchManyContainer.style.display = "block";
    toggleManyBtn.textContent = "🔽 隱藏多樣搜尋";
  } else {
    searchManyContainer.style.display = "none";
    toggleManyBtn.textContent = "🔍 顯示多樣搜尋";
  }
});


// ----------------------------------按修改按鈕進入個人修改頁面----------------------------------
function editMember(memberId) {
  window.location.href = `memberupdate.html?memberId=${memberId}`;
}



// ------------------------------------一進頁面就抓後端資料------------------------------------
fetch('http://localhost:8080/member') //這要看你的 REST API endpoint 是什麼
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
       <td>${member.password}</td>
       <td>${member.birthday}</td>
       <td>${member.registrationTime}</td>
       <td><img src="http://localhost:8080/member/image/${member.memberId}" width="50px" height="50px"></td>
       <td>
         <button onclick="editMember('${member.memberId}')">修改</button>
       </td>
     `;
      tbody.appendChild(row);
    });
  })
  .catch(error => console.error('載入會員資料錯誤:', error));



//---------------------------------------查詢單一資訊---------------------------------------------------------

//url要記得連到"正確的位置"，貓那邊是8080，前端原本預設5050，難怪連不到
let url = ""; // 要呼叫的 API 路徑
const BASE_URL = "http://localhost:8080";

//篩選資料
function searchMember(type, value) {
  if (!value) {
    alert("請輸入查詢內容！");i
    return;
  }

  const url = `${BASE_URL}/member/${type}/${value}`;

  fetch(url)
    .then((response) => {
      if (!response.ok) {
        throw new Error("查無資料");
      }
      return response.json();
    })
    .then((data) => {
      //先把所有會員的資料隱藏
      document.getElementById("memberBody").style.display = "none";
      const result = `
         <td>${data.memberId}</td>
         <td>${data.memberName}</td>
         <td>${data.email}</td>
         <td>${data.phone}</td>
         <td>${data.accountStatus}</td>
         <td>${data.password}</td>
         <td>${data.birthday}</td>
         <td>${data.registrationTime}</td>
         <td><img src="/MemberImageServlet?memberId=${data.memberId}" width="50px" height="50px"></td>
         <td><button onclick="editMember('${data.memberId}')">修改</button></td>
       `;
      document.getElementById("resultArea").innerHTML = result;
    })
    .catch((error) => {
      document.getElementById("resultArea").innerHTML = `<p style="color:red;">查詢失敗：${error.message}</p>`;
    });
}

//連結按鈕
document.getElementById("searchIdBtn").addEventListener("click", function () {
  let value = document.getElementById("memberId").value.trim();
  searchMember("id", value);
});

document.getElementById("searchNameBtn").addEventListener("click", function () {
  let value = document.getElementById("memberName").value.trim();
  searchMember("name", value);
});

document.getElementById("searchPhoneBtn").addEventListener("click", function () {
  let value = document.getElementById("phone").value.trim();
  searchMember("phone", value);
});

document.getElementById("searchEmailBtn").addEventListener("click", function () {
  let value = document.getElementById("email").value.trim();
  searchMember("email", value);
});



//---------------------------------------查詢多樣資訊----------------------------------------------
document.getElementById("searchManyBtn").addEventListener("click", function () {
  const accountStatus = document.getElementById("accountStatus").value;
  const registrationTime = document.getElementById("registrationTime").value;
  const birthday = document.getElementById("birthday").value;

  // 動態組成 query string
  const params = new URLSearchParams();
  if (accountStatus) params.append("accountStatus", accountStatus);
  if (registrationTime) params.append("registrationTime", registrationTime);
  if (birthday) params.append("birthday", birthday);

  const url = `${BASE_URL}/member/search?${params.toString()}`;

  fetch(url)
    .then(res => {
      if (!res.ok) throw new Error("查無資料");
      return res.json();
    })
    .then(data => {
      document.getElementById("memberBody").style.display = "none";

      const resultArea = document.getElementById("resultArea");
      resultArea.innerHTML = "";

      data.forEach(member => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${member.memberId}</td>
          <td>${member.memberName}</td>
          <td>${member.email}</td>
          <td>${member.phone}</td>
          <td>${member.accountStatus}</td>
          <td>${member.password}</td>
          <td>${member.birthday}</td>
          <td>${member.registrationTime}</td>
          <td><img src="/member/image/${member.memberId}" width="50px" height="50px"></td>
          <td><button onclick="editMember('${member.memberId}')">修改</button></td>
        `;
        resultArea.appendChild(row);
      });
    })
    .catch(error => {
      document.getElementById("resultArea").innerHTML = `<tr><td colspan="10" style="color:red;">查詢失敗：${error.message}</td></tr>`;
    });
});









// let urlMany = ""; // 要呼叫的 API 路徑
// //篩選資料
// function searchMember(type, value) {

//              //BASE_URL在前面已經定義囉
//   const url = `${BASE_URL}/member/${type}/${value}`;

//   fetch(url)
//     .then((response) => {
//       if (!response.ok) {
//         throw new Error("查無資料");
//       }
//       return response.json();
//     })
//     .then((data) => {
//       //先把所有會員的資料隱藏
//       document.getElementById("memberBody").style.display = "none";
//       const result = `
//          <td>${data.memberId}</td>
//          <td>${data.memberName}</td>
//          <td>${data.email}</td>
//          <td>${data.phone}</td>
//          <td>${data.accountStatus}</td>
//          <td>${data.password}</td>
//          <td>${data.birthday}</td>
//          <td>${data.registrationTime}</td>
//          <td><img src="/MemberImageServlet?memberId=${data.memberId}" width="50px" height="50px"></td>
//          <td><button onclick="editMember('${data.memberId}')">修改</button></td>
//        `;
//       document.getElementById("resultArea").innerHTML = result;
//     })
//     .catch((error) => {
//       document.getElementById("resultArea").innerHTML = `<p style="color:red;">查詢失敗：${error.message}</p>`;
//     });

// }


// //連結按鈕
// document.getElementById("searchManyBtn").addEventListener("click", function () {
//   const value = document.getElementById("accountStatus").value.trim();
//   searchMember("accountStatus", value);
// });






