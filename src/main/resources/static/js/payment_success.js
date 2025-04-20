const params = new URLSearchParams(window.location.search);
const orderId = params.get("orderId");

    fetch(`/orders/status/${orderId}`)
        .then(res => res.text())
        .then(orderStatus => {
            orderStatus = orderStatus.trim();
            console.log("orderStatus=", orderStatus);
            if (orderStatus == 1) {
                // 付款成功
                document.querySelector(".modal-content").style.display = "block";
                document.querySelector(".animation-container").style.display = "flex";

                if (typeof window["lineIt"] !== "undefined" && lineIt.loadButton) {
                    lineIt.loadButton();
                }
            } else {

				// 設定 "揪團去" 按鈕的連結，帶入 orderId
				   const eventButton = document.querySelector(".btn-primary");
				   if (eventButton) {
				       eventButton.onclick = function () {
				           location.href = `/event_create.html?orderId=${orderId}`;
				       };
				   }

				 } else {
                // 顯示付款失敗畫面
                window.location.href = "/payment_fail.html";
            }
        })
        .catch(err => {
            console.error("驗證失敗：", err);
            window.location.href = "/payment_fail.html";
        });

sessionStorage.removeItem("currentOrderId");

