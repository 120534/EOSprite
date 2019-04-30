$(function() {
    $('input[id="daterange"]').daterangepicker({
        opens: 'left'
    }, function(start, end, label) {
        console.log("A new date selection was made: " + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD'));
    });
});

$('.tab-nav li').click(function(){
    $('.tab-nav li').removeClass('path-row');
    $(this).addClass('path-row');
    $('.tab-content').css('display','none');
    $('.tab-content').eq($(this).index()).css('display','block')
});

var map = L.map('map',{zoomControl: false}).setView([35, 110], 4);
L.tileLayer('https://webrd0{s}.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}', {
    subdomains: ["1", "2", "3", "4"], //可用子域名，用于浏览器并发请求
    attribution: "&copy; 高德地图", //可以修改为其它内容
}).addTo(map); //添加tile层到地图
new L.Control.Zoom({ position: 'topright' }).addTo(map);

//先只做了数字校验,post一共提交了七个字段。
//提前处理一下
$("#btn-1").click(function(){
    const dataSet = $("#select-dataSet").val();
    const productCode = $("#select-product").val();
    const startPath = $("#startPath").val();
    const endPath = $("#endPath").val();
    const startRow = $("#startRow").val();
    const endRow = $("#endRow").val();
    const p = /^[0-9]*$/;

    //判断是否选择数据集
    if (dataSet == null){
        alert("请选择数据集");
        return false;
    }
    if (productCode == null){
        alert("请选择数据产品");
        return false;
    }

    if (startPath === "" || endPath === ""){
        alert("path不能为空!");
        return false;
    }
    
    if (!p.test(startPath) || !p.test(endPath))
    {
        alert("path必须为数字!");
        return false;
    }
    if (startRow === "" || endRow === ""){
        alert("row不能为空!");
        return false;
    }
    if (!p.test(startRow) || !p.test(endRow))
    {
        alert("Row必须为数字!");
        return false;
    }
});

