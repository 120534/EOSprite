//js for Modal components
$('#exampleModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget) // Button that triggered the modal
    var recipient = button.data('whatever') // Extract info from data-* attributes
    // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
    // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
    var modal = $(this)
    modal.find('.modal-title').text('New message to ' + recipient);
    modal.find('.modal-body input').val(recipient)
});

//js for data process

var orderId = document.getElementById('td-order-id').innerText;
console.log('orderId = '+ orderId);
$('#excuteBtn').click(function () {
    $('#th-order-status').text('开始处理订单');
    $("#excuteBtn").unbind();
    $.ajax({
        url: '/process',
        data:{orderId : orderId},
        dataType: 'json',
        success:function (res) {
            $('#td-complete-time').text(res.orderCompletedTime);
            $('#th-order-status').text(res.message);
            alert(res.message);
        }
    })
});

//长轮询查找数据产品对应的preview uri以及download uri
//查询条件，dataGranuleId或者orderDataGranuleId，或者直接使用orderId,可以据此找到orderDataGranule
//轮询判断条件$('#th-order-status').val() == "订单完成"||"订单错误"
var updater = {
    poll: function () {
        $("#order-detail-btn").unbind();
        $.ajax({
            url: "/data/polling",
            type: "GET",
            data: {orderId : orderId},
            dataType: "json",
            success: updater.onSuccess,
            error: updater.onError
        });
    },
    onSuccess: function (data, dataStatus) {
        try {
            //轮询结束条件为 id="th-order-status" innerHtml "订单已完成
            //传回来的data数据应该包含了Id，以及两个url
            for(var i in data){
                if (data[i].dataGranuleUri != null){
                    var idUrl = data[i].dataGranuleId + '-url';
                    var previewUrl = data[i].dataGranuleId + '-preview';
                    document.getElementById(idUrl).setAttribute('href',data[i].dataGranuleUri);
                    document.getElementById(previewUrl).setAttribute('href',data[i].dataGranulePreview);
                }
            }
        }
        catch (e) {
            updater.onError();
            return;
        }
        // 用页面判断是否已经完成订单，这时候，还需要再实现一次查询才可以将所有的数据信息包括在内。
        var orderStatus = document.getElementById('th-order-status').innerText;

        if (orderStatus === '订单已完成'){
            //判断订单已经完成后，最后再执行一次轮询
            $.ajax({
                url: "/data/polling",
                type: "GET",
                data: {orderId : orderId},
                dataType: "json",
                success: function (data) {
                    for(var i in data){
                        if (data[i].dataGranuleUri != null){
                            var idUrl = data[i].dataGranuleId + '-url';
                            var previewUrl = data[i].dataGranuleId + '-preview';
                            document.getElementById(idUrl).setAttribute('href',data[i].dataGranuleUri);
                            document.getElementById(previewUrl).setAttribute('href',data[i].dataGranulePreview);
                        }
                    }
                }
            });
            clearInterval(interval);
            return;
        }

//      收到回复后再次发出请求
        interval = window.setTimeout(updater.poll, 8000);
    },
    onError: function () {
        console.log("Poll error;");
    }
};
function transfer() {
    updater.poll();
}


