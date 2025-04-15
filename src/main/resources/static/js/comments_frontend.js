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


function getLoginMemberId() {
  return fetch("/comments/loginMember", {
    method: "GET",
    credentials: "include" // 讓 session cookie 自動帶過去
  })
  .then(response => {
    if (!response.ok) {
      throw new Error("尚未登入");
    }
    return response.text(); // 回傳 memberId 字串
  });
}

	
	
// 從網址上取得 ?eventId=XXX 的參數
const urlParams = new URLSearchParams(window.location.search);
const eventId = urlParams.get("eventId");

// 當前登入會員 ID（從 session 拿)
//const currentMemberId = 'EM001'; //這裡暫時寫死
let currentMemberId = null; // 動態取用

// 初始化留言相關變數
let page = 0;
let noMoreData = false;
let loading = false;

// 當網頁載入完成後才執行以下內容
$(document).ready(function () {
  // ⚠️ 如果網址沒帶 eventId，跳錯誤提醒並中止
  if (!eventId) {
    alert("找不到活動編號，無法載入留言板！");
    return;
  }

  // 1. 先從 Session 抓目前登入的會員 ID（eventMemberId）
  getLoginMemberId()
    .then(memberId => {
      currentMemberId = memberId;
	  
	  
	  console.log("送出留言時的 memberId：", currentMemberId);

	  
      // 2. 成功後才開始載入資料（避免留言錯亂）
      loadEventInfo(); // 載入活動圖片／主辦人／時間
      loadComments(); // 載入留言串
      setupInfiniteScroll(); // 滾動載入更多留言
    })
    .catch(err => {
      alert("尚未登入或未參加活動，請先登入");
      console.warn(err);
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
	  
	  // append base64 圖片
	  const carousel = $(".myclass");
	        carousel.empty(); // 清空舊內容
	        data.eventPhotos.forEach(base64 => {
	          const img = `<div><img src="data:image/jpeg;base64,${base64}" class="carousel-image"></div>`;
	          carousel.append(img);
	        });


	  
	  
//      (data.photoUrls || []).forEach(photo => {
//        carousel.append(`<div><img src="http://localhost:8080${photo}" alt="活動圖片"></div>`);
//      });

	  // slick 初始化放最後
      carousel.slick({
        arrows: true,
        dots: true,
        centerMode: true,
        centerPadding: "60px",
        slidesToShow: 3,
        autoplay: true,
        autoplaySpeed: 3000,
        responsive: [{
          breakpoint: 768,
          settings: {
            arrows: false,
            centerPadding: "0",
            slidesToShow: 1
          }
        }]
      });
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
}

// 建立留言區塊 DOM
function renderComment(comment, returnBox = false) {
  const isOwner = comment.eventMemberId === currentMemberId; // isOwner用來判斷留言是不是本人 → 控制是否顯示編輯／刪除按鈕
  const avatarUrl = comment.imageUrl || `https://i.pravatar.cc/40?u=${comment.eventMemberId}`;
  const timeStr = comment.commentTime ? new Date(comment.commentTime).toLocaleString() : '';
  const memberLink = `<a href="/members/${comment.eventMemberId}/profile">${comment.memberName || '匿名'}</a>`;

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
	box.find(".comment-message").text(comment.commentMessage);
	//box.find(".comment-meta").html(`${comment.memberName || '匿名'}<span class="comment-time">${comment.commentTime ? new Date(comment.commentTime).toLocaleString() : ''}</span>`);
	
	
	
  // 編輯／刪除／回覆邏輯：以下為你原有，可保留
  box.find(".options-btn").on("click", function () {
    box.find(".tooltip-text").css("opacity", 0);
    box.find(".dropdown").toggle();
  });

  box.find(".reply-btn").on("click", function () {
    const input = $("#newCommentInput");
    input.val(`@${comment.memberName} `).focus(); // ⚠️ 改為 comment.memberName
  });

  box.find(".edit-btn").click(function (e) {
    e.preventDefault();
    const msgDiv = box.find(".comment-message");
    const originalMsg = msgDiv.text();
    const input = $(`<input type="text" class="edit-input" value="${originalMsg}" />`);
    msgDiv.replaceWith(input);
    input.focus();

    input.on("keydown", function (e) {
      if (e.key === "Enter") {
        const newMsg = input.val().trim();
        if (!newMsg) return;
        $.ajax({
          url: `/comments/${commentId}`,
          method: "PUT",
          contentType: "application/json",
          data: JSON.stringify({
            commentMessage: newMsg,
            eventMember: { eventMemberId: currentMemberId }
          }),
          success: function () {
            page = 0;
            noMoreData = false;
            loadComments();
          }
        });
      } else if (e.key === "Escape") {
        input.replaceWith(`<div class="comment-message">${originalMsg}</div>`);
      }
    });
  });

  box.find(".delete-btn").click(function (e) {
    e.preventDefault();
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
	    eventMemberId: currentMemberId
	  }),
//      data: JSON.stringify({
//        commentMessage: msg,
//        eventMember: { eventMemberId: currentMemberId } // 使用 session 抓到的 memberId
//      }),
      success: function (newComment) {
        $("#newCommentInput").val('');
        const box = renderComment(newComment, true);
        $("#commentsContainer").append(box); // 把留言插入列表底部
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