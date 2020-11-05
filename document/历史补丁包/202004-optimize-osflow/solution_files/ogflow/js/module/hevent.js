/**
 * Event 对象相关处理
 */
var hevent;

// 按住Ctrl多选的对象
var selMultiple = {};
// 键盘ctrl按下
var ctrlKey = false;

define([], function() {

	// 键盘按下事件
	document.onkeydown = function(event) {
		keyDirection(event);
		
		ctrlKey = event.ctrlKey; // 键盘ctrl按下状态

		if(ctrlKey && event.keyCode == 90) { //Ctrl+Z 撤销
			event.returnvalue = false;
			parent.undo();
		}

	}

	/**
	 * 键盘上下左右移动矫正处理
	 */
	function keyDirection(event) {

		var e = event || window.event || arguments.callee.caller.arguments[0];
		if(e && e.keyCode == 37) { // 左   
			console.log(e.keyCode)
		}
		if(e && e.keyCode == 38) { // 上
			console.log(e.keyCode)
		}
		if(e && e.keyCode == 39) { // 右
			console.log(e.keyCode)
		}
		if(e && e.keyCode == 40) { // 下
			console.log(e.keyCode)
		}
	}


	/**
	 * 键盘弹起事件监听
	 */
	document.onkeyup = function() {

		ctrlKey = false;
		// lkwf.ChangeObjStyle();
	}

	/**
	 * ctrl + 鼠标左键多选事件
	 * @param {Object} moveobj 鼠标点击的对象
	 */
	function keyCtrlHandle(moveobj) {

		console.log(moveobj.id);

	}

	hevent = {
		keyDirection: keyDirection, // 键盘上下左右监听事件
		keyCtrlHandle: keyCtrlHandle
	}

	return hevent;
});