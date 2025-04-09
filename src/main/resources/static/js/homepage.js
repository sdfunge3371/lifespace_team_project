$(document).ready(function () {
            // 監聽跑馬燈訊息的點擊事件
            $('#marquee-alert').click(function () {
                // 取得跑馬燈內容
                var marqueeContent = $('#marquee-content').html();

                // 將跑馬燈內容放入 Modal body
                $('#marqueeModalBody').html(marqueeContent);

                // 顯示 Modal
                $('#marqueeModal').modal('show');
            });
        });