//0323增加，活動詳情關聯照片輪播的設定
//        $(document).ready(function() {
//            // 初始化輪播
//            $(".myclass").slick({
//                arrows: true,
//                dots: true,
//                centerMode: true,
//                centerPadding: "60px",
//                slidesToShow: 3,
//                autoplay: true,
//                autoplaySpeed: 4000,
//                pauseOnHover: false,
//                pauseOnFocus: false,
//                pauseOnDotsHover: false,
//                responsive: [{
//                    breakpoint: 768,
//                    settings: {
//                        arrows: false,
//                        centerPadding: "0",
//                        slidesToShow: 1
//                    }
//                }]
//            });
//            
//            // 透過 AJAX 獲取活動資訊
//            $.ajax({
//                url: 'comment.json', // 替換為你的 API 或 JSON 路徑
//                method: 'GET',
//                dataType: 'json',
//                success: function(data) {
//                    if (data.events && data.events.length > 0) {
//                        let firstEvent = data.events[0];
//                        $('#host').text(firstEvent.host);
//                        $('#location').text(firstEvent.location);
//                        $('#time').text(firstEvent.time);
//                        $('#more-info').attr('href', firstEvent.moreInfo);
//
//                        // 動態加載圖片到輪播
//                        let carousel = $('.myclass');
//                        firstEvent.images.forEach(img => {
//                            carousel.slick('slickAdd', `<div><img src="${img}" style="width:100%;"></div>`);
//                        });
//                    }
//                },
//                error: function() {
//                    console.error("無法載入活動資料");
//                }
//            });
//        });


//	fetch("http://localhost:8080/comments/page", {
//		method: "GET",
//		credentials: "include" // 記得加這個，才會帶 session cookie
//	})


//function getLoginMemberId() {
//  return fetch("/comments/loginMember", {
//    method: "GET",
//    credentials: "include" // 讓 session cookie 自動帶過去
//  })
//  .then(response => {
//    if (!response.ok) {
//      throw new Error("尚未登入");
//    }
//    return response.text(); // 回傳 memberId 字串
//  });
//}


let currentMemberId = null;
let currentlyEditingBox = null;
let currentlyDropdownBox = null; //用來追蹤「哪一則留言的 ⋯ 選單目前打開中」的變數


// 記得傳 eventId 當參數
function getLoginEventMemberId() {
    return fetch(`/comments/loginMember?eventId=${eventId}`, {
//  return fetch(`/comments/eventMember/${eventId}`, {
        method: "GET",
        credentials: "include"
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("尚未登入或未參加活動");
            }
            return response.text(); // ✅ 回傳 EM001
        });
}


// 從網址上取得 ?eventId=XXX 的參數
const urlParams = new URLSearchParams(window.location.search);
const eventId = urlParams.get("eventId");

// 當前登入會員 ID（從 session 拿)
//const currentMemberId = 'EM001'; //這裡暫時寫死
//let currentMemberId = null; // 動態取用


let currentEventMemberId = null;
//let currentCommentId = null;

// 初始化留言相關變數
let page = 0;
let noMoreData = false;
let loading = false;

// 當網頁載入完成後才執行以下內容
//$(document).ready(function () {
//  // ⚠️ 如果網址沒帶 eventId，跳錯誤提醒並中止
//  if (!eventId) {
//    alert("找不到活動編號，無法載入留言板！");
//    return;
//  }
//
//  // 1. 先從 Session 抓目前登入的會員 ID（eventMemberId）
//  getLoginMemberId()
//    .then(memberId => {
//      currentMemberId = memberId;
//	  
//	  
//	  console.log("送出留言時的 memberId：", currentMemberId);
//
//	  
//      // 2. 成功後才開始載入資料（避免留言錯亂）
//      loadEventInfo(); // 載入活動圖片／主辦人／時間
//      loadComments(); // 載入留言串
//      setupInfiniteScroll(); // 滾動載入更多留言
//    })
//    .catch(err => {
//      alert("尚未登入或未參加活動，請先登入");
//      window.location.href = "/login.html";
//      console.warn(err);
//    });
//});


