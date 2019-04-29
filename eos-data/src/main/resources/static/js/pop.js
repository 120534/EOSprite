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
        data:{orderId : id},
        dataType: 'json',
        success:function (res) {
            $('#td-complete-time').text(res.orderCompletedTime);
            $('#th-order-statu').text(res.message);
            alert(res.message);
        }
    })
});


// var getting = {
//     url:'/data/status?orderId='+orderId,
//     dataType:'json',
//     success:function(res) {
//         console.log(res);
//         if(res.code === 200){
//             console.log(res)
//         }else{
//             console.log('error');
//         }
//     }
// };
// //关键在这里，Ajax定时访问服务端，不断获取数据 ，这里是3秒请求一次。
// window.setInterval(function(){$.ajax(getting)},3000);


