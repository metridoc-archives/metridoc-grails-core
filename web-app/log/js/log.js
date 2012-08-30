/**
 * Created with IntelliJ IDEA.
 * User: dongheng
 * Date: 8/24/12
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */

$(function(){
    $('#typeSelector').change(function(){
        if( $(this).val()=="all" )
        {
            $('.info, .warn, .error').show();
        }
        else if( $(this).val()=="error" )
        {
            $('.error').show();
            $('.info, .warn').hide();
        }
        else if( $(this).val()=="info" )
        {
            $('.info').show();
            $('.error, .warn').hide();
        }
        else if( $(this).val()=="warn" )
        {
            $('.warn').show();
            $('.error, .info').hide();
        }
    });
});

$(function() {
    $('#dateSelector').change(function(){
        if( $(this).val()=="all" )
        {
            $('.all, .hour, .sixHours, .twelveHours, .day').show();
        }
        else if( $(this).val()=="hour" )
        {
            $('.all').hide();
            $('.hour').show();
        }
        else if( $(this).val()=="sixHours" )
        {
            $('.all').hide();
            $('sixHours').show();
        }
        else if( $(this).val()=="twelveHours" )
        {
            $('.all').hide();
            $('.twelveHours').show();
        }
        else if( $(this).val()=="day" )
        {
            $('.all').hide();
            $('.day').show();
        }
    });
})

$(window).load(function(){

})