$(document).ready(function () {
    if (!eventId) {
        alert("找不到活動編號");
        return;
    }

//    $(document).on("click", ".edit-btn", function (e) {
//        e.preventDefault();
//        console.log("點到編輯按鈕");
//
//        const box = $(this).closest(".comment-box");
//        box.find(".dropdown").hide();
//        currentlyDropdownBox = null;
//
//        if (currentlyEditingBox && currentlyEditingBox[0] !== box[0]) {
//            const previousInput = currentlyEditingBox.find(".edit-input");
//            const originalMsg = previousInput.attr("data-original");
//            previousInput.replaceWith(`<div class="comment-message">${originalMsg}</div>`);
//        }
//
//        const commentId = box.data("id");
//        const msgDiv = box.find(".comment-message");
//
//
//
//        if (msgDiv.length > 0) {
//            const originalMsg = msgDiv.text();
//            const input = $(`<input type="text" class="edit-input" value="${originalMsg}" />`);
//            input.attr("data-original", originalMsg);
//
//            // 儲存替換前的元素位置
//            const parent = msgDiv.parent();
//
//            // 執行替換
//            msgDiv.replaceWith(input);
//
//            // 驗證替換是否成功
//            console.log("替換後的元素:", parent.find(".edit-input"));
//        } else {
//            console.log("未找到要替換的元素");
//        }
//
//
//        input.focus();
//        currentlyEditingBox = box;
//
//        input.off("keydown").on("keydown", function (e) {
//            if (e.key === "Enter") {
//                const newMsg = input.val().trim();
//                if (!newMsg) return;
//
//                $.ajax({
//                    url: `/comments/${commentId}`,
//                    method: "PUT",
//                    contentType: "application/json",
//                    data: JSON.stringify({
//                        commentMessage: newMsg,
//                        eventMemberId: currentEventMemberId
//                    }),
//                    success: function () {
//                        page = 0;
//                        noMoreData = false;
//                        currentlyEditingBox = null;
//                        loadComments();
//                    }
//                });
//            } else if (e.key === "Escape") {
//                input.replaceWith(`<div class="comment-message">${originalMsg}</div>`);
//                currentlyEditingBox = null;
//            }
//        });
//    });

	// 編輯留言
	$(document).on("click", ".edit-btn", function (e) {
	  e.preventDefault();
	  console.log("✅ 點到編輯按鈕");
	
	  const box = $(this).closest(".comment-box");
	  const commentId = box.data("id");
	  console.log("🔍 目前 commentId：", commentId);
	  
	  box.find(".dropdown").hide(); // 收起 ⋯ 選單
	  currentlyDropdownBox = null;

	  console.log('currentlyEditingBox', currentlyEditingBox);
	  console.log('box', box);
	
	  // 如果有其他留言正在編輯，先還原
	  //if (currentlyEditingBox && currentlyEditingBox[0] !== box[0]) {
      const previousInput = $('[class^=edit-input]')
	  if ( previousInput.length > 0) {
		//const previousInput = currentlyEditingBox.find(".edit-input");
	    const original = previousInput.attr("data-original");
	    previousInput.replaceWith(`<div class="comment-message">${original}</div>`);
	  }
	
	  const msgDiv = box.find(".comment-message");
	  if (msgDiv.length === 0) {
	    console.error("❌ 找不到 .comment-message");
	    return;
	  }
	
	  const originalMsg = msgDiv.text();
	  console.log("原始留言文字：", originalMsg);
	  const inputHtml = `<input type="text" class="edit-input-${commentId}" value="${originalMsg}" />`;
	
	  msgDiv.replaceWith(inputHtml);
	  const input = $(`.edit-input-${commentId}`);
	  input.attr("data-original", originalMsg);

	  console.log("✅ 已將留言替換為 input：", input[0]);
	  
	  input.focus();
	  console.warn("11111111111");
	  console.log('box??', box);
	  currentlyEditingBox = box;
	  console.log('currentlyEditingBox??', currentlyEditingBox);
	  console.warn("2222222222222");
	  
	  // 處理按鍵事件（Enter = 送出編輯；Esc = 取消）
	  input.off("keydown").on("keydown", function (e) {
		console.warn("3333333333333");
	    if (e.key === "Enter") {
	      const newMsg = input.val().trim();
	      if (!newMsg) return;
	
	      $.ajax({
	        url: `/comments/${commentId}`,
	        method: "PUT",
	        contentType: "application/json",
	        data: JSON.stringify({
	          commentMessage: newMsg,
	          eventMemberId: currentEventMemberId,
			  eventId
	        }),
	        success: function () {
	          console.log("✅ 留言成功更新");
	          page = 0;
	          noMoreData = false;
	          currentlyEditingBox = null;
	          loadComments();
	        }
	      });
	    } else if (e.key === "Escape") {
	      input.replaceWith(`<div class="comment-message">${originalMsg}</div>`);
	      currentlyEditingBox = null;
		  console.log("↩️ 已取消編輯，還原留言");
	    }
	  });
	});



    getLoginEventMemberId()
        .then(eventMemberId => {
            currentEventMemberId = eventMemberId; // ✅ 這裡就會是 EM001
            console.log("目前登入者的 eventMemberId：", currentEventMemberId);
            loadEventInfo();
            loadComments();
            setupInfiniteScroll();
        })
        .catch(err => {
            alert("尚未登入或未參加此活動！");
            window.location.href = "/login.html";
        });

    // 點擊空白處時，同時關閉 ⋯ 選單 和 編輯模式
    $(document).on("click", function (e) {
//		// 如果目前有打開的 dropdown，且點擊的不是選單區域
//	    if (currentlyDropdownBox && !$(e.target).closest(".dropdown").length && !$(e.target).hasClass("options-btn")) {
//	      currentlyDropdownBox.find(".dropdown").hide();
//	      currentlyDropdownBox = null;
//	    }

//	  // 判斷不是選單也不是 ⋯ 按鈕本身
//	    const isClickOutsideDropdown = !$(e.target).closest(".dropdown").length && !$(e.target).hasClass("options-btn");
//	    const isClickOutsideEditBox = !$(e.target).hasClass("edit-input");
//
//	    if (isClickOutsideDropdown && currentlyDropdownBox) {
//	      currentlyDropdownBox.find(".dropdown").hide();
//	      currentlyDropdownBox = null;
//	    }
//
//	    if (isClickOutsideEditBox && $(".edit-input").length) {
//	      const input = $(".edit-input");
//	      const originalMsg = input.data("original");
//	      input.replaceWith(`<div class="comment-message">${originalMsg}</div>`);
//		  currentlyEditingBox = null;
//	    }
//	
console.log('$(e.target)', $(e.target));

        const isClickInsideDropdown = $(e.target).closest(".dropdown").length || $(e.target).hasClass("options-btn");
        const isClickInsideEdit = $(e.target).hasClass("edit-input");

        // 點空白處 => 關閉 ⋯
        if (!isClickInsideDropdown && currentlyDropdownBox) {
            currentlyDropdownBox.find(".dropdown").hide();
            currentlyDropdownBox = null;
        }

        // 點空白處 => 取消編輯
        if (!isClickInsideEdit && currentlyEditingBox) {
            const input = currentlyEditingBox.find(".edit-input");
            const original = input.attr("data-original");
            input.replaceWith(`<div class="comment-message">${original}</div>`);
            currentlyEditingBox = null;
			console.log('!!!!!!!!!!!!!!!!')
        }
    });


});


