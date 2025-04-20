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

                window.onload = function () {
                    // 顯示 QRCode 按鈕
                    document.getElementById("line-qrcode").style.display = "block";

                    // 初始化 Line的點擊加入好友按鈕
                    if (typeof lineIt !== "undefined" && lineIt.loadButton) {
                        lineIt.loadButton();
                    }
                };

                // 設定 "揪團去" 按鈕的連結，帶入 orderId
                const eventButton = document.querySelector(".btn-primary");
                if (eventButton) {
                    eventButton.onclick = function () {
                        location.href = `/lifespace/event_create?orderId=${orderId}`;
                    };
                }

            } else {
                // 顯示付款失敗畫面
                window.location.href = "/lifespace/payment_fail";
            }
        })
        .catch(err => {
            console.error("驗證失敗：", err);
            window.location.href = "/lifespace/payment_fail";
        });

sessionStorage.removeItem("currentOrderId");

