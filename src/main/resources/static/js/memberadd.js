
//---------------------圖片預覽 + AJAX 傳送（使用 FormData）----------------------------
function previewImage(event) {
    const reader = new FileReader();
    reader.onload = function () {
      const preview = document.getElementById('preview');
      preview.src = reader.result;
      preview.style.display = 'block';
      document.getElementById('cancelBtn').style.display = 'inline-block';
    };
    reader.readAsDataURL(event.target.files[0]);
  }
  
  function cancelImage() {
    document.getElementById('memberImage').value = '';
    document.getElementById('preview').style.display = 'none';
    document.getElementById('cancelBtn').style.display = 'none';
  }
  
  function submitForm() {
    const formData = new FormData();
    formData.append("memberName", document.getElementById("memberName").value);
    formData.append("email", document.getElementById("email").value);
    formData.append("phone", document.getElementById("phone").value);
    formData.append("accountStatus", document.getElementById("accountStatus").value);
    formData.append("password", document.getElementById("password").value);
    formData.append("birthday", document.getElementById("birthday").value);
  
    const fileInput = document.getElementById("memberImage");
    if (fileInput.files.length > 0) {
      formData.append("memberImage", fileInput.files[0]);  // 確保檔案有選再加
    }
  
	fetch("/member", {
	  method: "POST",
	  body: formData
	})
	.then(response => {
	  if (!response.ok) {
	    // 嘗試解析成 JSON
	    return response.json().then(err => {
	      // 顯示所有錯誤（或選擇只取第一筆）
	      alert(err.errors?.join("\n") || err.message || "未知錯誤");
	      throw new Error("驗證失敗");
	    }).catch(() => {
	      // 如果不是 JSON 格式（例如普通文字錯誤）
	      return response.text().then(msg => {
	        alert("錯誤：" + msg);
	        throw new Error("格式錯誤");
	      });
	    });
	  }
	  return response.text();
	})
	.then(msg => {
	  alert("新增成功！");
	  window.location.href = "/admin/member";
	})
	.catch(err => {
	  console.error("錯誤資訊：", err);
	});











//----------------------------------顯示照片有關------------------------------------
function previewImage(event) {
    var reader = new FileReader();
    reader.onload = function () {
        var output = document.getElementById('preview');
        output.src = reader.result;
        output.style.display = "block";

        // 當有選擇照片時，顯示取消按鈕
        document.getElementById('cancelBtn').style.display = "inline-block";
    }
    reader.readAsDataURL(event.target.files[0]);
}

// 取消照片的函式
function cancelImage() {
    document.getElementById('imageUpload').value = ""; // 清除 input
    var output = document.getElementById('preview');
    output.src = "#";
    output.style.display = "none"; // 隱藏預覽

    // 當沒有照片時，隱藏取消按鈕
    document.getElementById('cancelBtn').style.display = "none";
}
}