// 載入活動資訊（圖片輪播、主辦人、時間）
function loadEventInfo() {
    $.ajax({
        url: `/comments/eventInfo/${eventId}`,
        method: "GET",
        success: function (data) {

//      const photoUrls = data.photoUrls || [];
//	  const photoBase64List = data.eventPhotos || [];


            $("#eventName").text(data.eventName || "未建立的活動");
            $("#holderName").text("活動舉辦人：" + (data.holderName || "未知"));
            $("#orderStart").text("留言版開放時間：" + (data.orderStart || "未知"));
            $("#orderEnd").text("留言版關閉時間：" + (data.orderEnd || "未知"));
            $("#spaceLocation").text("活動地點：" + (data.spaceLocation || "未提供地點"));


            // 活動圖片輪播處理
//      const carousel = $(".myclass");
//	  if (Array.isArray(photoUrls) && photoUrls.length > 0) {
//	      photoUrls.forEach(base64 => {
//	          const imageSrc = `data:image/jpeg;base64,${base64}`;
//	          carousel.append(`<div><img src="${imageSrc}" alt="活動圖片"></div>`);
//	      });
//	      
//	  }

//	  const carousel = $(".myclass");
//	  if (Array.isArray(photoBase64List) && photoBase64List.length > 0) {
//	      photoBase64List.forEach(base64 => {
//	          const imageSrc = `data:image/jpeg;base64,${base64}`;
//	          carousel.append(`<div><img src="${imageSrc}" alt="活動圖片"></div>`);
//	      });
//	  }

//            // append base64 圖片
//            const carousel = $(".myclass");
//            carousel.empty(); // 清空舊內容
//            data.eventPhotos.forEach(base64 => {
//                const img = `<div><img src="data:image/jpeg;base64,${base64}" class="carousel-image"></div>`;
//                carousel.append(img);
//            });


//      (data.photoUrls || []).forEach(photo => {
//        carousel.append(`<div><img src="http://localhost:8080${photo}" alt="活動圖片"></div>`);
//      });

//            // slick 初始化放最後
//            carousel.slick({
//                arrows: true,
//                dots: true,
//                centerMode: true,
//                centerPadding: "60px",
//                slidesToShow: 3,
//                autoplay: true,
//                autoplaySpeed: 3000,
//                responsive: [{
//                    breakpoint: 768,
//                    settings: {
//                        arrows: false,
//                        centerPadding: "0",
//                        slidesToShow: 1
//                    }
//                }]
//            });
        },
        error: function () {
            alert("讀取活動資訊失敗！");
        }
    });
}

