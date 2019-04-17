$(function() {
    $('input[id="daterange"]').daterangepicker({
        opens: 'left'
    }, function(start, end, label) {
        console.log("A new date selection was made: " + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD'));
    });
});

$('.tab-nav li').click(function(){
    $('.tab-nav li').removeClass('current');
    $(this).addClass('current');
    $('.tab-content').css('display','none');
    $('.tab-content').eq($(this).index()).css('display','block')
})