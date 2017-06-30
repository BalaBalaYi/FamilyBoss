
$(document).ready(function() {

	// 表单提交
	$('.page-container form').submit(function(){
		var username = $(this).find('.username').val();
		var password = $(this).find('.password').val();
		if(username == '') {
			$(this).find('.error').fadeOut('fast', function(){
				$(this).css('top', '27px');
			});
			$(this).find('.error').fadeIn('fast', function(){
				$(this).parent().find('.username').focus();
			});
			return false;
		}
		if(password == '') {
			$(this).find('.error').fadeOut('fast', function(){
				$(this).css('top', '96px');
			});
			$(this).find('.error').fadeIn('fast', function(){
				$(this).parent().find('.password').focus();
			});
			return false;
		}
		
		// 验证码
		var verifyTxt = $(".drag_text").html();
		if(verifyTxt != "验证通过"){
			$(".drag_text").letterfx({"fx":"smear","fx_duration":"200ms"});
			return false;
		}
		
	});

	$('.page-container form .username, .page-container form .password').keyup(function(){
		$(this).parent().find('.error').fadeOut('fast');
	});

	var reason = $("#loginMsg").val();
	if("" != reason){
		$(".errorMsg").html(reason);
		$(".errorMsgDiv").css("visibility", "visible");
	}
	
	var msg = $("#reLoginMsg").val();
	if("" != msg){
		$(".errorMsg").html(msg);
		$(".errorMsgDiv").css("visibility", "visible");
	}
	
	// 滑块验证码
	$("#drag").drag();
});