// 載入留言
function loadComments() {
    if (noMoreData || loading) return;
    loading = true;

//  $.get("/comments/loginMember", function (memberId) {
//     currentMemberId = memberId; // 先拿到登入會員 ID

    $.ajax({
        url: `/comments/event/${eventId}/page/${page}/5`,
        method: "GET",
        success: function (data) {
            if (page === 0) {
                $("#commentsContainer").empty();
                $("#noCommentMessage").toggle(data.length === 0);
            }

            if (data.length === 0) {
                noMoreData = true;
                return;
            }

            data.forEach(comment => renderComment(comment));
            page++;
            loading = false;
        },
        error: function () {
            alert("無法載入留言資料");
            loading = false;
        }
    });
//	});
}

// 建立留言區塊 DOM
function renderComment(comment, returnBox = false) {

    // 🔧 workaround：如果是自己剛剛新增的留言，後端未帶 eventMember.memberId，這裡補上
    if (comment.eventMember && !comment.eventMember.memberId) {
        comment.eventMember.memberId = currentMemberId;
    }


//  const isOwner = comment.eventMemberId === currentMemberId; // isOwner用來判斷留言是不是本人 → 控制是否顯示編輯／刪除按鈕
//  const isOwner = comment.eventMember && comment.eventMember.memberId === currentMemberId;
//  const isOwner = comment.eventMember?.memberId === currentMemberId;
//  console.log("登入會員:", currentMemberId, "留言會員:", comment.eventMember?.memberId);


    const isOwner = comment.eventMemberId === currentEventMemberId;


    const avatarUrl = comment.imageUrl || `https://i.pravatar.cc/40?u=${comment.eventMemberId}`;
    const timeStr = comment.commentTime ? new Date(comment.commentTime).toLocaleString() : '';
    const memberLink = `<a href="/members/${comment.eventMemberId}/profile">${comment.memberName || '匿名'}</a>`;
    console.log("留言資料：", comment);


    const box = $(`
    <div class="comment-box" data-id="${comment.commentId}">
      <div class="comment-header">
        <a href="/members/${comment.eventMemberId}/profile">
          <img src="${avatarUrl}" class="profile-img">
        </a>
        <div style="flex-grow:1;">
          <div class="comment-meta">${memberLink}<span class="comment-time">${timeStr}</span></div>
          <div class="comment-message">${comment.commentMessage}</div>
          <button class="reply-btn">回覆</button>
        </div>
        ${isOwner ? `
          <button class="options-btn">⋯</button>
          <div class="tooltip-text">編輯或刪除此留言</div>
          <div class="dropdown position-absolute" style="top: 30px; right: 10px; display: none;">
            <ul class="dropdown-menu show" style="position:static;float:none;min-width:auto;">
              <li><a class="dropdown-item edit-btn" href="#">編輯</a></li>
              <li><a class="dropdown-item delete-btn" href="#">刪除</a></li>
            </ul>
          </div>
        ` : ''}
      </div>
    </div>
  `);

//	box.find(".comment-message").text(comment.commentMessage); // ❌ 這行會把 <input class="edit-input"> 蓋掉
    //box.find(".comment-meta").html(`${comment.memberName || '匿名'}<span class="comment-time">${comment.commentTime ? new Date(comment.commentTime).toLocaleString() : ''}</span>`);


    // 編輯／刪除／回覆邏輯：以下為你原有，可保留
//  box.find(".options-btn").on("click", function () {
//    box.find(".tooltip-text").css("opacity", 0);
//    box.find(".dropdown").toggle();
//  });
//  
    box.find(".options-btn").on("click", function (e) {
        e.preventDefault();

        // ✅ 若已有其他留言的選單打開，先關閉
        if (currentlyDropdownBox && currentlyDropdownBox !== box) {
            currentlyDropdownBox.find(".dropdown").hide();
        }

        // ✅ 切換目前留言的選單
        const dropdown = box.find(".dropdown");
        dropdown.toggle();

        // ✅ 記錄目前打開的留言
        currentlyDropdownBox = dropdown.is(":visible") ? box : null;
    });


    box.find(".reply-btn").on("click", function () {
        const input = $("#newCommentInput");
        input.val(`@${comment.memberName} `).focus(); // ⚠️ 改為 comment.memberName
    });

//   box.find(".edit-btn").click(function (e) {
// 	console.log("點到編輯按鈕"); // 加上這行來驗證點擊有觸發
//     e.preventDefault();
// 	box.find(".dropdown").hide(); // 編輯時收起選單
// 	currentlyDropdownBox = null; // 記得同步清除狀態
//
//
// 	// 若目前已有正在編輯的留言，先恢復原樣
// 	if (currentlyEditingBox && currentlyEditingBox !== box) {
// 	  const previousInput = currentlyEditingBox.find(".edit-input");
// 	  const originalMsg = previousInput.attr("data-original");
// 	  previousInput.replaceWith(`<div class="comment-message">${originalMsg}</div>`);
// 	}
//
// //	const commentBox = document.querySelector(".comment-box");
// //	const commentId = commentBox.dataset.id;
// 	const commentId = box.data("id");
//
//     const msgDiv = box.find(".comment-message");
//     const originalMsg = msgDiv.text();
//     const input = $(`<input type="text" class="edit-input" value="${originalMsg}" />`);
// 	input.attr("data-original", originalMsg); // 存原始留言，取消編輯時可用
// 	msgDiv.replaceWith(input);
//     input.focus();
//
// 	// 記錄目前正在編輯的 box
// 	currentlyEditingBox = box;
//
// //	$(".edit-input").off("keydown"); // ⛔ 移除舊的 keydown 綁定，避免多次觸發
// 	input.off("keydown"); // ✅ 只移除這個 input 的綁定，較安全
//
//     input.on("keydown", function (e) {
//       if (e.key === "Enter") {
//         const newMsg = input.val().trim();
//         if (!newMsg) return;
//         $.ajax({
//           url: `/comments/${commentId}`,
//           method: "PUT",
//           contentType: "application/json",
//           data: JSON.stringify({
//             commentMessage: newMsg,
// 			eventMemberId: currentEventMemberId
// //            eventMember: { eventMemberId: currentMemberId }
// //			eventMember: { eventMemberId: currentEventMemberId }
//           }),
//           success: function () {
//             page = 0;
//             noMoreData = false;
// 			currentlyEditingBox = null; // 清除狀態
//             loadComments();
//           }
//         });
//       } else if (e.key === "Escape") {
//         input.replaceWith(`<div class="comment-message">${originalMsg}</div>`);
// 		currentlyEditingBox = null; // 清除狀態
//       }
//     });
//   });

    box.find(".delete-btn").click(function (e) {
        e.preventDefault();
        box.find(".dropdown").hide(); // 刪除時收起選單
        currentlyDropdownBox = null; // 清除目前狀態
        const commentId = box.data("id");
        if (confirm("確定要刪除此留言？")) {
            $.ajax({
                url: `/comments/${commentId}`,
                method: "DELETE",
                success: function () {
                    page = 0;
                    noMoreData = false;
                    loadComments();
                }
            });
        }
    });

    if (returnBox) return box;
    $("#commentsContainer").append(box);
}


// 新增留言
$("#newCommentInput").on("keydown", function (e) {
    if (e.key === "Enter") {
        const msg = $(this).val().trim();
        if (!msg) return;
        $.ajax({
            url: `/events/${eventId}/comments`,
            method: "POST",
            contentType: "application/json",

            data: JSON.stringify({
                commentMessage: msg,
//	    eventMemberId: currentMemberId,
                eventMemberId: currentEventMemberId,
                eventId: eventId
            }),
//      data: JSON.stringify({
//        commentMessage: msg,
//        eventMember: { eventMemberId: currentMemberId } // 使用 session 抓到的 memberId
//      }),
//      success: function (newComment) {
//        console.log(newComment);
//        $("#newCommentInput").val('');
//        const box = renderComment(newComment, true);
//        $("#commentsContainer").append(box); // 把留言插入列表底部
//      }
            success: function () {
                $("#newCommentInput").val('');
                page = 0;
                noMoreData = false;
                loadComments(); // 重新查一次留言，拿到完整資料（包含 memberName）
            }
        });
    }
});

// 無限滾動載入留言
function setupInfiniteScroll() {
    $(window).on("scroll", function () {
        if ($(window).scrollTop() + $(window).height() >= $(document).height() - 50) {
            loadComments();
        }
    });
}